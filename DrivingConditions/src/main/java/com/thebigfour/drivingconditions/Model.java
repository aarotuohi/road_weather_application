package com.thebigfour.drivingconditions;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javafx.util.Pair;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Model {

    static final String TASK_URL_BEGIN = "https://tie.digitraffic.fi/api/maintenance/v1/tracking/routes";
    static final String CONDITIONS_URL_BEGIN = "https://tie.digitraffic.fi/api/v3/data/road-conditions/";
    static final String MESSAGES_URL_BEGIN = "https://tie.digitraffic.fi/api/traffic-message/v1/messages?inactiveHours=0&includeAreaGeometry=false&situationType=";
    static final String TASK_TYPES_URL_BEGIN = "https://tie.digitraffic.fi/api/maintenance/v1/tracking/tasks";

    private TreeMap<LocalDate, ArrayList<MaintenanceTask>> tasksByDate = new TreeMap<>();
    private ArrayList<RoadCondition> allConditions = new ArrayList<>();
    private ArrayList<TrafficMessage> allMessages = new ArrayList<>();
    private HashMap<String, String[]> allTaskTypes = new HashMap<>();

    // From FMI data
    private volatile ArrayList<WeatherObservation> allObservations = new ArrayList<>(50);
    private volatile ArrayList<WeatherForecast> allForecasts = new ArrayList<>(50);

    private volatile TreeMap<LocalDate, ArrayList<WeatherObservation>> observationsByDate = new TreeMap<>();
    private volatile TreeMap<LocalDate, ArrayList<WeatherForecast>> forecastsByDate = new TreeMap<>();
    private volatile TreeMap<LocalDate, ArrayList<WeatherObservation>> observationsByDateWholeMonth = new TreeMap<>();
    private volatile TreeMap<LocalDate, ArrayList<WeatherForecast>> forecastsByDateWholeMonth = new TreeMap<>();

    private String minY;
    private String minX;
    private String maxY;
    private String maxX;
    private Instant startTime;
    private Instant endTime;
    private WeatherSettings settings; //FMI data

    WeatherDataCalculator calculator = new WeatherDataCalculator();
    WeatherDataParser parserObs;
    WeatherDataParser parserForec;

    public enum ParserClassifier {OBSERVATION, FORECAST, BOTH}

    public Model(String minY, String minX, String maxY, String maxX, Instant startTime, Instant endTime, WeatherSettings settings) {
        this.minY = minY;
        this.minX = minX;
        this.maxY = maxY;
        this.maxX = maxX;
        this.startTime = startTime;
        this.endTime = endTime;
        this.settings = settings;
        this.parserObs = new WeatherDataParser(settings);
        this.parserForec = new WeatherDataParser(settings);
        observationsByDate.put(LocalDate.now(), allObservations);
        forecastsByDate.put(LocalDate.now(), allForecasts);
        observationsByDateWholeMonth.put(LocalDate.now(), allObservations);
        forecastsByDateWholeMonth.put(LocalDate.now(), allForecasts);
        fetchTaskTypes();
        System.out.println("Model ready.");
    }

    public void fetchRoadData() {

        allMessages.clear();
        allConditions.clear();
        tasksByDate.clear();
        fetchTasks();
        fetchConditions();
        fetchTrafficMessages();
        System.out.println("Fetched road data!");
    }

    private String fetchAPI(String url) throws Exception {

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Accept-Encoding", "gzip;q=0,deflate,sdch")
                .GET()
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> getResponse = httpClient.send(getRequest, BodyHandlers.ofString());

        return getResponse.body();
    }

    public void fetchTasks() {

        Instant currentStartTime = this.startTime;

        while (!currentStartTime.equals(this.endTime)) {
            LocalDate date = currentStartTime.atZone(ZoneOffset.UTC).toLocalDate();
            Instant currentEndTime = currentStartTime.plus(1, ChronoUnit.DAYS);

            this.tasksByDate.put(date, fetchDaysTasks(currentStartTime, currentEndTime));

            currentStartTime = currentEndTime;

        }
    }

    private ArrayList<MaintenanceTask> fetchDaysTasks(Instant startTime, Instant endTime) {

        String strStartTime = startTime.toString().replace(":", "%3A");
        String strEndTime = endTime.toString().replace(":", "%3A");

        String url = String.format("%s?endFrom=%s&endBefore=%s&xMin=%s&yMin=%s&xMax=%s&yMax=%s&domain=state-roads",
                TASK_URL_BEGIN, strStartTime, strEndTime, this.minY, this.minX, this.maxY, this.maxX);

        System.out.println(url);

        ArrayList<MaintenanceTask> daysTasks = new ArrayList<>();
        ArrayList<MaintenanceTask> filtered = new ArrayList<>();

        try {

            JSONObject obj = new JSONObject(fetchAPI(url));
            JSONArray jsonArr = obj.getJSONArray("features");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            sdf.setTimeZone(TimeZone.getTimeZone("Europe/Helsinki"));

            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject feature = jsonArr.getJSONObject(i);
                JSONObject task = feature.getJSONObject("properties");

                Gson gson = new Gson();
                MaintenanceTask newTask = gson.fromJson(task.toString(), MaintenanceTask.class);

                boolean isContinued = false;

                Date newStart = sdf.parse(newTask.getStartTime());
                Date newEnd = sdf.parse(newTask.getEndTime());

                for (int j = 0; j < daysTasks.size(); j++) {

                    // Check if task type matches
                    if (daysTasks.get(j).getTasks().get(0).equals(newTask.getTasks().get(0))) {

                        Date prevStart = sdf.parse(daysTasks.get(j).getStartTime());
                        Date prevEnd = sdf.parse(daysTasks.get(j).getEndTime());

                        if ((newStart.after(prevStart) && newStart.before(prevEnd)) || newStart.equals(prevEnd)) {

                            if (prevEnd.before(newEnd)) {
                                daysTasks.get(j).setEndTime(newTask.getEndTime());
                            }
                            isContinued = true;
                            break;
                        }
                    }
                }

                if (!isContinued) {
                    daysTasks.add(newTask);
                } else {
                    newTask = null;
                }
            }

            daysTasks.forEach(task -> {
                try {

                    task.formatDates();

                    // change the tasks to be nameEn instead of task id
                    ArrayList<String> nameEnlist = new ArrayList<>();

                    task.getTasks().forEach(taskName -> {
                        nameEnlist.add(allTaskTypes.get(taskName)[1]);
                    });

                    task.setTasks(nameEnlist);

                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            });

            daysTasks.forEach(task -> {

                long minutesDifference = ((task.getEndDate().getTime() - task.getStartDate().getTime()) / (1000 * 60)) % 60;

                if (minutesDifference >= 1) {
                    filtered.add(task);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return filtered;

    }

    public void fetchConditions() {
        String url = String.format("%s%s/%s/%s/%s", CONDITIONS_URL_BEGIN, this.minY, this.minX, this.maxY, this.maxX);
        System.out.println(url);

        try {

            JSONObject obj = new JSONObject(fetchAPI(url));
            JSONArray jsonArr = obj.getJSONArray("weatherData").getJSONObject(0).getJSONArray("roadConditions");

            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject data = jsonArr.getJSONObject(i);
                Gson gson = new Gson();
                RoadCondition newCondition = gson.fromJson(data.toString(), RoadCondition.class);
                allConditions.add(newCondition);
            }

        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public void fetchTrafficMessages() {

        try {

            JSONObject obj = new JSONObject(fetchAPI(MESSAGES_URL_BEGIN));
            JSONArray jsonArr = obj.getJSONArray("features");

            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject featureProperties = jsonArr.getJSONObject(i).getJSONObject("properties");
                JSONObject announcement = featureProperties.getJSONArray("announcements").getJSONObject(0);
                JSONArray messageFeatures = announcement.getJSONArray("features");
                ArrayList<String> newMessageFeatures = new ArrayList<>();

                for (int j = 0; j < messageFeatures.length(); j++) {
                    newMessageFeatures.add(messageFeatures.getJSONObject(j).getString("name"));
                }

                String situationType = featureProperties.getString("situationType");
                String title = announcement.getString("title");
                String description = announcement.getJSONObject("location").getString("description");
                String startTime = null;
                String endTime = null;

                if (announcement.getJSONObject("timeAndDuration").has("startTime")) {
                    startTime = announcement.getJSONObject("timeAndDuration").getString("startTime");
                }

                if (announcement.getJSONObject("timeAndDuration").has("endTime")) {
                    endTime = announcement.getJSONObject("timeAndDuration").getString("endTime");
                }

                TrafficMessage newMessage = new TrafficMessage(situationType, title, description, newMessageFeatures, startTime, endTime);
                allMessages.add(newMessage);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void calculateAveragesWeatherData(){
        /*
        Calculates averages once per model
         */

        calculator.calculate(this.observationsByDateWholeMonth, this.forecastsByDate);
    }

    public void parseAndClassifyWeatherData(Document document, ParserClassifier parserClassifier, int round){
        /*
        Parses fetched weather data and classifies it.
        Document is the fetched XML file.
         */
        try {
            if (parserClassifier == ParserClassifier.OBSERVATION) {
                if (round == 0){
                    this.parserObs = new WeatherDataParser(settings);
                    allObservations.addAll(parserObs.parseObservations(document));
                    observationsByDate.putAll(parserObs.classifyObservationsByDate());
                    observationsByDateWholeMonth.putAll(observationsByDate);

                } else {
                    this.parserObs = new WeatherDataParser(settings);
                    parserObs.parseObservations(document);

                    TreeMap<LocalDate, ArrayList<WeatherObservation>> ObsMap
                            = parserObs.classifyObservationsByDate();

                    if(ObsMap != null){
                        this.observationsByDateWholeMonth.putAll(parserObs.classifyObservationsByDate());
                    }


                }

                System.out.println("Observations parsed and classified, phase "+ round);
            }
            if (parserClassifier == ParserClassifier.FORECAST){
                this.parserForec = new WeatherDataParser(settings);
                this.allForecasts.addAll(parserForec.parseForecast(document));
                this.forecastsByDate.putAll(parserForec.classifyForecastsByDate());
                System.out.println("Forecasts parsed and classified, phase "+ round+"size:"+allForecasts.size());
            }
        } catch (NullPointerException e) {
            //If FMI does not give data properly!
            //e.printStackTrace();

        }

    }

    public void printDataFMI() {
        /*
        Prints the basic weather information for debugging.
         */

        /*
        if (!allObservations.isEmpty()) {
            for (WeatherObservation obs : allObservations) {
                System.out.print(obs.getTemperature() + " ");
            }
            System.out.println();
            for (WeatherObservation obs : allObservations) {
                System.out.print(obs.getWindSpeed() + " ");
            }
            System.out.println();
            for (WeatherObservation obs : allObservations) {
                System.out.print(obs.getCloudiness() + " ");
            }
        }

        if (!allForecasts.isEmpty()) {
            for (WeatherForecast obs : allForecasts) {
                System.out.print(obs.getTemperature() + " ");
            }
            System.out.println();
            for (WeatherForecast obs : allForecasts) {
                System.out.print(obs.getWindSpeed() + " ");
            }
            System.out.println();
            for (WeatherForecast obs : allForecasts) {
                System.out.print(obs.getCloudiness() + " ");
            }
        }*/

        /*
        System.out.println();
        if(this.observationsByDateWholeMonth.size() != 0) {

            for (Map.Entry<LocalDate, ArrayList<WeatherObservation>> entry : this.observationsByDateWholeMonth.entrySet()) {

                System.out.println(entry.getKey() + " and there are " + entry.getValue().size() + " observations.");

                for(WeatherObservation o : entry.getValue()){
                    System.out.print(o.getTime() + " " + o.getTemperature()+"C, ");
                }
                System.out.println();

            }
        }
        if(this.forecastsByDate.size() != 0) {

            for (Map.Entry<LocalDate, ArrayList<WeatherForecast>> entry : this.forecastsByDate.entrySet()) {

                System.out.println(entry.getKey() + " and there are " + entry.getValue().size() + " forecasts.");

                for(WeatherForecast o : entry.getValue()){
                    System.out.print(o.getTime() + " " + o.getTemperature()+"C, ");
                }
                System.out.println();

            }
        }
        System.out.println();*/


    }

    public void getXML_FMI(ArrayList<String> url, int round){

        // Fetch data
        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            if (settings.isPartial()) {

                // Get XML file 1: observation
                Document doc = dBuilder.parse(url.get(0));
                doc.getDocumentElement().normalize();
                parseAndClassifyWeatherData(doc, ParserClassifier.OBSERVATION, round);

                // Get XML file 2: forecast
                Document doc2 = dBuilder.parse(url.get(1));
                doc2.getDocumentElement().normalize();
                parseAndClassifyWeatherData(doc2, ParserClassifier.FORECAST, round);

                settings.setPartial(false);
                round = -1;

            }
            if (settings.isForecast()) {

                // Get XML file: forecast
                Document doc = dBuilder.parse(url.get(1));
                doc.getDocumentElement().normalize();
                parseAndClassifyWeatherData(doc, ParserClassifier.FORECAST, round);

                settings.setForecast(false);


            } else if (!settings.isForecast() && !settings.isPartial() && round >= 0) {

                // Get XML file: observation
                Document doc = dBuilder.parse(url.get(0));
                doc.getDocumentElement().normalize();
                parseAndClassifyWeatherData(doc, ParserClassifier.OBSERVATION, round);

            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        //printDataFMI();

    }

    public ArrayList<String> getURL_FMI(int round){

        String urlObs = String.format("https://opendata.fmi.fi/wfs?request=getFeature&version=2.0.0&storedquery_id=fmi::"
                        + "observations::weather::simple"
                        + "&bbox=%s,%s,%s,%s"
                        + "&timestep=%s"
                        + "&parameters=%s",
                this.minY, this.minX, this.maxY, this.maxX, settings.getInterval(), settings.getObservationParamString(round));

        String latitude = String.valueOf((Double.parseDouble(this.maxY) + Double.parseDouble(this.minY)) / 2);
        String longitude = String.valueOf((Double.parseDouble(this.maxX) + Double.parseDouble(this.minX)) / 2);;
        String urlForec = String.format("https://opendata.fmi.fi/wfs?request=getFeature&version=2.0.0&storedquery_id=fmi::"
                        + "forecast::harmonie::surface::point::simple"
                        + "&latlon=%s,%s"
                        + "&timestep=%s"
                        + "&parameters=%s",
                longitude, latitude, settings.getInterval(), settings.getForecastParamString());

        ArrayList<String> url = new ArrayList<>();
        url.add(urlObs);
        url.add(urlForec);

        System.out.println("Fetching FMI data from following addresses:");
        if(round==0){
            System.out.println(urlObs);
            System.out.println(urlForec);
        } else {
            System.out.println(urlObs);
        }


        return url;

    }

    public void fetchDataFMI() {

        allForecasts.clear();
        allObservations.clear();
        forecastsByDate.clear();
        observationsByDate.clear();
        observationsByDateWholeMonth.clear();

        LocalDate startMonthDate = settings.getStartMonthDate();
        LocalDate startMonthDateUser = settings.getStartDateWeather();
        LocalDate endMonthDate = settings.getEndMonthDate();
        LocalDate endMonthDateUser = settings.getEndDateWeather();

        boolean isForecast = settings.isForecast(); //only forecast if true
        boolean isPartial = settings.isPartial(); //both forecast and observation if true

        int round = 0; //To get the correct parameter string from WeatherSettings

        try{
            /*
        The weather API can not fetch over several days worth of data. Fetching has to be done in several steps.
         */
            //FIRST ROUND: GET THE INFO USER REQUESTED
            ArrayList<String> url;
            url = getURL_FMI(round);
            getXML_FMI(url, 0);

            //THE OTHER ROUNDS: GET INFO FOR MONTHLY AVERAGE CALCULATIONS
            //No need to do this if forecast.
            if(!isForecast){
                //First, look for the half that is before the user requested timeline
                while(startMonthDate.isBefore(startMonthDateUser)){
                    if (startMonthDate.plusDays(5).isBefore(startMonthDateUser)){

                        url = getURL_FMI(1);
                        getXML_FMI(url, 1);
                        startMonthDate = startMonthDate.plusDays(5);
                    }
                    else if (startMonthDate.plusDays(1).isBefore(startMonthDateUser)){

                        url = getURL_FMI(2);
                        getXML_FMI(url, 2);
                        startMonthDate = startMonthDate.plusDays(1);
                    }
                    else if (startMonthDate.plusDays(1).equals(startMonthDateUser)){
                        url = getURL_FMI(2);
                        getXML_FMI(url, 2);
                        break;
                    }
                }
                //Then, look for the half that is after the user requested timeline (if these are still observations)
                while (endMonthDate.isAfter(endMonthDateUser) && !isPartial){
                    if (endMonthDateUser.plusDays(5).isBefore(endMonthDate)){
                        //System.out.println("Beginning round 3");
                        url = getURL_FMI(3);
                        getXML_FMI(url, 3);
                        endMonthDateUser = endMonthDateUser.plusDays(5);
                    }
                    else if (endMonthDateUser.plusDays(1).isBefore(endMonthDate)){
                        //System.out.println("Beginning round 4");
                        url = getURL_FMI(4);
                        getXML_FMI(url, 4);
                        endMonthDateUser = endMonthDateUser.plusDays(1);
                    }
                    else if (endMonthDateUser.plusDays(1).equals(endMonthDate)){
                        url = getURL_FMI(4);
                        getXML_FMI(url, 4);
                        break;
                    }
                }
            }

            //If not within user search limits, search for data for this date.
            if ((!isForecast && !isPartial) || (isForecast && !isPartial)){
                settings.setPartial(true);
                settings.setAllTimes(LocalDate.now() + "T"
                                + LocalTime.now().truncatedTo(ChronoUnit.MINUTES).toString() + ":00Z"
                        ,LocalDate.now().plusDays(1) + "T06:00:00Z",
                        LocalDate.now() + "T00:00:00Z");
                url = getURL_FMI(0);
                getXML_FMI(url, 0);
            }


            calculateAveragesWeatherData();
            System.out.println("Data fetched FMI. Averages calculated.");

            //Example how to get calculation results:
            //System.out.println("Max temperature: "+ calculator.getObservationAveragesOfMonth(Month.NOVEMBER).getMaxTemperature());
            //System.out.println("Min temperature: "+ calculator.getObservationAveragesOfMonth(Month.NOVEMBER).getMinTemperature());
            //System.out.println("Max daily average temperature: "+ calculator.getObservationAveragesOfMonth(Month.NOVEMBER).getMaxAverTemperature());
            //System.out.println("Min daily average temperature: "+ calculator.getObservationAveragesOfMonth(Month.NOVEMBER).getMinAverTemperature());
            //System.out.println("Average temperature of month: "+ calculator.getObservationAveragesOfMonth(Month.NOVEMBER).getAverTemperature());
            System.out.println("Max temperature today: "+calculator.getObservationAveragesOfDate(LocalDate.now()).getMaxTemperature());
            System.out.println("Min temperature today: "+calculator.getObservationAveragesOfDate(LocalDate.now()).getMinTemperature());
            System.out.println("Average temperature today: "+calculator.getObservationAveragesOfDate(LocalDate.now()).getAverTemperature());
            //System.out.println("Max temperature today (forecast): "+calculator.getForecastAveragesOfDate(LocalDate.now()).getMaxTemperature());
            //System.out.println("Min temperature today (forecast): "+calculator.getForecastAveragesOfDate(LocalDate.now()).getMinTemperature());
            //System.out.println("Average temperature today (forecast): "+calculator.getForecastAveragesOfDate(LocalDate.now()).getAverTemperature());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public String getMinY() {
        return minY;
    }

    public String getMinX() {
        return minX;
    }

    public String getMaxY() {
        return maxY;
    }

    public String getMaxX() {
        return maxX;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public TreeMap<LocalDate, ArrayList<MaintenanceTask>> getTasksByDate() {
        return tasksByDate;
    }

    public ArrayList<RoadCondition> getAllConditions() {
        return allConditions;
    }

    public ArrayList<TrafficMessage> getAllMessages() {
        return allMessages;
    }
    
    public TreeMap<LocalDate, ArrayList<WeatherObservation>> getAllObservationsByDate() {
         
        return observationsByDate;
    }
    
    public TreeMap<LocalDate, ArrayList<WeatherForecast>> getAllForecastsByDate() {
         
        return forecastsByDate;
    }

    private void fetchTaskTypes() {
        try {

            JSONArray JSONarr = new JSONArray(fetchAPI(TASK_TYPES_URL_BEGIN));

            for (int i = 0; i < JSONarr.length(); ++i) {

                JSONObject obj = JSONarr.getJSONObject(i);
                String[] typenames = {obj.getString("nameFi"),
                    obj.getString("nameEn"),
                    obj.getString("nameSv")};

                allTaskTypes.put(obj.getString("id"), typenames);

            }

        } catch (Exception e) {
            System.out.println("something went wrong when fetching task types");
            //e.printStackTrace();
        }
    }

    // method for turning all recent search results to json format
    private Pair<String, JSONObject> makeRecentAllDataJSON(){
        
        // class that handles turning the search result container into json
        JSONManager jsonManager = new JSONManager();

        // final object for all of the search results
        JSONObject searchData = new JSONObject();

        // add maintenance tasks array to the search result
        searchData.put("maintenancetasks",
                jsonManager.maintenanceTasksIntoJSON(this.tasksByDate));

        searchData.put("roadconditions",
                jsonManager.roadConditionsIntoJSON(this.allConditions));

        searchData.put("trafficmessages",
                jsonManager.trafficMessagesIntoJSON(this.allMessages));

        searchData.put("weatherobservations",
                jsonManager.weatherObservationsIntoJSON(this.observationsByDate));

        searchData.put("weatherforecasts",
                jsonManager.weatherForecastIntoJSON(this.forecastsByDate));
        

        
        // Labeling the dataset with type and timeline
        String label = "RoadAndWeather " 
                            + tasksByDate.firstKey().toString() + " to " 
                            + tasksByDate.lastKey().toString();
        
        Pair<String, JSONObject> pair = new Pair<>(label, searchData);

        return pair;
    }
    
    // method for turning recent road data to json format
    private Pair<String, JSONObject> makeRecentRoadDataJSON(){
        
        // class that handles turning the search result container into json
        JSONManager jsonManager = new JSONManager();
        
        // final object for all of the search results
        JSONObject searchData = new JSONObject();
        
        // add maintenance tasks array to the search result
        searchData.put("maintenancetasks", 
                jsonManager.maintenanceTasksIntoJSON(this.tasksByDate));
        
        searchData.put("roadconditions", 
                jsonManager.roadConditionsIntoJSON(this.allConditions));
        
        searchData.put("trafficmessages", 
                jsonManager.trafficMessagesIntoJSON(this.allMessages));
        
        
        // Labeling the dataset with type and timeline
        String label = "Road " + tasksByDate.firstKey().toString() + " to " 
                               + tasksByDate.lastKey().toString();
       
        Pair<String, JSONObject> pair = new Pair<>(label, searchData);
        
        return pair;
    }
    
    // method for turning recent weather data to json format
    private Pair<String, JSONObject> makeRecentWeatherDataJSON(){
        
        // class that handles turning the search result container into json
        JSONManager jsonManager = new JSONManager();
        
        // final object for all of the search results
        JSONObject searchData = new JSONObject();
        
        searchData.put("weatherobservations", 
                jsonManager.weatherObservationsIntoJSON(this.observationsByDate));
        
        searchData.put("weatherforecasts", 
                jsonManager.weatherForecastIntoJSON(this.forecastsByDate));
        
        // Labeling the dataset with type and timeline
        String label = "Weather " 
                        + observationsByDate.firstKey().toString() + " to " 
                        + observationsByDate.lastKey().toString();
        
        Pair<String, JSONObject> pair = new Pair<>(label, searchData);

        return pair;
    }
    
    public Pair<String, JSONObject> getRecentAllDataAsJSON(){
        
        return this.makeRecentAllDataJSON();
    }
    
    public Pair<String, JSONObject> getRecentRoadDataAsJSON(){
        
        return this.makeRecentRoadDataJSON();
    }
    
    public Pair<String, JSONObject> getRecentWeatherDataAsJSON(){
        
        return this.makeRecentWeatherDataJSON();
    }

}

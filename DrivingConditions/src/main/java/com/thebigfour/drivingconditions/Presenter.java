package com.thebigfour.drivingconditions;

import java.io.FileReader;
import java.io.FileWriter;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Presenter {

    private Model model;
    private double coords[];
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private LocalTime timeFrom;
    private LocalTime timeTo;
    private ObservableList<String> presets = FXCollections.observableArrayList();
    private ObservableList<String> datasets = FXCollections.observableArrayList();

    public Presenter() {

        dateFrom = LocalDate.now();
        dateTo = LocalDate.now();

        timeFrom = LocalTime.now();
        timeTo = LocalTime.now();
        readPresets();
        initializeDatasetNamesList();

    }

    // Read existing presets from presets.json
    private void readPresets() {
        
        JSONParser parser = new JSONParser();

        try {
            
            Object obj;
            
            try(FileReader fileR = new FileReader("src/main/resources/json/presets.json")){
                obj = parser.parse(fileR);
            }
            
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray presetList = (JSONArray) jsonObject.get("presets");

            for (int i = 0; i < presetList.size(); i++) {
                JSONObject preset = (JSONObject) presetList.get(i);
                this.presets.add(preset.keySet().iterator().next().toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Get preset by name, returns preset as a SearchParamaters
    public SearchParameters getPreset(String presetName) {

        System.out.println("Getting preset");
        String filepath = "src/main/resources/json/presets.json";
        SearchParameters preset = new SearchParameters(getJSON(presetName, filepath, "presets"));

        return preset;

    }

    // Handle writing preset to presets.json
    public void savePreset(SearchParameters params) {

        JSONObject values = params.toJSON();

        JSONParser parser = new JSONParser();
        try {
            
            Object obj;
            
            try(FileReader FileR = new FileReader("src/main/resources/json/presets.json")){
                obj = parser.parse(FileR);
            }

            JSONObject jsonObject = (JSONObject) obj;

            JSONArray presetList = (JSONArray) jsonObject.get("presets");
            String presetName = String.format("Preset%d", presetList.size() + 1);
            
            String originalPresetName = presetName;
            String testPresetName = presetName;
            Integer endNumber = 1;
            
            // check if the name is available
            while (!checkNameAvailability(testPresetName)) {
                
                // add number to the end if not available and try again
                testPresetName = originalPresetName.concat("_") + endNumber;
                ++endNumber;
            }
            
            // use the earliest available name
            presetName = testPresetName;

            JSONObject preset = new JSONObject();
            preset.put(presetName, values);
            presetList.add(preset);
            
            try(FileWriter fileWr = new FileWriter("src/main/resources/json/presets.json")){
                fileWr.write(jsonObject.toJSONString());
                fileWr.flush();
            }

            this.presets.add(presetName);
            System.out.println("Saved preset: " + presetName);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean checkNameAvailability(String name) {

        return !this.presets.contains(name);
    }

    private JSONObject getJSON(String name, String filePath, String dataName) {

        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(filePath));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray List = (JSONArray) jsonObject.get(dataName);

            for (int i = 0; i < List.size(); i++) {
                JSONObject json = (JSONObject) List.get(i);
                if (json.keySet().iterator().next().toString().equals(name)) {

                    JSONObject presetJson = (JSONObject) json.get(name);

                    return presetJson;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void deletePreset(String presetName) {
        JSONParser parser = new JSONParser();
        JSONObject toDeleteObj = getJSON(presetName, "src/main/resources/json/presets.json", "presets");

        try {
            Object obj = parser.parse(new FileReader("src/main/resources/json/presets.json"));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray presetList = (JSONArray) jsonObject.get("presets");

            int deleteIndex = -1;

            for (int i = 0; i < presetList.size(); i++) {
                JSONObject preset = (JSONObject) presetList.get(i);
                if (preset.keySet().iterator().next().toString().equals(presetName)) {
                    deleteIndex = i;
                }
            }

            if (deleteIndex != -1) {
                presetList.remove(deleteIndex);
                this.presets.remove(presetName);
            } else {
                System.out.println("Didn't find matching name!");
            }

            FileWriter fileWr = new FileWriter("src/main/resources/json/presets.json");

            fileWr.write(jsonObject.toJSONString());
            fileWr.flush();
            fileWr.close();

            System.out.println("Deleted preset: " + presetName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initModel() {

        System.out.println("Initing model!");
        // TIMES AND DATES FOR APIs
        System.out.println("Current times are: " + dateFrom.toString() + " " + timeFrom.toString() + " and " + dateTo.toString() + " " + timeTo.toString());

        // ERROR CHECKS:
        // IF dateFrom is after dateTo, give an error! Delete if not needed
        if (dateFrom.isAfter(dateTo)) {
            return;
        }

        // For Traffic API
        Instant startTime = dateFrom.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endTime = dateTo.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        // For weather API
        String currentTimeWeather = LocalDate.now() + "T"
                + LocalTime.now().truncatedTo(ChronoUnit.MINUTES).toString() + ":00Z";
        /*String startTimeWeather = dateFrom.toString() + "T"
                + timeFrom.truncatedTo(ChronoUnit.MINUTES).toString() + ":00Z";
        String endTimeWeather = dateTo.toString() + "T"
                + timeTo.truncatedTo(ChronoUnit.MINUTES).toString() + ":00Z";*/
        String startTimeWeather = dateFrom.toString() + "T"
                + "00:00:00Z";
        String endTimeWeather = dateTo.toString() + "T"
                + "23:59:59Z";

        // WEATHER SETTINGS INITIALIZATION:
        WeatherSettings settings = new WeatherSettings();
        // Set parametres:
        // Observation params
        settings.setN_man(true);
        settings.setT2m(true);
        settings.setWs_10min(true);
        // Forecast params
        settings.setTemperature(true);
        settings.setWindSpeed(true);
        //settings.setWindDirection(true);
        settings.setCloudiness(true);

        // Set date and time:
        settings.setAllDates(LocalDate.now(), dateFrom, dateTo);
        settings.setAllTimes(currentTimeWeather, endTimeWeather, startTimeWeather);

        //Check if date now is before starting date. If yes then a forecast is requested.
        boolean isOnlyForecast = LocalDate.now().isBefore(dateFrom);
        //If the former is not true, it might still be possible that the end date is after date now (and a partial
        //forecast is requested.
        boolean isPartial = LocalDate.now().isBefore(dateTo);

        // Set these findings into settings
        if (isOnlyForecast) {
            settings.setForecast(true);
        } else if (isPartial) {
            settings.setPartial(true);
        }

        // CREATE A NEW MODEL
        this.model = new Model(Double.toString(this.coords[0]), Double.toString(this.coords[1]),
                Double.toString(this.coords[2]), Double.toString(this.coords[3]),
                startTime, endTime, settings);

    }

    public void runModelFetches(View.ViewState viewState) {

        // Fetch data based on viewState
        if (viewState != null) {
            switch (viewState) {
                case ROAD:
                    this.model.fetchRoadData();
                    break;
                case WEATHER:
                    this.model.fetchDataFMI();
                    break;
                case ROAD_AND_WEATHER:
                    this.model.fetchDataFMI();
                    this.model.fetchRoadData();
                    break;
                default:
                    break;
            }
        }

    }

    public void presentCurrentConditions(boolean isSavedDataset, String datasetName,
            Label tempLabel, Label roadTempLabel, Label windSpeedLabel, Label windDirectionLabel, Label OverallConditionLabel) {
        
        // data comes from saved dataset
        if(isSavedDataset){
            
            RoadCondition currentCondition = 
                    this.getRoadConditionDataset(datasetName).get(0);
            
            String windSpeed = Double.toString(currentCondition.getWindSpeed());
            int windDirection = currentCondition.getWindDirection();
            String windDirectionString = checkWindDirection(windDirection);

            tempLabel.setText(currentCondition.getTemperature() + " °C");
            roadTempLabel.setText(currentCondition.getRoadTemperature() + " °C");
            windSpeedLabel.setText("Wind Speed: " + windSpeed);
            windDirectionLabel.setText("Wind Direction: " + windDirectionString);
            
            if(currentCondition.getOverallRoadCondition().equals("NORMAL_CONDITION")){
                OverallConditionLabel.setText("Overall condition: " + "Normal");
            }else if(currentCondition.getOverallRoadCondition().equals("POOR_CONDITION")){
                OverallConditionLabel.setText("Overall condition: " + "Poor");
            }else{
                OverallConditionLabel.setText("Overall condition: " + "Unknown");
            }
            
            
        }else{
            if (!this.model.getAllConditions().isEmpty()) {
                RoadCondition currentCondition = this.model.getAllConditions().get(0);
                String windSpeed = Double.toString(currentCondition.getWindSpeed());
                int windDirection = currentCondition.getWindDirection();
                String windDirectionString = checkWindDirection(windDirection);

                tempLabel.setText(currentCondition.getTemperature() + " °C");
                roadTempLabel.setText(currentCondition.getRoadTemperature() + " °C");
                windSpeedLabel.setText("Wind Speed: " + windSpeed);
                windDirectionLabel.setText("Wind Direction: " + windDirectionString);
                
                if(currentCondition.getOverallRoadCondition().equals("NORMAL_CONDITION")){
                    OverallConditionLabel.setText("Overall condition: " + "Normal");
                }else if(currentCondition.getOverallRoadCondition().equals("POOR_CONDITION")){
                    OverallConditionLabel.setText("Overall condition: " + "Poor");
                }else{
                    OverallConditionLabel.setText("Overall condition: " + "Unknown");
                }
            } else {
                tempLabel.setText("?");
                roadTempLabel.setText("?");
                windSpeedLabel.setText("Wind Speed: ?");
                windDirectionLabel.setText("Wind Direction: ?");
                OverallConditionLabel.setText("No data found!");

            }
        }

        

    }

    public void presentConditionsForecast(boolean isSavedDataset, String datasetName, Label tempLabel2h, Label roadTempLabel2h, Label tempLabel4h, Label roadTempLabel4h,
            Label tempLabel6h, Label roadTempLabel6h, Label tempLabel12h, Label roadTempLabel12h) {
        
        ArrayList<RoadCondition> allConditions;
        
        if(isSavedDataset){
            allConditions = this.getRoadConditionDataset(datasetName);
        }else{
            allConditions = this.model.getAllConditions();
        }
        
        if (!allConditions.isEmpty()) {
            tempLabel2h.setText(allConditions.get(1).getTemperature() + " °C");
            roadTempLabel2h.setText(allConditions.get(1).getRoadTemperature() + " °C");
            tempLabel4h.setText(allConditions.get(2).getTemperature() + " °C");
            roadTempLabel4h.setText(allConditions.get(2).getRoadTemperature() + " °C");
            tempLabel6h.setText(allConditions.get(3).getTemperature() + " °C");
            roadTempLabel6h.setText(allConditions.get(3).getRoadTemperature() + " °C");
            tempLabel12h.setText(allConditions.get(4).getTemperature() + " °C");
            roadTempLabel12h.setText(allConditions.get(4).getRoadTemperature() + " °C");
        }
        
    }


    public ArrayList<RoadCondition> allPresentConditionsForecast(boolean isSavedDataset, String datasetName) {
        
        if(isSavedDataset){
            return this.getRoadConditionDataset(datasetName);
        }else{
            return this.model.getAllConditions();
        }

    }

    public ArrayList<TrafficMessage> getAllTrafficMessages(boolean isSavedDataset, String datasetName) {
        
        if(isSavedDataset){
            return this.getTrafficMessageDataset(datasetName);
        }else{
            return this.model.getAllMessages();
        }
        
    }

    public TreeMap<LocalDate, ArrayList<MaintenanceTask>> getAllTasks(boolean isSavedDataset, String datasetName) {
        
        if(isSavedDataset){
            return this.getMaintenanceDataset(datasetName);
        }else{
            return this.model.getTasksByDate();
        }
        
    }

    public ObservableList<String> getPresets() {
        return presets;
    }
    
    public TreeMap<LocalDate, ArrayList<WeatherObservation>> getObservationsByDate(boolean isSavedDataset, String datasetName) {
        
        if(isSavedDataset){
            return this.getWeatherObservationDataset(datasetName);
        }else{
            return this.model.getAllObservationsByDate();
        }
         
        
    }
    
    public TreeMap<LocalDate, ArrayList<WeatherForecast>> getForecastsByDate(boolean isSavedDataset, String datasetName) {
        
        if(isSavedDataset){
            
            return this.getWeatherForecastDataset(datasetName);
            
        }else{
            return this.model.getAllForecastsByDate();
        }

    }

    @Override
    public String toString() {
        return "Presenter{"
                + "model=" + model
                + ", coords=" + coords[0]
                + " " + coords[1]
                + " " + coords[2]
                + " " + coords[3]
                + ", " + "dateFrom=" + dateFrom
                + ", dateTo=" + dateTo
                + ", timeFrom=" + timeFrom
                + ", timeTo=" + timeTo + '}';
    }

    private String checkWindDirection(int windDirection) {

        String windDirectionString;

        if (22 <= windDirection && windDirection < 66) {
            windDirectionString = "NE";
        } else if (66 <= windDirection && windDirection < 112) {
            windDirectionString = "E";
        } else if (112 <= windDirection && windDirection < 157) {
            windDirectionString = "SE";
        } else if (157 <= windDirection && windDirection < 202) {
            windDirectionString = "S";
        } else if (202 <= windDirection && windDirection < 247) {
            windDirectionString = "SW";
        } else if (247 <= windDirection && windDirection < 292) {
            windDirectionString = "W";
        } else if (292 <= windDirection && windDirection < 337) {
            windDirectionString = "NW";
        } else {
            windDirectionString = "N";
        }

        return windDirectionString;
    }

    public void setAll(double[] coords, LocalDate dateFrom, LocalDate dateTo, String timeFrom, String timeTo) {

        // Init values and model again, if search parameters changed
        if (!IsDuplicate(coords, dateFrom, dateTo)) {
            System.out.println("Initing presenter!");
            setCoords(coords);
            setDateFrom(dateFrom);
            setDateTo(dateTo);
            setTimeFrom(timeFrom);
            setTimeTo(timeTo);
            initModel();
        }
    }

    private boolean IsDuplicate(double[] coords, LocalDate dateFrom, LocalDate dateTo) {

        if (coords.equals(this.coords) && dateFrom.equals(this.dateFrom) && dateTo.equals(this.dateTo)) {
            return true;
        }
        return false;
    }

    public void setCoords(double[] coords) {
        this.coords = coords;
    }

    public void setDateFrom(LocalDate dateFrom) {
        if (dateFrom != null) {
            this.dateFrom = dateFrom;
        }
    }

    public void setDateTo(LocalDate dateTo) {
        if (dateTo != null) {
            this.dateTo = dateTo;
        }
    }

    public void setTimeFrom(String timeFrom) {

        try {
            this.timeFrom = LocalTime.parse(timeFrom);
            return;

        } catch (Exception e) {
            //e.printStackTrace();
            this.timeFrom = LocalTime.now().minus(Duration.ofHours(12));
        }

        this.timeFrom = LocalTime.now().minus(Duration.ofHours(12));
    }

    public void setTimeTo(String timeTo) {

        try {
            this.timeTo = LocalTime.parse(timeTo);
            return;
        } catch (Exception e) {
            this.timeTo = LocalTime.now().plus(Duration.ofHours(12));
            //e.printStackTrace();
        }

        this.timeTo = LocalTime.now().plus(Duration.ofHours(12));
    }
    

    
    public HashMap<String, Double> calculateAveragesPerType( 
            HashMap<String, Double> taskAmounts, Double amountOfDays){
        
        HashMap<String, Double> averageMap = new HashMap<>();
        
        taskAmounts.forEach((type, amount) -> {
            
            averageMap.put(type, (amount / amountOfDays));

        });
                
        return averageMap;
    }
    
        
    public HashMap<String, Double> calculateTaskAmountPerType(ArrayList<MaintenanceTask> daysTasks){
        
        HashMap amountMap = new HashMap<>();
        
        if(daysTasks.isEmpty()){
            return amountMap;
        }
        
        // go through the days tasks
        for (var task : daysTasks){
            
            // go through the tasks types (multiple types per task
            for (var type : task.getTasks()){
                
                // if its a new type
                if(amountMap.get(type) == null){
                    amountMap.put(type, 1.0);
                    continue;
                }
                
                // if the type is already in the map
                Double newAmount = (Double) amountMap.get(type) + 1.0;
                amountMap.put(type, newAmount);
            }
            
        }
        
        return amountMap;
    }
    
    public Series convertToSeries(ArrayList<? extends DataPoint> weatherData, String data){
        
        Series dataSeries = new Series();
        
        for (var weatherDataPoint : weatherData ){
            
            try{
                Double yValue = 0.0;
                
                if(data.equalsIgnoreCase("temperature")){
                    yValue = Double.valueOf(weatherDataPoint.getTemperature());
                }else if(data.equalsIgnoreCase("windspeed")){
                    yValue = Double.valueOf(weatherDataPoint.getWindSpeed());
                }else if(data.equalsIgnoreCase("cloudiness")){
                    yValue = Double.valueOf(weatherDataPoint.getCloudiness());
                }
                
                Integer xValue = weatherDataPoint.getTime().getHour();
                dataSeries.getData().add(new XYChart.Data(xValue, yValue));
                
            }catch(Exception e){
                System.out.println("Something went wrong while making a series");
                return dataSeries; // return empty series
            }
        }
        
        return dataSeries;
    }
    
    public Double getMaxOfSeries(Series series){
        
        Double max = -9999999.9;
        ObservableList<XYChart.Data> dataList = series.getData();
        
        for (XYChart.Data data : dataList){
            
            Double yValue = (Double) data.getYValue();
            
            if (yValue > max){
                max = yValue;
            }
            
        }
        return max;
    }
    
    public Double getMinOfSeries(Series series){
        
        Double min = 9999999.9;
        ObservableList<XYChart.Data> dataList = series.getData();
        
        for (XYChart.Data data : dataList){
            
            Double yValue = (Double) data.getYValue();
            
            if (yValue < min){
                min = yValue;
            }
            
        }
        return min;
    }
    
    
    // API method to view
    public boolean saveRecentDataset(View.ViewState viewState) {
        
        JSONParser parser = new JSONParser(); 
        String datasetString = "";
        String datasetLabel = "";
        Pair<String, org.json.JSONObject> pair;

        // fetch data based on viewState
        if (viewState != null) {
            switch (viewState) {
                case ROAD:
                    // get roaddata as json
                    pair = this.model.getRecentRoadDataAsJSON();
                    
                    datasetLabel = pair.getKey();
                    datasetString = pair.getValue().toString();
                    
                    break;
                case WEATHER:
                    // get weatherdata as json
                    pair = this.model.getRecentWeatherDataAsJSON();
                    
                    datasetLabel = pair.getKey();
                    datasetString = pair.getValue().toString();
                    
                    break;
                case ROAD_AND_WEATHER:
                    // get road and weatherdata as json
                    pair = this.model.getRecentAllDataAsJSON();
                    
                    datasetLabel = pair.getKey();
                    datasetString = pair.getValue().toString();
                    
                    break;
                default:
                    break;
                    
            }
        }

        try 
        {
            JSONObject recentDatasetJSON = 
                    (JSONObject) parser.parse(datasetString);
            
            // save the dataset to a file
            boolean result = saveDataset(datasetLabel, recentDatasetJSON);
            if(result){
                return true; // saving the dataset was successful
            }
            else{
                return false; // saving the dataset was not successful
            }
            
        }
        catch(Exception ex)
            
        {
            System.out.println(ex.getMessage());
            return false;
        }

    }
    // save dataset as json object to a file
    private boolean saveDataset(String datasetLabel, JSONObject newDataset){
        
        try {
            
            JSONParser parser = new JSONParser();
            
            // get the dataset jsonfiles contents
            Object obj;
            try(FileReader fileR = new FileReader("src/main/resources/json/datasets.json")){
                obj = parser.parse(fileR);
            }catch(Exception e){
                System.out.println(
                        "Something went wrong while saving the dataset : " +
                                                            datasetLabel);
                return false;
            }
            
            // cast to json object
            JSONObject jsonObject = (JSONObject) obj;

            // get the datasetlist out
            JSONArray savedDatasetsList = (JSONArray) jsonObject.get("datasets");
            
            // check if the name of the new dataset is available 
            // rename if not and add to the list
            
            Integer endNumber = 1;
            
            // cache the original label
            String originalLabelBody = datasetLabel; 
            
            // temporary variable for testing
            String testLabel = datasetLabel; 
            
            // check if the plain label is available
            while(!checkDatasetNameAvailability(testLabel)){
                
                
                // if not available, add a number to the end and try again
                testLabel = originalLabelBody.concat("_") + endNumber;
                ++endNumber;

            }
            
            // earliest available label will be used
            datasetLabel = testLabel;
            
            // save the label to the list of labels
            this.datasets.add(datasetLabel);
            
            
            // label the dataset
            JSONObject labelledNewDataset = new JSONObject();
            
            // add the label to the dataset
            labelledNewDataset.put(datasetLabel, newDataset);
            
            // save the labelled new dataset to the list of datasets from the file
            savedDatasetsList.add(labelledNewDataset);
            
            // write the modified jsonobject back into the file
            try(FileWriter fileWr = new FileWriter("src/main/resources/json/datasets.json")){
                fileWr.write(jsonObject.toJSONString());
                fileWr.flush();
            }catch(Exception e){
                System.out.println(
                        "Something went wrong while saving the dataset : " +
                                                            datasetLabel);
                return false;
            }
            
        } catch (Exception ex) {
            System.out.println("Something went wrong while saving the dataset"); 
            ex.printStackTrace();
            return false;
        }
        
        // saving the dataset was successful
        
        return true;
    }
    
    // checks if the dataset name is available
    // returns true is available, false if not
    private boolean checkDatasetNameAvailability(String datasetName){
        return !this.datasets.contains(datasetName);
    }
    
    private boolean initializeDatasetNamesList(){
        
        // get the dataset jsonfiles contents
        JSONParser parser = new JSONParser();

        Object obj;
        try(FileReader fileR = new FileReader("src/main/resources/json/datasets.json")){
            obj = parser.parse(fileR);
        }catch(Exception e){
            System.out.println(
                "Something went wrong while initializing the dataset names");
            return false;
        }

        // cast to json object
        JSONObject jsonObject = (JSONObject) obj;

        // get the datasetlist out
        JSONArray savedDatasetsList = (JSONArray) jsonObject.get("datasets");
        
        if(savedDatasetsList != null){
            for (var dataset : savedDatasetsList){
                
                JSONObject jsonobject = (JSONObject) dataset;
                
                // get the name of the dataset
                String name = jsonobject.keySet().iterator().next().toString();
                
                // add the name to the list (to present in combobox
                this.datasets.add(name);
            }
        }
        
        return true;
    }
    
    // get the names of the saved datasets
    public ObservableList<String> getDatasetsNamesList(){
        
        return this.datasets;
    }
    
    // deleteh save dataset from the json file
    public boolean deleteDataset(String datasetName){
        
        String filepath = "src/main/resources/json/datasets.json";
        
        JSONParser parser = new JSONParser();
        
        Object obj;
        try {
            try(FileReader fileR = new FileReader(filepath)){
                obj = parser.parse(fileR);
                
            }catch(Exception e){
                System.out.println(
                        "Something went wrong while reading the datasets");
                return false;
            }
            
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray datasetList = (JSONArray) jsonObject.get("datasets");

            int deleteIndex = -1;

            for (int i = 0; i < datasetList.size(); i++) {
                JSONObject dataset = (JSONObject) datasetList.get(i);
                if (dataset.keySet().iterator().next().toString().equals(datasetName)) {
                    deleteIndex = i;
                }
            }

            if (deleteIndex != -1) {
                datasetList.remove(deleteIndex);
                this.datasets.remove(datasetName);
                
                // update the file
                try(FileWriter fileWr = new FileWriter(filepath)){
                    fileWr.write(jsonObject.toJSONString());
                    fileWr.flush();
                }catch(Exception e){
                    System.out.println(
                        "Something went wrong while writing to  : " + filepath);
                    return false;
                }
                
            } else {
                System.out.println("Didn't find matching name!");
                return false;
            }
            
            System.out.println("Deleted preset: " + datasetName);
            

        } catch (Exception e) {
            System.out.println("Something went wrong while deleting dataset");
            return false;
        }
        
        return true;
    }

    
    public TreeMap<LocalDate, ArrayList<MaintenanceTask>> getMaintenanceDataset(String datasetName){

        TreeMap<LocalDate, ArrayList<MaintenanceTask>> dataset;
        JSONManager jsonManager = new JSONManager();
        
        JSONObject datasetJSON = this.getDataset(datasetName);
        
        // turn the maintence task data json dataset into java object
        dataset = jsonManager.makeMaintenanceTaskTree(datasetJSON.get("maintenancetasks").toString());
        
        return dataset;
    }
    
    public ArrayList<RoadCondition> getRoadConditionDataset(String datasetName){

        ArrayList<RoadCondition> dataset;
        
        JSONManager jsonManager = new JSONManager();
        
        JSONObject datasetJSON = this.getDataset(datasetName);
        
        // turn the roadconditions data json dataset into java object
        dataset = jsonManager.makeRoadConditionArray(datasetJSON.get("roadconditions").toString());
        
        return dataset;
    }
    
    public ArrayList<TrafficMessage> getTrafficMessageDataset(String datasetName){

        ArrayList<TrafficMessage> dataset;
        
        JSONManager jsonManager = new JSONManager();
        
        JSONObject datasetJSON = this.getDataset(datasetName);
        
        // turn the trafficmessages data json dataset into java object
        dataset = jsonManager.makeTrafficMessageArray(datasetJSON.get("trafficmessages").toString());
        
        return dataset;
    }
    
    public TreeMap<LocalDate, ArrayList<WeatherObservation>> getWeatherObservationDataset(String datasetName){

        TreeMap<LocalDate, ArrayList<WeatherObservation>> dataset;
        JSONManager jsonManager = new JSONManager();
        
        JSONObject datasetJSON = this.getDataset(datasetName);
        
        // turn the weatherobservations data json dataset into java object
        dataset = jsonManager.makeWeatherObservationTree(datasetJSON.get("weatherobservations").toString());
        
        return dataset;
    }
    
    public TreeMap<LocalDate, ArrayList<WeatherForecast>> getWeatherForecastDataset(String datasetName){

        TreeMap<LocalDate, ArrayList<WeatherForecast>> dataset;
        JSONManager jsonManager = new JSONManager();
        
        JSONObject datasetJSON = this.getDataset(datasetName);
        
        // turn the weatherforecasts data json dataset into java object
        dataset = jsonManager.makeWeatherForecastTree(datasetJSON.get("weatherforecasts").toString());
        
        return dataset;
    }
    
    
    private JSONObject getDataset(String datasetName){
        
        // get the dataset jsonfiles contents
        JSONParser parser = new JSONParser();

        Object obj = new JSONObject();
        try(FileReader fileR = new FileReader("src/main/resources/json/datasets.json")){
            obj = parser.parse(fileR);
        }catch(Exception e){
            System.out.println(
                "Something went wrong while initializing the dataset names");
        }

        // cast to json object
        JSONObject jsonObject = (JSONObject) obj;

        // get the datasetlist out
        JSONArray savedDatasetsList = (JSONArray) jsonObject.get("datasets");
        
        JSONObject datasetObject = new JSONObject();
        
        int searchIndex = -1;
        
        if(savedDatasetsList != null){
            // search for the correct index that matches the dataset name
            for (int i = 0; i < savedDatasetsList.size(); i++) {
                JSONObject dataset = (JSONObject) savedDatasetsList.get(i);
                if (dataset.keySet().iterator().next().toString().equals(datasetName)) {
                    searchIndex = i;
                }
            }
            
            if (searchIndex != -1) {
                datasetObject = (JSONObject) savedDatasetsList.get(searchIndex);
                
            } else {
                System.out.println("Didn't find matching name!");
            }
        }
        
        return (JSONObject) datasetObject.get(datasetName);
    }

    public ArrayList<WeatherDataCalculator> compareWeatherDataObservations(TreeMap<LocalDate, ArrayList<WeatherObservation>> observationsByDate1,
                                   TreeMap<LocalDate, ArrayList<WeatherObservation>> observationsByDate2){

        WeatherDataCalculator calculator1 = new WeatherDataCalculator();
        WeatherDataCalculator calculator2 = new WeatherDataCalculator();
        TreeMap<LocalDate, ArrayList<WeatherForecast>> forecastsByDate = new TreeMap<>();

        calculator1.calculate(observationsByDate1, forecastsByDate);
        calculator2.calculate(observationsByDate2, forecastsByDate);

        ArrayList<WeatherDataCalculator> returnThis = new ArrayList<>();
        returnThis.add(calculator1);
        returnThis.add(calculator2);

        return returnThis;

    }
    
    //GET AVERAGES FROM FMI DATA:


    //Get all dates and their WeatherDataAverages class. Can use the class functions to obtain various values to show.
    public TreeMap<LocalDate, WeatherDataCalculator.WeatherDataAverages> getDailyFMIObservationAverages(){
        return this.model.calculator.getDailyObservationAverages();
    }
    //Get a WeatherDataAverages class of a certain date. Can use the class functions to obtain various values.
    public WeatherDataCalculator.WeatherDataAverages getFMIObservationAveragesOfDateByRequest(String date){
        return this.model.calculator.getObservationAveragesOfDate(LocalDate.parse(date));
    }
    //Get all months and their WeatherDataAverages class
    public TreeMap<java.time.Month, WeatherDataCalculator.WeatherDataAverages> getMonthlyFMIObservationAverages(){
        return this.model.calculator.getMonthlyObservationAverages();
    }
    //Get a WeatherDataAverages class of a certain month (java.time.Month) like NOVEMBER or 11. Can use the class functions to obtain various values.
    public WeatherDataCalculator.WeatherDataAverages getFMIObservationAveragesOfMonth(Month month){
        return this.model.calculator.getObservationAveragesOfMonth(month);
    }
    //Get all dates and their WeatherDataAverages class. Can use the class functions to obtain various values to show.
    public TreeMap<LocalDate, WeatherDataCalculator.WeatherDataAverages> getDailyFMIForecastAverages(){
        return this.model.calculator.getDailyForecastAverages();
    }
    //Get a WeatherDataAverages class of a certain date. Can use the class functions to obtain various values.
    public WeatherDataCalculator.WeatherDataAverages getFMIForecastAveragesOfDate(String date){
        return this.model.calculator.getForecastAveragesOfDate(LocalDate.parse(date));
    }
    //Get all months and their WeatherDataAverages class
    public TreeMap<java.time.Month, WeatherDataCalculator.WeatherDataAverages> getMonthlyFMIForecastAverages(){
        return this.model.calculator.getMonthlyForecastAverages();
    }
    //Get a WeatherDataAverages class of a certain month (java.time.Month) like NOVEMBER or 11. Can use the class functions to obtain various values.
    public WeatherDataCalculator.WeatherDataAverages getFMIForecastAveragesOfMonth(Month month){
        return this.model.calculator.getForecastAveragesOfMonth(month);
    }

    public class Result{
        double temperature = 0.0;
        double windSpeed = 0.0;
        double cloudiness = 0.0;
    }

    public enum Request {TEMPERATURE, WIND, CLOUD};

    public double compareMonthMax(Request req, TreeMap<LocalDate, ArrayList<WeatherObservation>> observationsByDate1,
                                  TreeMap<LocalDate, ArrayList<WeatherObservation>> observationsByDate2, Month month, Month month2){
        ArrayList<WeatherDataCalculator> calculators = compareWeatherDataObservations(observationsByDate1, observationsByDate2);

        Result result = new Result();

        double first = calculators.get(0).getObservationAveragesOfMonth(month).getMaxTemperature();
        double second = calculators.get(1).getForecastAveragesOfMonth(month2).getMaxTemperature();
        result.temperature = Math.abs((first-second));

        first = calculators.get(0).getObservationAveragesOfMonth(month).getMaxCloudiness();
        second = calculators.get(1).getForecastAveragesOfMonth(month2).getMaxCloudiness();
        result.cloudiness = Math.abs((first-second));

        first = calculators.get(0).getObservationAveragesOfMonth(month).getMaxWindSpeed();
        second = calculators.get(1).getForecastAveragesOfMonth(month2).getMaxWindSpeed();
        result.windSpeed = Math.abs((first-second));

        if(req == Request.TEMPERATURE){
            return result.temperature;
        } else if (req == Request.WIND) {
            return result.windSpeed;
        } else if (req == Request.CLOUD ) {
            return result.cloudiness;
        }
        return 0.0;

    }
    public double compareMonthMin(Request req, TreeMap<LocalDate, ArrayList<WeatherObservation>> observationsByDate1,
                                  TreeMap<LocalDate, ArrayList<WeatherObservation>> observationsByDate2, Month month, Month month2){
        ArrayList<WeatherDataCalculator> calculators = compareWeatherDataObservations(observationsByDate1, observationsByDate2);

        Result result = new Result();

        double first = calculators.get(0).getObservationAveragesOfMonth(month).getMinTemperature();
        double second = calculators.get(1).getForecastAveragesOfMonth(month2).getMinTemperature();
        result.temperature = Math.abs((first-second));

        first = calculators.get(0).getObservationAveragesOfMonth(month).getMinCloudiness();
        second = calculators.get(1).getForecastAveragesOfMonth(month2).getMinCloudiness();
        result.cloudiness = Math.abs((first-second));

        first = calculators.get(0).getObservationAveragesOfMonth(month).getMinWindSpeed();
        second = calculators.get(1).getForecastAveragesOfMonth(month2).getMinWindSpeed();
        result.windSpeed = Math.abs((first-second));

        if(req == Request.TEMPERATURE){
            return result.temperature;
        } else if (req == Request.WIND) {
            return result.windSpeed;
        } else if (req == Request.CLOUD ) {
            return result.cloudiness;
        }
        return 0.0;


    }
    public double compareMonthAverage(Request req, TreeMap<LocalDate, ArrayList<WeatherObservation>> observationsByDate1,
                                      TreeMap<LocalDate, ArrayList<WeatherObservation>> observationsByDate2, Month month, Month month2){
        ArrayList<WeatherDataCalculator> calculators = compareWeatherDataObservations(observationsByDate1, observationsByDate2);

        Result result = new Result();

        double first = calculators.get(0).getObservationAveragesOfMonth(month).getAverTemperature();
        double second = calculators.get(1).getForecastAveragesOfMonth(month2).getAverTemperature();
        result.temperature = Math.abs((first-second));

        first = calculators.get(0).getObservationAveragesOfMonth(month).getAverCloudiness();
        second = calculators.get(1).getForecastAveragesOfMonth(month2).getAverCloudiness();
        result.cloudiness = Math.abs((first-second));

        first = calculators.get(0).getObservationAveragesOfMonth(month).getAverWindSpeed();
        second = calculators.get(1).getForecastAveragesOfMonth(month2).getAverWindSpeed();
        result.windSpeed = Math.abs((first-second));

        if(req == Request.TEMPERATURE){
            return result.temperature;
        } else if (req == Request.WIND) {
            return result.windSpeed;
        } else if (req == Request.CLOUD ) {
            return result.cloudiness;
        }
        return 0.0;

    }

    //Get today's combined max
    public double getTodaysCombinedMax(Request result){

        Result res = new Result();

        double obs = this.model.calculator.getObservationAveragesOfDate(LocalDate.now()).getMaxTemperature();
        double forec = this.model.calculator.getForecastAveragesOfDate(LocalDate.now()).getMaxTemperature();
        res.temperature = Math.max(obs, forec);

        obs = this.model.calculator.getObservationAveragesOfDate(LocalDate.now()).getMaxCloudiness();
        forec = this.model.calculator.getForecastAveragesOfDate(LocalDate.now()).getMaxCloudiness();
        res.cloudiness = Math.max(obs, forec);

        obs = this.model.calculator.getObservationAveragesOfDate(LocalDate.now()).getMaxWindSpeed();
        forec = this.model.calculator.getForecastAveragesOfDate(LocalDate.now()).getMaxWindSpeed();
        res.windSpeed = Math.max(obs, forec);

        if(result == Request.TEMPERATURE){
            return res.temperature;
        } else if (result == Request.WIND) {
            return res.windSpeed;
        } else if (result == Request.CLOUD ) {
            return res.cloudiness;
        }
        return 0.0;
    }

    //Get today's combined min
    public double getTodaysCombinedMin(Request result){

        Result res = new Result();

        double obs = this.model.calculator.getObservationAveragesOfDate(LocalDate.now()).getMinTemperature();
        double forec = this.model.calculator.getForecastAveragesOfDate(LocalDate.now()).getMinTemperature();
        res.temperature = Math.min(obs, forec);

        obs = this.model.calculator.getObservationAveragesOfDate(LocalDate.now()).getMinCloudiness();
        forec = this.model.calculator.getForecastAveragesOfDate(LocalDate.now()).getMinCloudiness();
        res.cloudiness = Math.min(obs, forec);

        obs = this.model.calculator.getObservationAveragesOfDate(LocalDate.now()).getMinWindSpeed();
        forec = this.model.calculator.getForecastAveragesOfDate(LocalDate.now()).getMinWindSpeed();
        res.windSpeed = Math.min(obs, forec);

        if(result == Request.TEMPERATURE){
            return res.temperature;
        } else if (result == Request.WIND){
            return res.windSpeed;
        } else if (result == Request.CLOUD ){
            return res.cloudiness;
        }
        return 0.0;
    }

    //Get today's combined average
    public double getTodaysCombinedAverage(Request result){

        Result res = new Result();

        double obs = this.model.calculator.getObservationAveragesOfDate(LocalDate.now()).getAverTemperature();
        double forec = this.model.calculator.getForecastAveragesOfDate(LocalDate.now()).getAverTemperature();
        res.temperature = (obs+forec)/2;

        obs = this.model.calculator.getObservationAveragesOfDate(LocalDate.now()).getAverCloudiness();
        forec = this.model.calculator.getForecastAveragesOfDate(LocalDate.now()).getAverCloudiness();
        res.cloudiness = (obs+forec)/2;

        obs = this.model.calculator.getObservationAveragesOfDate(LocalDate.now()).getAverWindSpeed();
        forec = this.model.calculator.getForecastAveragesOfDate(LocalDate.now()).getAverWindSpeed();
        res.windSpeed = (obs+forec)/2;

        if(result == Request.TEMPERATURE){
            return res.temperature;
        } else if (result == Request.WIND) {
            return res.windSpeed;
        } else if (result == Request.CLOUD ) {
            return res.cloudiness;
        }
        return 0.0;

    }


}

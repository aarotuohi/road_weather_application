package com.thebigfour.drivingconditions;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 *
 * @author miika.hamalainen@tuni.fi
 */

public class WeatherDataParser {

    private ArrayList<WeatherObservation> allObservations = new ArrayList<>(20);
    private ArrayList<WeatherForecast> allForecasts = new ArrayList<>(20);
    private TreeMap<LocalDate, ArrayList<WeatherObservation>> observationsByDate = new TreeMap<>();
    private TreeMap<LocalDate, ArrayList<WeatherForecast>> forecastsByDate = new TreeMap<>();

    private WeatherSettings settings;

    public WeatherDataParser(WeatherSettings settings){
        this.settings = settings;
    }


    public ArrayList<WeatherObservation> parseObservations(Document doc) {

        Node rootElement = doc.getDocumentElement();
        // Check if values match (I don't know if it matters but just in case...)
        if (rootElement.getAttributes().getNamedItem("numberReturned").getTextContent().
                equals(rootElement.getAttributes().getNamedItem("numberMatched").getTextContent())) {

            //Get a list of elements
            NodeList elements = doc.getElementsByTagName("BsWfs:BsWfsElement");
            NodeList elNames = doc.getElementsByTagName("BsWfs:ParameterName");
            NodeList elValues = doc.getElementsByTagName("BsWfs:ParameterValue");
            NodeList elTimes = doc.getElementsByTagName("BsWfs:Time");
            System.out.println(elements.getLength() + " elements were found.");
            int elementsLength = elements.getLength();

            if (elementsLength == 0){
                return allObservations;
            }

            //Every observation (time,location) should have as many items of information as set in parameters
            int o = 1; // index of single observation
            int j = 1; // parameter index
            int i = 0;
            int k = settings.countO;
            boolean isNaN = false;
            //System.out.println(k + " observation parameters requested.");
            //Create a new WeatherObservation
            WeatherObservation observation = new WeatherObservation();

            while (i < elementsLength) {

                Node node = elements.item(i); //BsWfs:BsWfsElement
                Node value = elValues.item(i); //BsWfs:ParameterValue
                Node name = elNames.item(i); //BsWfs:ParameterName
                Node time = elTimes.item(i); //BsWfs:Time
                //NodeList children = node.getChildNodes(); //<BsWfs:Location>, <BsWfs:Time>, <BsWfs:ParameterName>, <BsWfs:ParameterValue>

                //Save time and location of the observation
                String timeString = (String) time.getTextContent().subSequence(0, 19);
                LocalDateTime dateTime = LocalDateTime.parse(timeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                LocalDate d = dateTime.toLocalDate();
                LocalTime t = dateTime.toLocalTime();

                observation.setTime(t);
                observation.setDate(d);

                String val;

                if (node.getAttributes().getNamedItem("gml:id").getTextContent().equals(String.format("BsWfsElement.1.%s.%s", o, j))) {
                    switch (name.getTextContent()) {
                        //Current temperature
                        case "t2m":
                            ++j;
                            val = value.getTextContent();
                            //Discard NaN values or if the value is empty
                            if(val.isEmpty() || val.equals("NaN")){
                                isNaN = true;
                                break;
                            }
                            observation.setTemperature(val); //BsWfs:ParameterValue
                            break;
                        //Wind speed
                        case "ws_10min":
                            ++j;
                            val = value.getTextContent();
                            if(val.isEmpty() || val.equals("NaN")){
                                isNaN = true;
                                break;
                            }
                            observation.setWindSpeed(val); //BsWfs:ParameterValue
                            break;
                        //Cloud cover
                        case "n_man":
                            ++j;
                            val = value.getTextContent();
                            if(val.isEmpty() || val.equals("NaN")){
                                isNaN = true;
                                break;
                            }
                            observation.setCloudiness(val); //BsWfs:ParameterValue
                            break;
                        //Average temperature
                        case "TA_PT1H_AVG":
                            ++j;
                            val = value.getTextContent();
                            if(val.isEmpty() || val.equals("NaN")){
                                isNaN = true;
                                break;
                            }
                            observation.setAverTemperature(val); //BsWfs:ParameterValue
                            break;
                        //Maximum temperature
                        case "TA_PT1H_MAX":
                            ++j;
                            val = value.getTextContent();
                            if(val.isEmpty() || val.equals("NaN")){
                                isNaN = true;
                                break;
                            }
                            observation.setMaxTemperature(val); //BsWfs:ParameterValue
                            break;
                        //Minimum temperature
                        case "TA_PT1H_MIN":
                            ++j;
                            val = value.getTextContent();
                            if(val.isEmpty() || val.equals("NaN")){
                                isNaN = true;
                                break;
                            }
                            observation.setMinTemperature(val); //BsWfs:ParameterValue
                            break;
                    }
                } else {
                    //MOVE TO THE NEXT OBSERVATION:

                    --i; // Retake this element next round, but
                    ++o; // take the next observation.
                    //Add the WeatherObservation into a list with the values set in
                    //Add if all requested parameters have been gathered, otherwise discard the whole observation!
                    if(j - 1 == k && !isNaN){
                        allObservations.add(observation);
                    }
                    j = 1; // Start from the first parameter again

                    //Create a new WeatherObservation
                    observation = new WeatherObservation();
                    isNaN = false; //Hopefully there'll be no NaNs
                }
                ++i;
            }
            observation.setTemperature("0");
            observation.setCloudiness("0");
            observation.setWindSpeed("0");
            allObservations.add(observation);
        }
        return allObservations;
}

    public ArrayList<WeatherForecast> parseForecast(Document doc) {

        Node rootElement = doc.getDocumentElement();
        // Check if values match (I don't know if it matters but just in case...)
        if (rootElement.getAttributes().getNamedItem("numberReturned").getTextContent().
                equals(rootElement.getAttributes().getNamedItem("numberMatched").getTextContent())) {

            //Get a list of elements
            NodeList elements = doc.getElementsByTagName("BsWfs:BsWfsElement");
            NodeList elValues = doc.getElementsByTagName("BsWfs:ParameterValue");
            NodeList elNames = doc.getElementsByTagName("BsWfs:ParameterName");
            NodeList elTimes = doc.getElementsByTagName("BsWfs:Time");
            int elementsLength = elements.getLength();

            if (elementsLength == 0){
                return allForecasts;
            }

            //Every observation (time,location) should have as many items of information as set in parameters
            int o = 1; // index of single observation
            int j = 1; // parameter index
            int i = 0;
            int k = settings.countF;
            boolean isNaN = false;
            //System.out.println(k + " forecast parameters requested.");

            //Create a new WeatherForecast
            WeatherForecast forecast = new WeatherForecast();

            while (i < elementsLength) {

                String val;

                Node node = elements.item(i); //BsWfs:BsWfsElement
                Node value = elValues.item(i); //BsWfs:ParameterValue
                Node name = elNames.item(i); //BsWfs:ParameterName
                Node time = elTimes.item(i); //BsWfs:Time
                //NodeList children = node.getChildNodes(); //<BsWfs:Location>, <BsWfs:Time>, <BsWfs:ParameterName>, <BsWfs:ParameterValue>

                //Save time and location of the observation
                String timeString = (String) time.getTextContent().subSequence(0, 19);
                LocalDateTime dateTime = LocalDateTime.parse(timeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                LocalDate d = dateTime.toLocalDate();
                LocalTime t = dateTime.toLocalTime();

                forecast.setTime(t);
                forecast.setDate(d);

                if (node.getAttributes().getNamedItem("gml:id").getTextContent().equals(String.format("BsWfsElement.1.%s.%s", o, j))) {
                    //Temperature
                    switch (name.getTextContent()) {
                        case "Temperature":
                            ++j;
                            val = value.getTextContent();
                            //Discard NaN values or if the value is empty
                            if(val.isEmpty() || val.equals("NaN")){
                                isNaN = true;
                                break;
                            }
                            forecast.setTemperature(val); //BsWfs:ParameterValue
                            break;
                        //Wind direction
                        case "WindDirection":
                            ++j;
                            val = value.getTextContent();
                            //Discard NaN values or if the value is empty
                            if(val.isEmpty() || val.equals("NaN")){
                                isNaN = true;
                                break;
                            }
                            forecast.setWindDirection(val); //BsWfs:ParameterValue
                            break;
                        //Cloud cover
                        case "TotalCloudCover":
                            ++j;
                            val = value.getTextContent();
                            //Discard NaN values or if the value is empty
                            if(val.isEmpty() || val.equals("NaN")){
                                isNaN = true;
                                break;
                            }
                            forecast.setCloudiness(val); //BsWfs:ParameterValue
                            break;
                        //Radiation
                        case "RadiationGlobal":
                            ++j;
                            val = value.getTextContent();
                            //Discard NaN values or if the value is empty
                            if(val.isEmpty() || val.equals("NaN")){
                                isNaN = true;
                                break;
                            }
                            forecast.setRadiation(val); //BsWfs:ParameterValue
                            break;
                        //Visibility
                        case "Visibility":
                            ++j;
                            val = value.getTextContent();
                            //Discard NaN values or if the value is empty
                            if(val.isEmpty() || val.equals("NaN")){
                                isNaN = true;
                                break;
                            }
                            forecast.setVisibility(val); //BsWfs:ParameterValue
                            break;
                        //Wind gust
                        case "WindGust":
                            ++j;
                            val = value.getTextContent();
                            //Discard NaN values or if the value is empty
                            if(val.isEmpty() || val.equals("NaN")){
                                isNaN = true;
                                break;
                            }
                            forecast.setWindGust(val); //BsWfs:ParameterValue
                            break;
                        //Wind speed
                        case "WindSpeedMS":
                            ++j;
                            val = value.getTextContent();
                            //Discard NaN values or if the value is empty
                            if(val.isEmpty() || val.equals("NaN")){
                                isNaN = true;
                                break;
                            }
                            forecast.setWindSpeed(val); //BsWfs:ParameterValue
                            break;
                    }
                } else {
                    //MOVE TO THE NEXT FORECAST:

                    --i; // Retake this element next round, but
                    ++o; // take the next observation.
                    //Add the WeatherForecast into a list with the values set in
                    //Add if all requested parameters have been gathered, otherwise discard the whole forecast!
                    if(j - 1 == k && !isNaN){
                        allForecasts.add(forecast);
                    }
                    j = 1; // Start from the first parameter again

                    //Create a new WeatherObservation
                    forecast = new WeatherForecast();
                    isNaN = false; //Hopefully there'll be no NaNs

                }
                ++i;
            }
            //The last one
            forecast.setTemperature("0");
            forecast.setWindSpeed("0");
            forecast.setCloudiness("0");
            allForecasts.add(forecast);
        }
        return allForecasts;
    }

    public TreeMap<LocalDate, ArrayList<WeatherForecast>> classifyForecastsByDate(){

        //No use for this function if empty
        if (allForecasts.isEmpty()) {
            return null;
        }
        //A list preserved for forecasts per a certain date.
        //Always clear when moving onto another date. Before that push the result into observationsByDate.
        ArrayList<WeatherForecast> forecastsDate = new ArrayList<>(20);

        //At the beginning: previous date is the first date of the list
        LocalDate datePrev = allForecasts.get(0).getDate();

        //Go through the allForecasts list
        for (WeatherForecast forecast : allForecasts) {

            LocalDate dateThis = forecast.getDate();

            //If not same date then create a new ArrayList
            if(!datePrev.equals(dateThis)){

                //1. Put handled date with observations into observationsByDate
                if (!forecastsDate.isEmpty()) {

                    if(forecastsByDate.containsKey(datePrev)) {
                        //Fill already existing list with new observations
                        ArrayList<WeatherForecast> forecastsPrevDate;
                        forecastsPrevDate = forecastsByDate.get(datePrev);
                        forecastsPrevDate.addAll(forecastsDate);
                        forecastsByDate.put(dateThis, forecastsDate);

                    } else {

                        //Put the collection as such
                        forecastsByDate.put(datePrev, forecastsDate);
                    }
                }
                //2. New list by clearing forecastsThisDate
                forecastsDate = new ArrayList<>();
                //3. Add a new forecast for this (current) date
                forecastsDate.add(forecast);

            //Else continue adding observations
            } else {

                forecastsDate.add(forecast);
            }

            datePrev = dateThis; //Update dates

        }
        forecastsByDate.put(datePrev, forecastsDate);

        //System.out.println("There are " + forecastsByDate.size() + " different dates of forecasts.");
        return forecastsByDate;

    }

    public TreeMap<LocalDate, ArrayList<WeatherObservation>> classifyObservationsByDate(){

        //No use for this function if empty
        if (allObservations.isEmpty()) {
            return null;
        }
        //A list preserved for observations per a certain date.
        //Always clear when moving onto another date. Before that push the result into observationsByDate.
        ArrayList<WeatherObservation> observationsDate = new ArrayList<>(20);

        //At the beginning: previous date is the first date of the list
        LocalDate datePrev = allObservations.get(0).getDate();

        //Go through the allObservations list
        for (WeatherObservation observation : allObservations) {

            LocalDate dateThis = observation.getDate();

            //If not same date then create a new ArrayList
            if(!datePrev.equals(dateThis)){

                //1. Put handled date with observations into observationsByDate
                if (!observationsDate.isEmpty()) {

                    if(observationsByDate.containsKey(datePrev)) {
                        //Fill already existing list with new observations
                        ArrayList<WeatherObservation> observationsPrevDate;
                        observationsPrevDate = observationsByDate.get(datePrev);
                        observationsPrevDate.addAll(observationsDate);
                        observationsByDate.put(dateThis, observationsDate);

                    } else {

                        //Put the collection as such
                        observationsByDate.put(datePrev, observationsDate);
                    }
                }
                //2. New list by clearing observationsThisDate
                observationsDate = new ArrayList<>();
                //3. Add a new observation for this (current) date
                observationsDate.add(observation);


                //Else continue adding observations
            } else {

                observationsDate.add(observation);

            }

            datePrev = dateThis; //Update dates

        }
        observationsByDate.put(datePrev, observationsDate);

        System.out.println("There are " + observationsByDate.size() + " different dates of observations.");
        return observationsByDate;
    }

    public void clear(){
        allObservations.clear();
        allForecasts.clear();
        observationsByDate.clear();
        forecastsByDate.clear();
    }
}

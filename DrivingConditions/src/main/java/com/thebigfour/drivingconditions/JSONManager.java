/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.thebigfour.drivingconditions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import org.json.JSONArray;
import org.json.JSONObject;


public class JSONManager {
    
    public JSONManager(){
        
    }
    
    // method for turning last maintenanceTasks treemmap to json format
    public JSONArray maintenanceTasksIntoJSON(TreeMap<LocalDate, ArrayList<MaintenanceTask>> tasksByDate){
        
        JSONArray allTasksJSONArray = new JSONArray();
        
        // turn maintenanceTasks to JSONArray
        for(Map.Entry<LocalDate, ArrayList<MaintenanceTask>> entry : tasksByDate.entrySet()){
            
            
            LocalDate date = entry.getKey();
            ArrayList<MaintenanceTask> tasks = entry.getValue();
            
            // json object for storing one days date + tasks in array
            JSONObject daysTasks = new JSONObject();
            
            // temporary array for tasks
            JSONArray tasksArray = new JSONArray();
            
            for (var task : tasks){
                tasksArray.put(task.toJSON());
            }
            
            daysTasks.put("date", date.toString());
            daysTasks.put("tasks", tasksArray);
            
            allTasksJSONArray.put(daysTasks);
            
        }
        
        return allTasksJSONArray;
    }
    
    public JSONArray roadConditionsIntoJSON(ArrayList<RoadCondition> allConditions){
        
        JSONArray allRoadConditionsJSONArray = new JSONArray();
        
        // turn roadConditions to JSONArray
        for(var condition : allConditions){

            JSONObject conditionJSON = condition.toJSON();
            
            allRoadConditionsJSONArray.put(conditionJSON);
            
        }
        
        return allRoadConditionsJSONArray;
    }
    
    // method for turning trafficmessages array into json
    public JSONArray trafficMessagesIntoJSON(ArrayList<TrafficMessage> allMessages){
        
        JSONArray allTrafficMessagesJSONArray = new JSONArray();
        
        // turn traffic messages into JSONArray
        for(var message : allMessages){

            JSONObject messageJSON = message.toJSON();
            
            allTrafficMessagesJSONArray.put(messageJSON);
            
        }

        return allTrafficMessagesJSONArray;
    }
    

    public JSONArray weatherObservationsIntoJSON(TreeMap<LocalDate, ArrayList<WeatherObservation>> observationsByDate){

        JSONArray allweatherObservationsJSONArray = new JSONArray();
        
        // turn weather observations to JSONArray
        for(Map.Entry<LocalDate, ArrayList<WeatherObservation>> entry : observationsByDate.entrySet()){
            
            LocalDate date = entry.getKey();
            ArrayList<WeatherObservation> array = entry.getValue();
            
            // json object for storing one days date + info in array
            JSONObject daysObservations = new JSONObject();
            
            // temporary array for info
            JSONArray observationsArray = new JSONArray();
            
            for (var info : array){
                observationsArray.put(info.toJSON());
            }
            
            daysObservations.put("date", date.toString());
            daysObservations.put("observations", observationsArray);
            
            allweatherObservationsJSONArray.put(daysObservations);
            
        }
        
        return allweatherObservationsJSONArray;
    }
    
    public JSONArray weatherForecastIntoJSON(TreeMap<LocalDate, ArrayList<WeatherForecast>> forecastsByDate){

        JSONArray allweatherForecastJSONArray = new JSONArray();
        
        // turn weather forecasts to JSONArray
        for(Map.Entry<LocalDate, ArrayList<WeatherForecast>> entry : forecastsByDate.entrySet()){
            
            LocalDate date = entry.getKey();
            ArrayList<WeatherForecast> array = entry.getValue();
            
            // json object for storing one days date + info in array
            JSONObject daysForecasts = new JSONObject();
            
            // temporary array for info
            JSONArray forecastsArray = new JSONArray();
            
            for (var info : array){
                forecastsArray.put(info.toJSON());
            }
            
            daysForecasts.put("date", date.toString());
            daysForecasts.put("forecasts", forecastsArray);
            
            allweatherForecastJSONArray.put(daysForecasts);
            
        }

        
        return allweatherForecastJSONArray;
    }
    
    public TreeMap<LocalDate, ArrayList<MaintenanceTask>> makeMaintenanceTaskTree(String body){
        
        TreeMap<LocalDate, ArrayList<MaintenanceTask>> dataset = new TreeMap<>();
        JSONArray jsonArray = new JSONArray(body);
        
        for (var json : jsonArray){
            
            LocalDate date;
            ArrayList<MaintenanceTask> arrayList = new ArrayList<>(); 
            
            JSONObject jsonobject = (JSONObject) json;
            JSONArray tasks = (JSONArray) jsonobject.get("tasks");
            
            for (var task : tasks){
                
                MaintenanceTask mTask = makeMaintenanceTask((JSONObject) task);
                
                arrayList.add(mTask);
                
            }
            
            String date_str = jsonobject.get("date").toString();
            
            //parse it into date
            date = LocalDate.parse(date_str);
            
            // add to the tree
            dataset.put(date, arrayList);
            
        }

        return dataset;
    }
        
    private MaintenanceTask makeMaintenanceTask(JSONObject task){
        

        MaintenanceTask newTask = new MaintenanceTask();
        
        // set attributes
        newTask.setId(task.get("id").toString());
        newTask.setStartTime(task.get("startTime").toString());
        newTask.setEndTime(task.get("endTime").toString());
        
        JSONArray jsonarray = (JSONArray) task.get("tasks");
        
        ArrayList<String> tasks = new ArrayList<>();
        
        for (var tsk : jsonarray){
            tasks.add(tsk.toString());
            
        }
        
        newTask.setTasks(tasks);

        return newTask;
    }
    
    public ArrayList<RoadCondition> makeRoadConditionArray(String body){
        
        ArrayList<RoadCondition> dataset = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(body);
        
        
        for (var json : jsonArray){
            
            
            RoadCondition rc = makeRoadCondition((JSONObject) json);
            
            dataset.add(rc);
        }
        
        
        return dataset;
    }
    
    private RoadCondition makeRoadCondition(JSONObject condition){
        
        RoadCondition rc = new RoadCondition();
        
        rc.setTime(condition.get("time").toString());
        rc.setType(condition.get("type").toString());
        rc.setForecastName(condition.get("forecastName").toString());
        rc.setDaylight(condition.get("daylight").toString());
        rc.setRoadTemperature(condition.get("roadTemperature").toString());
        rc.setTemperature(condition.get("temperature").toString());
        rc.setWindSpeed(condition.get("windSpeed").toString());
        rc.setWindDirection(condition.get("windDirection").toString());
        rc.setOverallRoadCondition(condition.get("overallRoadCondition").toString());
        rc.setReliability(condition.get("reliability").toString());

        return rc;
    }
    
    public ArrayList<TrafficMessage> makeTrafficMessageArray(String body){
        
        ArrayList<TrafficMessage> dataset = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(body);
        
        for (var json : jsonArray){
            
            TrafficMessage tm = makeTrafficMessage((JSONObject) json);
            
            dataset.add(tm);
        }
        
        
        return dataset;
    }
    
    private TrafficMessage makeTrafficMessage(JSONObject message){
        
        String situationType = message.get("situationType").toString();
        String title = message.get("title").toString();
        String description = message.get("description").toString();
        String startTime = message.get("startTime").toString();
        String endTime = message.get("endTime").toString();

        JSONArray jsonarray = (JSONArray) message.get("features");
        
        ArrayList<String> features = new ArrayList<>();
        
        for (var ftr : jsonarray){
            features.add(ftr.toString());
        }
        
        TrafficMessage tm = new TrafficMessage(
            situationType, title, description, features, startTime, endTime);
        
        tm.setStartTime(startTime);
        tm.setEndTime(endTime);
        
        return tm;
    }
    
    public TreeMap<LocalDate, ArrayList<WeatherObservation>> makeWeatherObservationTree(String body){
        
        TreeMap<LocalDate, ArrayList<WeatherObservation>> dataset = new TreeMap<>();
        JSONArray jsonArray = new JSONArray(body);
        
        for (var json : jsonArray){
            
            LocalDate date;
            ArrayList<WeatherObservation> arrayList = new ArrayList<>(); 
            
            JSONObject jsonobject = (JSONObject) json;
            JSONArray observationsJSON = (JSONArray) jsonobject.get("observations");
            
            for (var obs : observationsJSON){
                
                WeatherObservation weatherObs = makeWeatherObservation((JSONObject) obs);
                
                arrayList.add(weatherObs);
                
            }
            
            String date_str = jsonobject.get("date").toString();
            
            //parse it into date
            date = LocalDate.parse(date_str);
            
            // add to the tree
            dataset.put(date, arrayList);
            
        }

        return dataset;
    }
    
    private WeatherObservation makeWeatherObservation(JSONObject observation){
        
        WeatherObservation newObservation = new WeatherObservation();
        
        // parse into local time and date first
        LocalTime localTime = LocalTime.parse(observation.opt("time").toString());
        LocalDate localDate = LocalDate.parse(observation.opt("date").toString());
        
        if(localTime != null){
            newObservation.setTime(LocalTime.parse(observation.get("time").toString()));
        }
        
        if(localDate != null){
            newObservation.setDate(LocalDate.parse(observation.get("date").toString()));
        }
        
        if(observation.opt("location") != null){
            newObservation.setLocation(observation.get("location").toString());
        }
        if(observation.opt("windSpeed") != null){
            newObservation.setWindSpeed(observation.get("windSpeed").toString());
        }
        if(observation.opt("cloudiness") != null){
            newObservation.setCloudiness(observation.get("cloudiness").toString());
        }
        if(observation.opt("temperature") != null){
            newObservation.setTemperature(observation.get("temperature").toString());
        }
        if(observation.opt("averTemperature") != null){
            newObservation.setAverTemperature(observation.get("averTemperature").toString());
        }
        if(observation.opt("maxTemperature") != null){
            newObservation.setMaxTemperature(observation.get("maxTemperature").toString());
        }
        if(observation.opt("minTemperature") != null){
            newObservation.setMinTemperature(observation.get("minTemperature").toString());
        }

        return newObservation;
    }
    
    public TreeMap<LocalDate, ArrayList<WeatherForecast>> makeWeatherForecastTree(String body){
        
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        JsonElement je = JsonParser.parseString​(body);
//        String prettyJsonString = gson.toJson(je);
//        System.out.println(prettyJsonString);
        
        TreeMap<LocalDate, ArrayList<WeatherForecast>> dataset = new TreeMap<>();
        JSONArray jsonArray = new JSONArray(body);
        
        for (var json : jsonArray){
            
            LocalDate date;
            ArrayList<WeatherForecast> arrayList = new ArrayList<>(); 
            
            JSONObject jsonobject = (JSONObject) json;
            JSONArray forecastJSON = (JSONArray) jsonobject.get("forecasts");
            
            for (var frct : forecastJSON){
                
                WeatherForecast weatherForecast = makeWeatherForecast((JSONObject) frct);
                
                arrayList.add(weatherForecast);
                
            }
            
            String date_str = jsonobject.get("date").toString();
            
            //parse it into date
            date = LocalDate.parse(date_str);
            
            // add to the tree
            dataset.put(date, arrayList);
            
        }

        return dataset;
    }
    
    private WeatherForecast makeWeatherForecast(JSONObject observation){
        
        WeatherForecast newForecast = new WeatherForecast();
        
        // parse into local time and date first
        LocalTime localTime = LocalTime.parse(observation.opt("time").toString());
        LocalDate localDate = LocalDate.parse(observation.opt("date").toString());
        
        if(localTime != null){
            newForecast.setTime(LocalTime.parse(observation.get("time").toString()));
        }
        
        if(localDate != null){
            newForecast.setDate(LocalDate.parse(observation.get("date").toString()));
        }
        
        if(observation.opt("location") != null){
            newForecast.setLocation(observation.get("location").toString());
        }
        if(observation.opt("windSpeed") != null){
            newForecast.setWindSpeed(observation.get("windSpeed").toString());
        }
        if(observation.opt("cloudiness") != null){
            newForecast.setCloudiness(observation.get("cloudiness").toString());
        }
        if(observation.opt("temperature") != null){
            newForecast.setTemperature(observation.get("temperature").toString());
        }
        if(observation.opt("windDirection") != null){
            newForecast.setWindDirection(observation.get("windDirection").toString());
        }
        if(observation.opt("radiation") != null){
            newForecast.setRadiation(observation.get("radiation").toString());
        }
        if(observation.opt("visibility") != null){
            newForecast.setVisibility(observation.get("visibility").toString());
        }
        if(observation.opt("windGust") != null){
            newForecast.setWindGust(observation.get("windGust").toString());
        }
        if(observation.opt("averTemperature") != null){
            newForecast.setAverTemperature(observation.get("averTemperature").toString());
        }


        return newForecast;
    }
    
}

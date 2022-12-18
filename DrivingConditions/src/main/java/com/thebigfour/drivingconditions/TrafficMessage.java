package com.thebigfour.drivingconditions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import org.json.JSONArray;
import org.json.JSONObject;

public class TrafficMessage {
    
    private String situationType;
    private String title;
    private String description;
    private ArrayList<String> features;
    private String startTime;
    private String endTime;

    public TrafficMessage(String situationType, String title, String description, ArrayList<String> features, String startTime, String endTime) {
        this.situationType = situationType;
        this.title = title;
        this.description = description;
        this.features = features;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Helsinki"));
        
        
        if (startTime != null ) {
            Date date;
            try {
                date = sdf.parse(startTime);
                String newstring = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
                this.startTime = newstring;
            } catch (ParseException ex) {
                this.startTime = "error";
            }
        
        } else {
            this.startTime = "unknown";
        }
        
        if (endTime != null ) {
            Date date;
            try {
                date = sdf.parse(endTime);
                String newstring = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
                this.endTime = newstring;
            } catch (ParseException ex) {
                this.endTime = "error";
            }
            
        } else {
            this.endTime = "unknown";
        }
    }
    
    
    public String getSituationType() {
        return situationType;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getFeatures() {
        return features;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setSituationType(String situationType) {
        this.situationType = situationType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFeatures(ArrayList<String> features) {
        this.features = features;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "TrafficMessage{\n" + "situationType=" + situationType + "\ntitle=" + title + "\ndescription=" + description + "\nfeatures=" + features + "\nstartTime=" + startTime + "\nendTime=" + endTime + '}';
    }
    
    // returns trafficmessage in json format
    public JSONObject toJSON(){
        
        JSONObject values = new JSONObject();
        JSONArray featureArray = new JSONArray();
        
        for (var feature : features) {
            featureArray.put(feature);
        }
        
        values.put("situationType", this.situationType);
        values.put("title", this.title);
        values.put("description", this.description);
        values.put("features", featureArray);
        values.put("startTime", this.startTime);
        values.put("endTime", this.endTime);
        
        return values;
    }
    
}

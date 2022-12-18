package com.thebigfour.drivingconditions;

import java.time.LocalDate;
import java.time.LocalTime;
import org.json.JSONObject;

/**
 *
 * @author miika.hamalainen@tuni.fi
 */
public class WeatherObservation extends DataPoint{

    private LocalTime time;
    private LocalDate date;
    private String location;
    private String windSpeed;
    private String cloudiness;
    private String temperature;
    private String averTemperature;
    private String maxTemperature;
    private String minTemperature;

    public LocalTime getTime() {
        return time;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getAverTemperature() {
        return averTemperature;
    }

    public String getMaxTemperature() {
        return maxTemperature;
    }

    public String getMinTemperature() {
        return minTemperature;
    }

    public String getTemperature(){
        return temperature;
    }

    public String getLocation() {
        return location;
    }

    public String getCloudiness() {
        return cloudiness;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setCloudiness(String cloudiness) {
        this.cloudiness = cloudiness;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setAverTemperature(String averTemperature) {
        this.averTemperature = averTemperature;
    }

    public void setMaxTemperature(String maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public void setMinTemperature(String minTemperature) {
        this.minTemperature = minTemperature;
    }
    
    // returns weather observation in json format
    public JSONObject toJSON(){
        
        JSONObject values = new JSONObject();

        
        values.put("time", this.time.toString());
        values.put("date", this.date.toString());
        values.put("location", this.location);
        values.put("windSpeed", this.windSpeed);
        values.put("cloudiness", this.cloudiness);
        values.put("temperature", this.temperature);
        values.put("averTemperature", this.averTemperature);
        values.put("maxTemperature", this.maxTemperature);
        values.put("minTemperature", this.minTemperature);
        
        return values;
    }
    
}

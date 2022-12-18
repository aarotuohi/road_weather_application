package com.thebigfour.drivingconditions;

import java.time.LocalDate;
import java.time.LocalTime;
import org.json.JSONObject;

/**
 *
 * @author miika.hamalainen@tuni.fi
 */
public class WeatherForecast extends DataPoint{

    private LocalTime time;
    private LocalDate date;
    private String location;
    private String windSpeed;
    private String cloudiness;
    private String temperature;
    private String windDirection;
    private String radiation;
    private String visibility;
    private String windGust;
    private String averTemperature;

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public void setCloudiness(String cloudiness) {
        this.cloudiness = cloudiness;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setAverTemperature(String averTemperature) {
        this.averTemperature = averTemperature;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setRadiation(String radiation) {
        this.radiation = radiation;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setWindGust(String windGust) {
        this.windGust = windGust;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getCloudiness() {
        return cloudiness;
    }

    public String getLocation() {
        return location;
    }

    public LocalTime getTime() {
        return time;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public String getRadiation() {
        return radiation;
    }

    public String getVisibility() {
        return visibility;
    }

    public String getWindGust() {
        return windGust;
    }

    public String getAverTemperature() {
        return averTemperature;
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
        values.put("windDirection", this.windDirection);
        values.put("radiation", this.radiation);
        values.put("visibility", this.visibility);
        values.put("windGust", this.windGust);
        values.put("averTemperature", this.averTemperature);

        
        return values;
    }
    
}

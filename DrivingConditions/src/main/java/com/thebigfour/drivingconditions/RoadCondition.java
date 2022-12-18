package com.thebigfour.drivingconditions;


import org.json.JSONObject;


public class RoadCondition {
    private String time;
    private String type;
    private String forecastName;
    private String daylight;
    private String roadTemperature;
    private String temperature;
    private double windSpeed;
    private int windDirection;
    private String overallRoadCondition;
    private String reliability;

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getForecastName() {
        return forecastName;
    }

    public String getDaylight() {
        return daylight;
    }

    public String getRoadTemperature() {
        return roadTemperature;
    }

    public String getTemperature() {
        return temperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public int getWindDirection() {
        return windDirection;
    }

    public String getOverallRoadCondition() {
        return overallRoadCondition;
    }

    public String getReliability() {
        return reliability;
    }
    
    // setters
    
    public void setTime(String time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setForecastName(String forecastName) {
        this.forecastName = forecastName;
    }

    public void setDaylight(String daylight) {
        this.daylight = daylight;
    }

    public void setRoadTemperature(String roadTemperature) {
        this.roadTemperature = roadTemperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = Double.parseDouble(windSpeed);
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = Integer.parseInt(windDirection);
    }

    public void setOverallRoadCondition(String overallRoadCondition) {
        
        this.overallRoadCondition = overallRoadCondition;
    }

    public void setReliability(String reliability) {
        this.reliability = reliability;
    }

    @Override
    public String toString() {
        return "RoadCondition{\n" + "time=" + time + "\ntype=" + type + "\nforecastName=" + forecastName + "\ndaylight=" + daylight + "\nroadTemperature=" + roadTemperature + "\nwindSpeed=" + windSpeed + "\nwindDirection=" + windDirection + "\noverallRoadCondition=" + overallRoadCondition + "\nreliability=" + reliability + '}';
    }
    
    
    // returns roadcondition in json format
    public JSONObject toJSON(){
        
        JSONObject values = new JSONObject();
        
        values.put("time", this.time);
        values.put("type", this.type);
        values.put("forecastName", this.forecastName);
        values.put("daylight", this.daylight);
        values.put("roadTemperature", this.roadTemperature);
        values.put("temperature", this.temperature);
        values.put("windSpeed", this.windSpeed);
        values.put("windDirection", this.windDirection);
        values.put("overallRoadCondition", this.overallRoadCondition);
        values.put("reliability", this.reliability);
        
        return values;
    }

}

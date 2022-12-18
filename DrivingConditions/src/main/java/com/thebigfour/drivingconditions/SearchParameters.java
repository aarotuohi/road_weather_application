package com.thebigfour.drivingconditions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SearchParameters {

    private double[] coordinates;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String from;
    private String to;
    private boolean roadData;
    private boolean fmiData;

    public SearchParameters(double[] coordinates, LocalDate fromDate, LocalDate toDate, String from, String to, boolean roadData, boolean fmiData) {
        this.coordinates = coordinates;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.from = from;
        this.to = to;
        this.roadData = roadData;
        this.fmiData = fmiData;
    }

    public SearchParameters(JSONObject obj) {
        JSONArray arr = (JSONArray) obj.get("coordinates");

        this.coordinates = new double[4];
        for (int j = 0; j < arr.size(); j++) {
            coordinates[j] = (double) arr.get(j);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.fromDate = LocalDate.parse((String) obj.get("fromDate"), formatter);
        this.toDate = LocalDate.parse((String) obj.get("toDate"), formatter);
        this.roadData = (boolean) obj.get("roadData");
        this.fmiData = (boolean) obj.get("fmiData");
    }

    public JSONObject toJSON() {

        JSONObject values = new JSONObject();
        JSONArray coordinatesList = new JSONArray();
        for (int i = 0; i < 4; i++) {
            coordinatesList.add(this.coordinates[i]);
        }
        values.put("coordinates", coordinatesList);
        values.put("fromDate", this.fromDate.toString());
        values.put("toDate", this.toDate.toString());
        values.put("roadData", this.roadData);
        values.put("fmiData", this.fmiData);

        return values;

    }

    public boolean isRoadData() {
        return roadData;
    }

    public void setRoadData(boolean roadData) {
        this.roadData = roadData;
    }

    public boolean isFmiData() {
        return fmiData;
    }

    public void setFmiData(boolean fmiData) {
        this.fmiData = fmiData;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

}

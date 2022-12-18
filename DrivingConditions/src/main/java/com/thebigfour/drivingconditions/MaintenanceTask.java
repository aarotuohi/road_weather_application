package com.thebigfour.drivingconditions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import org.json.JSONArray;
import org.json.JSONObject;

public class MaintenanceTask {

    private String id;
    private ArrayList<String> tasks;
    private String startTime;
    private String endTime;
    private Date startDate;
    private Date endDate;

    public void formatDates() throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Helsinki"));

        Date dateFrom = sdf.parse(this.startTime);
        Date dateTo = sdf.parse(this.endTime);
        this.startDate = dateFrom;
        this.endDate = dateTo;

        String newStartTime = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(dateFrom);
        String newEndTime = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(dateTo);

        this.startTime = newStartTime;
        this.endTime = newEndTime;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getId() {
        return this.id;
    }

    public ArrayList<String> getTasks() {
        return this.tasks;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setTasks(ArrayList<String> tasks) {
        this.tasks = tasks;
    }
//    public void setStartDate(String date) {
//        return startDate;
//    }
//
//    public void setEndDate() {
//        return endDate;
//    }


    @Override
    public String toString() {
        return "MaintenanceTask{\n" + "id=" + id + "\ntasks=" + tasks + "\nstartTime=" + startTime + "\nendTime=" + endTime + '}';
    }
    
    // returns task in json format
    public JSONObject toJSON(){
        
        JSONObject values = new JSONObject();
        JSONArray tasksArray = new JSONArray();
        
        for (var task : tasks) {
            tasksArray.put(task);
        }
        
        values.put("id", this.id);
        values.put("tasks", tasksArray);
        values.put("startTime", this.startTime);
        values.put("endTime", this.endTime);
        values.put("startDate", this.startDate);
        values.put("endDate", this.endDate);
        
        return values;
    }

}

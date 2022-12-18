/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.thebigfour.drivingconditions;

import java.util.ArrayList;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/**
 *
 * @author Mikko
 */
public class TaskRectangle extends Rectangle{
    
    // convert string to int
    private String startTime = "";
    private String endTime = "";
    private String taskType = "";
    private int taskTooltipXOffset = 40;
    private int taskTooltipYOffset = 60;
    private int start = 0;
    private int end = 0;
    
    
    public TaskRectangle(MaintenanceTask task, double rectangleHeight, double rectangleWidth){
        
        this.startTime = task.getStartTime();
        this.endTime = task.getEndTime();
        this.taskType = task.getTasks().get(0);
        
        
        try{
            this.start = Integer.parseInt(this.startTime.substring(11,13));
            this.end = Integer.parseInt(this.endTime.substring(11,13));
            
        }catch(Exception e){
            System.out.println(task.getStartTime() + " " + task.getEndTime());
            e.printStackTrace();
        }
        
        int taskDuration = this.end - this.start;
        
        if (taskDuration == 0) {
            taskDuration += 1;
        }

        String color = "#FFFFFF";
        
        
        // change the types to match the real ones from the api
        
        if(this.taskType.equals("Brushing")){
            color = "#4aed4a";
            this.taskTooltipXOffset += 20;
        }
        else if(this.taskType.equals("Brush clearing")){
            color = "#9471d1";
        }
        else if(this.taskType.equals("Cleansing of bridges")){
            color = "#f75e5e";
        }
        else if(this.taskType.equals("Cleansing of rest areas")){
            color = "#fa43e2";
        }
        else if(this.taskType.equals("Cleansing of traffic signs")){
            color = "#f4fa43";
        }
        else if(this.taskType.equals("Clients quality control")){
            color = "#8f2757";
        }
        else if(this.taskType.equals("Compaction by rolling")){
            color = "#d99573";
        }
        else if(this.taskType.equals("Crack filling")){
            color = "#6eb5a8";
        }
        else if(this.taskType.equals("Ditching")){
            color = "#3c39e6";
        }
        else if(this.taskType.equals("Dust binding of gravel road surface")){
            color = "#91632f";
        }
        else if(this.taskType.equals("Filling of gravel road shoulders")){
            color = "#8f0a15";
        }
        else if(this.taskType.equals("Filling of road shoulders")){
            color = "#5632a8";
        }
        else if(this.taskType.equals("Heating")){
            color = "#a8204b";
        }
        else if(this.taskType.equals("Levelling gravel road surface")){
            color = "#20a87f";
        }
        else if(this.taskType.equals("Levelling of road shoulders")){
            color = "#20a840";
        }
        else if(this.taskType.equals("Levelling of road surface")){
            color = "#5cc8db";
        }
        else if(this.taskType.equals("Line sanding")){
            color = "#ebbab2";
        }
        else if(this.taskType.equals("Lowering of snowbanks")){
            color = "#42e3e3";
        }
        else if(this.taskType.equals("Maintenance of guide signs and reflector posts")){
            color = "#5e1408";
            this.taskTooltipXOffset += 75;
        }
        else if(this.taskType.equals("Mechanical cut")){
            color = "#0b2626";
        }
        else if(this.taskType.equals("Mixing or stabilization")){
            color = "#070ea3";
        }
        else if(this.taskType.equals("Other")){
            color = "#61638c";
        }
        else if(this.taskType.equals("Patching")){
            color = "#262740";
        }
        else if(this.taskType.equals("Paving")){
            color = "#32323d";
        }
        else if(this.taskType.equals("Ploughing and slush removal")){
            color = "#cc58b1";
        }
        else if(this.taskType.equals("Preventing melting water problems")){
            color = "#5e3855";
        }
        else if(this.taskType.equals("Removal of bulge ice")){
            color = "#e8d85d";
        }
        else if(this.taskType.equals("Reshaping gravel road surface")){
            color = "#423b05";
        }
        else if(this.taskType.equals("Road inspections")){
            color = "#d9d3a9";
        }
        else if(this.taskType.equals("Road markings")){
            color = "#becf04";
        }
        else if(this.taskType.equals("Road state checking")){
            color = "#04cf69";
        }
        else if(this.taskType.equals("Safety equipment")){
            color = "#104f2f";
        }
        else if(this.taskType.equals("Salting")){
            color = "#79aaf2";
        }
        else if(this.taskType.equals("Snow-ploughing sticks and snow fences")){
            color = "#4e77b5";
            this.taskTooltipXOffset += 50;
        }
        else if(this.taskType.equals("Spot sanding")){
            color = "#732306";
        }
        else if(this.taskType.equals("Spreading of crush")){
            color = "#e36d42";
        }
        else if(this.taskType.equals("Transfer of snow")){
            color = "#7df0a1";
        }
        else if(this.taskType.equals("Unknown")){
            color = "#910385";
        }
        else{
            color = "#1f181b";
        }
        
        // Set width and height of the rectangle
        setWidth(taskDuration*rectangleWidth);
        setHeight(rectangleHeight);

        // Set color
        setFill(Color.web(color));
        setOpacity(1.0);
        setArcWidth(20.0); 
        setArcHeight(20.0);


        
        
        int index = 0;
        
        ArrayList<String> tasks = task.getTasks();
        
        String taskDescription = tasks.get(0);
        
        
        for(int i = 1; i < tasks.size(); ++i){
            taskDescription += " / " + tasks.get(i);
        }
        

        String taskTime = this.startTime.split(" ")[1] + " - " + this.endTime.split(" ")[1];

        Tooltip tooltip = new Tooltip(taskDescription + "\n" + taskTime);
        tooltip.setShowDelay(Duration.seconds(0));
        tooltip.setTextAlignment(TextAlignment.CENTER);
        tooltip.setStyle("-fx-text-fill: black; "
                        + "-fx-background-color: white; "
                        + "-fx-border-color: black; "
                        + "-fx-content-display: left; "
                        + "-fx-wrap-text: true; "
                        + "-fx-text-fontsize: true; "
                        + "-fx-padding: 5 5 12 5; "
                        + "-fx-shape: \"M 400 300 L 450 250 L 600 250 L 600 100 L 200 100 L 200 250 L 350 250 L 400 300 z\";" );

        Tooltip.install(this, tooltip);


        // set tooltip position related to cursor
        setOnMouseMoved(e -> {
            tooltip.show((Node) e.getSource(), 
                    e.getScreenX() - this.taskTooltipXOffset, 
                    e.getScreenY() - this.taskTooltipYOffset);
        });
    }
    
    
    public int getStartTime(){
        return this.start;
    }
    
    public String getTaskType(){
        return this.taskType;
    }
}

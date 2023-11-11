/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.thebigfour.drivingconditions;

import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.util.Pair;

/**
 *
 * @author Aaro
 */
public class TaskChart extends AnchorPane {

    protected AnchorPane chart;
    protected Label leftLabel;
    protected Label rightLabel;

    protected int xAxisStart;
    protected int xAxisEnd;
    protected int xAxisLength;
    public double hourLineGapWidth;
    private double taskRectangleMaxHeight;
    private int yAxisLevel = 0;
    

    protected double chartWidthOffset = 26.2;
    protected double widthOfChart = 305.0;
    protected double heightOfChart = 158.0;
    protected double containerPaneWidth = 350;
    protected double containerPaneHeight = 150.0;
    

    protected ArrayList<MaintenanceTask> maintenanceTasks;
    protected ArrayList<TaskRectangle> taskRectangles;

    protected String date;

    public TaskChart() {

        taskRectangles = new ArrayList<>();

        setMinWidth(this.containerPaneWidth);
        setPrefWidth(this.containerPaneWidth);
        setMaxWidth(this.containerPaneWidth);
        setMinHeight(this.containerPaneHeight);
        setMaxHeight(this.containerPaneHeight);
        setPrefHeight(this.containerPaneHeight);

        // width and height of the outer pane
        
        //setStyle("-fx-border-color: black; -fx-border-width: 1px 1px 1px 1px");
        

        // chart x-axis labels
        this.leftLabel = new Label("00:00");
        this.rightLabel = new Label("24:00");

        getChildren().add(this.leftLabel);
        AnchorPane.setBottomAnchor(this.leftLabel, 0.0);
        AnchorPane.setLeftAnchor(this.leftLabel, 3.0);
        getChildren().add(this.rightLabel);
        AnchorPane.setBottomAnchor(this.rightLabel, 0.0);
        AnchorPane.setRightAnchor(this.rightLabel, 4.0);

    }

    public void createChart(String date, ArrayList<MaintenanceTask> daysTasks) {

        // save the tasks
        this.date = date;
        this.maintenanceTasks = daysTasks;
        drawChart();
        populateChart(daysTasks);
        
    }

    public void drawChart() {

        // area for drawing the chart
        this.chart = new AnchorPane();

        getChildren().add(this.chart);

        AnchorPane.setTopAnchor(this.chart, 0.0);
        AnchorPane.setLeftAnchor(this.chart, 15.0);
        AnchorPane.setRightAnchor(this.chart, 15.0);

        // set the width and heigth to stop resizing
        this.chart.setMinWidth(this.widthOfChart);
        this.chart.setPrefWidth(this.widthOfChart);
        this.chart.setMaxWidth(this.widthOfChart);
        this.chart.setMinHeight(this.heightOfChart);
        this.chart.setMaxHeight(this.heightOfChart);
        this.chart.setPrefHeight(this.heightOfChart);

        this.chart.setStyle("-fx-border-color: black; -fx-border-width: 1px 1px 1px 1px");

    }

    protected void populateChart(ArrayList<MaintenanceTask> daysTasks) {

        this.taskRectangleMaxHeight = this.heightOfChart / daysTasks.size();

        Pair<Integer, Integer> startEndPair = xAxisLength(daysTasks);
        this.xAxisStart = startEndPair.getKey();
        this.xAxisEnd = startEndPair.getValue();

        this.xAxisLength = this.xAxisEnd - this.xAxisStart;

        // The chart lines
        try {
            this.hourLineGapWidth = (this.widthOfChart + this.chartWidthOffset) / this.xAxisLength;
        } catch (ArithmeticException ae) {
            this.xAxisLength = 24;
        }

        // The chart lines
        for (int i = 1; i < this.xAxisLength; i++) {
            Line line = new Line(i * this.hourLineGapWidth, 1.0, i * this.hourLineGapWidth, this.heightOfChart - 1.0);
            this.chart.getChildren().add(line);
            AnchorPane.setBottomAnchor(line, 0.0);
            line.setOpacity(0.8);
            line.toBack();
        }

        // chart x-axis labels update
        this.leftLabel.setText(Integer.toString(xAxisStart) + ":00");
        this.rightLabel.setText(Integer.toString(xAxisEnd) + ":00");

        // create the rectangles for the tasks
        daysTasks.forEach(task -> {
            addTask(task);
        });

        // FOR TESTING RESIZING, REMOVE BEFORE RETURNING -------------------
//        setOnMouseMoved(e -> {
//            System.out.println("Container Width: " + getWidth());
//            System.out.println("Chart width: " + this.chart.getWidth());
//        });
    }

    private void addTask(MaintenanceTask task) {

        // rectangles start at 25.0 but once there are more tasks it shrinks
        double rectangleHeight = 25.0;

        if (this.taskRectangleMaxHeight < rectangleHeight) {
            rectangleHeight = this.taskRectangleMaxHeight;
        }

        TaskRectangle taskRectangle = new TaskRectangle(task, rectangleHeight, this.hourLineGapWidth);

        // add the new rectangle to the list
        this.taskRectangles.add(taskRectangle);

        // Set position
        this.chart.getChildren().add(taskRectangle);
        AnchorPane.setLeftAnchor(taskRectangle, (taskRectangle.getStartTime() - this.xAxisStart) * this.hourLineGapWidth);
        AnchorPane.setBottomAnchor(taskRectangle, this.yAxisLevel * rectangleHeight);
        ++this.yAxisLevel;

    }

    // redraw the chart when the filter parameters are changed
    public void redrawChart(ObservableList<String> taskTypeList) {
        
        if (taskTypeList == null){
            showAllTasks();
            return;
        }
        
        try {
            // remove the chart
            getChildren().remove(this.chart);
            this.taskRectangles.clear();
            this.yAxisLevel = 0;

            // filter the maintenance tasks based on the taskTypeList
            ArrayList<MaintenanceTask> filteredTaskList = new ArrayList<>();

            for (MaintenanceTask task : this.maintenanceTasks) {
                for (String taskType : taskTypeList) {
                    if (task.getTasks().get(0).equals(taskType)) {
                        filteredTaskList.add(task);
                    }
                }
            }

            // recreate the chart with the new tasks included
            drawChart();
            populateChart(filteredTaskList);

        } catch (Exception e) {
            System.out.println("Attempting to redraw the chart in TaskChart");
            e.printStackTrace();
        }
    }

    public void showAllTasks() {
        // remove the chart
        getChildren().remove(this.chart);

        // bookkeeping
        this.taskRectangles.clear();
        this.yAxisLevel = 0;

        // redraw and repopulate
        drawChart();
        populateChart(this.maintenanceTasks);
    }

    protected Pair<Integer, Integer> xAxisLength(ArrayList<MaintenanceTask> daysTasks) {

        int earliestStartTime = 25;
        int latestEndTime = -1;
        int start = 0;
        int end = 0;

        Pair<Integer, Integer> range;

        for (MaintenanceTask task : daysTasks) {

            start = Integer.parseInt(task.getStartTime().substring(11, 13));
            end = Integer.parseInt(task.getEndTime().substring(11, 13));

            // get the earliest start time for that day
            if (start < earliestStartTime) {
                earliestStartTime = start;
            }
            // find the lasted end time of the day
            if (end > latestEndTime) {
                latestEndTime = end;
            }

        }

        if (earliestStartTime - 1 > -1) {
            earliestStartTime -= 1;
        }

        if (latestEndTime + 1 < 25) {
            latestEndTime += 1;
        }

        range = new Pair<>(earliestStartTime, latestEndTime);
        
        return range;
    }

    public String getDate() {
        return this.date;
    }
    
    // set the chart offset for drawing
    public void setOffset(double newOffset){
        // set the chart width offset
        this.chartWidthOffset = newOffset;
    }
    
   

}

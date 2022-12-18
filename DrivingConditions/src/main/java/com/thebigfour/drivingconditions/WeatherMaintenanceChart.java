package com.thebigfour.drivingconditions;

import java.util.ArrayList;
import java.util.Objects;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Pair;

/**
 *
 * @author Mikko
 */
public class WeatherMaintenanceChart extends TaskChart {
    
    private Double yAxisStart;
    private Double yAxisEnd;
    private double yAxisLength;
    private double ObservationLineGapWidth;
    private double chartHeightOffset = 0.0;
    private ArrayList<Circle> observationPoints;
    private ArrayList<Label> yAxisTicksLabelList;
    private Double lowestTick;
    private Double highestTick;
    private Label unitLabel;
    private boolean taskChartsCreated = false;
    
    
    public WeatherMaintenanceChart(){
        
        super();
        
        setMinWidth(this.containerPaneWidth);
        setPrefWidth(this.containerPaneWidth);
        setMaxWidth(this.containerPaneWidth);
        setMinHeight(this.containerPaneHeight);
        setMaxHeight(this.containerPaneHeight);
        setPrefHeight(this.containerPaneHeight);
        
        this.observationPoints = new ArrayList<>();
        this.yAxisTicksLabelList = new ArrayList<>();
        
        this.chartWidthOffset = 24;
        
        AnchorPane.setLeftAnchor(this.leftLabel, 7.0);
        AnchorPane.setRightAnchor(this.rightLabel, 2.0);
        
        this.unitLabel = new Label("°C");
        getChildren().add(this.unitLabel);
        AnchorPane.setLeftAnchor(this.unitLabel, 5.0);
        AnchorPane.setTopAnchor(this.unitLabel, 0.0);
        
        
    }
    
    @Override
    public void drawChart() {

        // area for drawing the chart
        this.chart = new AnchorPane();

        getChildren().add(this.chart);

        AnchorPane.setTopAnchor(this.chart, 0.0);
        AnchorPane.setLeftAnchor(this.chart, 27.0);
        AnchorPane.setRightAnchor(this.chart, 3.0);

        // set the width and heigth to stop resizing
        this.chart.setMinWidth(this.widthOfChart);
        this.chart.setPrefWidth(this.widthOfChart);
        this.chart.setMaxWidth(this.widthOfChart);
        this.chart.setMinHeight(this.heightOfChart);
        this.chart.setMaxHeight(this.heightOfChart);
        this.chart.setPrefHeight(this.heightOfChart);

        this.chart.setStyle("-fx-border-color: black; -fx-border-width: 1px 1px 1px 1px");
        
    }
    
    @Override
    public void createChart(String date, ArrayList<MaintenanceTask> daysTasks) {
        
        if(daysTasks.isEmpty() || daysTasks == null){
            this.date = date;
            this.maintenanceTasks = null;
            drawChart();
            return;
        }

        // save the tasks
        this.date = date;
        this.maintenanceTasks = daysTasks;
        drawChart();
        populateChart(daysTasks);
        
        taskChartsCreated = true;
    }
    
    public void drawHorizontalLines(){
        
        // The chart y-axis lines
        try {
            this.ObservationLineGapWidth = (this.heightOfChart + this.chartHeightOffset) / 7;
        } catch (ArithmeticException ae) {
            this.yAxisLength = 60;
        }

        // The horizontal chart lines
        for (int i = 1; i < 7; i++) {
            
            Line line = new Line(0.0, i * this.ObservationLineGapWidth, this.widthOfChart + this.chartWidthOffset, i * this.ObservationLineGapWidth);
            this.chart.getChildren().add(line);
            
            line.setOpacity(0.8);
            line.toBack();
        }
    }
    
    public void drawVerticalLines(){
        
        // The chart x-axis lines

        this.xAxisStart = 0;
        this.xAxisEnd = 24;

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
    }
    // params: type, 0: forecast 1: observation
    public void populateWeatherInfo(ArrayList<? extends DataPoint> observationsList, Integer type) {
        
        if (!taskChartsCreated){
            
            drawChart();
            drawVerticalLines();
        }

        Pair<Double, Double> yStartEndPair = yAxisLength(observationsList);
        this.yAxisStart = yStartEndPair.getKey();
        this.yAxisEnd = yStartEndPair.getValue();

        this.yAxisLength = this.yAxisEnd - this.yAxisStart;

        drawHorizontalLines();
        
        placeYAxisTickLabels(observationsList);
        
        // create the circles for observations
        observationsList.forEach(obs -> {
            
            // place circles on the chart
            addObservation(obs, type);
            
        });
        
        addLinesToPoints(type);
        
        //addXTickLabels(observationsList); // for testing
        

        // ----------------- FOR TESTING RESIZING -------------------
//        setOnMouseMoved(e -> {
//            System.out.println("Container Width: " + getWidth());
//            System.out.println("Chart width: " + this.chart.getWidth());
//            System.out.println("Chart height: " + this.chart.getHeight());
//        });
    }

    private void addObservation(DataPoint dp, Integer type) {
        
        Circle newObsPoint = new Circle();
        
        
        newObsPoint.setRadius(2.0f);
        
        if(type == 0){
            
            // forecasts are marked as red
            newObsPoint.setFill(Color.RED);
        }
        else if(type == 1){
            
            // observations are marked as red
            newObsPoint.setFill(Color.BLUE);
        }
        
        
        
        // add the new circle to the list
        this.observationPoints.add(newObsPoint);
        
        //System.out.println(dp.getTime().toString() + " " + dp.getTemperature());
        
        Double time = Double.valueOf(dp.getTime().toString().substring(0, 2));
        
        if(time == 0.0){
            time = 24.0;
        }
        
        // set the points position
        newObsPoint.setCenterX(time * this.hourLineGapWidth);
        newObsPoint.setCenterY(reverse_map(Double.valueOf(dp.getTemperature()), this.lowestTick, this.highestTick, 158.0, 0.0));

        this.chart.getChildren().add(newObsPoint);

    }
    
    private Double reverse_map(Double x, Double in_min, Double in_max, Double out_min, Double out_max){
        
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
    
    
    private void addLinesToPoints(Integer type){
        
        Double lastPointEndX = 0.0;
        boolean isFirstRound = true;
        
        // The lines connecting the points
        for (int i = 0; i < this.observationPoints.size() - 1 ; i++) {
            
            Double startX = observationPoints.get(i).getCenterX();
            Double startY = observationPoints.get(i).getCenterY();
            Double endX = observationPoints.get(i + 1).getCenterX();
            Double endY = observationPoints.get(i + 1).getCenterY();
            
            if(Objects.equals(startX, endX)){
                continue;
            }
            if(startX > endX){
                    continue;
            }
            
                
            Line line = new Line(startX, startY, endX, endY);
            

            this.chart.getChildren().add(line);
            
            if(type == 0){
            
                // forecasts are marked as red
                line.setStroke(Color.RED);
            }
            else if(type == 1){

                // observations are marked as red
                line.setStroke(Color.BLUE);
            }
            
            // send the lines to the back so they don't overlap with the tasks or obsersvations
            line.toBack(); 
            
            // keep track where the last line was drawn
            lastPointEndX = endX;
            isFirstRound = false;
        }
        
    }
    
    /**
     * @brief get the yaxis low and high points
     * @param observationsList one days observations (24)
     * @return pair containing lowest and highest observations
     */
    private Pair<Double, Double> yAxisLength(ArrayList<? extends DataPoint> observationsList) {

        Double highestObservation = -999.0;
        Double lowestObservation = 999.0;


        Pair<Double, Double> range;
        
        Double temp;

        for (DataPoint obs : observationsList) {
            
            try{
                
                temp = Double.valueOf(obs.getTemperature());

                // get the lowest obs for that day
                if (temp < lowestObservation) {
                    lowestObservation = temp;
                }
                // find the highest obs of the day
                if (temp > highestObservation) {
                    highestObservation = temp;
                }

            }catch(NullPointerException npe){
                continue;
            }
            
            
        }

        range = new Pair<>(lowestObservation, highestObservation);
        
        return range;
    }
    
    @Override
    protected Pair<Integer, Integer> xAxisLength(ArrayList<MaintenanceTask> daysTasks) {
        
        Pair<Integer, Integer> range;
        range = new Pair<>(0, 24);
        
        return range;

    }
    
    private void addXTickLabels(ArrayList<? extends DataPoint> observationsList) {
        
        Integer index = 0;
        
        for(var obs : observationsList){
            
            Label xticklabel = new Label(index.toString());
            
            AnchorPane.setLeftAnchor(xticklabel, Integer.parseInt(obs.getTime().toString()) * this.hourLineGapWidth);
            AnchorPane.setBottomAnchor(xticklabel, 5.0);
            
            this.chart.getChildren().add(xticklabel);
            
            ++index;
            
        }
    }
    
    private void placeYAxisTickLabels(ArrayList<? extends DataPoint> observationsList){
        

        // finding the highest and the lowest measurements
        
        Double highestObs = -999.0;
        Double lowestObs = 999.0;
        
        for(var obs : observationsList){
            
            Double temp = Double.valueOf(obs.getTemperature());
            
            if (temp < lowestObs) {
                lowestObs = temp;
            }

            if (temp > highestObs) {
                highestObs = temp;
            }
        }
        
        // calculate the absolute distance between the highest and the lowest
        
        Double distance = 0.0;
        
        // if both are positive
        if(highestObs > 0 && lowestObs > 0){
            
            distance = highestObs - lowestObs;
            
        }
        
        // if lowest is negative
        if(highestObs > 0 && lowestObs < 0){
            
            distance = highestObs + ((lowestObs < 0) ? -lowestObs : lowestObs);
            
        }
        
        // if both are negative
        if(highestObs < 0 && lowestObs < 0){
            
            distance = -(lowestObs - highestObs);
            
        }
                
        Double yTickGap = distance / 5;
        
        this.lowestTick = lowestObs - yTickGap;
        this.highestTick = highestObs + yTickGap;

        for(int i = 1; i < 7; ++i){
            
            Double tick = lowestObs + (i - 1) * yTickGap;
            Label xticklabel;
            
            if( tick.toString().length() == 3){
                xticklabel = new Label(tick.toString().substring(0, 3));
            }else{
                xticklabel = new Label(tick.toString().substring(0, 4));
            }
            
            xticklabel.setStyle("-fx-font: " + 10 +" System; ");

            getChildren().add(xticklabel);

            AnchorPane.setLeftAnchor(xticklabel, 5.0);
            AnchorPane.setBottomAnchor(xticklabel, 11.0 + this.ObservationLineGapWidth * i);

        }
    }
    
    
    
} 


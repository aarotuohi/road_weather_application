/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.thebigfour.drivingconditions;

import java.util.ArrayList;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


/**
 *
 * @author Mikko
 */
public class WeatherRoadConditionChart extends WeatherMaintenanceChart{
    
    ArrayList<Rectangle> conditionRectangles = new ArrayList<>();
    
    public WeatherRoadConditionChart(){
        
        super();
        
        this.hourLineGapWidth = (this.widthOfChart + 24) / 24;
        
    }
    
    
    public void createConditionChart(ArrayList<RoadCondition> conditions) {
        

        // save the tasks
        this.date = conditions.get(0).getTime().substring(0, 10);

        drawChart();
        
        // method for drawing squares for road conditions
        drawConditionSquares(conditions);
        
    }
    
    private void drawConditionSquares(ArrayList<RoadCondition> conditions){
        
        int startTime = 0;
        int endTime = 0;
        
        double forcastDuration = 0.0; // endTime - startTime
        
        double rectangleWidth = 0;
        double rectangleHeight = 125.0;
        
        
        for (int i = 0; i < conditions.size() - 1; ++i){
            
            
//            System.out.println(conditions.get(i).getTime());
//            System.out.println(conditions.get(i + 1).getTime());
            
            String startDate = conditions.get(i).getTime().substring(0, 10);
            String endDate = conditions.get(i + 1).getTime().substring(0, 10);
            

            startTime = Integer.parseInt(conditions.get(i).getTime()
                        .substring(11, 13));
            
            endTime = Integer.parseInt(
                    conditions.get(i + 1).getTime().substring(11, 13));
            
            if(!startDate.equals(endDate) && endTime != 0){
                return;
            }
            
            forcastDuration = endTime - startTime;
            
            rectangleWidth = forcastDuration * this.hourLineGapWidth;
            
            
            Rectangle conditionRectangle = new Rectangle(rectangleWidth, rectangleHeight);
            
            //setFill(Color.web(color));
            
            conditionRectangle.setArcWidth(10.0); 
            conditionRectangle.setArcHeight(10.0);
            conditionRectangle.setOpacity(0.6);
            
            if(conditions.get(i + 1).getOverallRoadCondition().equals("NORMAL_CONDITION")){
                conditionRectangle.setFill(Color.LIGHTGREEN);
            }else if(conditions.get(i + 1).getOverallRoadCondition().equals("POOR_CONDITION")){
                conditionRectangle.setFill(Color.RED);
            }
            
            
            
            conditionRectangles.add(conditionRectangle);
            
            this.chart.getChildren().add(conditionRectangle);
            
            AnchorPane.setLeftAnchor(conditionRectangle, 
                    startTime * this.hourLineGapWidth );

            AnchorPane.setBottomAnchor(conditionRectangle, 0.0);
            
        }
    }
}

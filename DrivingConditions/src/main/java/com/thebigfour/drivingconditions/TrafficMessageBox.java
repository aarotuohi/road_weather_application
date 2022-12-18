/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.thebigfour.drivingconditions;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author Mikko
 */
public class TrafficMessageBox extends VBox {
    
    private Text timeText;
    private Text roadText;
    private Text placeText;
    private Text infoText;
    private String labelFontSize = "12";
    private int boxWidth = 200;
    private int boxHeight = 165;
    private int wrappingWidth = 270;
    
    public TrafficMessageBox(){
        
        // initialize the box
        setSpacing(10);
        
        setAlignment(Pos.CENTER);
        setPadding(new Insets(5, 5, 5, 5));
        
        
        // initialize the labels
        timeText = new Text("Time");
        timeText.setStyle("-fx-font: " + labelFontSize +" System; ");
        timeText.setWrappingWidth(wrappingWidth);
        timeText.setTextAlignment(TextAlignment.CENTER);
        
        
        roadText = new Text("Road");
        roadText.setStyle("-fx-font: " + labelFontSize +" System; ");
        roadText.setWrappingWidth(wrappingWidth);
        roadText.setTextAlignment(TextAlignment.CENTER);
        
        placeText = new Text("Place");
        placeText.setStyle("-fx-font: " + labelFontSize +" System;");
        placeText.setWrappingWidth(wrappingWidth);
        placeText.setTextAlignment(TextAlignment.CENTER);
        
        infoText = new Text("Info");
        infoText.setStyle("-fx-font: " + labelFontSize +" System;");
        infoText.setWrappingWidth(wrappingWidth);
        infoText.setTextAlignment(TextAlignment.CENTER);
        
        // add labels to the VBox
        getChildren().add(0, timeText);
        getChildren().add(1, roadText);
        getChildren().add(2, placeText);
        getChildren().add(3, infoText);
        
        // setting the size of the box so it doesn'tt resize with messages
        setMinWidth(boxWidth);
        setPrefWidth(boxWidth);
        setMaxWidth(VBox.USE_COMPUTED_SIZE);
        setMinHeight(boxHeight);
        setMaxHeight(boxHeight);
        setPrefHeight(boxHeight);
        
        // set style to see the borders (FOR TESTING)
        // setStyle("-fx-border-color: black; -fx-border-width: 1px 1px 1px 1px");
    }
    
    public void addMessage(String time, String road, String place, String info){
        
        this.timeText.setText(time);
        this.roadText.setText(road);
        this.placeText.setText(place);
        this.infoText.setText(info);
        
    }
    
    public void setBigMode(boolean state){
        if(state){
            labelFontSize = "18";
            wrappingWidth = 800;
         
            timeText.setStyle("-fx-font: " + labelFontSize +" System; ");
            timeText.setWrappingWidth(wrappingWidth);
            timeText.setTextAlignment(TextAlignment.CENTER);

            roadText.setStyle("-fx-font: " + labelFontSize +" System; ");
            roadText.setWrappingWidth(wrappingWidth);
            roadText.setTextAlignment(TextAlignment.CENTER);

            placeText.setStyle("-fx-font: " + labelFontSize +" System;");
            placeText.setWrappingWidth(wrappingWidth);
            placeText.setTextAlignment(TextAlignment.CENTER);

            infoText.setStyle("-fx-font: " + labelFontSize +" System;");
            infoText.setWrappingWidth(wrappingWidth);
            infoText.setTextAlignment(TextAlignment.CENTER);
        
        }else{
            labelFontSize = "12";
            wrappingWidth = 270;
            
            timeText.setStyle("-fx-font: " + labelFontSize +" System; ");
            timeText.setWrappingWidth(wrappingWidth);
            timeText.setTextAlignment(TextAlignment.CENTER);

            roadText.setStyle("-fx-font: " + labelFontSize +" System; ");
            roadText.setWrappingWidth(wrappingWidth);
            roadText.setTextAlignment(TextAlignment.CENTER);

            placeText.setStyle("-fx-font: " + labelFontSize +" System;");
            placeText.setWrappingWidth(wrappingWidth);
            placeText.setTextAlignment(TextAlignment.CENTER);

            infoText.setStyle("-fx-font: " + labelFontSize +" System;");
            infoText.setWrappingWidth(wrappingWidth);
            infoText.setTextAlignment(TextAlignment.CENTER);
        }
    }
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.thebigfour.drivingconditions;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;



public class ChartScroller extends AnchorPane {
    
    private ArrayList<Region> dayChartList;
    private ObservableList<String> filterList;
    private Label contentNumberLabel;
    private Label dateLabel;
    private ImageView nextChartImageView;
    private ImageView prevChartImageView;
    private boolean chartShowing = false;
    private int currentChartShowing = -1;
    private double topAnchor = 30.0;
    private double bottomAnchor = 2.0;
    private double leftAnchor = 31.0;
    private double rightAnchor = 31.0;
    private int buttonSides = 37;
    private double buttonHeight = 90.0;
    private int width = 400;
    private int height = 210;
    private Label infoLabel;
    private BorderPane taskAveragesBorderPane;
    private HashMap<String, Double> averageMap;
    private boolean infoIconVisibility = false;
    
    
    public ChartScroller(){
        
        //setStyle("-fx-border-color: black; -fx-border-width: 1px 1px 1px 1px");

        this.dayChartList = new ArrayList<>();
        
        // Setting up the outside border
        setMinWidth(this.width);
        setMinHeight(this.height);
        setMaxWidth(AnchorPane.USE_COMPUTED_SIZE);
        setMaxHeight(this.height);
        setPrefWidth(this.width);
        setPrefHeight(this.height);
        
        // initialize the label
        this.contentNumberLabel = new Label("0/0");
        this.contentNumberLabel.setStyle("-fx-font: " + 14 +" System;"
                                        + "-fx-font-weight: bold");
        
        getChildren().add(this.contentNumberLabel);
        AnchorPane.setTopAnchor(this.contentNumberLabel, 2.0 );
        AnchorPane.setRightAnchor(this.contentNumberLabel, 5.0);
        
        
        Image img = new Image(getClass().getResource("RightTriangle.png").toString());
        
        this.nextChartImageView = new ImageView(img);

        this.nextChartImageView.setOnMouseClicked(e -> {
            showNextChart();
        });

        this.nextChartImageView.setFitWidth(this.buttonSides);
        this.nextChartImageView.setFitHeight(this.buttonSides);

        getChildren().add(this.nextChartImageView);
        setRightAnchor(this.nextChartImageView, 0.0);
        setTopAnchor(this.nextChartImageView, this.buttonHeight);

        this.prevChartImageView = new ImageView(img);

        this.prevChartImageView.setOnMouseClicked(e -> {
            showPrevChart();
        });

        this.prevChartImageView.setRotate(180);

        this.prevChartImageView.setFitWidth(this.buttonSides);
        this.prevChartImageView.setFitHeight(this.buttonSides);
        
        this.prevChartImageView.setVisible(false);

        getChildren().add(this.prevChartImageView);
        setLeftAnchor(this.prevChartImageView, 0.0);
        setTopAnchor(this.prevChartImageView, this.buttonHeight);
        
        if (this.dayChartList.isEmpty()){
            this.nextChartImageView.setVisible(false);
            this.contentNumberLabel.setText("");
                
        }
        
        
        
    }
    
    public void addNewChart(Region pane){
        
        // add it to the list of charts
        this.dayChartList.add(pane);


        // show the new chart if nothing is showing yet
        if (!this.chartShowing){
            if(this.currentChartShowing == -1){
                this.currentChartShowing = 0;
            }
            
            getChildren().add(pane);
            AnchorPane.setTopAnchor(pane, this.topAnchor);
            AnchorPane.setBottomAnchor(pane, this.bottomAnchor);
            AnchorPane.setRightAnchor(pane, this.rightAnchor);
            AnchorPane.setLeftAnchor(pane, this.leftAnchor);
            this.chartShowing = true;
            
            String date = "";
            try{

                date = ((TaskChart) pane).getDate();

                this.dateLabel = new Label(date);
                this.dateLabel.setStyle("-fx-font: " + 15 +" System;"
                                                + "-fx-font-weight: bold");
                
                this.dateLabel.setAlignment(Pos.CENTER);
                getChildren().add(this.dateLabel);
                setLeftAnchor(this.dateLabel, 0.0);
                setRightAnchor(this.dateLabel, 0.0);
                setTopAnchor(this.dateLabel, 2.0);
            
            }catch(Exception e){
                // no label for other types
            }
        }
        
        // set the button visible
        // show the "button" to move back
            if(this.dayChartList.size() > 1){
                this.nextChartImageView.setVisible(true);
            }
        
        this.contentNumberLabel.setText((this.currentChartShowing + 1)  
                                        + "/" + this.dayChartList.size());
    }
    

    public void redrawContent(ObservableList<String> TypeList, boolean showAll){
        
        for(var chart : this.dayChartList){
            try{
                TaskChart taskChart = (TaskChart) chart;
                if (showAll){
                    taskChart.showAllTasks();
                }else{
                    taskChart.redrawChart(TypeList);
                }
            
            }catch(Exception e){
                System.out.println("Attempting to redraw content in chart" + "\n" 
                        + TypeList);
                e.printStackTrace();
            }
        }
    }
    
    public void setChartWidthOffset(double newOffset){
        for(var chart : this.dayChartList){
            try{
                TaskChart taskChart = (TaskChart) chart;

                taskChart.setOffset(newOffset);
                
            }catch(Exception e){
                System.out.println("Something went wrong with the resizing");
            }
        }
    }
     
  
    private void showNextChart(){

        // get the current chart showing
        Region oldChart;
        Region NewChart;

        // show the next one one the list
        // check if there are charts left
        if(this.currentChartShowing + 1 < this.dayChartList.size()){

            oldChart = this.dayChartList.get(this.currentChartShowing);
            NewChart = this.dayChartList.get(this.currentChartShowing + 1);


            // remove the current chart from the pane
            getChildren().remove(oldChart);
            String date = ""; 
            
            try{
                date = ((TaskChart) NewChart).getDate();
                this.dateLabel.setText(date);
            }catch (Exception e){
            }
            
            
            getChildren().add(NewChart);

            AnchorPane.setTopAnchor(NewChart, this.topAnchor);
            AnchorPane.setBottomAnchor(NewChart, this.bottomAnchor);
            AnchorPane.setRightAnchor(NewChart, this.rightAnchor);
            AnchorPane.setLeftAnchor(NewChart, this.leftAnchor);

            // keep track which chart if showing currently
            this.currentChartShowing += 1;

            // show the "button" to move back
            if(this.dayChartList.size() > 1){
                this.prevChartImageView.setVisible(true);
            }

            // hide the "button" when no more chart are left
            if(this.currentChartShowing == this.dayChartList.size() - 1){
                this.nextChartImageView.setVisible(false);
            }
            
            this.contentNumberLabel.setText((this.currentChartShowing + 1)  
                                        + "/" + this.dayChartList.size());

        }
    }
    
    private void showPrevChart(){
        
        // get the current chart showing
        Region oldChart;
        Region NewChart;
        
        // show the next one one the list
        // check if there are charts left
        if(this.currentChartShowing - 1 >= 0){
            
            oldChart = this.dayChartList.get(this.currentChartShowing);
            NewChart = this.dayChartList.get(this.currentChartShowing - 1);
            
            // remove the current chart from the pane
            getChildren().remove(oldChart);
            
            
            String date = ""; 
            
            try{
                date = ((TaskChart) NewChart).getDate();
                this.dateLabel.setText(date);
            }catch (Exception e){
            }
            
            // add the new one
            getChildren().add(NewChart);
            
            AnchorPane.setTopAnchor(NewChart, this.topAnchor);
            AnchorPane.setBottomAnchor(NewChart, this.bottomAnchor);
            AnchorPane.setRightAnchor(NewChart, this.rightAnchor);
            AnchorPane.setLeftAnchor(NewChart, this.leftAnchor);

            // keep track which chart if showing currently
            this.currentChartShowing -= 1;
            
            // show the "button" to move forward
            if(this.dayChartList.size() > 1){
                this.nextChartImageView.setVisible(true);
            }
            // hide the button if we are at the beginning of the list
            if (this.currentChartShowing == 0){
                this.prevChartImageView.setVisible(false);  
            }
            
            if (dayChartList.isEmpty()){
                this.nextChartImageView.setVisible(false);
            }
            
            this.contentNumberLabel.setText((this.currentChartShowing + 1)  
                                        + "/" + this.dayChartList.size());
            
        }     

    }
    
    public void removeAllCharts(){
        
        
        // remove the current chart if one is showing
        if (this.currentChartShowing > -1){
            
            // remove it from the pane
            getChildren().remove(this.dayChartList.get(this.currentChartShowing));
            
            // set now chart to show
            this.currentChartShowing = -1;
            
            // no chart currently showing
            this.chartShowing = false;
        }
        
        // empty the memory of tasks
        this.dayChartList.clear();
        
        if (this.dateLabel != null){
            
            this.dateLabel.setText("");
        }
        
        this.nextChartImageView.setVisible(false);
        this.prevChartImageView.setVisible(false);
    }
    
    // move the buttons vertically
    public void setButtonHeight(double height){
        this.buttonHeight = height;
        // setRightAnchor(nextChartImageView, 0.0);
        setTopAnchor(this.nextChartImageView, this.buttonHeight);
        setTopAnchor(this.prevChartImageView, this.buttonHeight);
    }
    
    public void resizeText(boolean state){
        
        for(var chart : this.dayChartList){
            try{
                TrafficMessageBox messageBox = (TrafficMessageBox) chart;

                messageBox.setBigMode(state);
                
            }catch(Exception e){
                System.out.println("Something went wrong with the setting bigmode");
            }
        }
    }
    
    private GridPane constructTaskAverageGridPane(){
        
        // initialize the gridpane
        GridPane gridpane = new GridPane();
        
        if(this.averageMap == null || this.averageMap.isEmpty()){
            return gridpane;
        }
        
        gridpane.setPadding(new Insets(7, 7, 7, 7));
        gridpane.setVgap(5);
        gridpane.setHgap(5);
        
        int maxRow = 0;
        
        int column = 0;
        int row = 0;
        
        
        if (this.averageMap.size() < 6){
            
            maxRow = 5;
            
        }else if(this.averageMap.size() < 10){
            if (this.averageMap.size() % 2 == 0){
                
                maxRow = (this.averageMap.size() / 2);
                
            }else{
                maxRow = 5;
            }
        }else{
            
        }
        
        for (Map.Entry<String, Double> set : this.averageMap.entrySet()) {
            
            String[] arrOfStr = set.getValue().toString().split("\\.");
            String fraction = "";

            if(arrOfStr[1].length() < 2){
                fraction = arrOfStr[1].substring(0, 1);
            }else{
                fraction = arrOfStr[1].substring(0, 2);
            }
            
            
            String tmpType = set.getKey() + " " + arrOfStr[0] + "." + fraction;
            
            Label taskLabel = new Label(tmpType);
            
            gridpane.add(taskLabel, column, row);
            
            ++row;
            
            if(row >= maxRow){
                row = 0;
                ++column;
            }
 
        }
        
        return gridpane;

    }
    
    private BorderPane constructPopUpBorderPane(){
        
        BorderPane bp = new BorderPane();
        
        bp.setStyle("-fx-background-color: white;" +
                    "-fx-border-color: black;" +
                    "-fx-background-radius: 30;" +
                    "-fx-border-radius: 30;");
        
        Label popUpLabel = new Label("Avg. number of tasks per type");
        
        popUpLabel.setFont(new Font("System", 13));
        popUpLabel.setStyle("-fx-font-weight: bold");

        AnchorPane.setRightAnchor(bp, 42.0);
        AnchorPane.setBottomAnchor(bp, 30.0);
        
        bp.setTop(popUpLabel);
        BorderPane.setAlignment(popUpLabel, Pos.CENTER);
        BorderPane.setMargin(popUpLabel, new Insets(10, 10, 0, 10));
        
        bp.setCenter(constructTaskAverageGridPane());
        
        bp.setVisible(false);

        
        return bp;
    
    }
    
    public void showInfoIcon(boolean state){
        
        this.infoIconVisibility = state;
        
        // initialization if not done yet
        if(this.infoLabel == null && state == true){
            this.infoLabel = new Label("  !  ");
        
            getChildren().add(this.infoLabel);
            setRightAnchor(this.infoLabel, 5.0);
            setBottomAnchor(this.infoLabel, 22.0);

            this.infoLabel.setStyle(
                              "-fx-border-color: black; "
                            + "-fx-border-width: 1px 1px 1px 1px;"
                            + "-fx-border-radius: 22 22 22 22;"
                            + "-fx-font: 20 System;");

            this.taskAveragesBorderPane = constructPopUpBorderPane();

            getChildren().add(this.taskAveragesBorderPane);

            // show the taska averages pop up pane
            this.infoLabel.setOnMouseMoved(e -> {
                this.taskAveragesBorderPane.setVisible(true);
                this.taskAveragesBorderPane.toFront();
            });


            // hide the taska averages pop up pane
            this.infoLabel.setOnMouseExited(e -> {
                this.taskAveragesBorderPane.setVisible(false);
                //this.taskAveragesBorderPane.toFront();
            });
            
        } else if (state == true){
            
            this.infoLabel.setVisible(state);
            this.taskAveragesBorderPane.setVisible(state);
            this.taskAveragesBorderPane.toFront();
            
        } else if (state == false){
            
            if(this.infoLabel != null){
                this.infoLabel.setVisible(state);
            }
            if(this.taskAveragesBorderPane != null){
                this.taskAveragesBorderPane.setVisible(state);
            }

        }
        
    }
    
    public void setAverages(HashMap<String, Double> newAverageMap){

        if(this.infoIconVisibility){
            
            // initialize the new label
            if(this.infoLabel == null){
                this.infoLabel = new Label("  !  ");

                getChildren().add(this.infoLabel);
                setRightAnchor(this.infoLabel, 5.0);
                setBottomAnchor(this.infoLabel, 22.0);

                this.infoLabel.setStyle(
                                  "-fx-border-color: black; "
                                + "-fx-border-width: 1px 1px 1px 1px;"
                                + "-fx-border-radius: 25 25 25 25;"
                                + "-fx-font: 15 System;");
            }
            
            if(this.averageMap != null){
                
                this.averageMap.clear();
                this.averageMap = newAverageMap;

                // make new borderpane
                getChildren().remove(this.taskAveragesBorderPane);
                this.taskAveragesBorderPane = constructPopUpBorderPane();
                getChildren().add(this.taskAveragesBorderPane);
                
            }else{
                
                this.averageMap = newAverageMap;

                // make new borderpane
                this.taskAveragesBorderPane = constructPopUpBorderPane();

                getChildren().add(this.taskAveragesBorderPane);

            }
        
            
            // show the task averages pop up pane
            this.infoLabel.setOnMouseMoved(e -> {
                this.taskAveragesBorderPane.setVisible(true);
                this.taskAveragesBorderPane.toFront();
            });


            // hide the task averages pop up pane
            this.infoLabel.setOnMouseExited(e -> {
                this.taskAveragesBorderPane.setVisible(false);
                
            });
            
        } 
        
    }
    
    public void lineChartMode(boolean state){
        
        if(state){
            
            // new outside border sizes
            setMinWidth(430);
            setMinHeight(this.height);
            setMaxWidth(AnchorPane.USE_PREF_SIZE);
            setMaxHeight(this.height);
            setPrefWidth(430);
            setPrefHeight(this.height);
            
            // new button sizes
            double newWidth = 25.0;
            double newHeight = 50.0;
            
            setTopAnchor(this.nextChartImageView, 75.0);
            setTopAnchor(this.prevChartImageView, 75.0);
            
            this.prevChartImageView.setFitWidth(newWidth);
            this.prevChartImageView.setFitHeight(newHeight);
            
            this.nextChartImageView.setFitWidth(newWidth);
            this.nextChartImageView.setFitHeight(newHeight);
            
            
            // new chart placement
            
            this.topAnchor = 15.0;
            this.bottomAnchor = 2.0;
            this.rightAnchor = 18.0;
            this.leftAnchor = 18.0;
            
//            AnchorPane.setTopAnchor(this.chart, this.topAnchor);
//            AnchorPane.setBottomAnchor(pane, this.bottomAnchor);
//            AnchorPane.setRightAnchor(pane, this.rightAnchor);
//            AnchorPane.setLeftAnchor(pane, this.leftAnchor);
            
            
        }else{
            setMinWidth(this.width);
            setMinHeight(this.height);
            setMaxWidth(AnchorPane.USE_COMPUTED_SIZE);
            setMaxHeight(this.height);
            setPrefWidth(this.width);
            setPrefHeight(this.height);
            
                        
            setTopAnchor(this.nextChartImageView, this.buttonHeight);
            setTopAnchor(this.prevChartImageView, this.buttonHeight);
            
            this.prevChartImageView.setFitWidth(this.buttonSides);
            this.prevChartImageView.setFitHeight(this.buttonSides);
            
            this.nextChartImageView.setFitWidth(this.buttonSides);
            this.nextChartImageView.setFitHeight(this.buttonSides);
            
            // new chart placement
            this.topAnchor = 30.0;
            this.bottomAnchor = 2.0;
            this.rightAnchor = 31.0;
            this.leftAnchor = 31.0;
        }
    }
    
    
}

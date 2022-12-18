/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.thebigfour.drivingconditions;

/**
 *
 * @author mikko
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class View implements Initializable {
    
    // are any searches made yet?
    boolean searchWasMade = false;
    boolean isSavedData = false; // is the displayed data a saved dataset or not

    // enums for the view state handling
    public enum ViewState {
        UNKNOWN, ROAD, WEATHER, ROAD_AND_WEATHER, ROAD_COMPARE, WEATHER_COMPARE, ROAD_AND_WEATHER_COMPARE
    }

    private final double[] xMinMax = {19.0, 32.0};
    private final double[] yMinMax = {59.0, 72.0};

    private ViewState viewState = ViewState.UNKNOWN;

    private Presenter presenter;

    private HashMap<String, double[]> constCoordinates;

    // list for task types to be shown
    private ObservableList<String> shownTaskTypes;

    String cities[] = {"Tampere",
                        "Helsinki",
                        "Turku",
                        "Oulu",
                        "Jyväskylä"};

    private final int width = 1000;

    // to prevent double clicking the menu button
    private boolean leftMenuButtonPressed = false;
    private boolean rightMenuButtonPressed = false;

    ChartScroller maintenanceChartScroller;
    ChartScroller trafficMessageScroller;
    ChartScroller tempAndMaintenanceChartScroller;
    ChartScroller tempAndRoadConditionChartScroller;
    ChartScroller temperatureChartScroller;
    ChartScroller cloudinessChartScroller;
    ChartScroller windChartScroller;
    
    ChartScroller tempAndMaintenanceChartScroller2;
    ChartScroller tempAndRoadConditionChartScroller2;
    ChartScroller maintenanceChartScroller2;
    ChartScroller trafficMessageScroller2;
    ChartScroller temperatureChartScroller2;
    ChartScroller windChartScroller2;


    
    private boolean taskListShowing = false;
    private boolean taskListInitialized = false;

    private AnchorPane currentRoadPane;

    private AnchorPane roadForecastPane;

    private AnchorPane maintenancePane;

    private AnchorPane TrafficMessagesPane;

    private AnchorPane cloudinessPane;

    private AnchorPane temperaturePane;

    private AnchorPane thisMonthsWeatherPane;

    private AnchorPane windPane;

    private AnchorPane TempAndMaintenancePane;
    
    private AnchorPane TempAndRoadConditionPane;
    
    private AnchorPane TempAndMaintenancePane2;
    
    private AnchorPane TempAndRoadConditionPane2;
    
    private AnchorPane maintenancePane2;

    private AnchorPane TrafficMessagesPane2;
    
    private AnchorPane temperaturePane2;
    
    private AnchorPane windPane2;
    


    private boolean maintenanceTaskFiltered = false;

    @FXML
    private Button menuButton;

    @FXML
    private ComboBox cityComboBox;

    @FXML
    private ComboBox presetComboBox;

    @FXML
    private Button savepreset;

    @FXML
    private Button deleteButton;

    @FXML
    private Label topCityLabel;

    @FXML
    private CheckBox digitrafficCheckBox;

    @FXML
    private CheckBox FMICheckBox;

    @FXML
    private TextField xMin;

    @FXML
    private TextField yMin;

    @FXML
    private TextField xMax;

    @FXML
    private TextField yMax;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

    @FXML
    private TextField fromTime;

    @FXML
    private TextField toTime;

    @FXML
    private AnchorPane menuPane;

    @FXML
    private Button menuBackButton;

    @FXML
    private AnchorPane parameterMenu;

    @FXML
    private Button parameterMenuButton1;

    @FXML
    private Button parameterMenuButton2;

    @FXML
    private GridPane mainGridPane;

    @FXML
    private CheckBox RoadForecastCheckBox;

    @FXML
    private CheckBox maintenanceTaskCheckBox;

    @FXML
    private CheckBox currentRoadCheckBox;

    @FXML
    private CheckBox TrafficCheckBox;

    @FXML
    private Button taskConfirmButton;

    @FXML
    private Button chooseTasksButton;

    @FXML
    private ListView taskListView;

    @FXML
    private Button showAllButton;

    @FXML
    private Label searchErrorLabel;
    
    @FXML
    private Button saveDatasetButton;
    
    @FXML
    private ComboBox savedDatasetsComboBox;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        cityComboBox.setItems(FXCollections.observableArrayList(cities));
        cityComboBox.setValue("Tampere");

        // initialize the listview for showing the tasktypes
        this.taskListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.taskListView.setOnMouseClicked(e -> {
            this.taskConfirmButton.setVisible(true);

        });

        // left side menu animations, sending it back
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), menuPane);
        translateTransition.setByX(-width);
        translateTransition.play();

        // bring menu to the screen
        menuButton.setOnMouseClicked(event -> {

            if (leftMenuButtonPressed == false) {
                leftMenuButtonPressed = true;
                TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(0.5), menuPane);
                translateTransition1.setByX(+width);
                translateTransition1.play();
            }

        });

        // send menu back
        menuBackButton.setOnMouseClicked(event -> {

            if (leftMenuButtonPressed == true) {
                leftMenuButtonPressed = false;
                TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(0.5), menuPane);
                translateTransition1.setByX(-width);
                translateTransition1.play();
            }

        });

        // right side menu animations
        TranslateTransition translateTransition2 = new TranslateTransition(Duration.seconds(0.5), parameterMenu);
        translateTransition2.setByX(+width);
        translateTransition2.play();

        parameterMenuButton1.setOnMouseClicked(event -> {

            if (rightMenuButtonPressed == false) {
                rightMenuButtonPressed = true;
                TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(0.5), parameterMenu);
                translateTransition1.setByX(-width);
                translateTransition1.play();

            }

        });

        parameterMenuButton2.setOnMouseClicked(event -> {
            if (rightMenuButtonPressed == true) {
                rightMenuButtonPressed = false;
                TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(0.5), parameterMenu);
                translateTransition1.setByX(+width);
                translateTransition1.play();

                closeMenus();
            }
        });

        // for changing the view
        RoadForecastCheckBox.setOnMouseClicked(e -> {
            RoadParametersChanged();
        });

        maintenanceTaskCheckBox.setOnMouseClicked(e -> {
            RoadParametersChanged();
        });

        currentRoadCheckBox.setOnMouseClicked(e -> {
            RoadParametersChanged();
        });

        TrafficCheckBox.setOnMouseClicked(e -> {
            RoadParametersChanged();
        });

        savepreset.setOnMouseClicked(e -> {
            System.out.println("Clicked preset");
            savePreset();
        });

        presetComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (this.presenter != null && newValue != null) {
                searchErrorLabel.setVisible(false);
                SearchParameters presetParams = this.presenter.getPreset(newValue.toString());
                xMin.setText(Double.toString(presetParams.getCoordinates()[0]));
                yMin.setText(Double.toString(presetParams.getCoordinates()[1]));
                xMax.setText(Double.toString(presetParams.getCoordinates()[2]));
                yMax.setText(Double.toString(presetParams.getCoordinates()[3]));

                fromDatePicker.setValue(presetParams.getFromDate());
                toDatePicker.setValue(presetParams.getToDate());

                digitrafficCheckBox.setSelected(presetParams.isRoadData());
                FMICheckBox.setSelected(presetParams.isFmiData());

            }
        });

        deleteButton.setOnMouseClicked(e -> {
            if (presetComboBox.getValue() != null && this.presenter != null) {
                this.presenter.deletePreset(presetComboBox.getValue().toString());
            }
        });

        initCoordinates();
        this.presenter = new Presenter();
        updatePresets();

        this.RoadForecastCheckBox.setSelected(true);
        this.maintenanceTaskCheckBox.setSelected(true);
        this.currentRoadCheckBox.setSelected(true);
        this.TrafficCheckBox.setSelected(true);
        
        // if no searches have been made yet
        if(!this.searchWasMade){
            saveDatasetButton.setVisible(false);
        }
        // set the items to save dataset combobox
        savedDatasetsComboBox.setItems(this.presenter.getDatasetsNamesList());

    }

    public void updatePresets() {
        presetComboBox.setItems(this.presenter.getPresets());
    }
    public void updateSavedDatasets() {
        savedDatasetsComboBox.setItems(this.presenter.getDatasetsNamesList());
    }

    public void searchButtonPressed() {

        if (!validateSearch()) {
            return;
        }

        double[] coordinates = getCoordinates();
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        String from = fromTime.getText();
        String to = toTime.getText();

        this.presenter.setAll(coordinates, fromDate, toDate, from, to);

        loadPanes();

        topCityLabel.setText(cityComboBox.getValue().toString());

        // move the search menu out of the window
        leftMenuButtonPressed = false;
        TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(0.5), menuPane);
        translateTransition1.setByX(-width);
        translateTransition1.play();

        searchErrorLabel.setVisible(false);
        
        searchWasMade = true;
        saveDatasetButton.setVisible(true);
        
        isSavedData = false; // is the displayed data a saved dataset or not
        
    }

    public void updateTrafficMessages(boolean isSavedDataset, String datasetName, boolean isCompare) {

        if (this.trafficMessageScroller != null) {
            
            
            if(isCompare){
                this.trafficMessageScroller2.removeAllCharts();
            }else{
                this.trafficMessageScroller.removeAllCharts();
            }
            // Remove old message
            
            
            ArrayList<TrafficMessage> allMessages;
            
            // is the data coming from a saved dataset or an api
            if(isSavedDataset){
                
                allMessages = this.presenter.getAllTrafficMessages(true, datasetName);
                
            }else{
                allMessages = this.presenter.getAllTrafficMessages(false, "");
            }



            for (int i = 0; i < allMessages.size(); i++) {

                TrafficMessage currentMessage = allMessages.get(i);

                TrafficMessageBox newTrafficMessageBox = new TrafficMessageBox();

                try {

                    String time = currentMessage.getStartTime() + " - " + currentMessage.getEndTime();

                    String feature;
                    if (currentMessage.getFeatures().isEmpty()) {
                        feature = "Missing info";
                    } else {
                        feature = currentMessage.getFeatures().get(0);
                    }

                    newTrafficMessageBox.addMessage(time, currentMessage.getTitle(),
                            currentMessage.getDescription(), feature);
                    
                    if(isCompare){
                        this.trafficMessageScroller2.addNewChart(newTrafficMessageBox);
                    }else{
                        this.trafficMessageScroller.addNewChart(newTrafficMessageBox);
                    }

                    

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void updateCurrentInfo(boolean isSavedDataset, String datasetName) {

        Label tempLabel = (Label) currentRoadPane.lookup("#tempLabel");
        Label roadTempLabel = (Label) currentRoadPane.lookup("#roadTempLabel");
        Label windSpeedLabel = (Label) currentRoadPane.lookup("#windSpeedLabel");
        Label windDirectionLabel = (Label) currentRoadPane.lookup("#windDirectionLabel");
        Label OverallConditionLabel = (Label) currentRoadPane.lookup("#OverallConditionLabel");

        this.presenter.presentCurrentConditions(isSavedDataset, datasetName,
                tempLabel, roadTempLabel, windSpeedLabel,
                windDirectionLabel, OverallConditionLabel);
    }

    public double[] getCoordinates() {

        System.out.println("Getting coords");

        double[] coordinates = new double[4];
        String strXmin = xMin.getText();

        if (strXmin.isBlank()) {
            coordinates = constCoordinates.get(cityComboBox.getValue());
        } else {
            coordinates[0] = Double.parseDouble(xMin.getText());
            coordinates[1] = Double.parseDouble(yMin.getText());
            coordinates[2] = Double.parseDouble(xMax.getText());
            coordinates[3] = Double.parseDouble(yMax.getText());
        }

        System.out.println("Got coords");
        return coordinates;

    }

    public boolean validateSearch() {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        String from = fromTime.getText();
        String to = toTime.getText();

        String strXmin = xMin.getText();
        String strYmin = yMin.getText();
        String strXmax = xMax.getText();
        String strYmax = yMax.getText();

        System.out.println(strXmin);
        System.out.println(strYmin);
        System.out.println(strXmax);
        System.out.println(strYmax);

        String[] coordInputs = {strXmin, strYmin, strXmax, strYmax};
        double[] coordinates = new double[4];

        // if data source is not selected
        if (!digitrafficCheckBox.isSelected() && !FMICheckBox.isSelected()) {
            searchErrorLabel.setText("Choose data!");
            searchErrorLabel.setVisible(true);
            return false;
        }

        // if start date and/or end date not selected
        if (fromDate == null || toDate == null) {
            searchErrorLabel.setText("Enter start and end dates!");
            searchErrorLabel.setVisible(true);
            return false;
        }

        // if end date is before start date
        if (toDate.isBefore(fromDate)) {
            searchErrorLabel.setText("Start date is after end date!");
            searchErrorLabel.setVisible(true);
            return false;
        }

        // if start to end is longer than 8 day
        if (fromDate.plusDays(7).isBefore(toDate)) {
            searchErrorLabel.setText("Max 8 days!");
            searchErrorLabel.setVisible(true);
            return false;
        }

        if (!strXmin.isBlank() || !strYmin.isBlank() || !strXmax.isBlank() || !strYmax.isBlank()) {

            for (int i = 0; i < coordInputs.length; i++) {
                try {
                    double coordinate = Double.parseDouble(coordInputs[i]);
                    coordinates[i] = coordinate;

                } catch (NumberFormatException e) {
                    searchErrorLabel.setText("Invalid coordinate values!");
                    searchErrorLabel.setVisible(true);
                    return false;
                }

                if (i % 2 == 0) {
                    if (coordinates[i] < xMinMax[0] || coordinates[i] > xMinMax[1]) {
                        searchErrorLabel.setText("X coordinates must be between 19.0 and 32.0");
                        searchErrorLabel.setVisible(true);
                        return false;
                    }
                } else {
                    if (coordinates[i] < yMinMax[0] || coordinates[i] > yMinMax[1]) {
                        searchErrorLabel.setText("Y coordinates must be between 59.0 and 72.0");
                        searchErrorLabel.setVisible(true);
                        return false;
                    }
                }

            }

            double areaSize = (coordinates[2] - coordinates[0]) * (coordinates[3] - coordinates[1]);
            System.out.println(areaSize);

            if (areaSize <= 0 || areaSize > 4) {
                searchErrorLabel.setText("Area is too big or negative");
                searchErrorLabel.setVisible(true);
                return false;
            }

        }
        System.out.println("Valid");
        return true;
    }

    public void updateRoadForecastInfo(boolean isSavedDataset, String datasetName) {

        Label tempLabel2h = (Label) roadForecastPane.lookup("#tempLabel2h");
        Label roadTempLabel2h = (Label) roadForecastPane.lookup("#roadTempLabel2h");

        Label tempLabel4h = (Label) roadForecastPane.lookup("#tempLabel4h");
        Label roadTempLabel4h = (Label) roadForecastPane.lookup("#roadTempLabel4h");

        Label tempLabel6h = (Label) roadForecastPane.lookup("#tempLabel6h");
        Label roadTempLabel6h = (Label) roadForecastPane.lookup("#roadTempLabel6h");

        Label tempLabel12h = (Label) roadForecastPane.lookup("#tempLabel12h");
        Label roadTempLabel12h = (Label) roadForecastPane.lookup("#roadTempLabel12h");

        this.presenter.presentConditionsForecast(isSavedDataset, datasetName, 
                tempLabel2h, roadTempLabel2h, tempLabel4h, roadTempLabel4h,
                tempLabel6h, roadTempLabel6h, tempLabel12h, roadTempLabel12h);

    }

    public void updateMaintenanceInfo(boolean isSavedDataset, String datasetName, boolean isCompare) {

        if (this.maintenanceChartScroller != null) {
            
            if(isCompare){
                this.maintenanceChartScroller2.removeAllCharts();
            }else{
                this.maintenanceChartScroller.removeAllCharts();
            }
            
            
            
            TreeMap<LocalDate, ArrayList<MaintenanceTask>> allTasks;
            
            // is the data saved or coming from the api
            if(isSavedDataset){
                allTasks = this.presenter.getAllTasks(true, datasetName);
            }
            else{
                allTasks = this.presenter.getAllTasks(false, "");
            }

            
            
            HashMap<String, Double> taskTypesAmountMap = new HashMap<>();

            for (Map.Entry<LocalDate, ArrayList<MaintenanceTask>> entry : allTasks.entrySet()) {
                
                LocalDate date = entry.getKey();
                ArrayList<MaintenanceTask> tasks = entry.getValue();
                
                Double newAmount;
                
                // calculate the amount of tasks per type for the day
                for (Map.Entry<String, Double> set : this.presenter.calculateTaskAmountPerType(tasks).entrySet()) {
                    
                    if(taskTypesAmountMap.get(set.getKey()) == null){
                        taskTypesAmountMap.put(set.getKey(), set.getValue());
                        continue;
                    }
                    
                    newAmount = taskTypesAmountMap.get(set.getKey()) + set.getValue();
                    
                    taskTypesAmountMap.put(set.getKey(), newAmount);
                    
                }
                

                // create new charts
                TaskChart newMaintenanceChart = new TaskChart();

                // create the chart with tasks and a date
                newMaintenanceChart.createChart(date.toString(), tasks);
                
                if(isCompare){
                    this.maintenanceChartScroller2.addNewChart(newMaintenanceChart);
                }else{
                    // populate the chart scroller
                    this.maintenanceChartScroller.addNewChart(newMaintenanceChart);
                }

                

            }
            
            // get the averages for all tasktypes per day for the timeline
            
            if (!taskTypesAmountMap.isEmpty()){
                
                Double timeline = Double.valueOf(allTasks.size());
                
                HashMap<String, Double> averageMap = 
                        this.presenter.calculateAveragesPerType(
                                                            taskTypesAmountMap, 
                                                            timeline);
                
                if(isCompare){
                    this.maintenanceChartScroller2.setAverages(averageMap);
                }else{
                   // populate the chart scroller
                   this.maintenanceChartScroller.setAverages(averageMap);
                }
            }
        }
    }

    public void savePreset() {
        if (this.presenter != null && validateSearch()) {
            searchErrorLabel.setVisible(false);
            System.out.println("Starting to save");
            SearchParameters params = new SearchParameters(
                    getCoordinates(),
                    fromDatePicker.getValue(),
                    toDatePicker.getValue(),
                    "1",
                    "1",
                    digitrafficCheckBox.isSelected(),
                    FMICheckBox.isSelected());
            
            this.presenter.savePreset(params);

        }
    }

    // run this method everytime ROAD settings change in the parameter menu
    public void RoadParametersChanged() {
        if (this.viewState != ViewState.ROAD || isSavedData) {
            return;
        }

        // Handling the task filtering
        if (this.maintenanceTaskCheckBox.isSelected()) {

            if (this.shownTaskTypes != null) {
                if (this.maintenanceChartScroller != null) {
                    this.maintenanceChartScroller.redrawContent(this.shownTaskTypes, false);
                }
            } else {

            }
        }

        // handling the resizing the current road pane
        if (this.currentRoadCheckBox.isSelected()) {
            if (!this.maintenanceTaskCheckBox.isSelected()) {

                this.maintenancePane.setVisible(false);

                GridPane.setColumnSpan(this.currentRoadPane, 2);

                this.currentRoadPane.setMaxWidth(Control.USE_COMPUTED_SIZE);

                // for positioning the icons to the middle
                AnchorPane iconAnchorPane = (AnchorPane) this.currentRoadPane.lookup("#iconAnchorPane");

                AnchorPane.setLeftAnchor(iconAnchorPane, 305.0);
                AnchorPane.setRightAnchor(iconAnchorPane, 305.0);

            } else {

                this.maintenancePane.setVisible(true);

                GridPane.setColumnSpan(this.currentRoadPane, 1);

                this.currentRoadPane.setMaxWidth(Control.USE_PREF_SIZE);

                // for positioning the icons to the middle
                AnchorPane iconAnchorPane = (AnchorPane) this.currentRoadPane.lookup("#iconAnchorPane");

                AnchorPane.setLeftAnchor(iconAnchorPane, 74.0);
                AnchorPane.setRightAnchor(iconAnchorPane, 74.0);

            }

        }

        // Handling maintenance panes resizing
        if (this.maintenanceTaskCheckBox.isSelected()) {
            if (!this.currentRoadCheckBox.isSelected()) {

                this.currentRoadPane.setVisible(false);

                // for alligning the chart lines correctly
                if (this.maintenanceChartScroller != null) {

                    // set a new offset for chart resizing
                    this.maintenanceChartScroller.setChartWidthOffset(504.6);

                    // redraw the content
                    this.maintenanceChartScroller.redrawContent(this.shownTaskTypes, false);

                }

                this.maintenancePane.setMaxWidth(Control.USE_COMPUTED_SIZE);

                GridPane.setColumnIndex(this.maintenancePane, 0);

                GridPane.setColumnSpan(this.maintenancePane, 2);

            } else {

                if (this.maintenanceChartScroller != null) {

                    // set a new offset for chart resizing
                    this.maintenanceChartScroller.setChartWidthOffset(26.2);

                    // redraw the content
                    this.maintenanceChartScroller.redrawContent(this.shownTaskTypes, false);
                }

                this.currentRoadPane.setVisible(true);

                GridPane.setColumnIndex(this.maintenancePane, 1);

                GridPane.setColumnSpan(this.maintenancePane, 1);

                this.maintenancePane.setMaxWidth(Control.USE_PREF_SIZE);
            }

        }

        // handling the resizing of forecast pane
        if (this.RoadForecastCheckBox.isSelected()) {
            if (!this.TrafficCheckBox.isSelected()) {

                this.TrafficMessagesPane.setVisible(false);

                HBox roadTempHBox = (HBox) this.roadForecastPane.lookup("#roadTempHBox");
                roadTempHBox.setSpacing(165);

                GridPane.setColumnSpan(this.roadForecastPane, 2);

                this.roadForecastPane.setMaxWidth(Control.USE_COMPUTED_SIZE);

            } else {

                HBox roadTempHBox = (HBox) this.roadForecastPane.lookup("#roadTempHBox");
                roadTempHBox.setSpacing(13);

                this.TrafficMessagesPane.setVisible(true);

                GridPane.setColumnSpan(this.roadForecastPane, 1);

                this.roadForecastPane.setMaxWidth(Control.USE_PREF_SIZE);

            }
        }

        // Handling traffic message panes resizing
        if (this.TrafficCheckBox.isSelected()) {
            if (!this.RoadForecastCheckBox.isSelected()) {

                // set the roadForecastPane invisible
                this.roadForecastPane.setVisible(false);

                // make the message text larger + increase wrapping width
                this.trafficMessageScroller.resizeText(true);

                // move one (1) row to the right
                GridPane.setColumnIndex(this.TrafficMessagesPane, 0);

                // set the row span to 2 rows (makes it wider)
                GridPane.setColumnSpan(this.TrafficMessagesPane, 2);

                // allow the pane to stretch
                this.TrafficMessagesPane.setMaxWidth(Control.USE_COMPUTED_SIZE);

            } else {

                // make the message text larger + increase wrapping width
                this.trafficMessageScroller.resizeText(false);

                // set the roadForecastPane visible
                this.roadForecastPane.setVisible(true);

                // move the message pane back to the second column
                GridPane.setColumnIndex(this.TrafficMessagesPane, 1);

                // set the span to one (1) column
                GridPane.setColumnSpan(this.TrafficMessagesPane, 1);

                // lock the size (don't allow stretching
                this.TrafficMessagesPane.setMaxWidth(Control.USE_PREF_SIZE);

            }

        }

        // handles turning the panes off and on
        if (!this.currentRoadCheckBox.isSelected()) {
            this.currentRoadPane.setVisible(false);
        } else {
            this.currentRoadPane.setVisible(true);
        }

        if (!this.maintenanceTaskCheckBox.isSelected()) {
            this.maintenancePane.setVisible(false);
        } else {
            this.maintenancePane.setVisible(true);
        }

        if (!this.RoadForecastCheckBox.isSelected()) {
            this.roadForecastPane.setVisible(false);
        } else {
            this.roadForecastPane.setVisible(true);
        }

        if (!this.TrafficCheckBox.isSelected()) {
            this.TrafficMessagesPane.setVisible(false);
        } else {
            this.TrafficMessagesPane.setVisible(true);
        }

    }

    private void initCoordinates() {

        this.constCoordinates = new HashMap<>();

        this.constCoordinates.put("Tampere", new double[]{23.5, 61.3, 24, 61.5});
        this.constCoordinates.put("Helsinki", new double[]{24.7, 60.1, 25.1, 60.3});
        this.constCoordinates.put("Turku", new double[]{22.1, 60.3, 22.4, 60.5});
        this.constCoordinates.put("Oulu", new double[]{25.2, 64.9, 25.6, 65.1});
        this.constCoordinates.put("Jyväskylä", new double[]{25.6, 62.2, 25.8, 62.3});

    }

    private ObservableList<String> getTaskTypes() {

        ObservableList<String> list = FXCollections.observableArrayList();
        JSONParser jsonParser = new JSONParser();

        try {

            String path = new File("src/main/resources/json/tasks.json").getAbsolutePath();
            Object obj;
            try(FileReader reader = new FileReader(path)){
                obj = jsonParser.parse(reader);
            }
            
            JSONArray jsonArray = (JSONArray) obj;

            jsonArray.forEach(jsonObj -> {

                JSONObject jsonObj1 = (JSONObject) jsonObj;
                list.add(jsonObj1.get("nameEn").toString());
            });

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void showTaskListView() {

        ImageView expandArrow = (ImageView) this.chooseTasksButton.lookup("#expandArrow");

        // initialization the first time the button is clicked
        if (!taskListInitialized) {

            this.taskListView.setItems(getTaskTypes());

            taskListInitialized = true;
        }

        if (!taskListShowing) {
            this.taskListView.setVisible(true);
            taskListShowing = true;
            expandArrow.setRotate(180);
        } else {
            this.taskListView.setVisible(false);
            this.taskListShowing = false;
            this.taskConfirmButton.setVisible(false);

            expandArrow.setRotate(0);
        }
    }

    private void closeMenus() {
        // for closing menua when exiting the parameter menu
        ImageView expandArrow = (ImageView) this.chooseTasksButton.lookup("#expandArrow");
        this.taskListView.setVisible(false);
        this.taskListShowing = false;
        this.taskConfirmButton.setVisible(false);

        expandArrow.setRotate(0);
    }

    public void confirmFilteredTypes() {

        this.taskListView.setVisible(false);
        this.taskConfirmButton.setVisible(false);
        this.taskListShowing = false;

        // lookup the arrow and rotate it back to the original position
        ImageView expandArrow = (ImageView) this.chooseTasksButton.lookup("#expandArrow");
        expandArrow.setRotate(0);

        // set the list with new items
        this.shownTaskTypes = this.taskListView.getSelectionModel().getSelectedItems();

        // show "Show all" button
        this.showAllButton.setVisible(true);

        maintenanceTaskFiltered = true;

        // rerender the view
        RoadParametersChanged();
    }

    public void showAllTasks() {
        this.showAllButton.setVisible(false);
        this.maintenanceTaskFiltered = false;
        this.maintenanceChartScroller.redrawContent(this.shownTaskTypes, true);
    }

    private void loadPanes() {

        if (digitrafficCheckBox.isSelected() && FMICheckBox.isSelected()) {

            if (this.viewState == ViewState.ROAD_AND_WEATHER) {

                this.presenter.runModelFetches(viewState);

                updateTempAndMaintenanceInfo(false, "", false);
                updateTempAndRoadConditionInfo(false, "", false);
                updateTrafficMessages(false, "", false);
                updateThisMonthsInfo();
                return;
            }
            
            // clear the old data
            this.mainGridPane.getChildren().clear();
            
            // change state of the view
            this.viewState = ViewState.ROAD_AND_WEATHER;

            // load the fxmlpanes to the view
            loadRoadAndWeatherPanesFXML(); 
            
            // initialize the chartscrollers
            initializeComboPanes();
            
            // fetch data
            this.presenter.runModelFetches(viewState);
            
            // load the new data and update the panes
            updateTempAndMaintenanceInfo(false, "", false);
            updateTempAndRoadConditionInfo(false, "", false);
            updateTrafficMessages(false, "", false);
            updateThisMonthsInfo();
            

        } else if (digitrafficCheckBox.isSelected()) {

            if (this.viewState == ViewState.ROAD) {

                this.presenter.runModelFetches(viewState);

                updateCurrentInfo(false, "");
                updateRoadForecastInfo(false, "");
                updateTrafficMessages(false, "", false);
                updateMaintenanceInfo(false, "", false);

                return;
            }
            
            // clear the old data
            this.mainGridPane.getChildren().clear();
            
            // change state of the view
            this.viewState = ViewState.ROAD;
            
            // load the fxmlpanes to the view
            loadRoadPanesFXML();
            
            // initialize the chartscrollers
            initializeRoadPanes();
            
            // fetch data
            this.presenter.runModelFetches(viewState);
            
            // load the new data and update the panes
            updateCurrentInfo(false, "");
            updateRoadForecastInfo(false, "");
            updateTrafficMessages(false, "", false);
            updateMaintenanceInfo(false, "", false);

        } else if (FMICheckBox.isSelected()) {

            if (this.viewState == ViewState.WEATHER) {

                this.presenter.runModelFetches(viewState);

                // update methods here
                updateTemperatureInfo(false, "", false);
                updateWindInfo(false, "", false);
                updateCloudinessInfo(false, "");
                updateThisMonthsInfo();
                return;
            }
            
            // clear the old data
            this.mainGridPane.getChildren().clear();
            
            // change state of the view
            this.viewState = ViewState.WEATHER;
            
            // load the fxmlpanes to the view
            loadWeatherPanesFXML();
            
            // initialize the chartscrollers
            initializeWeatherPanes();
            
            // fetch data
            this.presenter.runModelFetches(viewState);

            // load the new data and update the panes
            updateTemperatureInfo(false, "", false);
            updateWindInfo(false, "", false);
            updateCloudinessInfo(false, "");
            updateThisMonthsInfo();
            
        }

    }
    
    // method for loading roaddata related fxmlfiles into the view
    private void loadRoadPanesFXML(){
        try {
            currentRoadPane = FXMLLoader.load(getClass().getResource("/fxml/CurrentRoadPane.fxml"));
            mainGridPane.add(currentRoadPane, 0, 0);
        } catch (Exception e) {
            System.out.println("Can't load current road pane");
        }

        try {
            maintenancePane = FXMLLoader.load(getClass().getResource("/fxml/MaintenancePane.fxml"));
            mainGridPane.add(maintenancePane, 1, 0);
        } catch (Exception e) {
            System.out.println("Can't load maintenance pane");
        }

        try {
            roadForecastPane = FXMLLoader.load(getClass().getResource("/fxml/RoadForecastPane.fxml"));
            mainGridPane.add(roadForecastPane, 0, 1);
        } catch (Exception e) {
            System.out.println("Can't load road forecast pane");
        }

        try {
            TrafficMessagesPane = FXMLLoader.load(getClass().getResource("/fxml/TrafficMessagePane.fxml"));
            mainGridPane.add(TrafficMessagesPane, 1, 1);
        } catch (Exception e) {
            System.out.println("Can't load traffic message pane");
        }
}
    
    // method for loading weatherdata related fxmlfiles into the view
    private void loadWeatherPanesFXML(){
        
        try {
            temperaturePane = FXMLLoader.load(getClass().getResource("/fxml/TemperaturePane.fxml"));
            mainGridPane.add(temperaturePane, 0, 0);
        } catch (Exception e) {
            System.out.println("Can't load temperature pane");
        }

        try {
            windPane = FXMLLoader.load(getClass().getResource("/fxml/WindPane.fxml"));
            mainGridPane.add(windPane, 1, 0);
        } catch (Exception e) {
            System.out.println("Can't load wind pane");
        }

        try {
            cloudinessPane = FXMLLoader.load(getClass().getResource("/fxml/CloudinessPane.fxml"));
            mainGridPane.add(cloudinessPane, 0, 1);
        } catch (Exception e) {
            System.out.println("Can't load cloudiness pane");
        }

        try {
            thisMonthsWeatherPane = FXMLLoader.load(getClass().getResource("/fxml/ThisMonthsWeatherPane.fxml"));
            mainGridPane.add(thisMonthsWeatherPane, 1, 1);
        } catch (Exception e) {
            System.out.println("Can't load this months weather pane");
        }
    }
    
    // method for loading road and weather data related fxmlfiles into the view
    private void loadRoadAndWeatherPanesFXML(){
        
        try {
            this.TempAndMaintenancePane = FXMLLoader.load(getClass().getResource("/fxml/TempAndMaintenancePane.fxml"));
            this.mainGridPane.add(this.TempAndMaintenancePane, 0, 0);
        } catch (Exception e) {
            System.out.println("Can't load TempAndMaintenancePane ");
        }


        try {
            this.TempAndRoadConditionPane = FXMLLoader.load(getClass().getResource("/fxml/TempAndRoadConditionPane.fxml"));
            this.mainGridPane.add(this.TempAndRoadConditionPane, 0, 1);
        } catch (Exception e) {
                System.out.println("Can't load TempAndRoadConditionPane ");
        }

        try {
            TrafficMessagesPane = FXMLLoader.load(getClass().getResource("/fxml/TrafficMessagePane.fxml"));
            mainGridPane.add(TrafficMessagesPane, 1, 0);
        } catch (Exception e) {
            System.out.println("Can't load traffic message pane");
        }

        try {
            thisMonthsWeatherPane = FXMLLoader.load(getClass().getResource("/fxml/ThisMonthsWeatherPane.fxml"));
            mainGridPane.add(thisMonthsWeatherPane, 1, 1);
        } catch (Exception e) {
            System.out.println("Can't load this months weather pane");
        }
            
    }

    private void initializeRoadPanes() {

        // initialize the maintenance chart scroller and place on canvas
        maintenanceChartScroller = new ChartScroller();
        maintenanceChartScroller.showInfoIcon(true); // show info icon for task averages
        maintenancePane.getChildren().add(maintenanceChartScroller);
        AnchorPane.setLeftAnchor(maintenanceChartScroller, 13.0);
        AnchorPane.setRightAnchor(maintenanceChartScroller, 13.0);
        AnchorPane.setBottomAnchor(maintenanceChartScroller, 0.0);

        // initialize the traffic messages scroller and place on canvas
        trafficMessageScroller = new ChartScroller();
        trafficMessageScroller.setButtonHeight(75.0);
        TrafficMessagesPane.getChildren().add(trafficMessageScroller);
        AnchorPane.setLeftAnchor(trafficMessageScroller, 13.0);
        AnchorPane.setRightAnchor(trafficMessageScroller, 13.0);
        AnchorPane.setBottomAnchor(trafficMessageScroller, 0.0);

    }

    private void initializeWeatherPanes() {
           
        // initialize the temperature chart scroller and place on canvas
        this.temperatureChartScroller = new ChartScroller();
        this.temperatureChartScroller.lineChartMode(true);
        this.temperaturePane.getChildren().add(this.temperatureChartScroller);
        AnchorPane.setLeftAnchor(this.temperatureChartScroller, 8.0);
        AnchorPane.setBottomAnchor(this.temperatureChartScroller, 0.0);
        
        // initialize the cloudiness chart scroller and place on canvas
        this.cloudinessChartScroller = new ChartScroller();
        this.cloudinessChartScroller.lineChartMode(true);
        this.cloudinessPane.getChildren().add(this.cloudinessChartScroller);
        AnchorPane.setLeftAnchor(this.cloudinessChartScroller, 8.0);
        AnchorPane.setBottomAnchor(this.cloudinessChartScroller, 0.0);
        
        
        // initialize the wind chart scroller and place on canvas
        this.windChartScroller = new ChartScroller();
        this.windChartScroller.lineChartMode(true);
        this.windPane.getChildren().add(this.windChartScroller);
        AnchorPane.setLeftAnchor(this.windChartScroller, 8.0);
        AnchorPane.setBottomAnchor(this.windChartScroller, 0.0);
        
    }

    private void initializeComboPanes() {

        // initialize the tempAndMaintenanceChartScroller and place on canvas
        this.tempAndMaintenanceChartScroller = new ChartScroller();
        this.TempAndMaintenancePane.getChildren().add(this.tempAndMaintenanceChartScroller);
        AnchorPane.setLeftAnchor(this.tempAndMaintenanceChartScroller, 13.0);
        AnchorPane.setRightAnchor(this.tempAndMaintenanceChartScroller, 13.0);
        AnchorPane.setBottomAnchor(this.tempAndMaintenanceChartScroller, 0.0);
        
        // initialize the tempAndRoadConditionChartScroller and place on canvas
        this.tempAndRoadConditionChartScroller = new ChartScroller();
        this.TempAndRoadConditionPane.getChildren().add(this.tempAndRoadConditionChartScroller);
        AnchorPane.setLeftAnchor(this.tempAndRoadConditionChartScroller, 13.0);
        AnchorPane.setRightAnchor(this.tempAndRoadConditionChartScroller, 13.0);
        AnchorPane.setBottomAnchor(this.tempAndRoadConditionChartScroller, 0.0);
        
        // initialize the traffic messages scroller and place on canvas
        trafficMessageScroller = new ChartScroller();
        trafficMessageScroller.setButtonHeight(75.0);
        TrafficMessagesPane.getChildren().add(trafficMessageScroller);
        AnchorPane.setLeftAnchor(trafficMessageScroller, 13.0);
        AnchorPane.setRightAnchor(trafficMessageScroller, 13.0);
        AnchorPane.setBottomAnchor(trafficMessageScroller, 0.0);

    }

    public void updateTempAndMaintenanceInfo(boolean isSavedDataset, String datasetName, boolean isCompare) {

        if (this.tempAndMaintenanceChartScroller != null) {
            
            
            if(isCompare){
                // remove the old chart(s)
                this.tempAndMaintenanceChartScroller2.removeAllCharts();
            }else{
                // remove the old chart(s)
                this.tempAndMaintenanceChartScroller.removeAllCharts();
            }
            
            
            TreeMap<LocalDate, ArrayList<MaintenanceTask>> allTasks;
            TreeMap<LocalDate, ArrayList<WeatherObservation>> allObservations;
            

            // is the data coming from a saved dataset or from an API
            if(isSavedDataset){
                allTasks = this.presenter.getAllTasks(true, datasetName);
                allObservations = this.presenter.getObservationsByDate(true, datasetName);
            }else{
                allTasks = this.presenter.getAllTasks(false, "");
                allObservations = this.presenter.getObservationsByDate(false, "");
            }
            
            // create iterator for looping
            Iterator<Map.Entry<LocalDate, ArrayList<MaintenanceTask>>> 
                taskIterator = allTasks.entrySet().iterator();
            
            // create iterator for looping
            Iterator<Map.Entry<LocalDate, ArrayList<WeatherObservation>>> 
                observationIterator = allObservations.entrySet().iterator();
            
            // iterate through the two treemaps simultaneously
            while(taskIterator.hasNext() || observationIterator.hasNext()){
                
                
                // create new combo chart
                WeatherMaintenanceChart newWeatherMaintenanceChart = new WeatherMaintenanceChart();
                    
                if (taskIterator.hasNext()){
                    
                    // get the maintenance task entry for the day
                    Map.Entry<LocalDate, ArrayList<MaintenanceTask>> taskEntry 
                            = taskIterator.next();

                    // populate the chart scroller
                    try{
                        // create the chart with tasks and a date
                        newWeatherMaintenanceChart.createChart(
                            taskEntry.getKey().toString(), taskEntry.getValue());
                        
                    }catch(Exception e){
                        System.out.println("Something went wrong adding tasks info to tempAndMaintenanceChartScroller" + "info:" + taskEntry.getValue());
                    }
                }
                
                if (observationIterator.hasNext()){
                    
                    // get the observation array for the day
                    ArrayList<WeatherObservation> daysObservations 
                        = observationIterator.next().getValue();

                    // add the weather data
                    try{
                        newWeatherMaintenanceChart.populateWeatherInfo(daysObservations, 1);
                        
                    }catch(Exception e){
                        //e.printStackTrace();
                        System.out.println("Something went wrong adding  weather info to tempAndMaintenanceChartScroller");
                    }
                    
                }
                if(isCompare){
                    this.tempAndMaintenanceChartScroller2.addNewChart(newWeatherMaintenanceChart);
                }else{
                    this.tempAndMaintenanceChartScroller.addNewChart(newWeatherMaintenanceChart);
                }
                
                
            }
        }                  
    }
    
    public void updateTempAndRoadConditionInfo(boolean isSavedDataset, String datasetName, boolean isCompare){

        if(this.tempAndRoadConditionChartScroller != null){
            
            try{
                if(isCompare){
                    this.tempAndRoadConditionChartScroller2.removeAllCharts();
                }else{
                    this.tempAndRoadConditionChartScroller.removeAllCharts();
                }
                
            
                ArrayList<RoadCondition> allConditionsForecast;
                TreeMap<LocalDate, ArrayList<WeatherObservation>> allObservations;
                TreeMap<LocalDate, ArrayList<WeatherForecast>> allForecasts;

                // is the data coming from a saved dataset or not
                if(isSavedDataset){
                    allConditionsForecast = this.presenter.allPresentConditionsForecast(true, datasetName);
                    allObservations = this.presenter.getObservationsByDate(true, datasetName);
                    allForecasts = this.presenter.getForecastsByDate(true, datasetName);
                }else{
                    allConditionsForecast = this.presenter.allPresentConditionsForecast(false, "");
                    allObservations = this.presenter.getObservationsByDate(false, "");
                    allForecasts = this.presenter.getForecastsByDate(false, "");
                }
                

                // create iterator for looping
                Iterator<Map.Entry<LocalDate, ArrayList<WeatherObservation>>> 
                    observationIterator = allObservations.entrySet().iterator();
                
                // create iterator for looping
                Iterator<Map.Entry<LocalDate, ArrayList<WeatherForecast>>> 
                    forecastIterator = allForecasts.entrySet().iterator();
                
                // create iterator for looping keys
                Iterator<LocalDate> 
                    observationDateIterator = allObservations.keySet().iterator();
                

                // iterate through the treemaps
                while(observationIterator.hasNext() || forecastIterator.hasNext()){
                    
                    // create new charts
                    WeatherRoadConditionChart weatherRoadConditionChart = new WeatherRoadConditionChart();
                    
                    if(observationIterator.hasNext()){
                        
                        LocalDate now = LocalDate.now(); 
                        LocalDate date = observationDateIterator.next();

                        if(date.isEqual(now)){
                            
                            // create the chart with tasks and a date
                            weatherRoadConditionChart.createConditionChart(allConditionsForecast);
                        }
                    }
                   
                    
                    ArrayList<WeatherObservation> daysObservations = new ArrayList<>();
                    ArrayList<WeatherForecast> daysForecasts = new ArrayList<>();
                    
                    if(observationIterator.hasNext()){
                        // get the observation array for the day
                        daysObservations = observationIterator.next().getValue();
                    }
                    
                    if(forecastIterator.hasNext()){
                        // get the forecast array for the day
                        daysForecasts = forecastIterator.next().getValue();
                    }
                    
                    if(daysObservations != null){
                        
                        // use observations if available
                        weatherRoadConditionChart.populateWeatherInfo(daysObservations, 1);

                    }else if (daysForecasts != null){
                        
                        // if observations are not available use forecasts
                        weatherRoadConditionChart.populateWeatherInfo(daysForecasts, 0);
                    }
                    else{
                        System.out.println("daysObservations and daysForecasts are null in updateTempAndRoadConditionInfo");
                    }
                    
                    if(isCompare){
                        // populate the chart scroller
                        this.tempAndRoadConditionChartScroller2.addNewChart(weatherRoadConditionChart);
                    }else{
                        // populate the chart scroller
                        this.tempAndRoadConditionChartScroller.addNewChart(weatherRoadConditionChart);
                    }
                    
            

                }
               
                
            }catch(Exception e){
                e.printStackTrace();
                System.out.println("Something went wrong in updating TempAndRoadCondition info");
                return;
            }
        }
    else{

        System.out.println("tempAndRoadConditionChartScroller is null");
    }
            

    }
    
    public void updateTemperatureInfo(boolean isSavedDataset, String datasetName, boolean isCompare) {

        if (this.temperatureChartScroller != null) {
            
            
            
            if(isCompare){
                this.temperatureChartScroller2.removeAllCharts();
            }else{
                this.temperatureChartScroller.removeAllCharts();
            }
            
            
            TreeMap<LocalDate, ArrayList<WeatherObservation>> allObservations;
            TreeMap<LocalDate, ArrayList<WeatherForecast>> allForecasts;
            
            // is the data coming from a saved dataset or not
            if(isSavedDataset){
                allObservations = this.presenter.getObservationsByDate(true, datasetName);
                allForecasts = this.presenter.getForecastsByDate(true, datasetName);
            }else{
                allObservations = this.presenter.getObservationsByDate(false, "");
                allForecasts = this.presenter.getForecastsByDate(false, "");
            }


            Iterator<Map.Entry<LocalDate, ArrayList<WeatherObservation>>>
                    observationIterator = allObservations.entrySet().iterator();

            Iterator<Map.Entry<LocalDate, ArrayList<WeatherForecast>>>
                    forecastIterator = allForecasts.entrySet().iterator();

            // iterate thorugh the two treemaps simultaneously
            while(observationIterator.hasNext() || forecastIterator.hasNext()){

                NumberAxis xAxis = null;
                NumberAxis yAxis = null;
                LineChart newTemperatureLineChart = null;
                
                if (observationIterator.hasNext()){

                    ArrayList<WeatherObservation> daysObservations
                        = observationIterator.next().getValue();

                    if(!daysObservations.isEmpty()){

                        // add observation data series
                        Series newObservationSeries =
                                this.presenter.convertToSeries(daysObservations, "temperature");
                        
                        newObservationSeries.setName("Observations"); 
                        

                        // Defining X axis (to be decided)
                        xAxis = new NumberAxis(0, 23, 1);

                        // get the max and min values
                        Double max = this.presenter.getMaxOfSeries(newObservationSeries);
                        Double min = this.presenter.getMinOfSeries(newObservationSeries);

                        // Defining y axis (to be decided)
                        yAxis = new NumberAxis(min - 0.5, max + 0.5, 1);

                        // create new chart
                        newTemperatureLineChart = new LineChart(xAxis, yAxis);

                        newTemperatureLineChart.setLegendVisible(true);
                        newTemperatureLineChart.setCreateSymbols(false); 

                        newTemperatureLineChart.getData().add(newObservationSeries);

                    }
                }

                if (forecastIterator.hasNext()){

                    ArrayList<WeatherForecast> daysForecasts
                        = forecastIterator.next().getValue();


                    if(!daysForecasts.isEmpty()){

                        // add forecast data series
                        Series newForecastSeries =
                                this.presenter.convertToSeries(daysForecasts, "temperature");
                        
                        newForecastSeries.setName("Forecasts"); 

                        if(newTemperatureLineChart == null){
                            
                            // Defining X axis (to be decided)
                            xAxis = new NumberAxis(0, 23, 0.1);

                            // get the max and min values
                            Double max = this.presenter.getMaxOfSeries(newForecastSeries);
                            Double min = this.presenter.getMinOfSeries(newForecastSeries);

                            // Defining y axis (to be decided)
                            yAxis = new NumberAxis(min - 0.5, max + 0.5, 1);

                            // create new chart
                            newTemperatureLineChart = new LineChart(xAxis, yAxis);
                            
                            newTemperatureLineChart.setLegendVisible(true);
                            newTemperatureLineChart.setCreateSymbols(false); 
                            
                        }
                        
                        newTemperatureLineChart.getData().add(newForecastSeries);
                        
                    }

                }
                
                // if still null, set empty chart
                if(newTemperatureLineChart == null){
                    
                    // Defining X axis (to be decided)
                    xAxis = new NumberAxis(0, 23, 1);

                    // get the max and min values
                    Double max = 5.0;
                    Double min = -5.0;

                    // Defining y axis (to be decided)
                    yAxis = new NumberAxis(min, max, 0.1);

                    // create new chart
                    newTemperatureLineChart = new LineChart(xAxis, yAxis);
                            
                }
                
                if(isCompare){
                    // populate the chart scroller
                    this.temperatureChartScroller2.addNewChart(newTemperatureLineChart);
                }else{
                    // populate the chart scroller
                    this.temperatureChartScroller.addNewChart(newTemperatureLineChart);
                }
                
            }

        }
    }
    
    public void updateWindInfo(boolean isSavedDataset, String datasetName, boolean isCompare) {

        if (this.windChartScroller != null) {

            if(isCompare){
                this.windChartScroller2.removeAllCharts();
            }else{
                this.windChartScroller.removeAllCharts();
            }
            
            
            TreeMap<LocalDate, ArrayList<WeatherObservation>> allObservations;
            TreeMap<LocalDate, ArrayList<WeatherForecast>> allForecasts;
            
            // is the data coming from a saved dataset or not
            if(isSavedDataset){
                allObservations = this.presenter.getObservationsByDate(true, datasetName);
                allForecasts = this.presenter.getForecastsByDate(true, datasetName);
            }else{
                allObservations = this.presenter.getObservationsByDate(false, "");
                allForecasts = this.presenter.getForecastsByDate(false, "");
            }

            Iterator<Map.Entry<LocalDate, ArrayList<WeatherObservation>>>
                    observationIterator = allObservations.entrySet().iterator();

            Iterator<Map.Entry<LocalDate, ArrayList<WeatherForecast>>>
                    forecastIterator = allForecasts.entrySet().iterator();

            // iterate thorugh the two treemaps simultaneously
            while(observationIterator.hasNext() || forecastIterator.hasNext()){

                NumberAxis xAxis = null;
                NumberAxis yAxis = null;
                LineChart newLineChart = null;
                
                if (observationIterator.hasNext()){

                    ArrayList<WeatherObservation> daysObservations
                        = observationIterator.next().getValue();

                    if(!daysObservations.isEmpty()){

                        // add observation data series
                        Series newObservationSeries =
                                this.presenter.convertToSeries(daysObservations, "windspeed");
                        
                        newObservationSeries.setName("Observations"); 
                        

                        // Defining X axis (to be decided)
                        xAxis = new NumberAxis(0, 23, 1);

                        // get the max and min values
                        Double max = this.presenter.getMaxOfSeries(newObservationSeries);
                        Double min = this.presenter.getMinOfSeries(newObservationSeries);

                        // Defining y axis (to be decided)
                        yAxis = new NumberAxis(min - 0.5, max + 0.5, 1);

                        // create new chart
                        newLineChart = new LineChart(xAxis, yAxis);

                        newLineChart.setLegendVisible(true);
                        newLineChart.setCreateSymbols(false); 

                        newLineChart.getData().add(newObservationSeries);

                    }
                }

                if (forecastIterator.hasNext()){

                    ArrayList<WeatherForecast> daysForecasts
                        = forecastIterator.next().getValue();


                    if(!daysForecasts.isEmpty()){

                        // add forecast data series
                        Series newForecastSeries =
                                this.presenter.convertToSeries(daysForecasts, "windspeed");
                        
                        newForecastSeries.setName("Forecasts"); 

                        if(newLineChart == null){
                            
                            // Defining X axis (to be decided)
                            xAxis = new NumberAxis(0, 23, 0.1);

                            // get the max and min values
                            Double max = this.presenter.getMaxOfSeries(newForecastSeries);
                            Double min = this.presenter.getMinOfSeries(newForecastSeries);

                            // Defining y axis (to be decided)
                            yAxis = new NumberAxis(min - 0.5, max + 0.5, 1);

                            // create new chart
                            newLineChart = new LineChart(xAxis, yAxis);
                            
                            newLineChart.setLegendVisible(true);
                            newLineChart.setCreateSymbols(false); 
                            
                            
                        }
                        
                        newLineChart.getData().add(newForecastSeries);
                        
                    }

                }
                
                // if still null, set empty chart
                if(newLineChart == null){
                    
                    // Defining X axis (to be decided)
                    xAxis = new NumberAxis(0, 23, 1);

                    // get the max and min values
                    Double max = 5.0;
                    Double min = -5.0;

                    // Defining y axis (to be decided)
                    yAxis = new NumberAxis(min, max, 0.1);

                    // create new chart
                    newLineChart = new LineChart(xAxis, yAxis);
                            
                }
                
                if(isCompare){
                    // populate the chart scroller
                    this.windChartScroller2.addNewChart(newLineChart);
                }else{
                    // populate the chart scroller
                    this.windChartScroller.addNewChart(newLineChart);
                }

            }

        }
    }
    
    public void updateCloudinessInfo(boolean isSavedDataset, String datasetName) {

        if (this.cloudinessChartScroller != null) {
            
            this.cloudinessChartScroller.removeAllCharts();
            
            TreeMap<LocalDate, ArrayList<WeatherObservation>> allObservations;
            TreeMap<LocalDate, ArrayList<WeatherForecast>> allForecasts;
            
            // is the data coming from a saved dataset or not
            if(isSavedDataset){
                allObservations = this.presenter.getObservationsByDate(true, datasetName);
                allForecasts = this.presenter.getForecastsByDate(true, datasetName);
            }else{
                allObservations = this.presenter.getObservationsByDate(false, "");
                allForecasts = this.presenter.getForecastsByDate(false, "");
            }

            Iterator<Map.Entry<LocalDate, ArrayList<WeatherObservation>>>
                    observationIterator = allObservations.entrySet().iterator();

            Iterator<Map.Entry<LocalDate, ArrayList<WeatherForecast>>>
                    forecastIterator = allForecasts.entrySet().iterator();

            // iterate thorugh the two treemaps simultaneously
            while(observationIterator.hasNext() || forecastIterator.hasNext()){

                NumberAxis xAxis = null;
                NumberAxis yAxis = null;
                LineChart newLineChart = null;
                
                if (observationIterator.hasNext()){

                    ArrayList<WeatherObservation> daysObservations
                        = observationIterator.next().getValue();

                    if(!daysObservations.isEmpty()){

                        // add observation data series
                        Series newObservationSeries =
                                this.presenter.convertToSeries(daysObservations, "cloudiness");
                        
                        newObservationSeries.setName("Observations"); 
                        

                        // Defining X axis (to be decided)
                        xAxis = new NumberAxis(0, 23, 1);

                        // get the max and min values
                        Double max = this.presenter.getMaxOfSeries(newObservationSeries);
                        Double min = this.presenter.getMinOfSeries(newObservationSeries);

                        // Defining y axis (to be decided)
                        yAxis = new NumberAxis(min - 0.5, max + 0.5, 1);

                        // create new chart
                        newLineChart = new LineChart(xAxis, yAxis);

                        newLineChart.setLegendVisible(true);
                        newLineChart.setCreateSymbols(false); 

                        newLineChart.getData().add(newObservationSeries);

                    }
                }

                if (forecastIterator.hasNext()){

                    ArrayList<WeatherForecast> daysForecasts
                        = forecastIterator.next().getValue();


                    if(!daysForecasts.isEmpty()){

                        // add forecast data series
                        Series newForecastSeries =
                                this.presenter.convertToSeries(daysForecasts, "cloudiness");
                        
                        newForecastSeries.setName("Forecasts"); 

                        if(newLineChart == null){
                            
                            // Defining X axis (to be decided)
                            xAxis = new NumberAxis(0, 23, 0.1);

                            // get the max and min values
                            Double max = this.presenter.getMaxOfSeries(newForecastSeries);
                            Double min = this.presenter.getMinOfSeries(newForecastSeries);

                            // Defining y axis (to be decided)
                            yAxis = new NumberAxis(min - 0.5, max + 0.5, 1);

                            // create new chart
                            newLineChart = new LineChart(xAxis, yAxis);
                            
                            newLineChart.setLegendVisible(true);
                            newLineChart.setCreateSymbols(false); 
                            
                            
                        }
                        
                        newLineChart.getData().add(newForecastSeries);
                        
                    }

                }
                
                // if still null, set empty chart
                if(newLineChart == null){
                    
                    // Defining X axis (to be decided)
                    xAxis = new NumberAxis(0, 23, 1);

                    // get the max and min values
                    Double max = 5.0;
                    Double min = -5.0;

                    // Defining y axis (to be decided)
                    yAxis = new NumberAxis(min, max, 0.1);

                    // create new chart
                    newLineChart = new LineChart(xAxis, yAxis);
                            
                }

                // populate the chart scroller
                this.cloudinessChartScroller.addNewChart(newLineChart);
            }

        }
    } 
    
    public void updateThisMonthsInfo(){
        
        // get the labels for the info
        Label avgMonthTempLabel = (Label) this.thisMonthsWeatherPane.lookup("#avgMonthTempLabel");
        Label maxMonthTempLabel = (Label) this.thisMonthsWeatherPane.lookup("#maxMonthTempLabel");
        Label minMonthTempLabel = (Label) this.thisMonthsWeatherPane.lookup("#minMonthTempLabel");

        // get the current month
        Month currentMonth = LocalDate.now().getMonth();
        
        // get the averages of current month
        WeatherDataCalculator.WeatherDataAverages averages 
                = this.presenter.getFMIObservationAveragesOfMonth(currentMonth);
        
        // get the average temperature
        Double monthsAverageTemp = averages.getAverTemperature();
        
        // get the max temperature
        Double monthsMaxTemp = averages.getMaxTemperature();
        
        // get the min temperature
        Double monthsMinTemp = averages.getMinTemperature();
        
        // set the labels
        if(monthsAverageTemp.toString().length() == 3){
            avgMonthTempLabel.setText(monthsAverageTemp.toString().substring(0, 3));
        }else{
            avgMonthTempLabel.setText(monthsAverageTemp.toString().substring(0, 4));
        }
        
        if(monthsMaxTemp.toString().length() == 3){
            maxMonthTempLabel.setText(monthsMaxTemp.toString().substring(0, 3));
        }else{
            
            maxMonthTempLabel.setText(monthsMaxTemp.toString().substring(0, 4));
        }
        
        if(monthsMinTemp.toString().length() == 3){
            minMonthTempLabel.setText(monthsMinTemp.toString().substring(0, 3)); 
        }else{
            minMonthTempLabel.setText(monthsMinTemp.toString().substring(0, 4));
        }
        
    }
    
    public void saveRecentDataset(){
        
        boolean result = false;
        
        if(searchWasMade){
            result = this.presenter.saveRecentDataset(viewState);
        }
        if(result){
            System.out.println("Saving the dataset was successful");
            updateSavedDatasets();
        }else{
            System.out.println("Saving the dataset was not successful");
        }
        
    }
    
    public void deleteDataset(){
        
        Object value = savedDatasetsComboBox.getValue();
        String chosenDataset;
        
        if(value != null){
            
            chosenDataset = savedDatasetsComboBox.getValue().toString();
            
            // call presenter to delete the dataset
            this.presenter.deleteDataset(chosenDataset);
            this.updateSavedDatasets();
        }
        
    }
    
    // visualize a saved dataset
    public void visualizeDatasetButtonPressed(){
        
        // get the chosen dataset
        Object value = savedDatasetsComboBox.getValue();
        String chosenDataset;
        
        // if nothing is chosen do nothing
        if(value != null){
            
            chosenDataset = savedDatasetsComboBox.getValue().toString();
            
            // Visualize the dataset
            visualiseSavedDataset(chosenDataset);
            
        }
        
    }
    
    private void visualiseSavedDataset(String datasetName){
        
        // determine what type of data it is
        if(datasetName.substring(0,14).equals("RoadAndWeather")){
            
            // if the view is not already in the correct state
            if(this.viewState != ViewState.ROAD_AND_WEATHER){
                
                // clear the old data
                this.mainGridPane.getChildren().clear();
                
                // load the fxmlpanes to the view
                loadRoadAndWeatherPanesFXML();
                
                // initialize the chartscrollers
                initializeComboPanes();
                
                // set the state of the view
                this.viewState = ViewState.ROAD_AND_WEATHER;
            }
            
            // method for updating the panes with data
            updateTempAndMaintenanceInfo(true, datasetName, false);
            updateTempAndRoadConditionInfo(true, datasetName, false);
            updateTrafficMessages(true, datasetName, false);
            
          isSavedData = true;
        }
        else if(datasetName.substring(0,4).equals("Road")){
            
            // if the view is not already in the correct state
            if(this.viewState != ViewState.ROAD){
                
                // clear the old data
                this.mainGridPane.getChildren().clear();
                
                // load the fxmlpanes to the view
                loadRoadPanesFXML();
                
                initializeRoadPanes();
                
                // set the state of the view
                this.viewState = ViewState.ROAD;
            }

            // method for updating the panes with data
            updateCurrentInfo(true, datasetName);
            updateRoadForecastInfo(true, datasetName);
            updateTrafficMessages(true, datasetName, false);
            updateMaintenanceInfo(true, datasetName, false);
            
            isSavedData = true;
            
        }else if(datasetName.substring(0,7).equals("Weather")){
            
            // if the view is not already in the correct state
            if(this.viewState != ViewState.WEATHER){
                
                // clear the old data
                this.mainGridPane.getChildren().clear();
                
                // load the fxmlpanes to the view
                loadWeatherPanesFXML();
                
                initializeWeatherPanes();
                
                // set the state of the view
                this.viewState = ViewState.WEATHER;
            }

            // method for updating the panes with data
            updateTemperatureInfo(true, datasetName, false);
            updateWindInfo(true, datasetName, false);
            updateCloudinessInfo(true, datasetName);
            
            isSavedData = true;

        }
        
    }
    
    public void compareSavedDataset(){
        
        if (!validateSearch()) {
            return;
        }

        Object value = savedDatasetsComboBox.getValue();
        String chosenDataset;
        
        // some dataset has to be chosen
        if(value != null){
            
            double[] coordinates = getCoordinates();
            LocalDate fromDate = fromDatePicker.getValue();
            LocalDate toDate = toDatePicker.getValue();
            String from = fromTime.getText();
            String to = toTime.getText();

            this.presenter.setAll(coordinates, fromDate, toDate, from, to);
            

            // get the chosen dataset name
            chosenDataset = value.toString();

            
            if(chosenDataset.substring(0,14).equals("RoadAndWeather")){
                
                if (digitrafficCheckBox.isSelected() && FMICheckBox.isSelected()) {
                    
                    if(this.viewState == ViewState.ROAD_AND_WEATHER_COMPARE){
                        
                        // fetch data
                        this.presenter.runModelFetches(ViewState.ROAD_AND_WEATHER);

                        // load the new data and update the panes
                        updateTempAndMaintenanceInfo(false, "", false);
                        updateTempAndRoadConditionInfo(false, "", false);
                        
                        // load the new data and update the panes
                        updateTempAndMaintenanceInfo(true, chosenDataset, true);
                        updateTempAndRoadConditionInfo(true, chosenDataset, true);
                        
                        
                    }else{
                        // clear the old data
                        this.mainGridPane.getChildren().clear();

                        // change state of the view
                        this.viewState = ViewState.ROAD_AND_WEATHER_COMPARE;


                        loadRoadAndWeatherPanesForComparing();
                        InitializeRoadAndWeatherDataForComparing();

                        // fetch data
                        this.presenter.runModelFetches(ViewState.ROAD_AND_WEATHER);

                       // load the new data and update the panes
                        updateTempAndMaintenanceInfo(false, "", false);
                        updateTempAndRoadConditionInfo(false, "", false);

                        // load the new data and update the panes
                        updateTempAndMaintenanceInfo(true, chosenDataset, true);
                        updateTempAndRoadConditionInfo(true, chosenDataset, true);
                    }
                    
                }
                isSavedData = true;
            }else if(chosenDataset.substring(0,4).equals("Road")){
                
                if (digitrafficCheckBox.isSelected() ) {
                    
                    if(this.viewState == ViewState.ROAD_COMPARE){
                        
                        // fetch data
                        this.presenter.runModelFetches(ViewState.ROAD);
                        
                        updateTrafficMessages(false, "", true);
                        updateMaintenanceInfo(false, "", true);
                        
                        updateTrafficMessages(true, chosenDataset, true);
                        updateMaintenanceInfo(true, chosenDataset, true);
                        
                        
                    }else{
                        // clear the old data
                        this.mainGridPane.getChildren().clear();

                        // change state of the view
                        this.viewState = ViewState.ROAD_COMPARE;


                        loadRoadPanesForComparing();
                        InitializeRoadDataForComparing();
                        initializeRoadPanes();


                        // fetch data
                        this.presenter.runModelFetches(ViewState.ROAD);

                        updateTrafficMessages(false, "", false);
                        updateMaintenanceInfo(false, "", false);

                        updateTrafficMessages(true, chosenDataset, true);
                        updateMaintenanceInfo(true, chosenDataset, true);
                    }
                    
                }
                isSavedData = true;
            }else if(chosenDataset.substring(0,7).equals("Weather")){
                
                if (FMICheckBox.isSelected() ) {
                    
                    if(this.viewState == ViewState.WEATHER_COMPARE){
                        
                        // fetch data
                        this.presenter.runModelFetches(ViewState.WEATHER);
                        
                        updateTemperatureInfo(false, "", false);
                        updateWindInfo(false, "", false);
                        
                        updateTemperatureInfo(true, chosenDataset, true);
                        updateWindInfo(true, chosenDataset, true);

                    }else{
                        // clear the old data
                        this.mainGridPane.getChildren().clear();

                        // change state of the view
                        this.viewState = ViewState.WEATHER_COMPARE;


                        loadWeatherPanesForComparing();
                        InitializeWeatherDataForComparing();

                        // fetch data
                        this.presenter.runModelFetches(ViewState.WEATHER);

                        updateTemperatureInfo(false, "", false);
                        updateWindInfo(false, "", false);

                        updateTemperatureInfo(true, chosenDataset, true);
                        updateWindInfo(true, chosenDataset, true);
                    }
                    
                }
                isSavedData = true;

                
            }else{
                return; // name is corrupted 
            }


            // move the search menu out of the window
            leftMenuButtonPressed = false;
            TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(0.5), menuPane);
            translateTransition1.setByX(-width);
            translateTransition1.play();

            searchErrorLabel.setVisible(false);

            searchWasMade = true;
            isSavedData = true;
        }
        
        
    }
    
    private void loadRoadAndWeatherPanesForComparing(){
        
        try {
            this.TempAndMaintenancePane = FXMLLoader.load(getClass().getResource("/fxml/TempAndMaintenancePane.fxml"));
            this.mainGridPane.add(this.TempAndMaintenancePane, 0, 0);
        } catch (Exception e) {
            System.out.println("Can't load TempAndMaintenancePane ");
        }


        try {
            this.TempAndRoadConditionPane = FXMLLoader.load(getClass().getResource("/fxml/TempAndRoadConditionPane.fxml"));
            this.mainGridPane.add(this.TempAndRoadConditionPane, 1, 0);
        } catch (Exception e) {
                System.out.println("Can't load TempAndRoadConditionPane ");
        }

        try {
            this.TempAndMaintenancePane2 = FXMLLoader.load(getClass().getResource("/fxml/TempAndMaintenancePane.fxml"));
            mainGridPane.add(TempAndMaintenancePane2, 0, 1);
        } catch (Exception e) {
            System.out.println("Can't load traffic message pane");
        }

        try {
            this.TempAndRoadConditionPane2 = FXMLLoader.load(getClass().getResource("/fxml/TempAndRoadConditionPane.fxml"));
            mainGridPane.add(TempAndRoadConditionPane2, 1, 1);
        } catch (Exception e) {
            System.out.println("Can't load this months weather pane");
        }
    }
    
    private void loadRoadPanesForComparing(){
        
        try {
            maintenancePane = FXMLLoader.load(getClass().getResource("/fxml/MaintenancePane.fxml"));
            mainGridPane.add(maintenancePane, 0, 0);
        } catch (Exception e) {
            System.out.println("Can't load maintenance pane");
        }


        try {
            TrafficMessagesPane = FXMLLoader.load(getClass().getResource("/fxml/TrafficMessagePane.fxml"));
            mainGridPane.add(TrafficMessagesPane, 0, 1);
        } catch (Exception e) {
            System.out.println("Can't load traffic message pane");
        }
        
        try {
            maintenancePane2 = FXMLLoader.load(getClass().getResource("/fxml/MaintenancePane.fxml"));
            mainGridPane.add(maintenancePane2, 1, 0);
        } catch (Exception e) {
            System.out.println("Can't load maintenance pane");
        }


        try {
            TrafficMessagesPane2 = FXMLLoader.load(getClass().getResource("/fxml/TrafficMessagePane.fxml"));
            mainGridPane.add(TrafficMessagesPane2, 1, 1);
        } catch (Exception e) {
            System.out.println("Can't load traffic message pane");
        }
}
       
    private void loadWeatherPanesForComparing(){

        try {
            temperaturePane = FXMLLoader.load(getClass().getResource("/fxml/TemperaturePane.fxml"));
            mainGridPane.add(temperaturePane, 0, 0);
        } catch (Exception e) {
            System.out.println("Can't load temperature pane");
        }

        try {
            windPane = FXMLLoader.load(getClass().getResource("/fxml/WindPane.fxml"));
            mainGridPane.add(windPane, 1, 0);
        } catch (Exception e) {
            System.out.println("Can't load wind pane");
        }
        
        try {
            temperaturePane2 = FXMLLoader.load(getClass().getResource("/fxml/TemperaturePane.fxml"));
            mainGridPane.add(temperaturePane2, 0, 1);
        } catch (Exception e) {
            System.out.println("Can't load temperature pane");
        }

        try {
            windPane2 = FXMLLoader.load(getClass().getResource("/fxml/WindPane.fxml"));
            mainGridPane.add(windPane2, 1, 1);
        } catch (Exception e) {
            System.out.println("Can't load wind pane");
        }

        
    }
    
    
    
    private void InitializeRoadAndWeatherDataForComparing(){
        
        // initialize the tempAndMaintenanceChartScroller and place on canvas
        this.tempAndMaintenanceChartScroller = new ChartScroller();
        this.TempAndMaintenancePane.getChildren().add(this.tempAndMaintenanceChartScroller);
        AnchorPane.setLeftAnchor(this.tempAndMaintenanceChartScroller, 13.0);
        AnchorPane.setRightAnchor(this.tempAndMaintenanceChartScroller, 13.0);
        AnchorPane.setBottomAnchor(this.tempAndMaintenanceChartScroller, 0.0);
        
        // initialize the tempAndRoadConditionChartScroller and place on canvas
        this.tempAndRoadConditionChartScroller = new ChartScroller();
        this.TempAndRoadConditionPane.getChildren().add(this.tempAndRoadConditionChartScroller);
        AnchorPane.setLeftAnchor(this.tempAndRoadConditionChartScroller, 13.0);
        AnchorPane.setRightAnchor(this.tempAndRoadConditionChartScroller, 13.0);
        AnchorPane.setBottomAnchor(this.tempAndRoadConditionChartScroller, 0.0);
        
        // initialize the tempAndMaintenanceChartScroller2 and place on canvas
        this.tempAndMaintenanceChartScroller2 = new ChartScroller();
        this.TempAndMaintenancePane2.getChildren().add(this.tempAndMaintenanceChartScroller2);
        AnchorPane.setLeftAnchor(this.tempAndMaintenanceChartScroller2, 13.0);
        AnchorPane.setRightAnchor(this.tempAndMaintenanceChartScroller2, 13.0);
        AnchorPane.setBottomAnchor(this.tempAndMaintenanceChartScroller2, 0.0);
        
        // initialize the tempAndRoadConditionChartScroller2 and place on canvas
        this.tempAndRoadConditionChartScroller2 = new ChartScroller();
        this.TempAndRoadConditionPane2.getChildren().add(this.tempAndRoadConditionChartScroller2);
        AnchorPane.setLeftAnchor(this.tempAndRoadConditionChartScroller2, 13.0);
        AnchorPane.setRightAnchor(this.tempAndRoadConditionChartScroller2, 13.0);
        AnchorPane.setBottomAnchor(this.tempAndRoadConditionChartScroller2, 0.0);
    }
    
    private void InitializeRoadDataForComparing(){
        
        // initialize the maintenance chart scroller and place on canvas
        maintenanceChartScroller2 = new ChartScroller();
        maintenanceChartScroller2.showInfoIcon(true); // show info icon for task averages
        maintenancePane2.getChildren().add(maintenanceChartScroller2);
        AnchorPane.setLeftAnchor(maintenanceChartScroller2, 13.0);
        AnchorPane.setRightAnchor(maintenanceChartScroller2, 13.0);
        AnchorPane.setBottomAnchor(maintenanceChartScroller2, 0.0);

        // initialize the traffic messages scroller and place on canvas
        trafficMessageScroller2 = new ChartScroller();
        trafficMessageScroller2.setButtonHeight(75.0);
        TrafficMessagesPane2.getChildren().add(trafficMessageScroller2);
        AnchorPane.setLeftAnchor(trafficMessageScroller2, 13.0);
        AnchorPane.setRightAnchor(trafficMessageScroller2, 13.0);
        AnchorPane.setBottomAnchor(trafficMessageScroller2, 0.0);
        
    }
    
    private void InitializeWeatherDataForComparing(){
        
        // initialize the temperature chart scroller and place on canvas
        this.temperatureChartScroller = new ChartScroller();
        this.temperatureChartScroller.lineChartMode(true);
        this.temperaturePane.getChildren().add(this.temperatureChartScroller);
        AnchorPane.setLeftAnchor(this.temperatureChartScroller, 8.0);
        AnchorPane.setBottomAnchor(this.temperatureChartScroller, 0.0);
        
        
        // initialize the wind chart scroller and place on canvas
        this.windChartScroller = new ChartScroller();
        this.windChartScroller.lineChartMode(true);
        this.windPane.getChildren().add(this.windChartScroller);
        AnchorPane.setLeftAnchor(this.windChartScroller, 8.0);
        AnchorPane.setBottomAnchor(this.windChartScroller, 0.0);
        
        
        // initialize the temperature chart scroller and place on canvas
        this.temperatureChartScroller2 = new ChartScroller();
        this.temperatureChartScroller2.lineChartMode(true);
        this.temperaturePane2.getChildren().add(this.temperatureChartScroller2);
        AnchorPane.setLeftAnchor(this.temperatureChartScroller2, 8.0);
        AnchorPane.setBottomAnchor(this.temperatureChartScroller2, 0.0);
        
        
        // initialize the wind chart scroller and place on canvas
        this.windChartScroller2 = new ChartScroller();
        this.windChartScroller2.lineChartMode(true);
        this.windPane2.getChildren().add(this.windChartScroller2);
        AnchorPane.setLeftAnchor(this.windChartScroller2, 8.0);
        AnchorPane.setBottomAnchor(this.windChartScroller2, 0.0);
    }
    


}

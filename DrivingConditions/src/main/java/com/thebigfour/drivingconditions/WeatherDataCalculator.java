package com.thebigfour.drivingconditions;

import java.time.*;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 *
 * @author miika.hamalainen@tuni.fi
 */

public class WeatherDataCalculator {


    TreeMap<LocalDate, WeatherDataAverages> dataPerDayObs = new TreeMap<>();
    TreeMap<LocalDate, WeatherDataAverages> dataPerDayForec = new TreeMap<>();
    TreeMap<java.time.Month, WeatherDataAverages> dataPerMonthObs = new TreeMap<>();
    TreeMap<java.time.Month, WeatherDataAverages> dataPerMonthForec = new TreeMap<>();

    boolean isReady = false;

    public class WeatherDataAverages {

        private LocalTime time;
        private LocalDate date;
        private String location;
        private double averWindSpeed; //Average wind speed (daily or monthly)
        private double maxWindSpeed; //Max wind speed (daily or monthly)
        private double minWindSpeed; //Min wind speed (daily or monthly)
        private double maxAverWindSpeed; //Maximum of the averages of daily wind speeds (for month)
        private double minAverWindSpeed; //Minimum of the averages of daily wind speeds (for month)
        private double averCloudiness;
        private double maxCloudiness;
        private double minCloudiness;
        private double maxAverCloudiness;
        private double minAverCloudiness;
        private double averTemperature;
        private double maxTemperature;
        private double minTemperature;
        private double maxAverTemperature;
        private double minAverTemperature;


        public LocalDate getDate() {
            return date;
        }

        public String getLocation() {
            return location;
        }

        public double getAverCloudiness() {
            return averCloudiness;
        }

        public double getAverTemperature() {
            return averTemperature;
        }

        public double getAverWindSpeed() {
            return averWindSpeed;
        }

        public double getMaxCloudiness() {
            return maxCloudiness;
        }

        public double getMaxTemperature() {
            return maxTemperature;
        }

        public double getMaxWindSpeed() {
            return maxWindSpeed;
        }

        public double getMinCloudiness() {
            return minCloudiness;
        }

        public double getMinTemperature() {
            return minTemperature;
        }

        public double getMinWindSpeed() {
            return minWindSpeed;
        }

        public double getMaxAverCloudiness() {
            return maxAverCloudiness;
        }

        public double getMaxAverTemperature() {
            return maxAverTemperature;
        }

        public double getMaxAverWindSpeed() {
            return maxAverWindSpeed;
        }

        public double getMinAverCloudiness() {
            return minAverCloudiness;
        }

        public double getMinAverTemperature() {
            return minAverTemperature;
        }

        public double getMinAverWindSpeed() {
            return minAverWindSpeed;
        }

        public LocalTime getTime() {
            return time;
        }
        public void clear(){
            time = LocalTime.of(0,0,0);
            date = LocalDate.of(0,0,0);
            location = "";
            averWindSpeed = 0.0;
            maxWindSpeed = 0.0;
            minWindSpeed = 0.0;
            maxAverWindSpeed = 0.0;
            minAverWindSpeed = 0.0;
            averCloudiness = 0.0;
            maxCloudiness = 0.0;
            minCloudiness = 0.0;
            maxAverCloudiness = 0.0;
            minAverCloudiness = 0.0;
            averTemperature = 0.0;
            maxTemperature = 0.0;
            minTemperature = 0.0;
            maxAverTemperature = 0.0;
            minAverTemperature = 0.0;

        }

    }

    public void calculate(TreeMap<LocalDate, ArrayList<WeatherObservation>> observationsByDate, TreeMap<LocalDate, ArrayList<WeatherForecast>> forecastsByDate){
        if(!isReady){
            averageObservationData(observationsByDate);
            averageForecastData(forecastsByDate);
            isReady = true;
        }
    }

    private void averageObservationData(TreeMap<LocalDate, ArrayList<WeatherObservation>> observationsByDate){

        if (observationsByDate.isEmpty()) {
            return;
        }

        System.out.println("Beginning calculations...");
        //Store daily averages a single month at a time
        ArrayList<WeatherDataAverages> averagesDay = new ArrayList<>(50);

        //Go through dates in the sorted map
        for (LocalDate date : observationsByDate.keySet()) {
            ArrayList<WeatherObservation> observations = observationsByDate.get(date);
            WeatherDataAverages data = new WeatherDataAverages();
            //Averages
            data.averCloudiness = 0.0;
            data.averTemperature = 0.0;
            data.averWindSpeed = 0.0;
            //Maximums
            data.maxCloudiness = 0.0;
            data.maxTemperature = 0.0;
            data.maxWindSpeed = 0.0;
            //Minimums
            data.minCloudiness = 0.0;
            data.minTemperature = 0.0;
            data.minWindSpeed = 0.0;

            //Daily averages, max and min
            for (WeatherObservation wo : observations ) {

                //Averages:
                //Temperature
                data.averTemperature += Double.parseDouble(wo.getTemperature());
                data.averTemperature = data.averTemperature/2;
                //Cloudiness
                data.averCloudiness += Double.parseDouble(wo.getCloudiness());
                data.averCloudiness = data.averCloudiness/2;
                //Windiness
                data.averWindSpeed += Double.parseDouble(wo.getWindSpeed());
                data.averWindSpeed = data.averWindSpeed/2;

                //Maximums
                if (data.maxTemperature < Double.parseDouble(wo.getTemperature())) {
                    data.maxTemperature = Double.parseDouble(wo.getTemperature());
                }
                if(data.maxCloudiness < Double.parseDouble(wo.getCloudiness())){
                    data.maxCloudiness = Double.parseDouble(wo.getCloudiness());
                }
                if(data.maxWindSpeed < Double.parseDouble(wo.getWindSpeed())){
                    data.maxWindSpeed = Double.parseDouble(wo.getWindSpeed());
                }

                //Minimums
                if (data.minTemperature > Double.parseDouble(wo.getTemperature())) {
                    data.minTemperature = Double.parseDouble(wo.getTemperature());
                }
                if(data.minCloudiness > Double.parseDouble(wo.getCloudiness())){
                    data.minCloudiness = Double.parseDouble(wo.getCloudiness());
                }
                if(data.minWindSpeed > Double.parseDouble(wo.getWindSpeed())){
                    data.minWindSpeed = Double.parseDouble(wo.getWindSpeed());
                }
            }

            //Add this date into collection
            data.date = date;
            averagesDay.add(data);
            //Save the result
            dataPerDayObs.put(date,data);
            /*if(date.isEqual(LocalDate.now())){
                System.out.println("This date found.");
                System.out.println("Today the max is: "+dataPerDayObs.get(LocalDate.now()).getMaxTemperature());
                System.out.println("Today the average is: "+dataPerDayObs.get(LocalDate.now()).getAverTemperature());
            }*/

            //If the date was the last day of the month, calculate monthly average based on daily averages
            if(date.getDayOfMonth() == date.lengthOfMonth() || date.equals(observationsByDate.lastKey())){

                System.out.println("Calculating month " + date.getMonth());
                WeatherDataAverages dataMonth = new WeatherDataAverages();
                dataMonth.averTemperature = 0.0;
                dataMonth.averCloudiness = 0.0;
                dataMonth.averWindSpeed = 0.0;
                dataMonth.maxTemperature = 0.0;
                dataMonth.maxCloudiness = 0.0;
                dataMonth.maxWindSpeed = 0.0;
                dataMonth.minTemperature = 0.0;
                dataMonth.minCloudiness = 0.0;
                dataMonth.minWindSpeed = 0.0;
                dataMonth.maxAverTemperature = 0.0;
                dataMonth.maxAverCloudiness = 0.0;
                dataMonth.maxAverWindSpeed = 0.0;
                dataMonth.minAverTemperature = 0.0;
                dataMonth.minAverCloudiness = 0.0;
                dataMonth.minAverWindSpeed = 0.0;

                for (WeatherDataAverages avr : averagesDay){

                    //Temperature
                    dataMonth.averTemperature += avr.averTemperature;
                    dataMonth.averTemperature = dataMonth.averTemperature/2;
                    //Cloudiness
                    dataMonth.averCloudiness += avr.averCloudiness;
                    dataMonth.averCloudiness = dataMonth.averCloudiness/2;
                    //Windiness
                    dataMonth.averWindSpeed += avr.averWindSpeed;
                    dataMonth.averWindSpeed = dataMonth.averWindSpeed/2;

                    //Average Maximums
                    dataMonth.maxAverTemperature = avr.maxTemperature;
                    dataMonth.maxAverTemperature = dataMonth.maxAverTemperature/2;

                    dataMonth.maxAverCloudiness = avr.maxCloudiness;
                    dataMonth.maxAverCloudiness = dataMonth.maxAverCloudiness/2;

                    dataMonth.maxAverWindSpeed = avr.maxWindSpeed;
                    dataMonth.maxAverWindSpeed = dataMonth.maxAverWindSpeed/2;

                    //Average Minimums
                    dataMonth.minAverTemperature = avr.minTemperature;
                    dataMonth.minAverTemperature = dataMonth.minAverTemperature/2;

                    dataMonth.minAverCloudiness = avr.minCloudiness;
                    dataMonth.minAverCloudiness = dataMonth.minAverCloudiness/2;

                    dataMonth.minAverWindSpeed = avr.minWindSpeed;
                    dataMonth.minAverWindSpeed = dataMonth.minAverWindSpeed/2;

                    //Absolute maximums
                    if (dataMonth.maxTemperature < avr.maxTemperature) {
                        dataMonth.maxTemperature = avr.maxTemperature;
                    }
                    if(dataMonth.maxCloudiness < avr.maxCloudiness){
                        dataMonth.maxCloudiness = avr.maxCloudiness;
                    }
                    if(dataMonth.maxWindSpeed < avr.maxWindSpeed){
                        dataMonth.maxWindSpeed = avr.maxWindSpeed;
                    }

                    //Absolute Minimums
                    if (dataMonth.minTemperature > avr.minTemperature) {
                        dataMonth.minTemperature = avr.minTemperature;
                    }
                    if(dataMonth.minCloudiness > avr.minCloudiness){
                        dataMonth.minCloudiness = avr.minCloudiness;
                    }
                    if(dataMonth.minWindSpeed > avr.minWindSpeed){
                        dataMonth.minWindSpeed = avr.minWindSpeed;
                    }

                }

                //Clear monthly averages for next month
                averagesDay.clear();
                //Save the result
                dataPerMonthObs.put(date.getMonth(),dataMonth);
            }

        }
    }
    public TreeMap<LocalDate, WeatherDataAverages> getDailyObservationAverages(){
        return dataPerDayObs;
    }

    public WeatherDataAverages getObservationAveragesOfDate(LocalDate date){
        if (dataPerDayObs.containsKey(date)) {
            return dataPerDayObs.get(date);
        }
        return null;
    }
    public TreeMap<java.time.Month, WeatherDataAverages> getMonthlyObservationAverages(){
        return dataPerMonthObs;
    }

    public WeatherDataAverages getObservationAveragesOfMonth(java.time.Month month){
        if (dataPerMonthObs.containsKey(month)) {
            return dataPerMonthObs.get(month);
        }
        return null;
    }

    private void averageForecastData(TreeMap<LocalDate, ArrayList<WeatherForecast>> forecastsByDate){

        if (forecastsByDate.isEmpty()) {
            return;
        }

        //Store daily averages a single month at a time
        ArrayList<WeatherDataAverages> averagesDay = new ArrayList<>();

        //Go through dates in the sorted map
        for (LocalDate date : forecastsByDate.keySet()) {
            ArrayList<WeatherForecast> observations = forecastsByDate.get(date);
            WeatherDataAverages data = new WeatherDataAverages();
            //Averages
            data.averCloudiness = 0.0;
            data.averTemperature = 0.0;
            data.averWindSpeed = 0.0;
            //Maximums
            data.maxCloudiness = 0.0;
            data.maxTemperature = 0.0;
            data.maxWindSpeed = 0.0;
            //Minimums
            data.minCloudiness = 0.0;
            data.minTemperature = 0.0;
            data.minWindSpeed = 0.0;

            //Daily averages, max and min
            for (WeatherForecast wo : observations ) {

                //Averages:
                //Temperature
                data.averTemperature += Double.parseDouble(wo.getTemperature());
                data.averTemperature = data.averTemperature/2;
                //Cloudiness
                data.averCloudiness += Double.parseDouble(wo.getCloudiness());
                data.averCloudiness = data.averCloudiness/2;
                //Windiness
                data.averWindSpeed += Double.parseDouble(wo.getWindSpeed());
                data.averWindSpeed = data.averWindSpeed/2;

                //Maximums
                if (data.maxTemperature < Double.parseDouble(wo.getTemperature())) {
                    data.maxTemperature = Double.parseDouble(wo.getTemperature());
                }
                if(data.maxCloudiness < Double.parseDouble(wo.getCloudiness())){
                    data.maxCloudiness = Double.parseDouble(wo.getCloudiness());
                }
                if(data.maxWindSpeed < Double.parseDouble(wo.getWindSpeed())){
                    data.maxWindSpeed = Double.parseDouble(wo.getWindSpeed());
                }

                //Minimums
                if (data.minTemperature > Double.parseDouble(wo.getTemperature())) {
                    data.minTemperature = Double.parseDouble(wo.getTemperature());
                }
                if(data.minCloudiness > Double.parseDouble(wo.getCloudiness())){
                    data.minCloudiness = Double.parseDouble(wo.getCloudiness());
                }
                if(data.minWindSpeed > Double.parseDouble(wo.getWindSpeed())){
                    data.minWindSpeed = Double.parseDouble(wo.getWindSpeed());
                }
            }

            data.date = date;
            averagesDay.add(data);
            //Save the result
            dataPerDayForec.put(date,data);
            /*if(date.isEqual(LocalDate.now())){
                System.out.println("This date found (forecast).");
                System.out.println("Today the max is: "+dataPerDayObs.get(LocalDate.now()).getMaxTemperature());
                System.out.println("Today the average is: "+dataPerDayObs.get(LocalDate.now()).getAverTemperature());
            }*/

            //If the date was the last day of the month or the last day on the list,
            // calculate monthly average based on daily averages.
            if(date.getDayOfMonth() == date.lengthOfMonth() || date.equals(forecastsByDate.lastKey())){

                WeatherDataAverages dataMonth = new WeatherDataAverages();
                dataMonth.averTemperature = 0.0;
                dataMonth.averCloudiness = 0.0;
                dataMonth.averWindSpeed = 0.0;
                dataMonth.maxTemperature = 0.0;
                dataMonth.maxCloudiness = 0.0;
                dataMonth.maxWindSpeed = 0.0;
                dataMonth.minTemperature = 0.0;
                dataMonth.minCloudiness = 0.0;
                dataMonth.minWindSpeed = 0.0;
                dataMonth.maxAverTemperature = 0.0;
                dataMonth.maxAverCloudiness = 0.0;
                dataMonth.maxAverWindSpeed = 0.0;
                dataMonth.minAverTemperature = 0.0;
                dataMonth.minAverCloudiness = 0.0;
                dataMonth.minAverWindSpeed = 0.0;

                for (WeatherDataAverages avr : averagesDay){

                    //Temperature
                    dataMonth.averTemperature += avr.averTemperature;
                    dataMonth.averTemperature = dataMonth.averTemperature/2;
                    //Cloudiness
                    dataMonth.averCloudiness += avr.averCloudiness;
                    dataMonth.averCloudiness = dataMonth.averCloudiness/2;
                    //Windiness
                    dataMonth.averWindSpeed += avr.averWindSpeed;
                    dataMonth.averWindSpeed = dataMonth.averWindSpeed/2;

                    //Average Maximums
                    dataMonth.maxAverTemperature = avr.maxTemperature;
                    dataMonth.maxAverTemperature = dataMonth.maxAverTemperature/2;

                    dataMonth.maxAverCloudiness = avr.maxCloudiness;
                    dataMonth.maxAverCloudiness = dataMonth.maxAverCloudiness/2;

                    dataMonth.maxAverWindSpeed = avr.maxWindSpeed;
                    dataMonth.maxAverWindSpeed = dataMonth.maxAverWindSpeed/2;

                    //Average Minimums
                    dataMonth.minAverTemperature = avr.minTemperature;
                    dataMonth.minAverTemperature = dataMonth.minAverTemperature/2;

                    dataMonth.minAverCloudiness = avr.minCloudiness;
                    dataMonth.minAverCloudiness = dataMonth.minAverCloudiness/2;

                    dataMonth.minAverWindSpeed = avr.minWindSpeed;
                    dataMonth.minAverWindSpeed = dataMonth.minAverWindSpeed/2;

                    //Absolute maximums
                    if (dataMonth.maxTemperature < avr.maxTemperature) {
                        dataMonth.maxTemperature = avr.maxTemperature;
                    }
                    if(dataMonth.maxCloudiness < avr.maxCloudiness){
                        dataMonth.maxCloudiness = avr.maxCloudiness;
                    }
                    if(dataMonth.maxWindSpeed < avr.maxWindSpeed){
                        dataMonth.maxWindSpeed = avr.maxWindSpeed;
                    }

                    //Absolute Minimums
                    if (dataMonth.minTemperature > avr.minTemperature) {
                        dataMonth.minTemperature = avr.minTemperature;
                    }
                    if(dataMonth.minCloudiness > avr.minCloudiness){
                        dataMonth.minCloudiness = avr.minCloudiness;
                    }
                    if(dataMonth.minWindSpeed > avr.minWindSpeed){
                        dataMonth.minWindSpeed = avr.minWindSpeed;
                    }

                }

                //Clear monthly averages for next month
                averagesDay.clear();
                //Save the result
                dataPerMonthObs.put(date.getMonth(),dataMonth);
            }


        }
}

    public TreeMap<LocalDate, WeatherDataAverages> getDailyForecastAverages(){
        return dataPerDayForec;
    }

    public WeatherDataAverages getForecastAveragesOfDate(LocalDate date){
        if (dataPerDayForec.containsKey(date)) {
            return dataPerDayForec.get(date);
        }
        return null;
    }
    public TreeMap<java.time.Month, WeatherDataAverages> getMonthlyForecastAverages(){
        return dataPerMonthForec;
    }

    public WeatherDataAverages getForecastAveragesOfMonth(java.time.Month month){
        if (dataPerMonthForec.containsKey(month)) {
            return dataPerMonthForec.get(month);
        }
        return null;
    }
}

package com.thebigfour.drivingconditions;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author Aaro
 */
public class WeatherSettings {

    private LocalDate startMonthDate;
    private LocalDate endMonthDate;
    private LocalDate startDateWeather;
    private LocalDate endDateWeather;
    private LocalDate currentDateWeather;
    private String startTimeWeather = "";
    private String endTimeWeather = "";
    private String currentTimeWeather = "";
    private boolean isForecast = false; // Only forecast if true, only observations if false
    private boolean isPartial = false; // Both observations and forecast if true

    // Parameters for Observation
    boolean t2m = false; // Temp now
    boolean ws_10min = false; // Wind speed now
    boolean n_man = false; // Cloudiness now
    boolean TA_PT1H_AVG = false; // 1h temp
    boolean TA_PT1H_MAX = false; // 1h max temp
    boolean TA_PT1H_MIN = false; // 1h min temp
    boolean rrday = false; // Day precipitation
    boolean tday = false; // Day avg temp
    boolean snow = false; // Snow cover
    boolean tmin = false; // Day min temp
    boolean tmax = false; // Day max temp
    boolean TG_PT12H_min = false; //Ground min temp
    boolean wg_10min = false; // Gust speed
    boolean wd_10min = false; // Wind direction
    boolean vis = false; // Visibility
    boolean snow_aws = false; // Snow cover now
    boolean ri_10min = false; // Rain intensity now

    // Parameters for Forecast
    boolean temperature = false;
    boolean windDirection = false;
    boolean windSpeed = false;
    boolean precipitationAmount = false;
    boolean cloudiness = false;
    boolean radiation = false;
    boolean windGust = false;
    boolean visibility = false;


    String interval = "60";
    int countO = 0;
    int countF = 0;

    public void setN_man(boolean n_man) {
        this.n_man = n_man;
        ++this.countO;
    }

    public void setT2m(boolean t2m) {
        this.t2m = t2m;
        ++this.countO;
    }

    public void setTA_PT1H_AVG(boolean TA_PT1H_AVG) {
        this.TA_PT1H_AVG = TA_PT1H_AVG;
        ++this.countO;
    }

    public void setTA_PT1H_MAX(boolean TA_PT1H_MAX) {
        this.TA_PT1H_MAX = TA_PT1H_MAX;
        ++this.countO;
    }

    public void setTA_PT1H_MIN(boolean TA_PT1H_MIN) {
        this.TA_PT1H_MIN = TA_PT1H_MIN;
        ++this.countO;
    }

    public void setWs_10min(boolean ws_10min) {
        this.ws_10min = ws_10min;
        ++this.countO;
    }

    // FORECAST PARAMETERS:
    public void setTemperature(boolean temperature) {
        this.temperature = temperature;
        ++this.countF;
    }

    public void setWindSpeed(boolean windSpeed) {
        this.windSpeed = windSpeed;
        ++this.countF;
    }

    public void setCloudiness(boolean cloudiness) {
        this.cloudiness = cloudiness;
        ++this.countF;
    }

    public void setPrecipitationAmount(boolean precipitationAmount) {
        this.precipitationAmount = precipitationAmount;
        ++this.countF;
    }

    public void setRadiation(boolean radiation) {
        this.radiation = radiation;
        ++this.countF;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
        ++this.countF;
    }

    public void setWindDirection(boolean windDirection) {
        this.windDirection = windDirection;
        ++this.countF;
    }

    public void setWindGust(boolean windGust) {
        this.windGust = windGust;
        ++this.countF;
    }

    public String getObservationParamString(int round) {
        int i = 0;
        String params = "";
        ++i;

        if (t2m) {
            params += "t2m";
            if (this.countO != 1) {
                if (i < this.countO) {
                    params += ",";
                }
            }
        }
        if (ws_10min) {
            params += "ws_10min";
            ++i;
            if (this.countO != 1) {
                if (i < this.countO) {
                    params += ",";
                }
            }

        }
        if (n_man) {
            params += "n_man";
            ++i;
            if (this.countO != 1) {
                if (i < this.countO) {
                    params += ",";
                }
            }

        }
        if (TA_PT1H_AVG) {
            params += "TA_PT1H_AVG";
            ++i;
            if (this.countO != 1) {
                if (i < this.countO) {
                    params += ",";
                }
            }

        }
        if (TA_PT1H_MAX) {
            params += "TA_PT1H_MAX";
            ++i;
            if (this.countO != 1) {
                if (i < this.countO) {
                    params += ",";
                }
            }

        }
        if (TA_PT1H_MIN) {
            params += "TA_PT1H_MIN";
            ++i;
            if (this.countO != 1) {
                if (i < this.countO) {
                    params += ",";
                }
            }

        }

        if (round == 0) {

            if (!this.startTimeWeather.equals(this.endTimeWeather) && !isPartial) {
                params += "&starttime=" + this.startTimeWeather + "&endtime=" + this.endTimeWeather;
                return params;
            }
            if (isPartial) {
                params += "&starttime=" + this.startTimeWeather + "&endtime=" + this.currentTimeWeather;
                return params;
            }

        } else if (round == 1) {

            params += "&starttime=" + this.startMonthDate + "T" + LocalTime.of(0, 0, 0) + ":00Z" +
                    "&endtime=" + this.startMonthDate.plusDays(5) + "T" + LocalTime.of(0, 0, 0) + ":00Z";

            this.startMonthDate = this.startMonthDate.plusDays(5);
            return params;

        } else if (round == 2) {

            params += "&starttime=" + this.startMonthDate + "T" + LocalTime.of(0, 0, 0) + ":00Z" +
                    "&endtime=" + this.startMonthDate.plusDays(1) + "T" + LocalTime.of(0, 0, 0) + ":00Z";


            this.startMonthDate = this.startMonthDate.plusDays(1);
            return params;

        } else if (round == 3) {

            params += "&starttime=" + this.endDateWeather + "T" + LocalTime.of(0, 0, 0) + ":00Z" +
                    "&endtime=" + this.endDateWeather.plusDays(5) + "T" + LocalTime.of(0, 0, 0) + ":00Z";


            this.endDateWeather = this.endDateWeather.plusDays(5);
            return params;

        } else if (round == 4) {

            params += "&starttime=" + this.endDateWeather + "T" + LocalTime.of(0, 0, 0) + ":00Z" +
                    "&endtime=" + this.endDateWeather.plusDays(1) + "T" + LocalTime.of(0, 0, 0) + ":00Z";


            this.endDateWeather = this.endDateWeather.plusDays(1);
            return params;

        }

        System.out.println(params);
        return params;
    }


    public String getForecastParamString(){
        int i = 0;
        String params = "";
        ++i;

        if (temperature){
            params += "Temperature";
            if (this.countF != 1){
                if (i < this.countF){
                    params += ",";
                }
            }
        }
        if (windDirection){
            params += "WindDirection";
            ++i;
            if (this.countF != 1){
                if (i < this.countF){
                    params += ",";
                }
            }

        }
        if (cloudiness){
            params += "TotalCloudCover";
            ++i;
            if (this.countF != 1){
                if (i < this.countF){
                    params += ",";
                }
            }

        }
        if (radiation){
            params += "RadiationGlobal";
            ++i;
            if (this.countF != 1){
                if (i < this.countF){
                    params += ",";
                }
            }

        }
        if (visibility){
            params += "Visibility";
            ++i;
            if (this.countF != 1){
                if (i < this.countF){
                    params += ",";
                }
            }

        }
        if (windGust){
            params += "WindGust";
            ++i;
            if (this.countF != 1){
                if (i < this.countF){
                    params += ",";
                }
            }

        }
        if (windSpeed){
            params += "WindSpeedMS";
            ++i;
            if (this.countF != 1){
                if (i < this.countF){
                    params += ",";
                }
            }

        }

        if ( !this.startTimeWeather.equals(this.endTimeWeather) && isForecast ){
            params += "&starttime=" + this.startTimeWeather + "&endtime=" + this.endTimeWeather;
            return params;
        }
        if (isPartial) {
            params += "&starttime=" + this.currentTimeWeather + "&endtime=" + this.endTimeWeather;
            return params;

        }

        return params;
    }

    public String getInterval() {
        return interval;
    }

    public int getCountO() {
        return countO;
    }

    public int getCountF() {
        return countF;
    }

    public void setForecast(boolean forecast) {
        isForecast = forecast;
    }

    public void setPartial(boolean partial) {
        isPartial = partial;
    }

    public void setAllTimes(String currentTimeWeather, String endTimeWeather, String startTimeWeather){
        this.currentTimeWeather = currentTimeWeather;
        this.endTimeWeather = endTimeWeather;
        this.startTimeWeather = startTimeWeather;
    }
    public void setAllDates(LocalDate now, LocalDate start, LocalDate end){
        this.currentDateWeather = now;
        this.endDateWeather = end;
        this.startDateWeather = start;
        this.startMonthDate = LocalDate.of(startDateWeather.getYear(),startDateWeather.getMonthValue(),1);
        this.endMonthDate = LocalDate.of(endDateWeather.getYear(), endDateWeather.getMonthValue(), endDateWeather.lengthOfMonth());
    }

    public LocalDate getCurrentDateWeather() {
        return currentDateWeather;
    }

    public LocalDate getEndDateWeather() {
        return endDateWeather;
    }

    public LocalDate getStartDateWeather() {
        return startDateWeather;
    }

    public LocalDate getEndMonthDate() {
        return endMonthDate;
    }

    public LocalDate getStartMonthDate() {
        return startMonthDate;
    }

    public String getCurrentTimeWeather() {
        return currentTimeWeather;
    }

    public String getEndTimeWeather() {
        return endTimeWeather;
    }

    public String getStartTimeWeather() {
        return startTimeWeather;
    }

    public boolean isPartial() {
        return isPartial;
    }

    public boolean isForecast() {
        return isForecast;
    }
}

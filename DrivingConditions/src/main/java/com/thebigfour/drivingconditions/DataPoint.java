/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.thebigfour.drivingconditions;

import java.time.LocalTime;

/**
 * @brief an abstract class for weather data points
 * @author mikko
 */
abstract public class DataPoint {
    
    abstract public LocalTime getTime();
    
    abstract public String getTemperature();
    
    abstract public String getCloudiness();

    abstract public String getWindSpeed();
    
}

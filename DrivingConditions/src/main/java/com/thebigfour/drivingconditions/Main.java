/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.thebigfour.drivingconditions;




import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
   
    
    @Override
    public void start(Stage stage) throws Exception {
        
        try{
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/View.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Driving Conditions");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        
        
        
        }catch(LoadException e){
            
            e.printStackTrace();
            //System.out.println("Loading failed");
        }
        
    }
    
    public static void main(String[] args){
        
        launch(args);
        
    }
    
}


package com.physicalapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.physicalapp.view.MainWindow;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        MainWindow mainWindow = new MainWindow();
        Scene scene = new Scene(mainWindow, 800, 600);
        primaryStage.setTitle("Physics Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 
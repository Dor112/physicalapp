package com.physicalapp;

import com.physicalapp.view.MainWindow;
import com.physicalapp.model.Phenomenon;
import javafx.application.Application;
import javafx.stage.Stage;
import java.util.Arrays;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        MainWindow mainWindow = new MainWindow(Arrays.asList(Phenomenon.getAvailablePhenomena()));
        mainWindow.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 
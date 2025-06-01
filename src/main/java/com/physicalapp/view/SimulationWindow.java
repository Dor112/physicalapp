package com.physicalapp.view;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.physicalapp.model.Phenomenon;
import com.physicalapp.controller.SimulationController;

public class SimulationWindow extends Stage {
    private Canvas simulationCanvas;
    private VBox parametersPanel;
    private TextArea descriptionView;
    private SimulationController controller;

    public SimulationWindow(Phenomenon phenomenon) {
        BorderPane root = new BorderPane();
        
        // Setup simulation canvas
        simulationCanvas = new Canvas(600, 400);
        root.setCenter(simulationCanvas);

        // Setup parameters panel
        parametersPanel = new VBox(10);
        parametersPanel.getChildren().addAll(
            createParameterSlider("Parameter 1", 0, 100, 50),
            createParameterSlider("Parameter 2", 0, 100, 50)
        );
        root.setRight(parametersPanel);

        // Setup description view
        descriptionView = new TextArea();
        descriptionView.setEditable(false);
        descriptionView.setWrapText(true);
        ScrollPane descriptionScroll = new ScrollPane(descriptionView);
        descriptionScroll.setFitToWidth(true);
        descriptionScroll.setPrefHeight(200);
        root.setBottom(descriptionScroll);

        // Initialize controller
        controller = new SimulationController(phenomenon, simulationCanvas);

        // Load description
        loadDescription(phenomenon);

        setTitle(phenomenon.getName() + " Simulation");
        setScene(new Scene(root, 800, 700));
    }

    private Slider createParameterSlider(String name, double min, double max, double value) {
        Slider slider = new Slider(min, max, value);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        return slider;
    }

    private void loadDescription(Phenomenon phenomenon) {
        String description = String.format("""
            %s
            
            Description of the phenomenon and its mathematical model will be displayed here.
            
            Equations and explanations can be added in plain text format.
            """, phenomenon.getName());
        descriptionView.setText(description);
    }
} 
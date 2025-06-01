package com.physicalapp.view;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import com.physicalapp.model.Phenomenon;

public class MainWindow extends VBox {
    private ListView<Phenomenon> phenomenaList;
    private Button startButton;

    public MainWindow() {
        setPadding(new Insets(10));
        setSpacing(10);

        phenomenaList = new ListView<>();
        startButton = new Button("Start Simulation");

        // Add phenomena to the list
        phenomenaList.getItems().addAll(
            new Phenomenon("Simple Pendulum", "simple-pendulum"),
            new Phenomenon("Double Pendulum", "double-pendulum"),
            new Phenomenon("String Wave", "string-wave"),
            new Phenomenon("Spring Oscillator", "spring-oscillator"),
            new Phenomenon("Standing Waves", "standing-waves")
        );

        startButton.setOnAction(e -> openSimulationWindow());

        getChildren().addAll(phenomenaList, startButton);
    }

    private void openSimulationWindow() {
        Phenomenon selectedPhenomenon = phenomenaList.getSelectionModel().getSelectedItem();
        if (selectedPhenomenon != null) {
            SimulationWindow simulationWindow = new SimulationWindow(selectedPhenomenon);
            simulationWindow.show();
        }
    }
} 
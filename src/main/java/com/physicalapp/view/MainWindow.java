package com.physicalapp.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.physicalapp.model.Phenomenon;

public class MainWindow extends Application {
    private ListView<Phenomenon> phenomenaList;
    private Label descriptionLabel;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Create list of phenomena
        phenomenaList = new ListView<>();
        phenomenaList.getItems().addAll(Phenomenon.getAvailablePhenomena());
        phenomenaList.setCellFactory(param -> new PhenomenonListCell());
        
        // Create description label
        descriptionLabel = new Label();
        descriptionLabel.setWrapText(true);
        descriptionLabel.setPadding(new Insets(10, 0, 10, 0));

        // Create start button
        Button startButton = new Button("Запустить симуляцию");
        startButton.setMaxWidth(Double.MAX_VALUE);
        startButton.setOnAction(e -> {
            Phenomenon selected = phenomenaList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                SimulationWindow simulationWindow = new SimulationWindow(selected);
                simulationWindow.show();
            }
        });
        
        // Update description when selection changes
        phenomenaList.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    descriptionLabel.setText(newVal.getDescription());
                } else {
                    descriptionLabel.setText("");
                }
            }
        );
        
        // Layout
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(0, 0, 0, 10));
        rightPanel.getChildren().addAll(descriptionLabel, startButton);

        root.setCenter(phenomenaList);
        root.setRight(rightPanel);
        
        // Scene setup
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Физические симуляции");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Select first item by default
        if (!phenomenaList.getItems().isEmpty()) {
            phenomenaList.getSelectionModel().select(0);
        }
    }

    private static class PhenomenonListCell extends javafx.scene.control.ListCell<Phenomenon> {
        @Override
        protected void updateItem(Phenomenon item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                setText(item.getName());
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
} 
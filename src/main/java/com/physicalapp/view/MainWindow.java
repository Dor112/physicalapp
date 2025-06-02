package com.physicalapp.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import com.physicalapp.model.Phenomenon;
import java.util.List;

public class MainWindow extends Stage {
    private static final String FONT_FAMILY = "-fx-font-family: 'Segoe UI', 'Roboto', sans-serif;";
    
    private static final String TITLE_STYLE = """
        -fx-font-size: 24px;
        -fx-font-weight: bold;
        -fx-text-fill: #2c3e50;
        """ + FONT_FAMILY;
        
    private static final String LIST_STYLE = """
        -fx-background-color: white;
        -fx-background-radius: 8;
        -fx-border-radius: 8;
        -fx-border-color: #e0e0e0;
        -fx-border-width: 1;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);
        """ + FONT_FAMILY;
        
    private static final String LIST_CELL_STYLE = """
        -fx-padding: 10;
        -fx-background-radius: 4;
        """ + FONT_FAMILY;
        
    private static final String DESCRIPTION_STYLE = """
        -fx-font-size: 14px;
        -fx-text-fill: #34495e;
        -fx-background-color: white;
        -fx-background-radius: 8;
        -fx-border-radius: 8;
        -fx-border-color: #e0e0e0;
        -fx-border-width: 1;
        -fx-padding: 15;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);
        """ + FONT_FAMILY;
        
    private static final String BUTTON_STYLE = """
        -fx-background-color: #3498db;
        -fx-text-fill: white;
        -fx-font-size: 16px;
        -fx-font-weight: bold;
        -fx-padding: 15 30;
        -fx-background-radius: 8;
        -fx-cursor: hand;
        """ + FONT_FAMILY;
        
    private static final String BUTTON_HOVER_STYLE = """
        -fx-background-color: #2980b9;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 0);
        """ + FONT_FAMILY;

    private ListView<Phenomenon> phenomenaList;
    private TextArea descriptionArea;
    private Button startButton;

    public MainWindow(List<Phenomenon> phenomena) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        root.setPadding(new Insets(20));

        // Заголовок
        Label title = new Label("Физические симуляции");
        title.setStyle(TITLE_STYLE);
        BorderPane.setMargin(title, new Insets(0, 0, 20, 0));
        root.setTop(title);

        // Левая панель со списком
        phenomenaList = new ListView<>();
        phenomenaList.setStyle(LIST_STYLE);
        phenomenaList.setPrefWidth(300);
        phenomenaList.getItems().addAll(phenomena);
        phenomenaList.setCellFactory(this::createPhenomenonListCell);
        
        VBox leftPanel = new VBox(10);
        leftPanel.getChildren().add(phenomenaList);
        root.setLeft(leftPanel);

        // Правая панель с описанием и кнопкой
        VBox rightPanel = new VBox(20);
        rightPanel.setPadding(new Insets(0, 0, 0, 20));
        rightPanel.setAlignment(Pos.TOP_CENTER);

        descriptionArea = new TextArea();
        descriptionArea.setStyle(DESCRIPTION_STYLE);
        descriptionArea.setWrapText(true);
        descriptionArea.setEditable(false);
        descriptionArea.setPrefRowCount(10);
        VBox.setVgrow(descriptionArea, Priority.ALWAYS);

        startButton = new Button("Запустить симуляцию");
        startButton.setStyle(BUTTON_STYLE);
        startButton.setMaxWidth(Double.MAX_VALUE);
        startButton.setOnMouseEntered(e -> startButton.setStyle(BUTTON_STYLE + BUTTON_HOVER_STYLE));
        startButton.setOnMouseExited(e -> startButton.setStyle(BUTTON_STYLE));
        
        startButton.setOnAction(e -> {
            Phenomenon selected = phenomenaList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                SimulationWindow simulationWindow = new SimulationWindow(selected);
                simulationWindow.show();
            }
        });

        rightPanel.getChildren().addAll(descriptionArea, startButton);
        root.setCenter(rightPanel);

        // Обработчик выбора элемента списка
        phenomenaList.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    descriptionArea.setText(newVal.getDescription());
                    startButton.setDisable(false);
                } else {
                    descriptionArea.setText("");
                    startButton.setDisable(true);
                }
            }
        );

        // Выбираем первый элемент по умолчанию
        if (!phenomena.isEmpty()) {
            phenomenaList.getSelectionModel().select(0);
        }

        Scene scene = new Scene(root, 900, 600);
        setTitle("Физические симуляции");
        setScene(scene);
    }

    private ListCell<Phenomenon> createPhenomenonListCell(ListView<Phenomenon> listView) {
        return new ListCell<>() {
            @Override
            protected void updateItem(Phenomenon phenomenon, boolean empty) {
                super.updateItem(phenomenon, empty);
                
                if (empty || phenomenon == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Создаем контейнер для иконки и текста
                    HBox container = new HBox(10);
                    container.setAlignment(Pos.CENTER_LEFT);
                    
                    // Добавляем иконку в зависимости от типа явления
                    String iconPath = getIconPath(phenomenon.getId());
                    if (iconPath != null) {
                        try {
                            var iconStream = getClass().getResourceAsStream(iconPath);
                            if (iconStream != null) {
                                ImageView icon = new ImageView(new Image(iconStream));
                                icon.setFitHeight(32);
                                icon.setFitWidth(32);
                                container.getChildren().add(icon);
                            }
                        } catch (Exception e) {
                            // If icon loading fails, just skip it
                            System.out.println("Failed to load icon: " + iconPath);
                        }
                    }
                    
                    // Добавляем название
                    Label label = new Label(phenomenon.getName());
                    label.setStyle(FONT_FAMILY + "-fx-font-size: 14px;");
                    container.getChildren().add(label);
                    
                    setGraphic(container);
                    setStyle(LIST_CELL_STYLE);
                }
            }
        };
    }
    
    private String getIconPath(String phenomenonId) {
        return switch (phenomenonId) {
            case "simple-pendulum" -> "/icons/pendulum.png";
            case "double-pendulum" -> "/icons/double-pendulum.png";
            case "string-wave" -> "/icons/wave.png";
            case "spring-oscillator" -> "/icons/spring.png";
            case "standing-waves" -> "/icons/standing-wave.png";
            case "impulse-types" -> "/icons/impulse.png";
            case "collisions" -> "/icons/collision.png";
            case "mirror-reflection" -> "/icons/mirror.png";
            default -> null;
        };
    }
} 
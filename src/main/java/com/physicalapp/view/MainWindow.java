package com.physicalapp.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import com.physicalapp.model.Phenomenon;
import java.util.List;

public class MainWindow extends Stage {
    private BorderPane root;
    private Scene scene;
    private List<Phenomenon> phenomena;
    private VBox mainContent;
    private SimulationWindow currentSimulation;

    private static final String FONT_FAMILY = "-fx-font-family: 'Segoe UI', 'Roboto', sans-serif;";
    
    private static final String WINDOW_STYLE = """
        -fx-background-color: #f5f7fa;
        """ + FONT_FAMILY;
        
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
        -fx-padding: 10;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);
        """ + FONT_FAMILY;

    private static final String BUTTON_STYLE = """
        -fx-background-color: #3498db;
        -fx-text-fill: white;
        -fx-font-size: 14px;
        -fx-padding: 8 16;
        -fx-background-radius: 4;
        -fx-cursor: hand;
        """ + FONT_FAMILY;

    private static final String BUTTON_HOVER_STYLE = """
        -fx-background-color: #2980b9;
        """ + FONT_FAMILY;

    private static final String BACK_BUTTON_STYLE = """
        -fx-background-color: #95a5a6;
        -fx-text-fill: white;
        -fx-font-size: 14px;
        -fx-padding: 8 16;
        -fx-background-radius: 4;
        -fx-cursor: hand;
        """ + FONT_FAMILY;

    private static final String BACK_BUTTON_HOVER_STYLE = """
        -fx-background-color: #7f8c8d;
        """ + FONT_FAMILY;

    public MainWindow(List<Phenomenon> phenomena) {
        this.phenomena = phenomena;
        
        root = new BorderPane();
        root.setStyle(WINDOW_STYLE);
        root.setPadding(new Insets(20));
        
        setupMainContent();
        
        scene = new Scene(root, 960, 800);
        setScene(scene);
        setTitle("Физические симуляции");
    }

    private void setupMainContent() {
        mainContent = new VBox(20);
        mainContent.setAlignment(Pos.TOP_CENTER);
        
        Label title = new Label("Физические симуляции");
        title.setStyle(TITLE_STYLE);
        
        ListView<Phenomenon> phenomenaList = createPhenomenaList();
        
        mainContent.getChildren().addAll(title, phenomenaList);
        root.setCenter(mainContent);
    }

    private ListView<Phenomenon> createPhenomenaList() {
        ListView<Phenomenon> listView = new ListView<>();
        listView.setStyle(LIST_STYLE);
        listView.getItems().addAll(phenomena);
        listView.setPrefHeight(600);
        
        listView.setCellFactory(lv -> createPhenomenonListCell());
        
        return listView;
    }

    private ListCell<Phenomenon> createPhenomenonListCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Phenomenon phenomenon, boolean empty) {
                super.updateItem(phenomenon, empty);
                
                if (empty || phenomenon == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Создаем контейнер для иконки и текста
                    HBox container = new HBox(15);
                    container.setAlignment(Pos.CENTER_LEFT);
                    container.setPadding(new Insets(10));
                    
                    // Добавляем иконку
                    String iconPath = getIconPath(phenomenon.getId());
                    try {
                        Image icon = new Image(getClass().getResourceAsStream(iconPath));
                        ImageView imageView = new ImageView(icon);
                        imageView.setFitHeight(48);
                        imageView.setFitWidth(48);
                        container.getChildren().add(imageView);
                    } catch (Exception e) {
                        // Если иконка не найдена, создаем placeholder
                        StackPane placeholder = new StackPane();
                        placeholder.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 4;");
                        placeholder.setPrefSize(48, 48);
                        container.getChildren().add(placeholder);
                    }
                    
                    // Создаем контейнер для текста
                    VBox textContainer = new VBox(5);
                    
                    // Название явления
                    Label nameLabel = new Label(phenomenon.getName());
                    nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                    
                    // Краткое описание
                    Label descLabel = new Label(phenomenon.getShortDescription());
                    descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
                    descLabel.setWrapText(true);
                    
                    textContainer.getChildren().addAll(nameLabel, descLabel);
                    
                    // Добавляем кнопку запуска
                    Button launchButton = new Button("Запустить");
                    launchButton.setStyle(BUTTON_STYLE);
                    
                    launchButton.setOnMouseEntered(e -> 
                        launchButton.setStyle(BUTTON_STYLE + BUTTON_HOVER_STYLE));
                    
                    launchButton.setOnMouseExited(e -> 
                        launchButton.setStyle(BUTTON_STYLE));
                    
                    launchButton.setOnAction(e -> showSimulation(phenomenon));
                    
                    // Добавляем разделитель для выравнивания кнопки справа
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    
                    container.getChildren().addAll(textContainer, spacer, launchButton);
                    setGraphic(container);
                }
            }
        };
    }

    private String getIconPath(String phenomenonId) {
        return "/icons/" + phenomenonId + ".png";
    }

    private void showSimulation(Phenomenon phenomenon) {
        // Создаем новое содержимое для симуляции
        VBox simulationContent = new VBox(20);
        simulationContent.setAlignment(Pos.TOP_CENTER);
        simulationContent.setPadding(new Insets(20));

        // Создаем кнопку "Назад"
        Button backButton = new Button("← Назад к списку");
        backButton.setStyle(BACK_BUTTON_STYLE);
        
        backButton.setOnMouseEntered(e -> 
            backButton.setStyle(BACK_BUTTON_STYLE + BACK_BUTTON_HOVER_STYLE));
        
        backButton.setOnMouseExited(e -> 
            backButton.setStyle(BACK_BUTTON_STYLE));
        
        backButton.setOnAction(e -> {
            if (currentSimulation != null) {
                currentSimulation.stop(); // Останавливаем текущую симуляцию
                currentSimulation = null;
            }
            root.setCenter(mainContent);
        });

        // Создаем контейнер для кнопки назад
        HBox topBar = new HBox(backButton);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Создаем новую симуляцию
        currentSimulation = new SimulationWindow(phenomenon);
        
        // Добавляем содержимое симуляции в контейнер
        simulationContent.getChildren().addAll(
            topBar,
            currentSimulation.getContent()
        );

        // Заменяем содержимое главного окна на симуляцию
        root.setCenter(simulationContent);
    }
} 
package com.physicalapp.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import com.physicalapp.model.Phenomenon;
import com.physicalapp.controller.SimulationController;

public class SimulationWindow extends Stage {
    private Canvas simulationCanvas;
    private VBox parametersPanel;
    private TextArea descriptionView;
    private SimulationController controller;
    
    private static final String FONT_FAMILY = "-fx-font-family: 'Segoe UI', 'Roboto', sans-serif;";
    
    private static final String WINDOW_STYLE = """
        -fx-background-color: #f5f7fa;
        """ + FONT_FAMILY;
        
    private static final String TITLE_STYLE = """
        -fx-font-size: 20px;
        -fx-font-weight: bold;
        -fx-text-fill: #2c3e50;
        """ + FONT_FAMILY;
        
    private static final String PANEL_STYLE = """
        -fx-background-color: white;
        -fx-background-radius: 8;
        -fx-border-radius: 8;
        -fx-border-color: #e0e0e0;
        -fx-border-width: 1;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);
        """ + FONT_FAMILY;
    
    private static final String PARAM_GROUP_STYLE = """
        -fx-spacing: 10;
        -fx-padding: 15;
        -fx-background-color: white;
        -fx-background-radius: 8;
        -fx-border-radius: 8;
        -fx-border-color: #e0e0e0;
        -fx-border-width: 1;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 0);
        """ + FONT_FAMILY;
        
    private static final String SLIDER_STYLE = """
        -fx-pref-width: 250;
        -fx-background-radius: 4;
        """ + FONT_FAMILY;
        
    private static final String LABEL_STYLE = """
        -fx-text-fill: #34495e;
        -fx-font-size: 14px;
        """ + FONT_FAMILY;
        
    private static final String VALUE_LABEL_STYLE = """
        -fx-text-fill: #3498db;
        -fx-font-weight: bold;
        -fx-font-size: 14px;
        """ + FONT_FAMILY;
        
    private static final String DESCRIPTION_STYLE = """
        -fx-font-family: 'Segoe UI', Arial, sans-serif;
        -fx-font-size: 14px;
        -fx-text-fill: #34495e;
        -fx-background-color: white;
        -fx-background-radius: 8;
        -fx-border-radius: 8;
        -fx-border-color: #e0e0e0;
        -fx-border-width: 1;
        -fx-padding: 20;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);
        """;

    public SimulationWindow(Phenomenon phenomenon) {
        BorderPane root = new BorderPane();
        root.setStyle(WINDOW_STYLE);
        root.setPadding(new Insets(20));

        // Setup title
        Label title = new Label(phenomenon.getName());
        title.setStyle(TITLE_STYLE);
        BorderPane.setMargin(title, new Insets(0, 0, 20, 0));
        root.setTop(title);

        // Setup simulation canvas with border
        simulationCanvas = new Canvas(600, 400);
        VBox canvasContainer = new VBox(simulationCanvas);
        canvasContainer.setStyle(PANEL_STYLE);
        canvasContainer.setPadding(new Insets(15));
        root.setCenter(canvasContainer);

        // Setup parameters panel
        parametersPanel = new VBox(15);
        parametersPanel.setPadding(new Insets(0, 0, 0, 20));
        parametersPanel.setPrefWidth(300);
        
        // Add title to parameters panel
        Label paramsTitle = new Label("Параметры симуляции");
        paramsTitle.setStyle(TITLE_STYLE);
        parametersPanel.getChildren().add(paramsTitle);
        
        setupParameters(phenomenon);
        
        // Wrap parameters panel in scroll pane
        ScrollPane paramsScroll = new ScrollPane(parametersPanel);
        paramsScroll.setFitToWidth(true);
        paramsScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        paramsScroll.setPadding(new Insets(20, 0, 0, 0));
        root.setRight(paramsScroll);

        // Setup description view
        descriptionView = new TextArea();
        descriptionView.setEditable(false);
        descriptionView.setWrapText(true);
        descriptionView.setPrefRowCount(15);
        descriptionView.setStyle(DESCRIPTION_STYLE);
        
        VBox descriptionContainer = new VBox(15);
        descriptionContainer.setPadding(new Insets(20, 0, 0, 0));
        
        Label descTitle = new Label("Описание явления");
        descTitle.setStyle(TITLE_STYLE);
        
        descriptionContainer.getChildren().addAll(descTitle, descriptionView);
        descriptionContainer.setPrefHeight(350);
        root.setBottom(descriptionContainer);

        // Initialize controller
        controller = new SimulationController(phenomenon, simulationCanvas);

        // Load description
        loadDescription(phenomenon);

        setTitle(phenomenon.getName());
        setScene(new Scene(root, 960, 900));

        // Cleanup on window close
        setOnCloseRequest(e -> controller.stop());
    }

    private VBox createParameterGroup(String title) {
        VBox group = new VBox(10);
        group.setStyle(PARAM_GROUP_STYLE);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle(TITLE_STYLE);
        
        group.getChildren().add(titleLabel);
        return group;
    }

    private VBox createParameterSlider(String name, double min, double max, double value) {
        VBox container = new VBox(8);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(0, 10, 0, 10));
        
        HBox labelContainer = new HBox(10);
        labelContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label label = new Label(name);
        label.setStyle(LABEL_STYLE);
        
        Label valueLabel = new Label(String.format("%.2f", value));
        valueLabel.setStyle(VALUE_LABEL_STYLE);
        valueLabel.setMinWidth(60);
        
        labelContainer.getChildren().addAll(label, valueLabel);
        
        Slider slider = new Slider(min, max, value);
        slider.setStyle(SLIDER_STYLE);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit((max - min) / 4);
        slider.setBlockIncrement((max - min) / 10);
        
        slider.valueProperty().addListener((obs, old, val) -> 
            valueLabel.setText(String.format("%.2f", val.doubleValue())));
        
        container.getChildren().addAll(labelContainer, slider);
        
        // Store the slider in the container's properties for easy access
        container.setUserData(slider);
        
        return container;
    }

    private void setupParameters(Phenomenon phenomenon) {
        switch (phenomenon.getId()) {
            case "simple-pendulum" -> setupSimplePendulumParams();
            case "double-pendulum" -> setupDoublePendulumParams();
            case "string-wave" -> setupStringWaveParams();
            case "spring-oscillator" -> setupSpringOscillatorParams();
            case "standing-waves" -> setupStandingWaveParams();
            case "impulse-types" -> setupImpulseParams();
            case "collisions" -> setupCollisionParams();
            case "mirror-reflection" -> setupMirrorParams();
        }
    }

    private void setupSimplePendulumParams() {
        // Length parameter (0.1 to 2.0 meters)
        VBox lengthContainer = createParameterSlider("Length (m)", 0.1, 2.0, 1.0);
        ((Slider)lengthContainer.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("length", val.doubleValue()));

        // Initial angle parameter (-180 to 180 degrees)
        VBox angleContainer = createParameterSlider("Initial Angle (degrees)", -180, 180, 45);
        ((Slider)angleContainer.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("angle", val.doubleValue()));

        // Damping coefficient (0 to 1)
        VBox dampingContainer = createParameterSlider("Damping", 0, 1, 0.1);
        ((Slider)dampingContainer.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("damping", val.doubleValue()));

        parametersPanel.getChildren().addAll(
            createParameterGroup("Simple Pendulum Parameters:"),
            lengthContainer,
            angleContainer,
            dampingContainer
        );
    }

    private void setupDoublePendulumParams() {
        // Length of first pendulum (0.1 to 2.0 meters)
        VBox length1Container = createParameterSlider("Length 1 (m)", 0.1, 2.0, 1.0);
        ((Slider)length1Container.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("length1", val.doubleValue()));

        // Length of second pendulum (0.1 to 2.0 meters)
        VBox length2Container = createParameterSlider("Length 2 (m)", 0.1, 2.0, 1.0);
        ((Slider)length2Container.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("length2", val.doubleValue()));

        // Initial angles (-180 to 180 degrees)
        VBox angle1Container = createParameterSlider("Angle 1 (degrees)", -180, 180, 45);
        ((Slider)angle1Container.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("angle1", val.doubleValue()));

        VBox angle2Container = createParameterSlider("Angle 2 (degrees)", -180, 180, 45);
        ((Slider)angle2Container.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("angle2", val.doubleValue()));

        parametersPanel.getChildren().addAll(
            createParameterGroup("Double Pendulum Parameters:"),
            length1Container,
            length2Container,
            angle1Container,
            angle2Container
        );
    }

    private void setupStringWaveParams() {
        // String tension (0 to 100 N)
        VBox tensionContainer = createParameterSlider("Tension (N)", 0, 100, 50);
        ((Slider)tensionContainer.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("tension", val.doubleValue()));

        // Wave amplitude (0 to 1)
        VBox amplitudeContainer = createParameterSlider("Amplitude", 0, 1, 0.5);
        ((Slider)amplitudeContainer.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("amplitude", val.doubleValue()));

        // Wave frequency (0 to 10 Hz)
        VBox frequencyContainer = createParameterSlider("Frequency (Hz)", 0, 10, 1);
        ((Slider)frequencyContainer.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("frequency", val.doubleValue()));

        parametersPanel.getChildren().addAll(
            createParameterGroup("String Wave Parameters:"),
            tensionContainer,
            amplitudeContainer,
            frequencyContainer
        );
    }

    private void setupSpringOscillatorParams() {
        // Spring constant (0 to 100 N/m)
        VBox springConstantContainer = createParameterSlider("Spring Constant (N/m)", 0, 100, 50);
        ((Slider)springConstantContainer.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("springConstant", val.doubleValue()));

        // Mass (0.1 to 10 kg)
        VBox massContainer = createParameterSlider("Mass (kg)", 0.1, 10, 1);
        ((Slider)massContainer.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("mass", val.doubleValue()));

        // Damping coefficient (0 to 1)
        VBox dampingContainer = createParameterSlider("Damping", 0, 1, 0.1);
        ((Slider)dampingContainer.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("damping", val.doubleValue()));

        parametersPanel.getChildren().addAll(
            createParameterGroup("Spring Oscillator Parameters:"),
            springConstantContainer,
            massContainer,
            dampingContainer
        );
    }

    private void setupStandingWaveParams() {
        // Wave speed (0 to 10 m/s)
        VBox speedContainer = createParameterSlider("Wave Speed (m/s)", 0, 10, 5);
        ((Slider)speedContainer.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("speed", val.doubleValue()));

        // Number of nodes (1 to 10)
        VBox nodesContainer = createParameterSlider("Number of Nodes", 1, 10, 3);
        ((Slider)nodesContainer.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("nodes", val.doubleValue()));

        // Amplitude (0 to 1)
        VBox amplitudeContainer = createParameterSlider("Amplitude", 0, 1, 0.5);
        ((Slider)amplitudeContainer.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("amplitude", val.doubleValue()));

        parametersPanel.getChildren().addAll(
            createParameterGroup("Standing Wave Parameters:"),
            speedContainer,
            nodesContainer,
            amplitudeContainer
        );
    }

    private void setupImpulseParams() {
        // Impulse type selection (0-3: Gaussian, Square, Triangular, Sinc)
        VBox typeContainer = createParameterSlider("Impulse Type", 0, 3, 0);
        Slider typeSlider = (Slider)typeContainer.getUserData();
        typeSlider.setSnapToTicks(true);
        typeSlider.setMajorTickUnit(1);
        typeSlider.setMinorTickCount(0);
        typeSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("type", val.doubleValue()));

        // Amplitude (0 to 2)
        VBox amplitudeContainer = createParameterSlider("Amplitude", 0, 2, 1);
        ((Slider)amplitudeContainer.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("amplitude", val.doubleValue()));

        // Width/Duration (0.1 to 2.0)
        VBox widthContainer = createParameterSlider("Width", 0.1, 2.0, 0.5);
        ((Slider)widthContainer.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("width", val.doubleValue()));

        // Propagation speed (0 to 10)
        VBox speedContainer = createParameterSlider("Speed", 0, 10, 5);
        ((Slider)speedContainer.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("speed", val.doubleValue()));

        parametersPanel.getChildren().addAll(
            createParameterGroup("Impulse Parameters:"),
            new Label("Type: 0-Gaussian, 1-Square, 2-Triangular, 3-Sinc"),
            typeContainer,
            amplitudeContainer,
            widthContainer,
            speedContainer
        );
    }

    private void setupCollisionParams() {
        // Масса первого тела (0.1 to 10 кг)
        VBox mass1Container = createParameterSlider("Масса 1 (кг)", 0.1, 10, 1);
        ((Slider)mass1Container.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("mass1", val.doubleValue()));

        // Масса второго тела (0.1 to 10 кг)
        VBox mass2Container = createParameterSlider("Масса 2 (кг)", 0.1, 10, 1);
        ((Slider)mass2Container.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("mass2", val.doubleValue()));

        // Начальная скорость первого тела (-10 to 10 м/с)
        VBox velocity1Container = createParameterSlider("Скорость 1 (м/с)", -10, 10, 5);
        ((Slider)velocity1Container.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("velocity1", val.doubleValue()));

        // Начальная скорость второго тела (-10 to 10 м/с)
        VBox velocity2Container = createParameterSlider("Скорость 2 (м/с)", -10, 10, -5);
        ((Slider)velocity2Container.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("velocity2", val.doubleValue()));

        // Коэффициент восстановления (0 - АНУ до 1 - АУУ)
        VBox restitutionContainer = createParameterSlider("Коэффициент восстановления", 0, 1, 1);
        ((Slider)restitutionContainer.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("restitution", val.doubleValue()));

        parametersPanel.getChildren().addAll(
            createParameterGroup("Параметры столкновения:"),
            mass1Container,
            mass2Container,
            velocity1Container,
            velocity2Container,
            restitutionContainer
        );
    }

    private void setupMirrorParams() {
        VBox group = createParameterGroup("Параметры отражения");
        
        // Тип зеркала (0 - плоское, 1 - вогнутое, 2 - выпуклое)
        VBox mirrorTypeContainer = createParameterSlider("Тип зеркала", 0, 2, 0);
        Slider mirrorTypeSlider = (Slider)mirrorTypeContainer.getUserData();
        mirrorTypeSlider.setSnapToTicks(true);
        mirrorTypeSlider.setMajorTickUnit(1);
        mirrorTypeSlider.setMinorTickCount(0);
        mirrorTypeSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("mirrorType", val.doubleValue()));

        Label typeHint = new Label("0 - плоское, 1 - вогнутое, 2 - выпуклое");
        typeHint.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 11px;");
        
        // Остальные слайдеры
        VBox curvatureContainer = createParameterSlider("Кривизна", 0.1, 2.0, 1.0);
        ((Slider)curvatureContainer.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("curvature", val.doubleValue()));

        VBox angleContainer = createParameterSlider("Угол падения (°)", -80, 80, 30);
        ((Slider)angleContainer.getUserData()).valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("angle", val.doubleValue()));

        VBox raysContainer = createParameterSlider("Количество лучей", 1, 10, 3);
        Slider raysSlider = (Slider)raysContainer.getUserData();
        raysSlider.setSnapToTicks(true);
        raysSlider.setMajorTickUnit(1);
        raysSlider.setMinorTickCount(0);
        raysSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("rays", val.doubleValue()));

        group.getChildren().addAll(
            mirrorTypeContainer,
            typeHint,
            curvatureContainer,
            angleContainer,
            raysContainer
        );
        
        parametersPanel.getChildren().add(group);
    }

    private void loadDescription(Phenomenon phenomenon) {
        String description = switch (phenomenon.getId()) {
            case "simple-pendulum" -> """
                Простой маятник
                
                Математическая модель:
                d²θ/dt² + (g/L)sin(θ) + γdθ/dt = 0
                
                где:
                θ - угол отклонения
                g - ускорение свободного падения
                L - длина маятника
                γ - коэффициент затухания
                
                Параметры:
                - Длина маятника влияет на период колебаний
                - Начальный угол определяет амплитуду
                - Коэффициент затухания влияет на скорость затухания колебаний
                """;
                
            case "double-pendulum" -> """
                Двойной маятник
                
                Система из двух связанных маятников демонстрирует хаотическое поведение.
                
                Параметры:
                - Длины маятников влияют на их периоды
                - Начальные углы определяют начальное положение
                - Система очень чувствительна к начальным условиям
                """;
                
            case "string-wave" -> """
                Волна на струне
                
                Волновое уравнение:
                ∂²y/∂t² = c²(∂²y/∂x²)
                
                где:
                c = √(T/μ) - скорость распространения волны
                T - натяжение струны
                μ - линейная плотность
                
                Параметры:
                - Натяжение влияет на скорость волны
                - Амплитуда определяет высоту волны
                - Частота определяет количество колебаний
                """;
                
            case "spring-oscillator" -> """
                Пружинный осциллятор
                
                Уравнение движения:
                mẍ + kx + bẋ = 0
                
                где:
                m - масса груза
                k - жесткость пружины
                b - коэффициент затухания
                
                Параметры:
                - Жесткость пружины влияет на период колебаний
                - Масса груза влияет на инерцию системы
                - Коэффициент затухания определяет скорость затухания
                """;
                
            case "standing-waves" -> """
                Стоячие волны
                
                Образуются при интерференции двух волн, распространяющихся в противоположных направлениях.
                
                y(x,t) = 2A·sin(kx)·cos(ωt)
                
                где:
                A - амплитуда
                k - волновое число
                ω - угловая частота
                
                Параметры:
                - Скорость волны влияет на длину волны
                - Число узлов определяет моду колебаний
                - Амплитуда определяет максимальное смещение
                """;
                
            case "impulse-types" -> """
                Виды импульса
                
                Моделирование различных типов импульсных сигналов и их распространения:
                
                1. Гауссов импульс:
                   f(t) = A·exp(-(t-t₀)²/(2σ²))
                   Гладкий колоколообразный импульс, часто встречается в природе
                
                2. Прямоугольный импульс:
                   f(t) = A при |t-t₀| ≤ w/2
                   f(t) = 0 при |t-t₀| > w/2
                   Простейший импульс с резкими фронтами
                
                3. Треугольный импульс:
                   Линейно нарастающий и спадающий сигнал
                
                4. Sinc-импульс:
                   f(t) = A·sin(π(t-t₀)/w)/(π(t-t₀)/w)
                   Важен в теории сигналов и обработке данных
                
                Параметры:
                - Тип импульса определяет его форму
                - Амплитуда задает максимальное значение
                - Ширина влияет на длительность импульса
                - Скорость определяет распространение в пространстве
                """;
                
            case "collisions" -> """
                Столкновения тел
                
                Моделирование столкновений тел с разными коэффициентами восстановления:
                
                1. Абсолютно упругий удар (k = 1):
                   - Сохраняется кинетическая энергия
                   - Сохраняется импульс
                   v1' = ((m1-m2)v1 + 2m2v2)/(m1+m2)
                   v2' = (2m1v1 - (m1-m2)v2)/(m1+m2)
                
                2. Абсолютно неупругий удар (k = 0):
                   - Тела "слипаются"
                   - Сохраняется только импульс
                   v' = (m1v1 + m2v2)/(m1+m2)
                
                3. Частично упругий удар (0 < k < 1):
                   - Промежуточный случай
                   - k - коэффициент восстановления
                   
                Параметры:
                - Массы тел влияют на результат столкновения
                - Начальные скорости определяют движение до удара
                - Коэффициент восстановления задает тип удара:
                  0 - абсолютно неупругий
                  1 - абсолютно упругий
                """;
                
            case "mirror-reflection" -> """
                Отражение в зеркалах
                
                Моделирование отражения световых лучей в зеркалах различных типов:
                
                1. Плоское зеркало:
                   - Угол падения равен углу отражения
                   - Изображение мнимое, прямое, равное по размеру
                   - Расстояние до изображения равно расстоянию до предмета
                
                2. Вогнутое зеркало:
                   - Параллельные лучи собираются в фокусе
                   - Действительное изображение при d > F
                   - Мнимое увеличенное изображение при d < F
                   - Применяется в телескопах, фарах
                
                3. Выпуклое зеркало:
                   - Параллельные лучи рассеиваются
                   - Всегда дает мнимое уменьшенное изображение
                   - Применяется в обзорных зеркалах
                
                Основные законы:
                - Луч падает, отражается и нормаль лежат в одной плоскости
                - Угол падения равен углу отражения
                - Для сферических зеркал: 1/F = 2/R, где R - радиус кривизны
                
                Параметры:
                - Тип зеркала определяет характер отражения
                - Кривизна влияет на фокусное расстояние
                - Угол падения определяет направление лучей
                - Количество лучей для наглядности
                """;
            default -> "Описание для данного типа симуляции отсутствует.";
        };
        
        descriptionView.setText(description);
    }
} 
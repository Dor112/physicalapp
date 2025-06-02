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
    private static final String PANEL_STYLE = """
        -fx-background-color: #f5f5f5;
        -fx-border-color: #ddd;
        -fx-border-width: 1;
        -fx-border-radius: 5;
        -fx-background-radius: 5;
        """;
    
    private static final String TITLE_STYLE = """
        -fx-font-size: 14px;
        -fx-font-weight: bold;
        -fx-text-fill: #2c3e50;
        -fx-padding: 5 0 5 0;
        """;
    
    private static final String PARAM_GROUP_STYLE = """
        -fx-spacing: 10;
        -fx-padding: 10;
        -fx-background-color: white;
        -fx-border-color: #eee;
        -fx-border-width: 1;
        -fx-border-radius: 3;
        -fx-background-radius: 3;
        """;

    public SimulationWindow(Phenomenon phenomenon) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");
        
        // Setup simulation canvas with border
        simulationCanvas = new Canvas(600, 400);
        VBox canvasContainer = new VBox(simulationCanvas);
        canvasContainer.setStyle(PANEL_STYLE);
        canvasContainer.setPadding(new Insets(10));
        root.setCenter(canvasContainer);

        // Setup parameters panel
        parametersPanel = new VBox(15);
        parametersPanel.setPadding(new Insets(15));
        parametersPanel.setPrefWidth(300);
        parametersPanel.setStyle(PANEL_STYLE);
        
        // Add title to parameters panel
        Label paramsTitle = new Label("Параметры симуляции");
        paramsTitle.setStyle(TITLE_STYLE);
        parametersPanel.getChildren().add(paramsTitle);
        
        setupParameters(phenomenon);
        
        // Wrap parameters panel in scroll pane
        ScrollPane paramsScroll = new ScrollPane(parametersPanel);
        paramsScroll.setFitToWidth(true);
        paramsScroll.setStyle("-fx-background-color: transparent;");
        root.setRight(paramsScroll);

        // Setup description view
        descriptionView = new TextArea();
        descriptionView.setEditable(false);
        descriptionView.setWrapText(true);
        descriptionView.setStyle("""
            -fx-font-family: 'Segoe UI', Arial, sans-serif;
            -fx-font-size: 13px;
            """);
        
        VBox descriptionContainer = new VBox(5);
        descriptionContainer.setStyle(PANEL_STYLE);
        descriptionContainer.setPadding(new Insets(10));
        
        Label descTitle = new Label("Описание явления");
        descTitle.setStyle(TITLE_STYLE);
        
        descriptionContainer.getChildren().addAll(descTitle, descriptionView);
        descriptionContainer.setPrefHeight(200);
        root.setBottom(descriptionContainer);

        // Initialize controller
        controller = new SimulationController(phenomenon, simulationCanvas);

        // Load description
        loadDescription(phenomenon);

        setTitle(phenomenon.getName());
        setScene(new Scene(root, 920, 700));

        // Cleanup on window close
        setOnCloseRequest(e -> controller.stop());
    }

    private VBox createParameterGroup(String title) {
        VBox group = new VBox(8);
        group.setStyle(PARAM_GROUP_STYLE);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        group.getChildren().add(titleLabel);
        return group;
    }

    private Slider createParameterSlider(String name, double min, double max, double value) {
        VBox container = new VBox(5);
        container.setAlignment(Pos.CENTER_LEFT);
        
        Label label = new Label(name);
        label.setStyle("-fx-text-fill: #2c3e50;");
        
        Slider slider = new Slider(min, max, value);
        slider.setStyle("""
            -fx-pref-width: 200;
            -fx-background-radius: 3;
            """);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        
        Label valueLabel = new Label(String.format("%.2f", value));
        valueLabel.setStyle("-fx-text-fill: #7f8c8d;");
        slider.valueProperty().addListener((obs, old, val) -> 
            valueLabel.setText(String.format("%.2f", val.doubleValue())));
        
        HBox valueContainer = new HBox(5);
        valueContainer.setAlignment(Pos.CENTER_LEFT);
        valueContainer.getChildren().addAll(label, valueLabel);
        
        container.getChildren().addAll(valueContainer, slider);
        return slider;
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
        Slider lengthSlider = createParameterSlider("Length (m)", 0.1, 2.0, 1.0);
        lengthSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("length", val.doubleValue()));

        // Initial angle parameter (-180 to 180 degrees)
        Slider angleSlider = createParameterSlider("Initial Angle (degrees)", -180, 180, 45);
        angleSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("angle", val.doubleValue()));

        // Damping coefficient (0 to 1)
        Slider dampingSlider = createParameterSlider("Damping", 0, 1, 0.1);
        dampingSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("damping", val.doubleValue()));

        parametersPanel.getChildren().addAll(
            createParameterGroup("Simple Pendulum Parameters:"),
            lengthSlider,
            angleSlider,
            dampingSlider
        );
    }

    private void setupDoublePendulumParams() {
        // Length of first pendulum (0.1 to 2.0 meters)
        Slider length1Slider = createParameterSlider("Length 1 (m)", 0.1, 2.0, 1.0);
        length1Slider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("length1", val.doubleValue()));

        // Length of second pendulum (0.1 to 2.0 meters)
        Slider length2Slider = createParameterSlider("Length 2 (m)", 0.1, 2.0, 1.0);
        length2Slider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("length2", val.doubleValue()));

        // Initial angles (-180 to 180 degrees)
        Slider angle1Slider = createParameterSlider("Angle 1 (degrees)", -180, 180, 45);
        angle1Slider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("angle1", val.doubleValue()));

        Slider angle2Slider = createParameterSlider("Angle 2 (degrees)", -180, 180, 45);
        angle2Slider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("angle2", val.doubleValue()));

        parametersPanel.getChildren().addAll(
            createParameterGroup("Double Pendulum Parameters:"),
            length1Slider,
            length2Slider,
            angle1Slider,
            angle2Slider
        );
    }

    private void setupStringWaveParams() {
        // String tension (0 to 100 N)
        Slider tensionSlider = createParameterSlider("Tension (N)", 0, 100, 50);
        tensionSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("tension", val.doubleValue()));

        // Wave amplitude (0 to 1)
        Slider amplitudeSlider = createParameterSlider("Amplitude", 0, 1, 0.5);
        amplitudeSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("amplitude", val.doubleValue()));

        // Wave frequency (0 to 10 Hz)
        Slider frequencySlider = createParameterSlider("Frequency (Hz)", 0, 10, 1);
        frequencySlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("frequency", val.doubleValue()));

        parametersPanel.getChildren().addAll(
            createParameterGroup("String Wave Parameters:"),
            tensionSlider,
            amplitudeSlider,
            frequencySlider
        );
    }

    private void setupSpringOscillatorParams() {
        // Spring constant (0 to 100 N/m)
        Slider springConstantSlider = createParameterSlider("Spring Constant (N/m)", 0, 100, 50);
        springConstantSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("springConstant", val.doubleValue()));

        // Mass (0.1 to 10 kg)
        Slider massSlider = createParameterSlider("Mass (kg)", 0.1, 10, 1);
        massSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("mass", val.doubleValue()));

        // Damping coefficient (0 to 1)
        Slider dampingSlider = createParameterSlider("Damping", 0, 1, 0.1);
        dampingSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("damping", val.doubleValue()));

        parametersPanel.getChildren().addAll(
            createParameterGroup("Spring Oscillator Parameters:"),
            springConstantSlider,
            massSlider,
            dampingSlider
        );
    }

    private void setupStandingWaveParams() {
        // Wave speed (0 to 10 m/s)
        Slider speedSlider = createParameterSlider("Wave Speed (m/s)", 0, 10, 5);
        speedSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("speed", val.doubleValue()));

        // Number of nodes (1 to 10)
        Slider nodesSlider = createParameterSlider("Number of Nodes", 1, 10, 3);
        nodesSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("nodes", val.doubleValue()));

        // Amplitude (0 to 1)
        Slider amplitudeSlider = createParameterSlider("Amplitude", 0, 1, 0.5);
        amplitudeSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("amplitude", val.doubleValue()));

        parametersPanel.getChildren().addAll(
            createParameterGroup("Standing Wave Parameters:"),
            speedSlider,
            nodesSlider,
            amplitudeSlider
        );
    }

    private void setupImpulseParams() {
        // Impulse type selection (0-3: Gaussian, Square, Triangular, Sinc)
        Slider typeSlider = createParameterSlider("Impulse Type", 0, 3, 0);
        typeSlider.setSnapToTicks(true);
        typeSlider.setMajorTickUnit(1);
        typeSlider.setMinorTickCount(0);
        typeSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("type", val.doubleValue()));

        // Amplitude (0 to 2)
        Slider amplitudeSlider = createParameterSlider("Amplitude", 0, 2, 1);
        amplitudeSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("amplitude", val.doubleValue()));

        // Width/Duration (0.1 to 2.0)
        Slider widthSlider = createParameterSlider("Width", 0.1, 2.0, 0.5);
        widthSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("width", val.doubleValue()));

        // Propagation speed (0 to 10)
        Slider speedSlider = createParameterSlider("Speed", 0, 10, 5);
        speedSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("speed", val.doubleValue()));

        parametersPanel.getChildren().addAll(
            createParameterGroup("Impulse Parameters:"),
            new Label("Type: 0-Gaussian, 1-Square, 2-Triangular, 3-Sinc"),
            typeSlider,
            amplitudeSlider,
            widthSlider,
            speedSlider
        );
    }

    private void setupCollisionParams() {
        // Масса первого тела (0.1 to 10 кг)
        Slider mass1Slider = createParameterSlider("Масса 1 (кг)", 0.1, 10, 1);
        mass1Slider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("mass1", val.doubleValue()));

        // Масса второго тела (0.1 to 10 кг)
        Slider mass2Slider = createParameterSlider("Масса 2 (кг)", 0.1, 10, 1);
        mass2Slider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("mass2", val.doubleValue()));

        // Начальная скорость первого тела (-10 to 10 м/с)
        Slider velocity1Slider = createParameterSlider("Скорость 1 (м/с)", -10, 10, 5);
        velocity1Slider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("velocity1", val.doubleValue()));

        // Начальная скорость второго тела (-10 to 10 м/с)
        Slider velocity2Slider = createParameterSlider("Скорость 2 (м/с)", -10, 10, -5);
        velocity2Slider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("velocity2", val.doubleValue()));

        // Коэффициент восстановления (0 - АНУ до 1 - АУУ)
        Slider restitutionSlider = createParameterSlider("Коэффициент восстановления", 0, 1, 1);
        restitutionSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("restitution", val.doubleValue()));

        parametersPanel.getChildren().addAll(
            createParameterGroup("Параметры столкновения:"),
            mass1Slider,
            mass2Slider,
            velocity1Slider,
            velocity2Slider,
            restitutionSlider
        );
    }

    private void setupMirrorParams() {
        VBox group = createParameterGroup("Параметры отражения");
        
        // Тип зеркала (0 - плоское, 1 - вогнутое, 2 - выпуклое)
        Slider mirrorTypeSlider = createParameterSlider("Тип зеркала", 0, 2, 0);
        mirrorTypeSlider.setSnapToTicks(true);
        mirrorTypeSlider.setMajorTickUnit(1);
        mirrorTypeSlider.setMinorTickCount(0);
        mirrorTypeSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("mirrorType", val.doubleValue()));

        Label typeHint = new Label("0 - плоское, 1 - вогнутое, 2 - выпуклое");
        typeHint.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 11px;");
        
        // Остальные слайдеры
        Slider curvatureSlider = createParameterSlider("Кривизна", 0.1, 2.0, 1.0);
        curvatureSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("curvature", val.doubleValue()));

        Slider angleSlider = createParameterSlider("Угол падения (°)", -80, 80, 30);
        angleSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("angle", val.doubleValue()));

        Slider raysSlider = createParameterSlider("Количество лучей", 1, 10, 3);
        raysSlider.setSnapToTicks(true);
        raysSlider.setMajorTickUnit(1);
        raysSlider.setMinorTickCount(0);
        raysSlider.valueProperty().addListener((obs, old, val) -> 
            controller.updateParameters("rays", val.doubleValue()));

        group.getChildren().addAll(
            mirrorTypeSlider,
            typeHint,
            curvatureSlider,
            angleSlider,
            raysSlider
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
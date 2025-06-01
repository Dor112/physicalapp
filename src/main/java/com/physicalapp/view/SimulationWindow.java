package com.physicalapp.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
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
        parametersPanel.setPadding(new Insets(10));
        setupParameters(phenomenon);
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

        // Cleanup on window close
        setOnCloseRequest(e -> controller.stop());
    }

    private void setupParameters(Phenomenon phenomenon) {
        switch (phenomenon.getId()) {
            case "simple-pendulum" -> setupSimplePendulumParams();
            case "double-pendulum" -> setupDoublePendulumParams();
            case "string-wave" -> setupStringWaveParams();
            case "spring-oscillator" -> setupSpringOscillatorParams();
            case "standing-waves" -> setupStandingWaveParams();
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
            new Label("Simple Pendulum Parameters:"),
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
            new Label("Double Pendulum Parameters:"),
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
            new Label("String Wave Parameters:"),
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
            new Label("Spring Oscillator Parameters:"),
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
            new Label("Standing Wave Parameters:"),
            speedSlider,
            nodesSlider,
            amplitudeSlider
        );
    }

    private Slider createParameterSlider(String name, double min, double max, double value) {
        VBox container = new VBox(5);
        Label label = new Label(name);
        
        Slider slider = new Slider(min, max, value);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        
        Label valueLabel = new Label(String.format("%.2f", value));
        slider.valueProperty().addListener((obs, old, val) -> 
            valueLabel.setText(String.format("%.2f", val.doubleValue())));
        
        container.getChildren().addAll(label, slider, valueLabel);
        parametersPanel.getChildren().add(container);
        return slider;
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
            default -> "Описание для данного типа симуляции отсутствует.";
        };
        
        descriptionView.setText(description);
    }
} 
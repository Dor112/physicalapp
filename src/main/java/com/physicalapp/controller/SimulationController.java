package com.physicalapp.controller;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.physicalapp.model.Phenomenon;
import java.util.HashMap;
import java.util.Map;

public class SimulationController {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Phenomenon phenomenon;
    private final Map<String, Double> parameters;
    private AnimationTimer animationTimer;
    private long lastUpdate = 0;
    private double time = 0;

    public SimulationController(Phenomenon phenomenon, Canvas canvas) {
        this.phenomenon = phenomenon;
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.parameters = new HashMap<>();
        
        // Set default parameters based on phenomenon type
        initializeDefaultParameters();
        
        // Start animation
        startSimulation();
    }

    private void initializeDefaultParameters() {
        switch (phenomenon.getId()) {
            case "simple-pendulum" -> {
                parameters.put("length", 1.0);
                parameters.put("angle", 45.0);
                parameters.put("damping", 0.1);
            }
            case "double-pendulum" -> {
                parameters.put("length1", 1.0);
                parameters.put("length2", 1.0);
                parameters.put("angle1", 45.0);
                parameters.put("angle2", 45.0);
                parameters.put("mass1", 1.0);
                parameters.put("mass2", 1.0);
            }
            case "string-wave" -> {
                parameters.put("tension", 50.0);
                parameters.put("amplitude", 0.5);
                parameters.put("frequency", 1.0);
            }
            case "spring-oscillator" -> {
                parameters.put("springConstant", 50.0);
                parameters.put("mass", 1.0);
                parameters.put("damping", 0.1);
            }
            case "standing-waves" -> {
                parameters.put("speed", 5.0);
                parameters.put("nodes", 3.0);
                parameters.put("amplitude", 0.5);
            }
            default -> throw new IllegalStateException("Unknown phenomenon: " + phenomenon.getId());
        }
    }

    public void updateParameters(String name, double value) {
        parameters.put(name, value);
    }

    public void startSimulation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }

                double deltaTime = (now - lastUpdate) / 1e9; // Convert to seconds
                time += deltaTime;
                lastUpdate = now;

                updateSimulation(deltaTime);
            }
        };
        animationTimer.start();
    }

    public void stop() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    private void updateSimulation(double deltaTime) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        switch (phenomenon.getId()) {
            case "simple-pendulum" -> drawSimplePendulum();
            case "double-pendulum" -> drawDoublePendulum();
            case "string-wave" -> drawStringWave();
            case "spring-oscillator" -> drawSpringOscillator();
            case "standing-waves" -> drawStandingWaves();
            default -> throw new IllegalStateException("Unknown phenomenon: " + phenomenon.getId());
        }
    }

    private void drawSimplePendulum() {
        double length = parameters.get("length") * 100; // Convert to pixels
        double angle = Math.toRadians(parameters.get("angle"));
        double damping = parameters.get("damping");
        
        // Calculate pendulum position
        double g = 9.81;
        double omega = Math.sqrt(g / (length / 100)); // Convert length back to meters for calculation
        double dampedAngle = angle * Math.exp(-damping * time) * Math.cos(omega * time);
        
        // Drawing
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 3;
        
        double bobX = centerX + length * Math.sin(dampedAngle);
        double bobY = centerY + length * Math.cos(dampedAngle);
        
        // Draw string
        gc.strokeLine(centerX, centerY, bobX, bobY);
        
        // Draw bob
        gc.setFill(Color.RED);
        gc.fillOval(bobX - 10, bobY - 10, 20, 20);
    }

    private void drawDoublePendulum() {
        double l1 = parameters.get("length1") * 100;
        double l2 = parameters.get("length2") * 100;
        double theta1 = Math.toRadians(parameters.get("angle1"));
        double theta2 = Math.toRadians(parameters.get("angle2"));
        
        // Calculate positions
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 3;
        
        double x1 = centerX + l1 * Math.sin(theta1);
        double y1 = centerY + l1 * Math.cos(theta1);
        
        double x2 = x1 + l2 * Math.sin(theta2);
        double y2 = y1 + l2 * Math.cos(theta2);
        
        // Draw strings
        gc.strokeLine(centerX, centerY, x1, y1);
        gc.strokeLine(x1, y1, x2, y2);
        
        // Draw bobs
        gc.setFill(Color.RED);
        gc.fillOval(x1 - 10, y1 - 10, 20, 20);
        gc.fillOval(x2 - 10, y2 - 10, 20, 20);
    }

    private void drawStringWave() {
        double tension = parameters.get("tension");
        double amplitude = parameters.get("amplitude") * 100;
        double frequency = parameters.get("frequency");
        
        double width = canvas.getWidth();
        double centerY = canvas.getHeight() / 2;
        
        gc.beginPath();
        gc.moveTo(0, centerY);
        
        for (double x = 0; x <= width; x++) {
            double wavelength = Math.sqrt(tension) * 50; // Simplified wave speed calculation
            double y = centerY + amplitude * Math.sin(2 * Math.PI * (x / wavelength - frequency * time));
            gc.lineTo(x, y);
        }
        
        gc.stroke();
    }

    private void drawSpringOscillator() {
        double k = parameters.get("springConstant");
        double m = parameters.get("mass");
        double damping = parameters.get("damping");
        
        double omega = Math.sqrt(k / m);
        double amplitude = 100; // pixels
        
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;
        
        // Calculate displacement
        double displacement = amplitude * Math.exp(-damping * time) * Math.cos(omega * time);
        
        // Draw spring (simplified as a line)
        gc.strokeLine(centerX - 100, centerY, centerX + displacement - 20, centerY);
        
        // Draw mass
        gc.setFill(Color.BLUE);
        gc.fillRect(centerX + displacement - 20, centerY - 20, 40, 40);
        
        // Draw wall
        gc.setFill(Color.GRAY);
        gc.fillRect(centerX - 120, centerY - 50, 20, 100);
    }

    private void drawStandingWaves() {
        double speed = parameters.get("speed");
        int nodes = parameters.get("nodes").intValue();
        double amplitude = parameters.get("amplitude") * 100;
        
        double width = canvas.getWidth();
        double centerY = canvas.getHeight() / 2;
        
        gc.beginPath();
        gc.moveTo(0, centerY);
        
        for (double x = 0; x <= width; x++) {
            double k = nodes * Math.PI / width;
            double omega = k * speed;
            double y = centerY + amplitude * Math.sin(k * x) * Math.cos(omega * time);
            gc.lineTo(x, y);
        }
        
        gc.stroke();
        
        // Draw nodes
        gc.setFill(Color.RED);
        for (int i = 0; i <= nodes; i++) {
            double x = i * width / nodes;
            gc.fillOval(x - 5, centerY - 5, 10, 10);
        }
    }
} 
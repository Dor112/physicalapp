package com.physicalapp.simulation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SimplePendulumSimulation implements PhysicsSimulation {
    private static final double G = 9.81; // Acceleration due to gravity (m/s^2)
    
    private double length = 1.0; // Length of pendulum (m)
    private double angle = Math.PI / 4; // Initial angle (radians)
    private double angularVelocity = 0.0; // Angular velocity (rad/s)
    private double damping = 0.1; // Damping coefficient
    
    @Override
    public void update(double deltaTime) {
        // Using RK4 integration method for better accuracy
        double k1 = calculateAcceleration(angle, angularVelocity);
        double l1 = angularVelocity;
        
        double k2 = calculateAcceleration(angle + 0.5 * deltaTime * l1, 
                                        angularVelocity + 0.5 * deltaTime * k1);
        double l2 = angularVelocity + 0.5 * deltaTime * k1;
        
        double k3 = calculateAcceleration(angle + 0.5 * deltaTime * l2,
                                        angularVelocity + 0.5 * deltaTime * k2);
        double l3 = angularVelocity + 0.5 * deltaTime * k2;
        
        double k4 = calculateAcceleration(angle + deltaTime * l3,
                                        angularVelocity + deltaTime * k3);
        double l4 = angularVelocity + deltaTime * k3;
        
        angularVelocity += (deltaTime / 6.0) * (k1 + 2 * k2 + 2 * k3 + k4);
        angle += (deltaTime / 6.0) * (l1 + 2 * l2 + 2 * l3 + l4);
    }
    
    private double calculateAcceleration(double theta, double omega) {
        return -G / length * Math.sin(theta) - damping * omega;
    }
    
    @Override
    public void draw(GraphicsContext gc) {
        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();
        
        // Convert simulation coordinates to screen coordinates
        double scale = height / 3; // Scale factor to convert meters to pixels
        double pivotX = width / 2;
        double pivotY = height / 3;
        
        double bobX = pivotX + scale * length * Math.sin(angle);
        double bobY = pivotY + scale * length * Math.cos(angle);
        
        // Draw pivot point
        gc.setFill(Color.BLACK);
        gc.fillOval(pivotX - 5, pivotY - 5, 10, 10);
        
        // Draw rod
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeLine(pivotX, pivotY, bobX, bobY);
        
        // Draw bob
        gc.setFill(Color.RED);
        gc.fillOval(bobX - 15, bobY - 15, 30, 30);
    }
    
    @Override
    public void updateParameter(String paramName, double value) {
        switch (paramName) {
            case "length" -> length = value;
            case "damping" -> damping = value;
            case "angle" -> {
                angle = Math.toRadians(value);
                angularVelocity = 0; // Reset velocity when angle is changed
            }
        }
    }
} 
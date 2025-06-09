package com.physicalapp.simulation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class DoublePendulumSimulation implements PhysicsSimulation {
    // Physics constants
    private double g = 9.81;          // gravity
    private double length1 = 120;     // length of first pendulum arm
    private double length2 = 120;     // length of second pendulum arm
    private double mass1 = 10;        // mass of first bob
    private double mass2 = 10;        // mass of second bob

    // State variables
    private double angle1 = Math.PI / 4;    // initial angle of first pendulum (45 degrees)
    private double angle2 = Math.PI / 2;    // initial angle of second pendulum (90 degrees)
    private double angleVelocity1 = 0;      // angular velocity of first pendulum
    private double angleVelocity2 = 0;      // angular velocity of second pendulum
    
    // Trail effect
    private List<Point> trail = new ArrayList<>();
    private static final int MAX_TRAIL_LENGTH = 50;
    
    private static class Point {
        double x, y;
        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
    
    @Override
    public void update(double deltaTime) {

        deltaTime = Math.min(deltaTime, 0.016); // Max 16ms (roughly 60 FPS)
        

        double scaleFactor = 2.0;
        

        double num1 = -g * (2 * mass1 + mass2) * Math.sin(angle1);
        double num2 = -mass2 * g * Math.sin(angle1 - 2 * angle2);
        double num3 = -2 * Math.sin(angle1 - angle2) * mass2;
        double num4 = angleVelocity2 * angleVelocity2 * length2 
                     + angleVelocity1 * angleVelocity1 * length1 * Math.cos(angle1 - angle2);
        double den = length1 * (2 * mass1 + mass2 - mass2 * Math.cos(2 * angle1 - 2 * angle2));
        double angleAcceleration1 = (num1 + num2 + num3 * num4) / den;

        double num5 = 2 * Math.sin(angle1 - angle2);
        double num6 = angleVelocity1 * angleVelocity1 * length1 * (mass1 + mass2);
        double num7 = g * (mass1 + mass2) * Math.cos(angle1);
        double num8 = angleVelocity2 * angleVelocity2 * length2 * mass2 * Math.cos(angle1 - angle2);
        double den2 = length2 * (2 * mass1 + mass2 - mass2 * Math.cos(2 * angle1 - 2 * angle2));
        double angleAcceleration2 = (num5 * (num6 + num7 + num8)) / den2;


        angleVelocity1 += angleAcceleration1 * deltaTime * scaleFactor;
        angleVelocity2 += angleAcceleration2 * deltaTime * scaleFactor;
        angle1 += angleVelocity1 * deltaTime * scaleFactor;
        angle2 += angleVelocity2 * deltaTime * scaleFactor;


        angleVelocity1 *= 0.9999;
        angleVelocity2 *= 0.9999;
    }
    
    @Override
    public void draw(GraphicsContext gc) {
        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();
        

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);
        

        double originX = width / 2;
        double originY = height / 3;
        

        double x1 = originX + length1 * Math.sin(angle1);
        double y1 = originY + length1 * Math.cos(angle1);
        double x2 = x1 + length2 * Math.sin(angle2);
        double y2 = y1 + length2 * Math.cos(angle2);
        

        gc.setStroke(Color.rgb(0, 150, 255, 0.2));
        gc.beginPath();
        for (int i = 0; i < trail.size() - 1; i++) {
            Point p1 = trail.get(i);
            Point p2 = trail.get(i + 1);
            gc.strokeLine(p1.x, p1.y, p2.x, p2.y);
        }
        gc.stroke();
        

        trail.add(new Point(x2, y2));
        if (trail.size() > MAX_TRAIL_LENGTH) {
            trail.remove(0);
        }
        

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeLine(originX, originY, x1, y1);
        gc.strokeLine(x1, y1, x2, y2);
        

        gc.setFill(Color.BLACK);
        double bobRadius = 10;
        gc.fillOval(x1 - bobRadius, y1 - bobRadius, bobRadius * 2, bobRadius * 2);
        gc.fillOval(x2 - bobRadius, y2 - bobRadius, bobRadius * 2, bobRadius * 2);
    }
    
    @Override
    public void updateParameter(String paramName, double value) {
        switch (paramName) {
            case "length1":
                length1 = value;
                break;
            case "length2":
                length2 = value;
                break;
            case "mass1":
                mass1 = value;
                break;
            case "mass2":
                mass2 = value;
                break;
            case "gravity":
                g = value;
                break;
            case "angle1":
                angle1 = value;
                break;
            case "angle2":
                angle2 = value;
                break;
        }
    }
} 
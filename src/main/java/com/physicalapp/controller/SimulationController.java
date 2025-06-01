package com.physicalapp.controller;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import com.physicalapp.model.Phenomenon;
import com.physicalapp.simulation.PhysicsSimulation;
import com.physicalapp.simulation.SimplePendulumSimulation;
import com.physicalapp.simulation.DoublePendulumSimulation;
import com.physicalapp.simulation.StringWaveSimulation;
import com.physicalapp.simulation.SpringOscillatorSimulation;
import com.physicalapp.simulation.StandingWaveSimulation;

public class SimulationController {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private PhysicsSimulation simulation;
    private AnimationTimer animationTimer;

    public SimulationController(Phenomenon phenomenon, Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        
        // Create appropriate simulation based on phenomenon type
        simulation = createSimulation(phenomenon);
        
        // Setup animation timer
        setupAnimationTimer();
    }

    private PhysicsSimulation createSimulation(Phenomenon phenomenon) {
        return switch (phenomenon.getId()) {
            case "simple-pendulum" -> new SimplePendulumSimulation();
            case "double-pendulum" -> new DoublePendulumSimulation();
            case "string-wave" -> new StringWaveSimulation();
            case "spring-oscillator" -> new SpringOscillatorSimulation();
            case "standing-waves" -> new StandingWaveSimulation();
            default -> throw new IllegalArgumentException("Unknown phenomenon: " + phenomenon.getId());
        };
    }

    private void setupAnimationTimer() {
        animationTimer = new AnimationTimer() {
            private long lastUpdate = 0;
            
            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }
                
                double elapsedSeconds = (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;
                
                // Update simulation
                simulation.update(elapsedSeconds);
                
                // Clear canvas
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                
                // Draw simulation
                simulation.draw(gc);
            }
        };
        animationTimer.start();
    }

    public void stop() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    public void updateParameters(String paramName, double value) {
        simulation.updateParameter(paramName, value);
    }
} 
package com.physicalapp.simulation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SpringOscillatorSimulation implements PhysicsSimulation {
    // TODO: Implement spring oscillator physics
    
    @Override
    public void update(double deltaTime) {

    }
    
    @Override
    public void draw(GraphicsContext gc) {

        gc.setFill(Color.BLACK);
        gc.fillText("Spring Oscillator Simulation (Not Implemented)", 10, 20);
    }
    
    @Override
    public void updateParameter(String paramName, double value) {

    }
} 
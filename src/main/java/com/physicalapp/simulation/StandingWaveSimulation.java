package com.physicalapp.simulation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class StandingWaveSimulation implements PhysicsSimulation {

    
    @Override
    public void update(double deltaTime) {

    }
    
    @Override
    public void draw(GraphicsContext gc) {

        gc.setFill(Color.BLACK);
        gc.fillText("Standing Wave Simulation (Not Implemented)", 10, 20);
    }
    
    @Override
    public void updateParameter(String paramName, double value) {

    }
} 
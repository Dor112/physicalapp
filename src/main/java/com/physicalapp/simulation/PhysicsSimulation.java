package com.physicalapp.simulation;

import javafx.scene.canvas.GraphicsContext;

public interface PhysicsSimulation {
    void update(double deltaTime);
    void draw(GraphicsContext gc);
    void updateParameter(String paramName, double value);
} 
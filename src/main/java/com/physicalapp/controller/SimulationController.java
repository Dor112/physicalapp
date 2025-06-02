package com.physicalapp.controller;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.paint.RadialGradient;
import javafx.scene.effect.DropShadow;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
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

    // Modern color scheme
    private static final Color PRIMARY_COLOR = Color.web("#3498db");
    private static final Color SECONDARY_COLOR = Color.web("#2980b9");
    private static final Color ACCENT_COLOR = Color.web("#e74c3c");
    private static final Color BACKGROUND_COLOR = Color.web("#f5f7fa");
    private static final Color GRID_COLOR = Color.web("#ecf0f1");
    private static final double SHADOW_BLUR = 10;

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
            case "impulse-types" -> {
                parameters.put("type", 0.0);  // 0: Gaussian, 1: Square, 2: Triangular, 3: Sinc
                parameters.put("amplitude", 1.0);
                parameters.put("width", 0.5);
                parameters.put("speed", 5.0);
            }
            case "collisions" -> {
                parameters.put("mass1", 1.0);
                parameters.put("mass2", 1.0);
                parameters.put("velocity1", 5.0);
                parameters.put("velocity2", -5.0);
                parameters.put("restitution", 1.0);
                parameters.put("collision_time", -1.0); // Время столкновения
                parameters.put("x1", 200.0); // Начальное положение первого тела
                parameters.put("x2", 400.0); // Начальное положение второго тела
            }
            case "mirror-reflection" -> {
                parameters.put("mirrorType", 0.0);  // 0: плоское, 1: вогнутое, 2: выпуклое
                parameters.put("curvature", 1.0);
                parameters.put("angle", 30.0);
                parameters.put("rays", 3.0);
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

    private void drawGrid() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        double spacing = 20;

        gc.setStroke(GRID_COLOR);
        gc.setLineWidth(1);

        // Draw vertical lines
        for (double x = 0; x <= width; x += spacing) {
            gc.strokeLine(x, 0, x, height);
        }

        // Draw horizontal lines
        for (double y = 0; y <= height; y += spacing) {
            gc.strokeLine(0, y, width, y);
        }
    }

    private void updateSimulation(double deltaTime) {
        gc.setFill(BACKGROUND_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        drawGrid();
        
        gc.setLineWidth(2);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);

        switch (phenomenon.getId()) {
            case "simple-pendulum" -> drawSimplePendulum();
            case "double-pendulum" -> drawDoublePendulum();
            case "string-wave" -> drawStringWave();
            case "spring-oscillator" -> drawSpringOscillator();
            case "standing-waves" -> drawStandingWaves();
            case "impulse-types" -> drawImpulse();
            case "collisions" -> updateCollision(deltaTime);
            case "mirror-reflection" -> drawMirrorReflection();
            default -> throw new IllegalStateException("Unknown phenomenon: " + phenomenon.getId());
        }
    }

    private void drawSimplePendulum() {
        double length = parameters.get("length") * 100;
        double angle = Math.toRadians(parameters.get("angle"));
        double damping = parameters.get("damping");
        
        double g = 9.81;
        double omega = Math.sqrt(g / (length / 100));
        double dampedAngle = angle * Math.exp(-damping * time) * Math.cos(omega * time);
        
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 3;
        
        double bobX = centerX + length * Math.sin(dampedAngle);
        double bobY = centerY + length * Math.cos(dampedAngle);
        
        // Draw string with gradient
        LinearGradient stringGradient = new LinearGradient(
            centerX, centerY,
            bobX, bobY,
            false, CycleMethod.NO_CYCLE,
            new Stop(0, PRIMARY_COLOR),
            new Stop(1, SECONDARY_COLOR)
        );
        gc.setStroke(stringGradient);
        gc.setLineWidth(3);
        gc.strokeLine(centerX, centerY, bobX, bobY);
        
        // Draw pivot point with shadow
        gc.setFill(PRIMARY_COLOR);
        gc.setEffect(new DropShadow(SHADOW_BLUR, Color.rgb(0, 0, 0, 0.3)));
        gc.fillOval(centerX - 8, centerY - 8, 16, 16);
        
        // Draw bob with gradient and shadow
        RadialGradient bobGradient = new RadialGradient(
            0, 0, bobX, bobY, 15,
            false, CycleMethod.NO_CYCLE,
            new Stop(0, ACCENT_COLOR),
            new Stop(1, ACCENT_COLOR.darker())
        );
        gc.setFill(bobGradient);
        gc.fillOval(bobX - 15, bobY - 15, 30, 30);
        gc.setEffect(null);
    }

    private void drawDoublePendulum() {
        double l1 = parameters.get("length1") * 100;
        double l2 = parameters.get("length2") * 100;
        double theta1 = Math.toRadians(parameters.get("angle1"));
        double theta2 = Math.toRadians(parameters.get("angle2"));
        
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 3;
        
        double x1 = centerX + l1 * Math.sin(theta1);
        double y1 = centerY + l1 * Math.cos(theta1);
        
        double x2 = x1 + l2 * Math.sin(theta2);
        double y2 = y1 + l2 * Math.cos(theta2);
        
        // Draw strings with gradients
        LinearGradient string1Gradient = new LinearGradient(
            centerX, centerY, x1, y1,
            false, CycleMethod.NO_CYCLE,
            new Stop(0, PRIMARY_COLOR),
            new Stop(1, SECONDARY_COLOR)
        );
        LinearGradient string2Gradient = new LinearGradient(
            x1, y1, x2, y2,
            false, CycleMethod.NO_CYCLE,
            new Stop(0, PRIMARY_COLOR),
            new Stop(1, SECONDARY_COLOR)
        );
        
        gc.setLineWidth(3);
        gc.setStroke(string1Gradient);
        gc.strokeLine(centerX, centerY, x1, y1);
        gc.setStroke(string2Gradient);
        gc.strokeLine(x1, y1, x2, y2);
        
        // Draw pivot and bobs with shadows
        gc.setEffect(new DropShadow(SHADOW_BLUR, Color.rgb(0, 0, 0, 0.3)));
        
        // Pivot point
        gc.setFill(PRIMARY_COLOR);
        gc.fillOval(centerX - 8, centerY - 8, 16, 16);
        
        // First bob
        RadialGradient bob1Gradient = new RadialGradient(
            0, 0, x1, y1, 15,
            false, CycleMethod.NO_CYCLE,
            new Stop(0, ACCENT_COLOR),
            new Stop(1, ACCENT_COLOR.darker())
        );
        gc.setFill(bob1Gradient);
        gc.fillOval(x1 - 15, y1 - 15, 30, 30);
        
        // Second bob
        RadialGradient bob2Gradient = new RadialGradient(
            0, 0, x2, y2, 15,
            false, CycleMethod.NO_CYCLE,
            new Stop(0, ACCENT_COLOR),
            new Stop(1, ACCENT_COLOR.darker())
        );
        gc.setFill(bob2Gradient);
        gc.fillOval(x2 - 15, y2 - 15, 30, 30);
        
        gc.setEffect(null);
    }

    private void drawStringWave() {
        double tension = parameters.get("tension");
        double amplitude = parameters.get("amplitude") * 100;
        double frequency = parameters.get("frequency");
        
        double width = canvas.getWidth();
        double centerY = canvas.getHeight() / 2;
        
        // Draw wave with gradient
        LinearGradient waveGradient = new LinearGradient(
            0, centerY - amplitude,
            0, centerY + amplitude,
            false, CycleMethod.NO_CYCLE,
            new Stop(0, PRIMARY_COLOR),
            new Stop(1, SECONDARY_COLOR)
        );
        gc.setStroke(waveGradient);
        gc.setLineWidth(3);
        
        gc.beginPath();
        gc.moveTo(0, centerY);
        
        for (double x = 0; x <= width; x++) {
            double wavelength = Math.sqrt(tension) * 50;
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
        double amplitude = 100;
        
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;
        
        double displacement = amplitude * Math.exp(-damping * time) * Math.cos(omega * time);
        
        // Draw wall with shadow
        gc.setEffect(new DropShadow(SHADOW_BLUR, Color.rgb(0, 0, 0, 0.3)));
        gc.setFill(Color.web("#95a5a6"));
        gc.fillRect(centerX - 120, centerY - 50, 20, 100);
        
        // Draw spring with gradient
        LinearGradient springGradient = new LinearGradient(
            centerX - 100, centerY,
            centerX + displacement - 20, centerY,
            false, CycleMethod.NO_CYCLE,
            new Stop(0, PRIMARY_COLOR),
            new Stop(1, SECONDARY_COLOR)
        );
        gc.setStroke(springGradient);
        gc.setLineWidth(3);
        gc.strokeLine(centerX - 100, centerY, centerX + displacement - 20, centerY);
        
        // Draw mass with gradient
        RadialGradient massGradient = new RadialGradient(
            0, 0,
            centerX + displacement, centerY,
            20, false, CycleMethod.NO_CYCLE,
            new Stop(0, ACCENT_COLOR),
            new Stop(1, ACCENT_COLOR.darker())
        );
        gc.setFill(massGradient);
        gc.fillRect(centerX + displacement - 20, centerY - 20, 40, 40);
        
        gc.setEffect(null);
    }

    private void drawStandingWaves() {
        double speed = parameters.get("speed");
        int nodes = parameters.get("nodes").intValue();
        double amplitude = parameters.get("amplitude") * 100;
        
        double width = canvas.getWidth();
        double centerY = canvas.getHeight() / 2;
        
        // Draw wave with gradient
        LinearGradient waveGradient = new LinearGradient(
            0, centerY - amplitude,
            0, centerY + amplitude,
            false, CycleMethod.NO_CYCLE,
            new Stop(0, PRIMARY_COLOR),
            new Stop(1, SECONDARY_COLOR)
        );
        gc.setStroke(waveGradient);
        gc.setLineWidth(3);
        
        gc.beginPath();
        gc.moveTo(0, centerY);
        
        for (double x = 0; x <= width; x++) {
            double k = nodes * Math.PI / width;
            double omega = k * speed;
            double y = centerY + amplitude * Math.sin(k * x) * Math.cos(omega * time);
            gc.lineTo(x, y);
        }
        
        gc.stroke();
        
        // Draw nodes with shadow effect
        gc.setEffect(new DropShadow(SHADOW_BLUR, Color.rgb(0, 0, 0, 0.3)));
        gc.setFill(ACCENT_COLOR);
        for (int i = 0; i <= nodes; i++) {
            double x = i * width / nodes;
            gc.fillOval(x - 6, centerY - 6, 12, 12);
        }
        gc.setEffect(null);
    }

    private void drawImpulse() {
        int type = parameters.get("type").intValue();
        double amplitude = parameters.get("amplitude");
        double width = parameters.get("width");
        double speed = parameters.get("speed");
        
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
        double centerY = canvasHeight / 2;
        
        // Draw axis
        gc.setStroke(Color.GRAY);
        gc.strokeLine(0, centerY, canvasWidth, centerY);
        gc.setStroke(Color.BLACK);
        
        // Calculate position of impulse center
        double t0 = (time * speed) % (canvasWidth * 1.5) - canvasWidth * 0.25;
        
        gc.beginPath();
        gc.moveTo(0, centerY);
        
        for (double x = 0; x <= canvasWidth; x++) {
            double t = (x - t0) / (width * 100);  // Scale width
            double y = centerY - amplitude * 100 * calculateImpulse(type, t);  // Scale amplitude
            gc.lineTo(x, y);
        }
        
        gc.stroke();
    }
    
    private double calculateImpulse(int type, double t) {
        return switch (type) {
            case 0 -> // Gaussian
                Math.exp(-t * t / 2);
            
            case 1 -> // Square
                Math.abs(t) <= 1 ? 1 : 0;
            
            case 2 -> // Triangular
                Math.abs(t) <= 1 ? 1 - Math.abs(t) : 0;
            
            case 3 -> // Sinc
                t == 0 ? 1 : Math.sin(Math.PI * t) / (Math.PI * t);
            
            default -> 0;
        };
    }

    private void updateCollision(double deltaTime) {
        double m1 = parameters.get("mass1");
        double m2 = parameters.get("mass2");
        double v1 = parameters.get("velocity1");
        double v2 = parameters.get("velocity2");
        double k = parameters.get("restitution");
        double x1 = parameters.get("x1");
        double x2 = parameters.get("x2");
        
        double radius1 = 20 * Math.pow(m1, 1.0/3.0); // Радиус пропорционален кубическому корню из массы
        double radius2 = 20 * Math.pow(m2, 1.0/3.0);
        
        // Сначала проверяем, произойдет ли столкновение на следующем шаге
        double nextX1 = x1 + v1 * deltaTime * 100;
        double nextX2 = x2 + v2 * deltaTime * 100;
        
        boolean willCollide = (nextX1 + radius1 >= nextX2 - radius2) && (v1 > v2);
        
        if (willCollide) {
            // Вычисляем новые скорости при столкновении
            double v1New, v2New;
            
            if (k == 1.0) { // Абсолютно упругий удар
                v1New = ((m1 - m2) * v1 + 2 * m2 * v2) / (m1 + m2);
                v2New = (2 * m1 * v1 - (m1 - m2) * v2) / (m1 + m2);
            } else if (k == 0.0) { // Абсолютно неупругий удар
                v1New = v2New = (m1 * v1 + m2 * v2) / (m1 + m2);
            } else { // Частично упругий удар
                double v1New_elastic = ((m1 - m2) * v1 + 2 * m2 * v2) / (m1 + m2);
                double v2New_elastic = (2 * m1 * v1 - (m1 - m2) * v2) / (m1 + m2);
                double vCom = (m1 * v1 + m2 * v2) / (m1 + m2);
                
                v1New = vCom + k * (v1New_elastic - vCom);
                v2New = vCom + k * (v2New_elastic - vCom);
            }
            
            // Обновляем скорости
            parameters.put("velocity1", v1New);
            parameters.put("velocity2", v2New);
            v1 = v1New;
            v2 = v2New;
            
            // Корректируем позиции, чтобы тела точно не перекрывались
            double minDistance = radius1 + radius2;
            double overlap = (x1 + radius1) - (x2 - radius2);
            if (overlap > 0) {
                x1 -= overlap / 2;
                x2 += overlap / 2;
            }
        }
        
        // Обновляем позиции с новыми скоростями
        x1 += v1 * deltaTime * 100;
        x2 += v2 * deltaTime * 100;
        
        // Проверяем границы экрана и отражение от стенок
        if (x1 - radius1 < 0) {
            x1 = radius1;
            parameters.put("velocity1", Math.abs(v1));
        } else if (x1 + radius1 > canvas.getWidth()) {
            x1 = canvas.getWidth() - radius1;
            parameters.put("velocity1", -Math.abs(v1));
        }
        
        if (x2 - radius2 < 0) {
            x2 = radius2;
            parameters.put("velocity2", Math.abs(v2));
        } else if (x2 + radius2 > canvas.getWidth()) {
            x2 = canvas.getWidth() - radius2;
            parameters.put("velocity2", -Math.abs(v2));
        }
        
        // Сохраняем обновленные позиции
        parameters.put("x1", x1);
        parameters.put("x2", x2);
        
        // Рисуем тела
        double centerY = canvas.getHeight() / 2;
        
        // Первое тело
        gc.setFill(Color.BLUE);
        gc.fillOval(x1 - radius1, centerY - radius1, radius1 * 2, radius1 * 2);
        
        // Второе тело
        gc.setFill(Color.RED);
        gc.fillOval(x2 - radius2, centerY - radius2, radius2 * 2, radius2 * 2);
        
        // Рисуем векторы скорости
        gc.setStroke(Color.BLACK);
        drawVelocityVector(x1, centerY, v1, radius1);
        drawVelocityVector(x2, centerY, v2, radius2);
        
        // Отображаем информацию о скоростях и энергии
        gc.setFill(Color.BLACK);
        gc.fillText(String.format("v1 = %.2f м/с", v1), 10, 20);
        gc.fillText(String.format("v2 = %.2f м/с", v2), 10, 40);
        
        double kineticEnergy = 0.5 * m1 * v1 * v1 + 0.5 * m2 * v2 * v2;
        double momentum = m1 * v1 + m2 * v2;
        
        gc.fillText(String.format("Кинетическая энергия = %.2f Дж", kineticEnergy), 10, 60);
        gc.fillText(String.format("Импульс = %.2f кг·м/с", momentum), 10, 80);
    }
    
    private void drawVelocityVector(double x, double y, double velocity, double radius) {
        double scale = 10; // Масштаб для отображения вектора скорости
        double arrowLength = velocity * scale;
        double arrowSize = 10;
        
        double endX = x + arrowLength;
        double endY = y;
        
        gc.strokeLine(x, y, endX, endY);
        
        if (velocity != 0) {
            double angle = velocity > 0 ? 0 : Math.PI;
            gc.strokeLine(endX, endY, 
                         endX - arrowSize * Math.cos(angle + Math.PI/6), 
                         endY - arrowSize * Math.sin(angle + Math.PI/6));
            gc.strokeLine(endX, endY, 
                         endX - arrowSize * Math.cos(angle - Math.PI/6), 
                         endY - arrowSize * Math.sin(angle - Math.PI/6));
        }
    }

    private void drawMirrorReflection() {
        int mirrorType = parameters.get("mirrorType").intValue();
        double curvature = parameters.get("curvature");
        double incidentAngle = Math.toRadians(parameters.get("angle")); // Угол падения в радианах
        int rays = parameters.get("rays").intValue();
        
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        double centerX = width / 2;
        double centerY = height / 2;
        
        // Рисуем систему координат
        gc.setStroke(Color.LIGHTGRAY);
        gc.strokeLine(0, centerY, width, centerY);  // Ось X
        gc.strokeLine(centerX, 0, centerX, height); // Ось Y
        
        // Рисуем зеркало
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(3);
        
        switch (mirrorType) {
            case 0 -> { // Плоское зеркало
                gc.strokeLine(centerX - 100, centerY, centerX + 100, centerY);
                drawNormal(centerX, centerY, Math.PI/2); // Нормаль к плоскому зеркалу
            }
            case 1 -> { // Вогнутое зеркало
                double radius = 200 / curvature;
                double startAngle = -Math.PI/3;
                double endAngle = Math.PI/3;
                
                gc.beginPath();
                for (double a = startAngle; a <= endAngle; a += 0.01) {
                    double x = centerX + radius * Math.sin(a);
                    double y = centerY + radius * (1 - Math.cos(a));
                    if (a == startAngle) {
                        gc.moveTo(x, y);
                    } else {
                        gc.lineTo(x, y);
                    }
                }
                gc.stroke();
                
                // Рисуем фокус
                gc.setFill(Color.RED);
                double focalPoint = radius / 2;
                gc.fillOval(centerX - 5, centerY + focalPoint - 5, 10, 10);
                gc.strokeText("F", centerX + 10, centerY + focalPoint);
            }
            case 2 -> { // Выпуклое зеркало
                double radius = 200 / curvature;
                double startAngle = 5*Math.PI/6;
                double endAngle = 7*Math.PI/6;
                
                gc.beginPath();
                for (double a = startAngle; a <= endAngle; a += 0.01) {
                    double x = centerX + radius * Math.sin(a);
                    double y = centerY - 100 + radius * (1 - Math.cos(a));
                    if (a == startAngle) {
                        gc.moveTo(x, y);
                    } else {
                        gc.lineTo(x, y);
                    }
                }
                gc.stroke();
                
                // Рисуем мнимый фокус
                gc.setStroke(Color.RED);
                gc.setLineDashes(5);
                double focalPoint = -radius / 2;
                gc.strokeOval(centerX - 5, centerY - 100 + focalPoint - 5, 10, 10);
                gc.setLineDashes();
                gc.strokeText("F", centerX + 10, centerY - 100 + focalPoint);
            }
        }
        
        // Рисуем падающие и отраженные лучи
        gc.setStroke(Color.GOLD);
        gc.setLineWidth(2);
        
        double raySpacing = 40;  // Расстояние между лучами
        double startY = centerY - (rays - 1) * raySpacing / 2;
        
        for (int i = 0; i < rays; i++) {
            double rayY = startY + i * raySpacing;
            
            // Вычисляем точку падения на зеркало
            double incidentX = centerX;
            double incidentY = centerY;
            
            if (mirrorType != 0) {
                // Для сферических зеркал находим точку пересечения с поверхностью
                double radius = 200 / curvature;
                double dx = rayY - centerY;
                double angle = Math.atan2(dx, radius);
                
                if (mirrorType == 1) { // Вогнутое зеркало
                    incidentX = centerX + radius * Math.sin(angle);
                    incidentY = centerY + radius * (1 - Math.cos(angle));
                } else { // Выпуклое зеркало
                    angle = Math.PI + angle;
                    incidentX = centerX + radius * Math.sin(angle);
                    incidentY = centerY - 100 + radius * (1 - Math.cos(angle));
                }
            }
            
            // Падающий луч
            double rayStartX = incidentX - 200 * Math.cos(incidentAngle);
            gc.strokeLine(rayStartX, rayY, incidentX, incidentY);
            
            // Вычисляем угол отражения
            double normalAngle;
            if (mirrorType == 0) {
                normalAngle = Math.PI/2; // Для плоского зеркала нормаль вертикальна
            } else {
                // Для сферических зеркал нормаль направлена к/от центра кривизны
                normalAngle = Math.atan2(incidentY - centerY, incidentX - centerX);
                if (mirrorType == 2) { // Для выпуклого зеркала нормаль направлена наружу
                    normalAngle += Math.PI;
                }
            }
            
            // Вычисляем угол отражения относительно нормали
            double reflectionAngle = 2 * normalAngle - incidentAngle;
            
            // Отраженный луч
            double reflectedLength = 200;
            double reflectedEndX = incidentX + reflectedLength * Math.cos(reflectionAngle);
            double reflectedEndY = incidentY + reflectedLength * Math.sin(reflectionAngle);
            gc.strokeLine(incidentX, incidentY, reflectedEndX, reflectedEndY);
            
            // Рисуем стрелки на лучах
            drawArrow(incidentX - 20 * Math.cos(incidentAngle), 
                     incidentY - 20 * Math.sin(incidentAngle), 
                     incidentAngle);
            drawArrow(incidentX + 20 * Math.cos(reflectionAngle), 
                     incidentY + 20 * Math.sin(reflectionAngle), 
                     reflectionAngle);
            
            // Рисуем нормаль в точке падения
            gc.setStroke(Color.GREEN);
            gc.setLineDashes(5);
            double normalLength = 30;
            gc.strokeLine(incidentX - normalLength * Math.cos(normalAngle),
                         incidentY - normalLength * Math.sin(normalAngle),
                         incidentX + normalLength * Math.cos(normalAngle),
                         incidentY + normalLength * Math.sin(normalAngle));
            gc.setLineDashes();
            gc.setStroke(Color.GOLD);
        }
    }
    
    private void drawNormal(double x, double y, double angle) {
        gc.setStroke(Color.GREEN);
        gc.setLineDashes(5);
        double length = 50;
        gc.strokeLine(x, y - length, x, y + length);
        gc.setLineDashes();
    }
    
    private void drawArrow(double x, double y, double angle) {
        double arrowSize = 10;
        double arrowAngle = Math.PI / 6;
        
        gc.strokeLine(x, y,
                     x - arrowSize * Math.cos(angle + arrowAngle),
                     y - arrowSize * Math.sin(angle + arrowAngle));
        gc.strokeLine(x, y,
                     x - arrowSize * Math.cos(angle - arrowAngle),
                     y - arrowSize * Math.sin(angle - arrowAngle));
    }
} 
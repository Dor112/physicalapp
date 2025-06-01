package com.physicalapp.model;

public class Phenomenon {
    private final String id;
    private final String name;
    private final String description;

    public Phenomenon(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static Phenomenon[] getAvailablePhenomena() {
        return new Phenomenon[] {
            new Phenomenon(
                "simple-pendulum",
                "Простой маятник",
                "Моделирование движения простого маятника с учетом затухания"
            ),
            new Phenomenon(
                "double-pendulum",
                "Двойной маятник",
                "Моделирование движения двойного маятника, демонстрирующего хаотическое поведение"
            ),
            new Phenomenon(
                "string-wave",
                "Волна на струне",
                "Моделирование распространения волны по натянутой струне"
            ),
            new Phenomenon(
                "spring-oscillator",
                "Пружинный осциллятор",
                "Моделирование колебаний груза на пружине с учетом затухания"
            ),
            new Phenomenon(
                "standing-waves",
                "Стоячие волны",
                "Моделирование стоячих волн при интерференции встречных волн"
            )
        };
    }

    @Override
    public String toString() {
        return name;
    }
} 
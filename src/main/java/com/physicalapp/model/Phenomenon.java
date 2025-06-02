package com.physicalapp.model;

public class Phenomenon {
    private final String id;
    private final String name;
    private final String description;
    private final String shortDescription;

    public Phenomenon(String id, String name, String description, String shortDescription) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.shortDescription = shortDescription;
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

    public String getShortDescription() {
        return shortDescription;
    }

    public static Phenomenon[] getAvailablePhenomena() {
        return new Phenomenon[] {
            new Phenomenon(
                "simple-pendulum",
                "Простой маятник",
                "Полное описание простого маятника...",
                "Моделирование колебаний простого маятника с учетом затухания"
            ),
            new Phenomenon(
                "double-pendulum",
                "Двойной маятник",
                "Полное описание двойного маятника...",
                "Демонстрация хаотической динамики двойного маятника"
            ),
            new Phenomenon(
                "string-wave",
                "Волна на струне",
                "Полное описание волны на струне...",
                "Визуализация распространения волн в натянутой струне"
            ),
            new Phenomenon(
                "spring-oscillator",
                "Пружинный осциллятор",
                "Полное описание пружинного осциллятора...",
                "Моделирование колебаний груза на пружине"
            ),
            new Phenomenon(
                "standing-waves",
                "Стоячие волны",
                "Полное описание стоячих волн...",
                "Демонстрация образования стоячих волн и их мод"
            ),
            new Phenomenon(
                "impulse-types",
                "Типы импульсов",
                "Полное описание типов импульсов...",
                "Изучение различных типов импульсных сигналов"
            ),
            new Phenomenon(
                "collisions",
                "Столкновения",
                "Полное описание столкновений...",
                "Моделирование упругих и неупругих столкновений"
            ),
            new Phenomenon(
                "mirror-reflection",
                "Отражение в зеркалах",
                "Полное описание отражения в зеркалах...",
                "Визуализация отражения света в различных типах зеркал"
            )
        };
    }

    @Override
    public String toString() {
        return name;
    }
} 
package com.physicalapp.model;

public class Phenomenon {
    private String name;
    private String id;

    public Phenomenon(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
} 
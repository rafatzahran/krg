package com.example.krg.models;

public enum EUnit {
    KREFTREGISTERET(Integer.valueOf(1), "Kreftregisteret"),
    AKERSHUS_HF(Integer.valueOf(2), "Akershus universitetssykehus HF"),
    SOUTH_HF(Integer.valueOf(3), "Sørlandet sykehus HF"),
    WEST_HF(Integer.valueOf(4), "Vestre Viken HF");

    private Integer value;
    private String description;

    private EUnit(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    public Integer getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}

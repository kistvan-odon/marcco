package com.garmin.marcco.model;

public class Trash {

    public ObjectType type;
    public int row;
    public int column;
    public int volume;
    public double coefficient;

    public Trash(ObjectType type, int row, int column, int volume) {
        this.type = type;
        this.row = row;
        this.column = column;
        this.volume = volume;
        this.coefficient = 0D;
    }
}

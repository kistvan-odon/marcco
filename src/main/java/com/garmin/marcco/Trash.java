package com.garmin.marcco;

public class Trash {

    public ObjectType type;
    public int row;
    public int collumn;
    public int volume;

    public Trash(ObjectType type, int row, int collumn, int volume) {
        this.type = type;
        this.row = row;
        this.collumn = collumn;
        this.volume = volume;
    }
}

package com.garmin.marcco;

public class Container {
    int filled;
    ObjectType type;

    public Container() {
        this.filled=0;
        this.type=null;
    }

    public int getFilled() {
        return filled;
    }

    public void setFilled(int filled) {
        this.filled = filled;
    }

    public ObjectType getType() {
        return type;
    }

    public void setType(ObjectType type) {
        this.type = type;
    }
}

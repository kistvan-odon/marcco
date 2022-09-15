package com.garmin;

public class Container {
    int filled;
    String type;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

package com.garmin.marcco.model;

public class Container {
    private int volume;
    private ObjectType type;

    public Container() {
        this.volume = 0;
        this.type = null;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public ObjectType getType() {
        return type;
    }

    public void setType(ObjectType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Container{" +
                "volume=" + volume +
                ", type=" + type +
                '}';
    }
}

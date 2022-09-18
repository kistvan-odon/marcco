package com.garmin.marcco.model;

public class LeeResult {
    public int[][] distanceMatrix;
    public Pair[][] positionsMatrix;

    public LeeResult(int[][] distanceMatrix, Pair[][] positionsMatrix) {
        this.distanceMatrix = distanceMatrix;
        this.positionsMatrix = positionsMatrix;
    }
}

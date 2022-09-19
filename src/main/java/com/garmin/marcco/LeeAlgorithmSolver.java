package com.garmin.marcco;

import com.garmin.marcco.model.LeeResult;
import com.garmin.marcco.model.Pair;
import com.garmin.marcco.model.Trash;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

public class LeeAlgorithmSolver {
    private static final int[] k_x = {-1, 0, 1, 0};
    private static final int[] k_y = {0, 1, 0, -1};

    private static void convertCharacterMatrixToDistanceMatrix(int[][] distanceMatrix, char[][] characterMatrix, int sizeRows,
                                                               int sizeColumns) {
        for (int i = 0; i < sizeRows; i++) {
            for (int j = 0; j < sizeColumns; j++) {
                if (characterMatrix[i][j] == '#' || characterMatrix[i][j] == 'o') {
                    distanceMatrix[i][j] = -1;
                }
                if (characterMatrix[i][j] == ' ' || characterMatrix[i][j] == 'r' || characterMatrix[i][j] == 'P' || characterMatrix[i][j] == 'W' || characterMatrix[i][j] == 'M' || characterMatrix[i][j] == 'G' || characterMatrix[i][j] == 'E') {
                    distanceMatrix[i][j] = 0;
                }
            }
        }
    }

    /**
     * The classic implementation of Lee's Algorithm
     *
     * @param characterMatrix - the board received from the server, parsed by Istvan
     * @param start_x         - part of the starting point
     * @param start_y         - part of the starting point
     */
    public static LeeResult solveLee(char[][] characterMatrix, int start_x, int start_y, List<Trash> trashes) {
        int matrixSizeRows = characterMatrix.length;
        int matrixSizeColumns = characterMatrix[0].length;
        int[][] distanceMatrix = new int[matrixSizeRows][matrixSizeColumns];
        Pair[][] positionsMatrix =
                new Pair[matrixSizeRows][matrixSizeColumns]; // for a certain point, it saves from where we reached this point
        convertCharacterMatrixToDistanceMatrix(distanceMatrix, characterMatrix, matrixSizeRows, matrixSizeColumns);
        distanceMatrix[start_x][start_y] = 0;
        Queue<Pair<Integer, Integer>> coveredPoints = new LinkedList<>();
        coveredPoints.add(new Pair<>(start_x, start_y));
        while (!coveredPoints.isEmpty()) {
            Pair<Integer, Integer> currentPoint = coveredPoints.poll();
            int currentX = currentPoint.getFirst();
            int currentY = currentPoint.getSecond();
            for (int i = 0; i < 4; i++) {
                int new_x = currentX + k_x[i];
                int new_y = currentY + k_y[i];
                if (distanceMatrix[new_x][new_y] == 0) {
                    distanceMatrix[new_x][new_y] = 1 + distanceMatrix[currentX][currentY];
                    positionsMatrix[new_x][new_y] = new Pair<>(currentX, currentY);
                    coveredPoints.add(new Pair<>(new_x, new_y));
                    Optional<Trash> trash = trashes.stream().filter(trash1 -> trash1.row == new_x && trash1.column == new_y).findFirst();
                    if (trash.isPresent()) {
                        continue;
                    }
                    int new_x_run = new_x + k_x[i];
                    int new_y_run = new_y + k_y[i];
                    if (characterMatrix[currentX][currentY] == 'r' && new_x_run >= 0 && new_y_run >= 0 && new_x_run < distanceMatrix.length && new_y_run < distanceMatrix.length && characterMatrix[new_x_run][new_y_run] != '#' && distanceMatrix[new_x_run][new_y_run] == 0) {
                        distanceMatrix[new_x_run][new_y_run] = 1 + distanceMatrix[currentX][currentY];
                        positionsMatrix[new_x_run][new_y_run] = new Pair<>(currentX, currentY);
                        coveredPoints.add(new Pair<>(new_x_run, new_y_run));
                    }
                    trash = trashes.stream().filter(trash1 -> trash1.row == new_x_run && trash1.column == new_y_run).findFirst();
                    if (trash.isPresent()) {
                        continue;
                    }
                    int new_x_run_battery = new_x_run + k_x[i];
                    int new_y_run_battery = new_y_run + k_y[i];
                    if (MyClient.hasBattery && new_x_run_battery >= 0 && new_y_run_battery >= 0 && new_x_run_battery < distanceMatrix.length && new_y_run_battery < distanceMatrix.length && characterMatrix[new_x_run_battery][new_y_run_battery] != '#' && distanceMatrix[new_x_run_battery][new_y_run_battery] == 0) {
                        distanceMatrix[new_x_run_battery][new_y_run_battery] = 1 + distanceMatrix[currentX][currentY];
                        positionsMatrix[new_x_run_battery][new_y_run_battery] = new Pair<>(currentX, currentY);
                        coveredPoints.add(new Pair<>(new_x_run_battery, new_y_run_battery));
                    }
                }
            }
        }
        return new LeeResult(distanceMatrix, positionsMatrix);
    }
}

package com.garmin.marcco;

import java.util.Arrays;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class MarccoMessage {

    public MessageType messageType;
    public char[][] gameBoard;
    public int col;
    public int row;
    public int round;
    public int state;
    public int maxVol;
    public Map<ObjectType, MarccoObject[]> objects;

    @Override
    public String toString() {
        return "MarccoMessage{" + "messageType=" + messageType + ", gameBoard=" + getGameBoardAsString(
                gameBoard) + ", col=" + col + ", row=" + row + ", round=" + round + ", state=" + state + ", maxVol=" + maxVol + ", objects=" + getObjectsAsString(objects) + '}';
    }

    public String getGameBoardAsString(char[][] gameBoard) {
        if (gameBoard != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            for (char[] row : gameBoard) {
                stringBuilder.append("[");
                for (char column : row) {
                    stringBuilder.append("\"");
                    stringBuilder.append(column);
                    stringBuilder.append("\"");
                    stringBuilder.append(",");
                }
                stringBuilder.append("]");
            }
            return stringBuilder.toString();
        }
        return "";
    }

    public String getObjectsAsString(Map<ObjectType, MarccoObject[]> objects) {
        if (objects != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("{");
            for (Map.Entry<ObjectType, MarccoObject[]> entry : objects.entrySet()) {
                stringBuilder.append("\"");
                stringBuilder.append(entry.getKey());
                stringBuilder.append("\"");
                stringBuilder.append("=");
                stringBuilder.append("\"");
                stringBuilder.append(Arrays.toString(entry.getValue()));
                stringBuilder.append("\"");
            }
            return stringBuilder.toString();
        }
        return "";
    }

    public void addObjectTypeToGameBoard() {
        for (Map.Entry<ObjectType, MarccoObject[]> mapEntry: objects.entrySet()) {
            ObjectType objectType = mapEntry.getKey();
            MarccoObject[] marccoObjects = mapEntry.getValue();
            for(MarccoObject object: marccoObjects) {
                this.gameBoard[object.row][object.col] = Character.toLowerCase(objectType.toString().charAt(0));
            }
        }
    }
}

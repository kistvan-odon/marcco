package com.garmin.marcco;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utils {

    public static Trash findTrashAtIndexes(int row, int collumn, List<Trash> trashes) {
        return trashes.stream()
                .filter(trash -> trash.collumn == collumn && trash.row == row)
                .findFirst()
                .orElse(null);
    }

    public static String checkPosition(Robot robot, char[][] tabla, Trash trashAtIndexes) {
        int i = robot.row;
        int j = robot.col;
        if (tabla[i][j] == 'P' || tabla[i][j] == 'W' || tabla[i][j] == 'M' || tabla[i][j] == 'G' || tabla[i][j] == 'E') {
            if (robot.canDrop(ObjectType.valueOf(String.valueOf(tabla[i][j])))) {
                return "drop";
            }
        }
        if (trashAtIndexes != null) {
            if (robot.canStore(trashAtIndexes.type, trashAtIndexes.volume)) {
                return "pick";
            }
        }
        if (tabla[i][j] == 'r') {
            return "road";
        }
        return "grass";
    }

    public static String makeAction(Robot robot, char[][] tabla, List<Trash> trashes, String botId) {
        int i = robot.row;
        int j = robot.col;
        Trash trashAtIndexes = findTrashAtIndexes(i, j, trashes);
        String decider = checkPosition(robot, tabla, trashAtIndexes);
        switch (decider) {
            case "drop":
                robot.drop(ObjectType.valueOf(String.valueOf(tabla[i][j])));
                return "{ \"action\" : \"drop\", \"bot_id\" :" + botId + " }";
            case "pick":
                robot.pickUp(trashAtIndexes.type, trashAtIndexes.volume);
                return "{ \"action\" : \"pick\", \"bot_id\" :" + botId + " }";
            case "road":
                return "{ \"move\" : \"up\", \"speed\": 2, \"bot_id\" :" + botId + " }";
            default:
                return "{ \"move\" : \"down\", \"speed\": 1, \"bot_id\" :" + botId + " }";
        }
    }

    public static List<Trash> changeMappingToList(Map<ObjectType, MarccoObject[]> objects) {
        List<Trash> trashes = new ArrayList<>();
        for (Map.Entry<ObjectType, MarccoObject[]> mapEntry : objects.entrySet()) {
            ObjectType objectType = mapEntry.getKey();
            MarccoObject[] marccoObjects = mapEntry.getValue();
            for (MarccoObject object : marccoObjects) {
                Trash trash = new Trash(objectType, object.row, object.col, object.volume);
                trashes.add(trash);
            }
        }
        return trashes;
    }

}

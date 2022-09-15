package com.garmin;

import jdk.internal.net.http.common.Pair;

import java.util.Map;

public class Utils {

    public static String checkPosition(Robot robot, char[][] tabla, Map<Pair<Integer,Integer>,Integer> trashMap) {
        int i = robot.row;
        int j = robot.col;
        if (tabla[i][j] == 'P' || tabla[i][j] == 'W' || tabla[i][j] == 'M' || tabla[i][j] == 'G' || tabla[i][j] == 'E') {
            if (robot.canDrop(String.valueOf(tabla[i][j]))) {
                return "drop";
            }
        }
        if (tabla[i][j] == 'p' || tabla[i][j] == 'w' || tabla[i][j] == 'm' || tabla[i][j] == 'g' || tabla[i][j] == 'e') {
            if (robot.canStore(String.valueOf(tabla[i][j]), trashMap.get(new Pair<>(i,j)))) {
                return "pick";
            }
        }
        if (tabla[i][j] == 'r') {
            return "road";
        }
        return "grass";
    }

    public static String makeAction(Robot robot,char[][] tabla,Map<Pair<Integer,Integer>,Integer> trashMap,String botId){
        String decider=checkPosition(robot,tabla,trashMap);
        switch (decider){
            case "drop":
                return "{ \"action\" : \"drop\", \"bot_id\" :"+botId+" }";
            default:
                return null;
        }
    }


}

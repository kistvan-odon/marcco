package com.garmin.marcco;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Utils {

    public static Trash findTrashAtIndexes(int row, int column, List<Trash> trashes) {
        return trashes.stream()
                .filter(trash -> trash.column == column && trash.row == row)
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
            if (robot.canStore(trashAtIndexes)) {
                return "pick";
            }
        }
        return "move";
    }

    public static String makeAction(Robot robot, char[][] tabla, List<Trash> trashes, String botId,List<Dumpster> dumpsterList) {
        int i = robot.row;
        int j = robot.col;
        Trash trashAtIndexes = findTrashAtIndexes(i, j, trashes);
        String decider = checkPosition(robot, tabla, trashAtIndexes);
        switch (decider) {
            case "drop":
                robot.drop(ObjectType.valueOf(String.valueOf(tabla[i][j])));
                return "{ \"action\" : \"drop\", \"bot_id\" :\"" + botId + "\" }";
            case "pick":
                robot.pickUp(trashAtIndexes.type, trashAtIndexes.volume);
                return "{ \"action\" : \"pick\", \"bot_id\" :\"" + botId + "\" }";
            default:
                return computeResponse(robot,tabla,trashes,dumpsterList,botId);
//            case "road":
//                return "{ \"move\" : \"up\", \"speed\": 2, \"bot_id\" :\"" + botId + "\" }";
//            default:
//                return "{ \"move\" : \"up\", \"speed\": 1, \"bot_id\" :\"" + botId + "\" }";
        }
    }

    public static List<Trash> changeMappingToList(Map<ObjectType, MarccoObject[]> objects) {
        List<Trash> trashes = new ArrayList<>();
        for (Map.Entry<ObjectType, MarccoObject[]> mapEntry : objects.entrySet()) {
            ObjectType objectType = mapEntry.getKey();
            if(objectType==ObjectType.R||objectType==ObjectType.B){
                continue;
            }
            MarccoObject[] marccoObjects = mapEntry.getValue();
            for (MarccoObject object : marccoObjects) {
                Trash trash = new Trash(objectType, object.row, object.col, object.volume);
                trashes.add(trash);
            }
        }
        return trashes;
    }

    private static String computeResponse(Robot robot,char[][] matrix,List<Trash> trashes,List<Dumpster> dumpsterList,String botId){
        String direction=getDirection(robot, matrix, trashes, dumpsterList);
        if(direction==null){
            return null;
        }
        String directionSide=direction.split(":")[0];
        int speed=Integer.parseInt(direction.split(":")[1]);
        return "{ \"move\" : \""+directionSide+"\", \"speed\":"+speed+", \"bot_id\" :\"" + botId + "\" }";
    }

    private static String getDirection(Robot robot,char[][] matrix,List<Trash> trashes,List<Dumpster> dumpsterList){
        int i = robot.row;
        int j = robot.col;
        LeeResult result=LeeAlgorithmSolver.solveLee(matrix,i,j);
        computeCoeffs(trashes,result);
        trashes.sort((o1, o2) -> Double.compare(o2.coefficient, o1.coefficient));
        Trash bestTrash=null;
        for (Trash trash : trashes) {
            if (robot.canStore(trash)) {
                bestTrash = trash;
                break;
            }
        }
        if(bestTrash!=null){
            return getNextMove(new Pair<>(bestTrash.row, bestTrash.column), robot, result.positionsMatrix);
        }
        List<Container> containers = robot.containerList;
        containers.sort(new Comparator<Container>() {
            @Override
            public int compare(Container o1, Container o2) {
                return Integer.compare(o2.getFilled(),o1.filled);
            }
        });
        Container bestContainer=containers.get(0);
        ObjectType containerType=bestContainer.getType();
        Dumpster dumpster=dumpsterList.stream()
                .filter(dumpster1 -> dumpster1.objectType.equals(containerType))
                .findFirst()
                .orElse(null);
        if(dumpster==null){
            return null;
        }
        return getNextMove(new Pair<>(dumpster.row, dumpster.column), robot, result.positionsMatrix);
    }
    private static String getNextMove(Pair<Integer,Integer> currentPoint, Robot robot, Pair[][] positionsDistances) {
        if(positionsDistances[currentPoint.getFirst()][currentPoint.getSecond()].equals(new Pair<>(robot.row, robot.col))) {
            if(currentPoint.getFirst() == robot.row) {
                int distance = Math.abs(robot.col - currentPoint.getSecond());
                if(currentPoint.getSecond() < robot.col) {
                    return "left:" + distance;
                }
                else {
                    return "right:"+distance;
                }
            }
            else {
                int distance = Math.abs(robot.row - currentPoint.getFirst());
                if(currentPoint.getFirst() < robot.row) {
                    return "up:"+distance;
                }
                else {
                    return "down:"+distance;
                }
            }
        }
        else {
            return getNextMove(positionsDistances[currentPoint.getFirst()][currentPoint.getSecond()], robot, positionsDistances);
        }
    }

    public static List<Dumpster> getAllDumpstersFromMatrix(char[][]matrix){
        List<Dumpster> result=new ArrayList<>();
        for(int i=0;i<matrix.length;i++){
            for(int j=0;j<matrix[0].length;j++){
                if(matrix[i][j] == 'P' || matrix[i][j] == 'W' || matrix[i][j] == 'M' || matrix[i][j] == 'G' || matrix[i][j] == 'E'){
                    Dumpster dumpster=new Dumpster();
                    dumpster.objectType=ObjectType.valueOf(String.valueOf(matrix[i][j]));
                    dumpster.row=i;
                    dumpster.column=j;
                    result.add(dumpster);

                }
            }
        }
        return result;
    }

    private static void computeCoeffs(List<Trash> trashes,LeeResult result){
        trashes.stream().
                forEach(trash -> {
                    trash.coefficient=(double)(trash.volume)/result.distanceMatrix[trash.row][trash.column];
        });
    }
}

package com.garmin.marcco;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.garmin.marcco.MyClient.maxVol;

public class Robot {
    public int row;
    public int col;
    List<Container> containerList;

    public Robot() {
        Container c1 = new Container();
        Container c2 = new Container();
        Container c3 = new Container();
        List<Container> containerList = new ArrayList<>(Arrays.asList(c1, c2, c3));
    }

    public void updateLocation(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public boolean canStore(String type, int qty) {

        for (int i = 0; i < 3; i++) {
            if (containerList.get(i).getType() == null) {
                return true;
            }
            if (containerList.get(i).getType() == type && containerList.get(i).getFilled() + qty <= maxVol) {
                return true;
            }
        }

        return false;

    }

    public void pickUp(String type, int qty) {
//@TODO DACA CRAPA E DE AICI
        boolean ok = false;
        Container nullContainer = null;
        for (int i = 0; i < 3; i++) {
            Container c = containerList.get(i);
            if (c.getType() == null) {
                if (ok == true) {
                    c.setType(type);
                    c.setFilled(qty);
                    return;
                } else {
                    nullContainer = c;
                }
            }
            if (c.getType() == type) {
                if (c.getFilled() + qty <= maxVol) {
                    c.setFilled(c.getFilled() + qty);
                    return;
                }
                int remaining = maxVol - c.getFilled();
                c.setFilled(maxVol);
                qty -= remaining;
                ok = true;
                if (nullContainer != null) {
                    nullContainer.setType(type);
                    nullContainer.setFilled(qty);
                    return;
                }
            }
        }
        nullContainer.setType(type);
        nullContainer.setFilled(qty);

    }

    public void drop(String type){
        for(int i=0;i<3;i++){
            if(containerList.get(i).getType()==type){
                containerList.get(i).setType(null);
                containerList.get(i).setFilled(0);
            }
        }
    }

    public boolean canDrop(String type){
        for(int i=0;i<3;i++) {
            if (containerList.get(i).getType() == type) {
                return true;
            }
        }
        return false;
    }
}

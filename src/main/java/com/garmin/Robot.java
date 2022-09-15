package com.garmin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.garmin.MyClient.MAX_VOL;

public class Robot {
    int row;
    int col;
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
            if (containerList.get(i).getType() == type && containerList.get(i).getFilled() + qty <= MAX_VOL) {
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
                if (c.getFilled() + qty <= MAX_VOL) {
                    c.setFilled(c.getFilled() + qty);
                    return;
                }
                int remaning = MAX_VOL - c.getFilled();
                c.setFilled(MAX_VOL);
                qty -= remaning;
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
}

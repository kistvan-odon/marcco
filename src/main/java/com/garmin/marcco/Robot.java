package com.garmin.marcco;

import com.garmin.marcco.model.Container;
import com.garmin.marcco.model.ObjectType;
import com.garmin.marcco.model.Trash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.garmin.marcco.MyClient.maxVol;

public class Robot {
    public int row;
    public int col;
    List<Container> containerList;
    boolean hasBattery = false;

    public Robot() {
        Container c1 = new Container();
        Container c2 = new Container();
        Container c3 = new Container();
        containerList = new ArrayList<>(Arrays.asList(c1, c2, c3));
    }

    public void updateLocation(int row, int col) {
        this.col = col;
        this.row = row;
    }

    public boolean canStore(Trash trash) {

        for (int i = 0; i < 3; i++) {
            if (containerList.get(i).getType() == null) {
                return true;
            }
            if (containerList.get(i).getType() == trash.type && containerList.get(i).getVolume() + trash.volume <= maxVol) {
                return true;
            }
        }

        return false;

    }

    public void pickUp(ObjectType type, int qty) {
//@TODO DACA CRAPA E DE AICI
        boolean ok = false;
        if (ObjectType.B == type) {
            hasBattery = true;
        }
        Container nullContainer = null;
        for (int i = 0; i < 3; i++) {
            Container c = containerList.get(i);
            if (c.getType() == null) {
                if (ok) {
                    c.setType(type);
                    c.setVolume(qty);
                    return;
                } else {
                    nullContainer = c;
                }
            }
            if (c.getType() == type) {
                if (c.getVolume() + qty <= maxVol) {
                    c.setVolume(c.getVolume() + qty);
                    return;
                }
                int remaining = maxVol - c.getVolume();
                c.setVolume(maxVol);
                qty -= remaining;
                ok = true;
                if (nullContainer != null) {
                    nullContainer.setType(type);
                    nullContainer.setVolume(qty);
                    return;
                }
            }
        }
        if (nullContainer != null) {
            nullContainer.setType(type);
            nullContainer.setVolume(qty);
        }

    }

    public void drop(ObjectType type){
        for(int i=0;i<3;i++){
            if(containerList.get(i).getType()==type){
                containerList.get(i).setType(null);
                containerList.get(i).setVolume(0);
            }
        }
    }

    public boolean canDrop(ObjectType type){
        for(int i=0;i<3;i++) {
            if (containerList.get(i).getType() == type) {
                return true;
            }
        }
        return false;
    }
}

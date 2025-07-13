package core;

import org.checkerframework.checker.units.qual.A;
import tileengine.Tileset;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Room {
    int xCoord;
    int yCoord;
    int height;
    int width;
    Map<String, ArrayList<Integer>> wallCoordinates = new TreeMap<>();

    public Room(int xCoord, int yCoord, int width, int height) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.width = width;
        this.height = height;
        wallCoordinates.put("Top", new ArrayList<>());
        wallCoordinates.put("Bottom", new ArrayList<>());
        wallCoordinates.put("Left", new ArrayList<>());
        wallCoordinates.put("Right", new ArrayList<>());


        // puts coordinates of bottom and top side into map
        for (int x = xCoord; x < xCoord + width; x++) {
            wallCoordinates.get("Bottom").add(x);
            wallCoordinates.get("Top").add(x);

        }

        //puts coordinates of left and right side into map
        for (int y = yCoord; y < yCoord + height; y++) {
            wallCoordinates.get("Left").add(y);
            wallCoordinates.get("Right").add(y);
        }
    }

    public Map<String, ArrayList<Integer>> getRoomWallCoordinates() {
        return wallCoordinates;
    }
}
package model;

import java.util.List;

/**
 *
 */
public class Room {
    private double square;
    private double squareOfFurniture;
    private int illuminate;
    private int countOfWindows;
    private List<Lamp> lamps;
    private List<Furniture> furnitureList;

    public Room(double square, int countOfWindows) {
        this.square = square;
        this.countOfWindows = countOfWindows;
    }

    public void add(Lamp lamp){
        lamps.add(lamp);
    }

    public void add(Furniture furniture){
        furnitureList.add(furniture);
    }
}

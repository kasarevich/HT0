package model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Building {
    private String name;
    private Map<String, Room> rooms;

    public void addRoom(String name, double square, int countOfWindows){
        rooms.put(name, new Room(square, countOfWindows));
    }

    public Room getRoom(String name){
        return rooms.get(name);
    }

    public String describe(){

        return null;
    }
}

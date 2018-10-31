package model;

import exceptions.IlluminanceTooLitleException;
import exceptions.IlluminanceTooMuchException;
import exceptions.SpaceUsageTooMuchException;
import exceptions.ValidateException;
import utils.Validator;

import java.util.HashMap;
import java.util.Map;

public class Building {

    private String name;
    private Map<String, Room> rooms;
    private boolean isValide;

    public Building(String name) {
        this.name = name;
        rooms = new HashMap<>();
        isValide = false;
    }
    public Building(String name, Map<? extends String, ? extends Room> rooms) {
        this.name = name;
        this.rooms = new HashMap<>(rooms);
        isValide = false;
    }
    public String getName(){
        return name;
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public Room getRoom(String name){
        return rooms.get(name);
    }
    //полная свобода добавления и работы со зданием, все проверки в validate
    public void addRoom(String name, double square, int countOfWindows){
        rooms.put(name, new Room(square, countOfWindows));
    }
    public void removeRoom(String name){
        rooms.remove(name);
    }
    public void addAllRooms(HashMap<? extends String,? extends Room> newRooms){
        rooms.putAll(newRooms);
    }

    public boolean validateBuilding() throws IlluminanceTooLitleException, IlluminanceTooMuchException, SpaceUsageTooMuchException, ValidateException {
        return Validator.validate(this);
    }

    public String describe(){
        StringBuilder sb = new StringBuilder();
        sb.append("Здание " + name + "\n");
        for (Map.Entry entry : rooms.entrySet()){
            sb.append("\t Комната " + entry.getKey() + "\n");
            sb.append(entry.getValue().toString());
        }
        sb.append(isValide? "\n Здание проверено. Ошибок планировки нет." : "\n Здание не проверено. Могут быть ошибки планировки.");
        return sb.toString();
    }
}

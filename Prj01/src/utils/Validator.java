package utils;

import exceptions.IlluminanceTooLitleException;
import exceptions.IlluminanceTooMuchException;
import exceptions.SpaceUsageTooMuchException;
import exceptions.ValidateException;
import model.Building;
import model.Room;

import java.util.Map;

public class Validator {
    private static final int MAX_ILLUMINANCE = 4000;
    private static final int MIN_ILLUMINANCE = 300;
    private static final double PERCENT_SQUARE = 0.7;
    public static boolean validate(Building building) throws IlluminanceTooLitleException, IlluminanceTooMuchException, SpaceUsageTooMuchException, ValidateException {
        boolean isValide = false;
        StringBuilder errorLog = new StringBuilder();
        if(building.getName() == null){
            errorLog.append("Name of building is empty!");
        } else {
            isValide = true;
        }
        for(Map.Entry entry : building.getRooms().entrySet()){
            String currentName = (String) entry.getKey();
            Room currentRoom = (Room) entry.getValue();

            if(currentName == null){
                errorLog.append("Name of room is empty!");
                isValide = false;
            }
            if(currentRoom == null){
                errorLog.append("Room " + currentName + "is empty!");
                isValide = false;
            }
            if(currentRoom.getSquare() <= 0){
                errorLog.append("Square of room " + currentName + " is lower or equals 0");
                isValide = false;
            }
            if (currentRoom.getIlluminance() < MIN_ILLUMINANCE){
                isValide = false;
                throw new IlluminanceTooLitleException("Exception in room \"" + currentName + "\". Illuminance of room equals "
                        + String.valueOf(currentRoom.getIlluminance()) + " lx\n use min 300");
            }
            if (currentRoom.getIlluminance() > MAX_ILLUMINANCE){
                isValide = false;
                throw new IlluminanceTooMuchException("Exception in room \"" + currentName + "\". Illuminance of room equals "
                        + String.valueOf(currentRoom.getIlluminance()) + " lx\n use max 4000");
            }
            if(currentRoom.getSquareOfFurniture()[1] > currentRoom.getSquare()*PERCENT_SQUARE){
                isValide = false;
                throw new SpaceUsageTooMuchException("Exception in room \"" + currentName + "\". Square of furniture equals "
                        + String.valueOf(currentRoom.getSquareOfFurniture()[1]) + " m^2 \n use max 70% of square");
            }
            if (!errorLog.toString().isEmpty()){
                throw new ValidateException(errorLog.toString());
            }
        }
        return isValide;

    }
}

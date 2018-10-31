import exceptions.IlluminanceTooLitleException;
import exceptions.IlluminanceTooMuchException;
import exceptions.SpaceUsageTooMuchException;
import exceptions.ValidateException;
import model.Building;
import model.Furniture;
import model.Lamp;

public class App {
    /**
     * Example of working with the library
     * @param args
     */
    public static void main(String[] args) {
        Building building = new Building("1");
        building.addRoom("Комната 1", 100, 3);
        building.addRoom("Спальня", 5, 2);
        building.getRoom("Комната 1").add(new Lamp(500));
        building.getRoom("Комната 1").add(new Lamp(250));
        building.getRoom("Комната 1").add(new Furniture("Стол", 2));
        building.getRoom("Комната 1").add(new Furniture("Кровать", 2,3));

        System.out.println(building.describe());

        try {
           if(building.validateBuilding()){
               System.out.println("Здание проверено, ошибок планировки нет");
           }
        } catch (IlluminanceTooLitleException | IlluminanceTooMuchException | SpaceUsageTooMuchException | ValidateException e) {
            System.out.println(e.getMessage());
        }

    }
}

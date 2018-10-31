package model;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private static final int ILLUMINANCE_OF_WINDOW = 700;

    private double square;
    private int countOfWindows;
    private List<Lamp> lamps;
    private List<Furniture> furnitureList;

    public Room(double square, int countOfWindows) {
        this.square = square;
        this.countOfWindows = countOfWindows;
        lamps = new ArrayList<>();
        furnitureList = new ArrayList<>();
    }

    public int getIlluminance(){
        int illuminance = 0;
        for(Lamp l: lamps){
            illuminance += l.getIlluminance();
        }
        illuminance += (countOfWindows*ILLUMINANCE_OF_WINDOW);
        return illuminance;
    }

    public double[] getSquareOfFurniture(){
        double minSquare = 0;
        double maxSquare = 0;

        for(Furniture f: furnitureList){
            minSquare += f.getMinSquare();
            maxSquare += f.getMaxSquare();
        }
        return new double[]{minSquare, maxSquare};
    }


    public void add(Lamp lamp){
        lamps.add(lamp);
    }
    public void add(Furniture furniture){
        furnitureList.add(furniture);
    }


    public double getSquare() {
        return square;
    }
    public int getCountOfWindows() {
        return countOfWindows;
    }
    public List<Lamp> getLamps() {
        return lamps;
    }
    public List<Furniture> getFurnitureList() {
        return furnitureList;
    }


    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("\t\tОсвещенность = " + getIlluminance() + " (" + "Количество окон = " + countOfWindows + " по " + ILLUMINANCE_OF_WINDOW + " лк");
        if(lamps.size() == 0){
            sb.append(", ламп нет. )\n");
        }else {
            sb.append(", лампочки : ");
        for (Lamp l: lamps){
                sb.append(l.getIlluminance() + " лк; ");
            }
            sb.append(")\n");
        }
        sb.append("\t\tПлощадь = " + square + " м^2" );
        if(furnitureList.size() == 0){
            sb.append("(свободно 100%)\n\t\tМебели нет\n");
        }else {
            sb.append("(занято : ");
            double minSquare = 0;
            double maxSquare = 0;
            StringBuilder furniture = new StringBuilder(); // чтобы за один проход по циклу взять инфу
            furniture.append("\t\tМебель:\n");
            for (Furniture f: furnitureList){
                minSquare += f.getMinSquare();
                maxSquare += f.getMaxSquare();
                furniture.append("\t\t\t" + f.getName() + " (площадь " + (f.getMaxSquare()==f.getMinSquare() ? f.getMaxSquare() + " м^2)\n" : "от " + f.getMinSquare() + " м^2 " + f.getMaxSquare() + " м^2)\n"));
            }
                sb.append((maxSquare == minSquare) ? maxSquare + " м^2" : minSquare + " - " + maxSquare + " м^2");
            double free = square - maxSquare;
            double freePercent = free*100/square;
            sb.append(" гарантировано свободно " + free + " м^2 или " + freePercent + "% площади)\n");
            sb.append(furniture);
        }
        return sb.toString();
    }
}

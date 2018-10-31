package model;

public class Furniture {
    private String name;
    private double minSquare;
    private double maxSquare;
    
    public Furniture(String name, double minSquare) {
        this.name = name;
        this.minSquare = minSquare;
        maxSquare = minSquare;
    }

    public Furniture(String name, double minSquare, double maxSquare) {
        this.name = name;
        this.minSquare = minSquare;
        this.maxSquare = maxSquare;
    }

    public String getName() {
        return name;
    }
    public double getMinSquare() {
        return minSquare;
    }
    public double getMaxSquare() {
        return maxSquare;
    }

}

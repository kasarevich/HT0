package model;

public class Furniture {
    private String name;
    private int minSquare;
    private int maxSquare;

    public Furniture(String name, int maxSquare) {
        this.name = name;
        this.maxSquare = maxSquare;
    }

    public Furniture(String name, int minSquare, int maxSquare) {
        this.name = name;
        this.minSquare = minSquare;
        this.maxSquare = maxSquare;
    }
}

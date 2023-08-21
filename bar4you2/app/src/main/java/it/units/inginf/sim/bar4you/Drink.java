package it.units.inginf.sim.bar4you;

public class Drink {


    private final DrinkType drinkType;
    private final String name;
    private final String description;
    private final double price;

    public Drink(DrinkType drinkType, String name, String description, double price) {
        this.drinkType = drinkType;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public DrinkType getDrinkType() {
        return this.drinkType;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public double getPrice() {
        return this.price;
    }

}

package it.units.inginf.sim.bar4you;

public class OrderElement {

    private final Drink drink;
    private final String personName;

    public OrderElement(Drink drink, String personName) {
        this.drink = drink;
        this.personName = personName;
    }

    public Drink getDrink() {
        return this.drink;
    }

    public String getPersonName() {
        return this.personName;
    }
}

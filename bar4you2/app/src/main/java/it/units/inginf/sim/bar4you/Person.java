package it.units.inginf.sim.bar4you;

import java.util.List;
public class Person {
    private final int personId;
    private final String name;
    private double bill;

    public Person(int personId, String name) {
        this.personId = personId;
        this.name = name;
        this.bill= 0.00;
    }

    public int getPersonId() {
        return this.personId;
    }

    public String getName() {
        return this.name;
    }

    public double getBill() { return this.bill;}

    public void incrementBill(double increment) {
        this.bill += increment;
    }

    public boolean nameIsUnique(List<Person> personsList) {
        for(Person person: personsList) {
            if(this.name.equals(person.getName())) {
                return false;
            }
        }
        return true;
    }

}

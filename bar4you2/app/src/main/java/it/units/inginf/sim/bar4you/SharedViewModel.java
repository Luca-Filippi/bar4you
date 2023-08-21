package it.units.inginf.sim.bar4you;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<Integer> tableNumberMutableLiveData = new MutableLiveData<>();
    private final List<MutableLiveData<Person>> personsMutableLiveData = new ArrayList<>();
    private final List<MutableLiveData<OrderElement>> orderMutableLiveData = new ArrayList<>();
    private final MutableLiveData<Integer> numberOfOrdersMutableLiveData = new MutableLiveData<>();

    public SharedViewModel() {
        this.tableNumberMutableLiveData.setValue(0);
        this.numberOfOrdersMutableLiveData.setValue(0);
    }
    public void setTableNumberMutableLiveData(int tableNumber) {
        this.tableNumberMutableLiveData.setValue(tableNumber);
    }

    public void addPersonMutableLiveData(Person person) {
        MutableLiveData<Person> personMutableLiveData = new MutableLiveData<>();
        personMutableLiveData.setValue(person);
        this.personsMutableLiveData.add(personMutableLiveData);
    }

    public void addElementInOrderMutableLiveData(OrderElement orderElement) {
        MutableLiveData<OrderElement> orderElementMutableLiveData = new MutableLiveData<>();
        orderElementMutableLiveData.setValue(orderElement);
        this.orderMutableLiveData.add(orderElementMutableLiveData);
    }

    public void increaseNumberOfOrdersMutableLiveData() {
        if(this.numberOfOrdersMutableLiveData.getValue() != null) {
            int newValue = this.numberOfOrdersMutableLiveData.getValue() + 1;
            this.numberOfOrdersMutableLiveData.setValue(newValue);
        }
    }

    public void setNumberOfOrdersMutableLiveData(int numberOfOrders) {
        this.numberOfOrdersMutableLiveData.setValue(numberOfOrders);
    }

    public MutableLiveData<Integer> getTableNumberMutableLiveData() {
        return this.tableNumberMutableLiveData;
    }

    public List<MutableLiveData<Person>> getPersonsMutableLiveData() {
        return this.personsMutableLiveData;
    }

    public List<MutableLiveData<OrderElement>> getOrderMutableLiveData() {
        return this.orderMutableLiveData;
    }

    public MutableLiveData<Integer> getNumberOfOrdersMutableLiveData() {
        return this.numberOfOrdersMutableLiveData;
    }

    public void resetOrderMutableLiveData() {
        this.orderMutableLiveData.clear();
    }

    public Person getPersonByName(String name) {
        for(MutableLiveData<Person> personMutableLiveData : personsMutableLiveData) {
            if(personMutableLiveData.getValue() != null && personMutableLiveData.getValue().getName().equals(name)) {
                return personMutableLiveData.getValue();
            }
        }
        return null;
    }

    public void removeOrderElementMutableLiveData(MutableLiveData<OrderElement> orderElementMutableLiveData) {
        this.orderMutableLiveData.remove(orderElementMutableLiveData);
    }

    public MutableLiveData<Person> getPersonMutableLiveDataByName(String name) {
        for(MutableLiveData<Person> personMutableLiveData : this.personsMutableLiveData) {
            if(personMutableLiveData.getValue() != null && personMutableLiveData.getValue().getName().equals(name)) {
                return personMutableLiveData;
            }
        }
        return null;
    }

    public void resetSharedViewModel() {
        this.tableNumberMutableLiveData.setValue(0);
        this.personsMutableLiveData.clear();
        this.orderMutableLiveData.clear();
        this.numberOfOrdersMutableLiveData.setValue(0);
    }
}

package main.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;
import java.util.function.Function;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public abstract class Consumable<Data, Unit extends Number & Comparable<Unit>> {

    protected Data data;
    protected Unit minimum;
    protected ObjectProperty<Unit> amount;

    public Consumable(Data data, Unit amount) {
        this.data = data;
        this.amount = new SimpleObjectProperty<>(amount);
    }

    public Consumable(String dataName, Unit amount, List<Data> masterList, Function<Data,String> nameGetter) {
        this.data = getData(dataName, masterList, nameGetter);
        this.amount = new SimpleObjectProperty<>(amount);
    }

    public abstract void update(Unit modifier);

    public boolean isEmpty() {
        return amount.get().compareTo(minimum) < 0;
    }

    public Data getData() {
        return data;
    }

    public Unit getAmount() {
        return amount.get();
    }

    public ObjectProperty<Unit> amountProperty() {
        return amount;
    }

    public String toString() {
        return String.format("%f x %s", amount.get().doubleValue(), data);
    }

    private Data getData(String name, List<Data> masterList, Function<Data,String> nameGetter) throws IllegalArgumentException {
        return masterList.stream()
                .filter(d -> nameGetter.apply(d).equals(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("no consumable named " + name));
    }

}

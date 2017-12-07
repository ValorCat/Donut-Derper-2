package main.model;

import javafx.beans.property.*;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * @since 12/6/2017
 */
public class DonutType {

    private static ListProperty<DonutType> types = new SimpleListProperty<>(observableArrayList(
            new DonutType("Plain", 1.2),
            new DonutType("Chocolate", 1.5)
    ));

    public static final DonutType PLAIN = types.get(0);
    private static int nextId = 0;

    private StringProperty name;
    private DoubleProperty cost;
    private int hashcode;

    public DonutType(String name, double cost) {
        this.name = new SimpleStringProperty(name);
        this.cost = new SimpleDoubleProperty(cost);
        hashcode = nextId++;
    }

    public String getName() {
        return name.get();
    }

    public final StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public double getCost() {
        return cost.get();
    }

    public final DoubleProperty costProperty() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost.set(cost);
    }

    public String toString() {
        return getName();
    }

    public int hashCode() {
        return hashcode;
    }

    public static ListProperty<DonutType> typesProperty() {
        return types;
    }

}

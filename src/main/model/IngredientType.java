package main.model;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * @since 12/6/2017
 */
public class IngredientType {

    private static ListProperty<IngredientType> types = new SimpleListProperty<>(observableArrayList(
            new IngredientType("Flour", .5, "tbsp."),
            new IngredientType("Sugar", .3, "teasp.")
    ));

    public static final IngredientType FLOUR = types.get(0);
    public static final IngredientType SUGAR = types.get(1);
    private static int nextId = 0;

    private StringProperty name;
    private DoubleProperty baseValue;
    private StringProperty unit;
    private int hashcode;

    public IngredientType(String name, double baseValue, String unit) {
        this.name = new SimpleStringProperty(name);
        this.baseValue = new SimpleDoubleProperty(baseValue);
        this.unit = new SimpleStringProperty(unit);
        hashcode = nextId++;
    }

    public String getName() {
        return name.get();
    }

    public final StringProperty nameProperty() {
        return name;
    }

    public double getBaseValue() {
        return baseValue.get();
    }

    public final DoubleProperty baseValueProperty() {
        return baseValue;
    }

    public String getUnit() {
        return unit.get();
    }

    public String toString() {
        return getName();
    }

    public int hashCode() {
        return hashcode;
    }

    public static ObservableList<IngredientType> getTypes() {
        return types.get();
    }

    public static ListProperty<IngredientType> typesProperty() {
        return types;
    }

}

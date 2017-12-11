package main.model.ingredient;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * @since 12/6/2017
 */
public class IngredientType {

    private static final SortedMap<Integer, String> FLUID_UNITS = new TreeMap<>(Map.of(
            0, "tsp.",
            3, "tbsp.",
            6, "fl. oz.",
            48, "cups",
            96, "pints",
            192, "qt.",
            768, "gal."
    ));
    private static final ListProperty<IngredientType> types = new SimpleListProperty<>(observableArrayList(
            new IngredientType("All-Purpose Flour", 0, 144, new TreeMap<>(Map.of(
                    0, "tsp.",
                    3, "tbsp.",
                    48, "cups",
                    181, "lb."))),
            new IngredientType("Butter", 0, 96, new TreeMap<>(Map.of(
                    0, "tsp.",
                    3, "tbsp.",
                    24, "sticks",
                    96, "lb."))),
            new IngredientType("Eggs", 0, Integer.MAX_VALUE, new TreeMap<>(Map.of(
                    0, "1/12 egg",
                    12, "eggs",
                    144, "cartons"))),
            new IngredientType("Sugar", 0, 48, new TreeMap<>(Map.of(
                    0, "tsp.",
                    3, "tbsp.",
                    48, "cups",
                    109, "lb."))),
            new IngredientType("Whole Milk", 0, 144, FLUID_UNITS)
    ));

    private static int nextId = 0;

    private StringProperty name;
    private DoubleProperty baseValue;
    private SortedMap<Integer,String> units;
    private int decimalThreshold;
    private final int hashcode;

    public IngredientType(String name, double baseValue, int decimalThreshold, SortedMap<Integer,String> units) {
        this.name = new SimpleStringProperty(name);
        this.baseValue = new SimpleDoubleProperty(baseValue);
        this.units = units;
        this.decimalThreshold = decimalThreshold;
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

    public SortedMap<Integer, String> getUnits() {
        return units;
    }

    public int getDecimalThreshold() {
        return decimalThreshold;
    }

    public boolean isOfType(IngredientBatch ingredient) {
        return ingredient.getType() == this;
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

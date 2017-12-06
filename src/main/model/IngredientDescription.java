package main.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public class IngredientDescription {

    private StringProperty name;
    private DoubleProperty baseCost;
    private StringProperty unit;

    public IngredientDescription(String name, double baseCost, String unit) {
        this.name = new SimpleStringProperty(name);
        this.baseCost = new SimpleDoubleProperty(baseCost);
        this.unit = new SimpleStringProperty(unit);
    }

    public double getCost(int amount) {
        return baseCost.get() * amount;
    }

    public String getName() {
        return name.get();
    }

    public final StringProperty nameProperty() {
        return name;
    }

    public double getBaseCost() {
        return baseCost.get();
    }

    public final DoubleProperty baseCostProperty() {
        return baseCost;
    }

    public String getUnit() {
        return unit.get();
    }

    public final StringProperty unitProperty() {
        return unit;
    }

    public String toString() {
        return getName();
    }

}

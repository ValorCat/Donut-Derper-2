package main.model;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

import static javafx.collections.FXCollections.*;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public class DonutTypeDescription {

    private StringProperty name;
    private DoubleProperty cost;
    private ListProperty<Ingredient> recipe;

    public DonutTypeDescription(String name) {
        this(name, 0.0);
    }

    public DonutTypeDescription(String name, double cost, Ingredient... recipe) {
        this.name = new SimpleStringProperty(name);
        this.cost = new SimpleDoubleProperty(cost);
        this.recipe = new SimpleListProperty<>(observableArrayList(recipe));
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public double getCost() {
        return cost.get();
    }

    public DoubleProperty costProperty() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost.set(cost);
    }

    public ObservableList<Ingredient> getRecipe() {
        return recipe.get();
    }

    public ListProperty<Ingredient> recipeProperty() {
        return recipe;
    }

    public String toString() {
        return name.get();
    }

}

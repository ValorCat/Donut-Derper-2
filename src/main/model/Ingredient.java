package main.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public class Ingredient extends Consumable<IngredientDescription, Double> {

    public static final ListProperty<IngredientDescription> ALL_INGREDIENTS = new SimpleListProperty<>(observableArrayList(
            new IngredientDescription("Flour", .5, "lb."),
            new IngredientDescription("Sugar", .3, "lb.")
    ));

    {
        this.minimum = 0.01;
    }

    public Ingredient(IngredientDescription descr, double amount) {
        super(descr, amount);
    }

    public Ingredient(String dataName, double amount) {
        super(dataName, amount, ALL_INGREDIENTS.get(), IngredientDescription::getName);
    }

    public Ingredient(Ingredient other) {
        super(other.data, other.getAmount());
    }

    public void update(Double modifier) {
        setAmount(getAmount() + modifier);
    }

}

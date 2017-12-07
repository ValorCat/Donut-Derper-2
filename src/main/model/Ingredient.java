package main.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;

/**
 * @since 12/6/2017
 */
public class Ingredient {

    private final IngredientType type;
    private final IntegerProperty amount;

    public Ingredient(IngredientType type, int amount) {
        this.type = type;
        this.amount = new SimpleIntegerProperty(amount);
    }

    public final StringProperty nameProperty() {
        return type.nameProperty();
    }

    public int getAmount() {
        return amount.get();
    }

    public final IntegerProperty amountProperty() {
        return amount;
    }

}

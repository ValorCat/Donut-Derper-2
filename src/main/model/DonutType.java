package main.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public class DonutType extends Consumable<DonutTypeDescription, Integer> {

    public static final ListProperty<DonutTypeDescription> DONUT_TYPES = new SimpleListProperty<>(observableArrayList(
            new DonutTypeDescription("Plain", 1.5,
                    new Ingredient("Flour", 0.085),
                    new Ingredient("Sugar", 0.01)),
            new DonutTypeDescription("Chocolate", 1.5,
                    new Ingredient("Flour", 0.085),
                    new Ingredient("Sugar", 0.01))
    ));

    public static final DonutTypeDescription PLAIN = DONUT_TYPES.get(0);

    {
        this.minimum = 1;
    }

    public DonutType(DonutTypeDescription descr, int amount) {
        super(descr, amount);
    }

    public DonutType(String dataName, int amount) {
        super(dataName, amount, DONUT_TYPES.get(), DonutTypeDescription::getName);
    }

    public DonutType(DonutType other) {
        super(other.data, other.amount.get());
    }

    public void update(Integer modifier) {
        amount.set(amount.get() + modifier);
    }

    public String toString() {
        return data.getName();
    }

}

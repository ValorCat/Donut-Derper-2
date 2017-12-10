package main.model.ingredient;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @since 12/6/2017
 */
public class IngredientBatch {

    protected final IngredientType type;
    protected final IntegerProperty amount;

    public IngredientBatch(IngredientType type, int amount) {
        amount = Math.max(0, amount);
        this.type = type;
        this.amount = new SimpleIntegerProperty(amount);
    }

    public IngredientBatch(IngredientBatch other, int amountLess) {
        this(other.type, other.getAmount() - amountLess);
    }

    public IngredientType getType() {
        return type;
    }

    public final StringProperty nameProperty() {
        return type.nameProperty();
    }

    public int getAmount() {
        return amount.get();
    }

    public StringProperty amountProperty() {
        // return a StringProperty rather than a StringExpression because the TableView's
        // PropertyValueFactory only works with ReadOnlyProperty and not any ObservableValue
        StringProperty property = new SimpleStringProperty();
        property.bind(Bindings.format("%d %s", amount, type.getUnit()));
        return property;
    }

    public boolean isSameTypeAs(IngredientBatch other) {
        return type == other.type;
    }

    public boolean hasLessThan(IngredientBatch other) {
        return getAmount() <= other.getAmount();
    }

    public static IngredientBatch of(String typeName, int amount) {
        for (IngredientType type : IngredientType.getTypes()) {
            if (type.getName().equals(typeName)) {
                return new IngredientBatch(type, amount);
            }
        }
        throw new IllegalArgumentException("No ingredient named " + typeName);
    }

}

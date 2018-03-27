package main.model.ingredient;

import javafx.beans.property.*;

import static main.ui.UILinker.getIngredientAmount;

/**
 * @since 12/6/2017
 */
public class IngredientBatch {

    protected ObjectProperty<IngredientType> type;
    protected IntegerProperty amount;
    protected DoubleProperty quality;
    protected IngredientSupplier brand;

    public IngredientBatch(IngredientType type, int amount) {
        this(type, amount, 1, IngredientSupplier.LOCAl);
    }

    public IngredientBatch(IngredientType type, int amount, double quality, IngredientSupplier brand) {
        amount = Math.max(0, amount);
        this.type = new SimpleObjectProperty<>(type);
        this.amount = new SimpleIntegerProperty(amount);
        this.quality = new SimpleDoubleProperty(quality);
        this.brand = brand;
    }

    public IngredientType getType() {
        return type.get();
    }

    public final ObjectProperty<IngredientType> typeProperty() {
        return type;
    }

    @SuppressWarnings("unused") // used in TableView
    public final StringProperty nameProperty() {
        return getType().nameProperty();
    }

    public int getAmount() {
        return amount.get();
    }

    public final IntegerProperty amountProperty() {
        return amount;
    }

    @SuppressWarnings("unused") // accessed reflectively by TableView
    public StringProperty amountTextProperty() {
        // return a StringProperty rather than a StringExpression because the TableView's
        // PropertyValueFactory only works with ReadOnlyProperty and not any ObservableValue
        StringProperty property = new SimpleStringProperty();
        property.bind(getIngredientAmount(this));
        return property;
    }

    public double getQuality() {
        return quality.get();
    }

    public final DoubleProperty qualityProperty() {
        return quality;
    }

    public IngredientSupplier getBrand() {
        return brand;
    }

    public boolean isSameTypeAs(IngredientBatch other) {
        return getType() == other.getType();
    }

    public boolean hasAtLeast(IngredientBatch other) {
        return getAmount() >= other.getAmount();
    }

    public static IngredientBatch of(String typeName, int amount) {
        return new IngredientBatch(IngredientType.byName(typeName), amount);
    }

}

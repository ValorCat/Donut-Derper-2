package main.model.ingredient;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import main.ui.UILinker;

/**
 * @since 12/15/2017
 */
public class IngredientOffer {

    private IngredientBatch content;
    private DoubleProperty price;

    public IngredientOffer(IngredientBatch content, double price) {
        this.content = content;
        this.price = new SimpleDoubleProperty(price);
    }

    public IngredientBatch getContent() {
        return content;
    }

    public final StringProperty supplierNameProperty() {
        return content.getBrand().nameProperty();
    }

    public final StringProperty amountTextProperty() {
        return content.amountTextProperty();
    }

    public final DoubleProperty qualityProperty() {
        return content.qualityProperty();
    }

    public double getPrice() {
        return price.get();
    }

    public final StringProperty priceTextProperty() {
        StringProperty property = new SimpleStringProperty();
        property.bind(UILinker.getMoneyText(price));
        return property;
    }

}

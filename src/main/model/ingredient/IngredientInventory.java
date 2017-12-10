package main.model.ingredient;

import javafx.collections.ObservableList;

import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * @since 12/10/2017
 */
public class IngredientInventory {

    private ObservableList<IngredientStock> stock;

    public IngredientInventory(IngredientBatch... initial) {
        stock = observableArrayList();
        for (IngredientBatch batch : initial) {
            stock.add(new IngredientStock(batch));
        }
    }

    public ObservableList<IngredientStock> getStock() {
        return stock;
    }

    public int getAmount(IngredientType type) {
        return stock.stream()
                .filter(type::isOfType)
                .findAny()
                .map(IngredientBatch::getAmount)
                .orElse(0);
    }

    public boolean hasAtLeast(IngredientBatch query) {
        return stock.stream()
                .filter(query::isSameTypeAs)
                .findAny()
                .filter(query::hasLessThan)
                .isPresent();
    }

    public void remove(List<IngredientBatch> removalList, int multiplier) {
        for (IngredientBatch toRemove : removalList) {
            for (IngredientStock stocked : stock) {
                if (toRemove.isSameTypeAs(stocked)) {
                    stocked.decrease(toRemove.getAmount() * multiplier);
                    break;
                }
            }
        }
    }

}

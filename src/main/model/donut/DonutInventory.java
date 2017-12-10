package main.model.donut;

import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.ObservableMap;
import main.RNG;

import static javafx.collections.FXCollections.observableHashMap;

/**
 * @since 12/6/2017
 */
public class DonutInventory {

    private MapProperty<DonutType,Integer> stock;

    public DonutInventory(DonutType initialType) {
        stock = new SimpleMapProperty<>(observableHashMap());
        stock.put(initialType, 0);
    }

    public void add(DonutBatch batch) {
        stock.merge(batch.getType(), batch.getAmount(),
                (currAmount, newBatch) -> currAmount + newBatch);
    }

    public void remove(DonutBatch batch) {
        stock.merge(batch.getType(), batch.getAmount(),
                (currAmount, toRemove) -> Math.max(0, currAmount - toRemove));
    }

    public DonutType pickRandomType() {
        // todo update to DonutBatch
        return RNG.choose(stock.keySet());
    }

    public ObservableMap<DonutType, Integer> getStock() {
        return stock.get();
    }

    public MapProperty<DonutType, Integer> stockProperty() {
        return stock;
    }

}

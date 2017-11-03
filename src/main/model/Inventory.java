package main.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;

import java.util.*;

import static javafx.collections.FXCollections.*;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public class Inventory<
        Item extends Consumable<?, Unit>,
        Unit extends Number & Comparable<Unit>> {

    private final ListProperty<Item> stock;

    public Inventory() {
        this.stock = new SimpleListProperty<>(observableArrayList());
    }

    @SafeVarargs
    public Inventory(Item... initialItems) {
        this.stock = new SimpleListProperty<>(observableArrayList(initialItems));
    }

    public void update(Item item) {
        for (Iterator<Item> iter = stock.iterator(); iter.hasNext();) {
            Item current = iter.next();
            if (current.getData().equals(item.getData())) {
                current.update(item.getAmount());
                if (current.isEmpty()) {
                    iter.remove();
                }
                return;
            }
        }
        stock.add(item);
    }

    public Item getRandom() {
        return stock.get(Game.random.nextInt(stock.size()));
    }

    public ObservableList<Item> getStock() {
        return stock.get();
    }

    public ListProperty<Item> stockProperty() {
        return stock;
    }

}

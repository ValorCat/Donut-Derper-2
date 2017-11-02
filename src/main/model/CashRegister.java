package main.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.TitledPane;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public class CashRegister extends Appliance {

    public static final CashRegister INITIAL = new CashRegister(0);

    private DoubleProperty balance;

    public CashRegister(double sellValue) {
        super(sellValue);
        this.balance = new SimpleDoubleProperty(0);
    }

    public void initialize(TitledPane pane) {

    }

    public void operate() {

    }

    public double collect() {
        double collected = balance.get();
        balance.set(0);
        return collected;
    }

    public void addBalance(double amount) {
        balance.set(balance.get() + amount);
    }

    public double getBalance() {
        return balance.get();
    }

    public DoubleProperty balanceProperty() {
        return balance;
    }

}

package main.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

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

    public void operate() { /* not yet implemented */ }

    public void assignPlayer() {
        location.getRegisters().assignToPlayer(this);
    }

    public void unassignPlayer() {
        location.getRegisters().unassignPlayer();
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

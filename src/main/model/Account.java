package main.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Anthony Morrell
 * @since 10/28/2017
 */
public class Account {

    public static final long INTEREST_INTERVAL = (long) 10e9; // nano seconds between interest deposits
    public static long untilInterestDeposit = INTEREST_INTERVAL;

    private StringProperty name;
    private DoubleProperty balance;
    private DoubleProperty interestRate;
    private Location location;

    public Account(String name, double interestRate, Location location) {
        this.name = new SimpleStringProperty(name);
        this.balance = new SimpleDoubleProperty(0.0);
        this.interestRate = new SimpleDoubleProperty(interestRate);
        this.location = location;
    }

    public void deposit(double amount) {
        balance.set(balance.get() + amount);
    }

    public void addInterest() {
        balance.set(balance.get() * (1 + interestRate.get()));
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public double getBalance() {
        return balance.get();
    }

    public DoubleProperty balanceProperty() {
        return balance;
    }

    public void addBalance(double amount) {
        balance.set(balance.get() + amount);
        location.updateTotalBalance(amount);
    }

    public double getInterestRate() {
        return interestRate.get();
    }

    public DoubleProperty interestRateProperty() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate.set(interestRate);
    }

    public String toString() {
        return name.get();
    }

}

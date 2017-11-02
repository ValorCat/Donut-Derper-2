package main.model;

import javafx.beans.property.*;

/**
 * @author Anthony Morrell
 * @since 10/28/2017
 */
public class Account {

    public static final byte INTEREST_INTERVAL = 120;
    public static final byte PAY_PERIOD_LENGTH = 60;

    private StringProperty name;
    private DoubleProperty balance;
    private FloatProperty interestRate;

    public Account(String name, float interestRate) {
        this.name = new SimpleStringProperty(name);
        this.balance = new SimpleDoubleProperty(0.0);
        this.interestRate = new SimpleFloatProperty(interestRate);
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

    public void setBalance(double balance) {
        this.balance.set(balance);
    }

    public float getInterestRate() {
        return interestRate.get();
    }

    public FloatProperty interestRateProperty() {
        return interestRate;
    }

    public void setInterestRate(float interestRate) {
        this.interestRate.set(interestRate);
    }

    public String toString() {
        return name.get();
    }

}

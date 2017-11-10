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

    private static final long INTEREST_INTERVAL = (long) 10e9; // nano seconds between interest deposits
    private static final long PAYDAY_INTERVAL = (long) 60e9;   // nano seconds between employee payments

    private static long untilInterestDeposit = INTEREST_INTERVAL;
    private static long untilPayday = PAYDAY_INTERVAL;
    private static DoubleProperty payPeriodProgress = new SimpleDoubleProperty(0);

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

    public void addInterest() {
        updateBalance(getInterest());
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

    public void updateBalance(double amount) {
        balance.set(balance.get() + amount);
        location.updateTotalBalance(amount);
    }

    public double getInterest() {
        return balance.get() > 0 ? balance.get() * interestRate.get() : 0;
    }

    public double getInterestRate() {
        return interestRate.get();
    }

    public DoubleProperty interestRateProperty() {
        return interestRate;
    }

    public String toString() {
        return name.get();
    }

    public static boolean readyForInterestDeposit(long delta) {
        untilInterestDeposit -= delta;
        if (untilInterestDeposit <= 0) {
            untilInterestDeposit = INTEREST_INTERVAL;
            return true;
        }
        return false;
    }

    public static boolean readyForPayday(long delta) {
        untilPayday -= delta;
        payPeriodProgress.set((PAYDAY_INTERVAL - untilPayday) / (double) PAYDAY_INTERVAL);
        if (untilPayday <= 0) {
            untilPayday = PAYDAY_INTERVAL;
            return true;
        }
        return false;
    }

    public static DoubleProperty payPeriodProgressProperty() {
        return payPeriodProgress;
    }

}

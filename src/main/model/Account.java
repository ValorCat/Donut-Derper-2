package main.model;

import javafx.beans.property.*;

/**
 * @author Anthony Morrell
 * @since 10/28/2017
 */
public class Account {

    private static final long INTEREST_INTERVAL = (long) 10e9; // nano seconds between interest deposits
    private static final long PAYDAY_INTERVAL = (long) 60e9;   // nano seconds between employee payments

    private static long untilInterestDeposit = INTEREST_INTERVAL;
    private static long untilPayday = PAYDAY_INTERVAL;
    private static DoubleProperty payPeriodProgress = new SimpleDoubleProperty();
    private static IntegerProperty payPeriodSeconds = new SimpleIntegerProperty();

    private StringProperty name;
    private DoubleProperty balance;
    private DoubleProperty interestRate;
    private Location location;

    public Account(String name, double interestRate, Location location) {
        this.name = new SimpleStringProperty(name);
        this.balance = new SimpleDoubleProperty();
        this.interestRate = new SimpleDoubleProperty(interestRate);
        this.location = location;
    }

    public void addInterest() {
        updateBalance(getInterest());
    }

    public void updateBalance(double amount) {
        setBalance(getBalance() + amount);
        location.updateTotalBalance(amount);
    }

    public double getInterest() {
        return getBalance() > 0 ? getBalance() * getInterestRate() : 0;
    }

    private String getName() {
        return name.get();
    }

    public final StringProperty nameProperty() {
        return name;
    }

    private double getBalance() {
        return balance.get();
    }

    private void setBalance(double balance) {
        this.balance.set(balance);
    }

    public final DoubleProperty balanceProperty() {
        return balance;
    }

    private double getInterestRate() {
        return interestRate.get();
    }

    public final DoubleProperty interestRateProperty() {
        return interestRate;
    }

    public String toString() {
        return getName();
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
        payPeriodSeconds.set((int) (untilPayday / (long) 1e9));
        if (untilPayday <= 0) {
            untilPayday = PAYDAY_INTERVAL;
            return true;
        }
        return false;
    }

    public static DoubleProperty payPeriodProgressProperty() {
        return payPeriodProgress;
    }

    public static IntegerProperty payPeriodSecondsProperty() {
        return payPeriodSeconds;
    }

}

package main.model;

import javafx.beans.property.*;

import static main.model.Time.*;

/**
 * @author Anthony Morrell
 * @since 10/28/2017
 */
public class Account {

    private static final Period INTEREST_LENGTH = duration (15); // time between interest deposits
    private static final Period PAYDAY_LENGTH = duration(30);    // time between employee payments

    private static Moment nextInterestDeposit = INTEREST_LENGTH.fromNow();
    private static Timer untilPayday = PAYDAY_LENGTH.asTimer();
    private static DoubleProperty payPeriodProgress = new SimpleDoubleProperty();
    private static IntegerProperty payPeriodSeconds = new SimpleIntegerProperty();

    private StringProperty name;
    private DoubleProperty balance;
    private DoubleProperty interestRate;
    private Location location;

    public Account(String name, double interestRate, Location location) {
        this.name = new SimpleStringProperty(name);
        balance = new SimpleDoubleProperty();
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

    public static boolean readyForInterestDeposit() {
        if (nextInterestDeposit.hasPassed()) {
            nextInterestDeposit.pushBack(INTEREST_LENGTH);
            return true;
        }
        return false;
    }

    public static boolean readyForPayday(Period delta) {
        untilPayday.update(delta);
        setPayPeriodProgress(untilPayday.getProgress());
        setPayPeriodSeconds(untilPayday.getSeconds());
        if (untilPayday.isDone()) {
            untilPayday.reset();
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

    private static void setPayPeriodProgress(double payPeriodProgress) {
        Account.payPeriodProgress.set(payPeriodProgress);
    }

    private static void setPayPeriodSeconds(int payPeriodSeconds) {
        Account.payPeriodSeconds.set(payPeriodSeconds);
    }
}

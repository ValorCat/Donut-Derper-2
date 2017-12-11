package main.model.station;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import main.model.Job;
import main.model.donut.DonutBatch;
import main.model.donut.DonutType;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public class CashRegister extends Station {

    public static final CashRegister INITIAL = new CashRegister(1.2, 0);
    public static final String DESCRIPTION = "Checks out customers and stores the profits while operated by a cashier.";

    private DoubleProperty balance;

    public CashRegister(double baseSpeed, double sellValue) {
        super(baseSpeed, sellValue);
        balance = new SimpleDoubleProperty();
        skill = Job.Skill.USE_REGISTER;
    }

    public void assignPlayer() {
        location.getRegisters().assignToPlayer(this);
    }

    public void unassignPlayer() {
        location.getRegisters().unassignPlayer();
    }

    protected boolean canBegin() {
        return location.getCustomers() > 0;
    }

    public void begin() {
        super.begin();
        location.setLastCheckOut();
    }

    public void finish() {
        super.finish();
        if (location.getCustomers() > 0 && location.getDonutStock() > 0) {
            DonutType type = location.getDonuts().pickRandomType();
            location.removeDonuts(new DonutBatch(type, 1));
            addBalance(type.getCost());
            location.leaveCustomer();
            location.boostAppeal(location.getAppealBoostPerPerson());
        }
    }

    public void update() {
        super.update();
        if (isInUse() && location.getCustomers() == 0) {
            super.finish();
        }
    }

    private void addBalance(double amount) {
        setBalance(getBalance() + amount);
    }

    public void collect() {
        location.getDepositAccount().updateBalance(balance.get());
        balance.set(0);
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

}

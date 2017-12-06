package main.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public class CashRegister extends Station {

    public static final CashRegister INITIAL = new CashRegister(1.2, 0);

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

    public void begin() {
        super.begin();
        location.setLastCheckOut();
    }

    public void finish() {
        super.finish();
        if (location.getCustomers() > 0 && location.getDonutStock() > 0) {
            DonutType random = getRandomDonutType(-1);
            location.updateDonuts(random);
            addBalance(random.getData().getCost());
            location.leaveCustomer();
            location.boostAppeal(location.getAppealBoostPerPerson());
        }
    }

    public void update() {
        super.update();
        if (isInUse() && location.getCustomers() == 0) {
            super.finish();
        }
        if (!isInUse() && automatic && location.getCustomers() > 0) {
            begin();
        }
    }

    private DonutType getRandomDonutType(int amount) {
        assert location.getDonutStock() > 0 : "no donuts in stock";
        DonutType random = location.getDonuts().getRandom();
        return new DonutType(random.getData(), amount);
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

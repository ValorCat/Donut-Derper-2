package main.model;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public class Location {

    private StringProperty name;
    private IntegerProperty customers;
    private IntegerProperty maxCapacity;
    private ApplianceGroup<CashRegister> registers;
    private ApplianceGroup<Fryer> fryers;

    private IntegerProperty donutStock;
    private Inventory<Ingredient,Double> ingredients;
    private Inventory<DonutType,Integer> donuts;

    private DoubleProperty totalBalance;
    private DoubleProperty totalWages;
    private ListProperty<Account> accounts;
    private ObjectProperty<Account> wageSourceAccount;
    private ListProperty<Employee> roster;

    public Location(String name, int maxCapacity) {
        this.name = new SimpleStringProperty(name);
        this.customers = new SimpleIntegerProperty(0);
        this.maxCapacity = new SimpleIntegerProperty(maxCapacity);
        this.registers = new ApplianceGroup<>(this);
        this.fryers = new ApplianceGroup<>(this);

        this.donutStock = new SimpleIntegerProperty(0);
        this.ingredients = new Inventory<>(
                new Ingredient("Flour", 2000),
                new Ingredient("Sugar", 1000));
        this.donuts = new Inventory<>(
                new DonutType("Plain", 0));

        this.totalBalance = new SimpleDoubleProperty(0);
        this.totalWages = new SimpleDoubleProperty(0);
        this.accounts = new SimpleListProperty<>(observableArrayList());
        this.wageSourceAccount = new SimpleObjectProperty<>(null);
        this.roster = new SimpleListProperty<>(observableArrayList(Employee.PLAYER));
    }

    public void enterCustomer() {
        customers.set(customers.get() + 1);
    }

    public void leaveCustomer() {
        assert customers.get() > 0;
        customers.set(customers.get() - 1);
    }

    public void updateDonuts(DonutType newDonuts) {
        int addition = newDonuts.getAmount();
        int newTotal = donutStock.intValue() + addition;
        this.donutStock.set(newTotal < 0 ? 0 : newTotal);
        this.donuts.update(newDonuts);
        if (addition > 0) {
            Game.game.addDonuts(addition);
        }
    }

    public void updateTotalBalance(double amount) {
        totalBalance.set(totalBalance.get() + amount);
    }

    public void updateTotalWages(double amount) {
        totalWages.set(totalWages.get() + amount);
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

    public int getCustomers() {
        return customers.get();
    }

    public IntegerProperty customersProperty() {
        return customers;
    }

    public int getMaxCapacity() {
        return maxCapacity.get();
    }

    public IntegerProperty maxCapacityProperty() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity.set(maxCapacity);
    }

    public ObservableList<Employee> getRoster() {
        return roster.get();
    }

    public ListProperty<Employee> rosterProperty() {
        return roster;
    }

    public ApplianceGroup<CashRegister> getRegisters() {
        return registers;
    }

    public ApplianceGroup<Fryer> getFryers() {
        return fryers;
    }

    public int getDonutStock() {
        return donutStock.get();
    }

    public IntegerProperty donutStockProperty() {
        return donutStock;
    }

    public Inventory<Ingredient, Double> getIngredients() {
        return ingredients;
    }

    public Inventory<DonutType, Integer> getDonuts() {
        return donuts;
    }

    public double getTotalBalance() {
        return totalBalance.get();
    }

    public DoubleProperty totalBalanceProperty() {
        return totalBalance;
    }

    public double getTotalWages() {
        return totalWages.get();
    }

    public DoubleProperty totalWagesProperty() {
        return totalWages;
    }

    public ObservableList<Account> getAccounts() {
        return accounts.get();
    }

    public ListProperty<Account> accountsProperty() {
        return accounts;
    }

    public Account getWageSourceAccount() {
        return wageSourceAccount.get();
    }

    public ObjectProperty<Account> wageSourceAccountProperty() {
        return wageSourceAccount;
    }

    public void setWageSourceAccount(Account wageSourceAccount) {
        this.wageSourceAccount.set(wageSourceAccount);
    }

    public String toString() {
        return name.get();
    }

}

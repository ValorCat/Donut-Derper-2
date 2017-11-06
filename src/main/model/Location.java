package main.model;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.control.TitledPane;
import main.RNG;
import main.TitledPaneFactory;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public class Location {

    private StringProperty name;
    private double appeal = 1;                       // modifies rate of customer entry
    private int lowStockTolerance = 12;              // min donuts before customers will enter
    private long slowServiceTolerance = (long) 5e9;  // min nanoseconds w/o service before customers walk out
    private double baseEnterChance = 0.003;          // base chance of customer entry per tick
    private double leaveChance = 0.006;              // base chance of customer walking out per tick
    private double appealBoostPerPerson = .05;       // effect of one happy customer on appeal
    private double maxAppealDropPerPerson = 0.02;    // max effect of one unhappy customer on appeal

    private IntegerProperty customers;
    private IntegerProperty maxCapacity;
    private DoubleBinding occupancy;
    private long lastCheckOut;
    private long noCustomerTimeRemaining;
    private boolean customersLeaving;

    private IntegerProperty donutStock;
    private Inventory<DonutType,Integer> donuts;
    private Inventory<Ingredient,Double> ingredients;
    private StationGroup<CashRegister> registers;
    private StationGroup<Fryer> fryers;

    private DoubleProperty totalBalance;
    private DoubleProperty totalWages;
    private ObjectProperty<Account> depositAccount;
    private ObjectProperty<Account> wageSourceAccount;
    private ListProperty<Account> accounts;
    private ListProperty<Employee> roster;
    private ObservableList<TitledPane> accountPanes;

    public Location(String name, int maxCapacity) {
        this.name = new SimpleStringProperty(name);
        this.customers = new SimpleIntegerProperty();
        this.maxCapacity = new SimpleIntegerProperty(maxCapacity);
        this.occupancy = customers.add(0.0).divide(this.maxCapacity);

        this.donutStock = new SimpleIntegerProperty();
        this.donuts = new Inventory<>(
                new DonutType("Plain", 0));
        this.ingredients = new Inventory<>(
                new Ingredient("Flour", 2000),
                new Ingredient("Sugar", 1000));
        this.registers = new StationGroup<>(this);
        this.fryers = new StationGroup<>(this);

        this.totalBalance = new SimpleDoubleProperty();
        this.totalWages = new SimpleDoubleProperty();
        this.accounts = new SimpleListProperty<>(observableArrayList());
        this.accountPanes = observableArrayList();
        addAccount(new Account("Checking", .007, this));
        addAccount(new Account("Savings", .011, this));
        this.depositAccount = new SimpleObjectProperty<>(accounts.get(0));
        this.wageSourceAccount = new SimpleObjectProperty<>(accounts.get(0));
        this.roster = new SimpleListProperty<>(observableArrayList(Employee.PLAYER));
    }

    public void update(long now, long last) {
        boolean haveCustomers = customers.get() > 0;
        boolean recentCheckout = (now - lastCheckOut) < slowServiceTolerance;
        if (customersLeaving) {
            if (!haveCustomers) {
                customersLeaving = false;
                noCustomerTimeRemaining = RNG.range((long) 3e9, (long) 8e9);
            } else if (recentCheckout) {
                customersLeaving = false;
            } else {
                RNG.chance(leaveChance, this::leaveCustomer);
            }
        } else if (haveCustomers && !recentCheckout) {
            customersLeaving = true;
        } else if (customerCanEnter()) {
            double chance = baseEnterChance * appeal;
            if (occupancy.get() > .5) {
                chance /= 3;
            }
            RNG.chance(chance, this::enterCustomer);
        }
        if (noCustomerTimeRemaining > 0) {
            noCustomerTimeRemaining -= now - last;
        }
        Account.untilInterestDeposit -= now - last;
        if (Account.untilInterestDeposit <= 0) {
            depositInterest();
            Account.untilInterestDeposit = Account.INTEREST_INTERVAL;
        }
    }

    private void depositInterest() {
        accounts.forEach(Account::addInterest);
    }

    private boolean customerCanEnter() {
        return noCustomerTimeRemaining <= 0
                && occupancy.get() < 1
                && donutStock.get() >= lowStockTolerance;
    }

    public void enterCustomer() {
        assert customers.get() < maxCapacity.get();
        if (customers.get() == 0) {
            // ensure customers don't immediately leave upon entering
            // after a long break without anyone showing up
            setLastCheckOut();
        }
        customers.set(customers.get() + 1);
    }

    public void leaveCustomer() {
        assert customers.get() > 0;
        if (customersLeaving) {
            appeal -= RNG.range(maxAppealDropPerPerson);
        }
        customers.set(customers.get() - 1);
    }

    public void updateDonuts(DonutType newDonuts) {
        int addition = newDonuts.getAmount();
        int newTotal = donutStock.get() + addition;
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

    public double getAppealBoostPerPerson() {
        return appealBoostPerPerson;
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

    public void setLastCheckOut() {
        this.lastCheckOut = System.nanoTime();
    }

    public void boostAppeal(double amount) {
        this.appeal += amount;
    }

    public ObservableList<Employee> getRoster() {
        return roster.get();
    }

    public ListProperty<Employee> rosterProperty() {
        return roster;
    }

    public StationGroup<CashRegister> getRegisters() {
        return registers;
    }

    public StationGroup<Fryer> getFryers() {
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

    public Account getDepositAccount() {
        return depositAccount.get();
    }

    public ObjectProperty<Account> depositAccountProperty() {
        return depositAccount;
    }

    public ObservableList<TitledPane> getAccountPanes() {
        // todo move out of Location
        return accountPanes;
    }

    public String toString() {
        return name.get();
    }

    private void addAccount(Account acc) {
        accounts.add(acc);
        accountPanes.add(TitledPaneFactory.buildAccountPane(acc));
    }

}

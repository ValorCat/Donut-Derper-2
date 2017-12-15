package main.model;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.control.TitledPane;
import main.Game;
import main.RNG;
import main.model.donut.DonutBatch;
import main.model.donut.DonutInventory;
import main.model.donut.DonutType;
import main.model.ingredient.IngredientBatch;
import main.model.ingredient.IngredientInventory;
import main.model.station.CashRegister;
import main.model.station.Fryer;
import main.model.station.StationGroup;
import main.ui.TitledPaneFactory;

import static javafx.collections.FXCollections.observableArrayList;
import static main.model.Time.*;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public class Location {

    public static final Location NONE = new Location("--", 0);

    private StringProperty name;
    private double appeal = 1;                 // modifies rate of customer entry
    private int lowStockTolerance = 10;        // min donuts before customers will enter
    private Period slowServiceTolerance = duration(6.5); // min time w/o service before customers walk out
    private double baseEnterChance = .0015;    // base chance of customer entry per tick
    private double leaveChance = .006;         // base chance of customer walking out per tick
    private double appealBoostPerPerson = .03; // effect of one happy customer on appeal

    private IntegerProperty customers;
    private IntegerProperty maxCapacity;
    private DoubleBinding occupancy;
    private Moment lastCheckOut;
    private Moment canHaveCustomers;
    private boolean customersLeaving;

    private IntegerProperty donutStock;
    private DonutInventory donuts;
    private IngredientInventory ingredients;
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
        customers = new SimpleIntegerProperty();
        this.maxCapacity = new SimpleIntegerProperty(maxCapacity);
        occupancy = customers.add(0.0).divide(this.maxCapacity);
        lastCheckOut = blankTime();
        canHaveCustomers = blankTime();

        donutStock = new SimpleIntegerProperty();
        donuts = new DonutInventory(DonutType.PLAIN);
        ingredients = new IngredientInventory(
                IngredientBatch.of("All-Purpose Flour", 10 * 181),
                IngredientBatch.of("Butter", 15 * 24),
                IngredientBatch.of("Eggs", 6 * 144),
                IngredientBatch.of("Sugar", 3 * 109),
                IngredientBatch.of("Whole Milk", 2 * 768)
        );
        registers = new StationGroup<>(this);
        fryers = new StationGroup<>(this);

        totalBalance = new SimpleDoubleProperty();
        totalWages = new SimpleDoubleProperty();
        accounts = new SimpleListProperty<>(observableArrayList());
        accountPanes = observableArrayList();
        addAccount(new Account("Checking", .007, this));
        addAccount(new Account("Savings", .011, this));
        depositAccount = new SimpleObjectProperty<>(accounts.get(0));
        wageSourceAccount = new SimpleObjectProperty<>(accounts.get(0));
        roster = new SimpleListProperty<>(observableArrayList(Employee.PLAYER, Employee.UNASSIGNED));
    }

    public void update(Moment now, Period delta, boolean isInterestDay, boolean isPayday) {
        updateCustomers(now, delta);
        updateStations();
        if (isInterestDay) {
            depositInterest();
        }
        if (isPayday) {
            payEmployees();
        }
    }

    public void enterCustomer() {
        assert getCustomers() < getMaxCapacity();
        if (getCustomers() == 0) {
            // ensure customers don't immediately leave upon entering
            // after a long break without anyone showing up
            setLastCheckOut();
        }
        setCustomers(getCustomers() + 1);
    }

    public void leaveCustomer() {
        assert getCustomers() > 0;
        setCustomers(getCustomers() - 1);
    }

    public void addDonuts(DonutBatch batch) {
        setDonutStock(getDonutStock() + batch.getAmount());
        Game.game.addDonuts(batch.getAmount());
        donuts.add(batch);
    }

    public void removeDonuts(DonutBatch batch) {
        setDonutStock(getDonutStock() - batch.getAmount());
        donuts.remove(batch);
    }

    private void setDonutStock(int donutStock) {
        this.donutStock.set(Math.max(0, donutStock));
    }

    public void dismiss(Employee emp) {
        assert roster.contains(emp);
        emp.getStation().ifPresent(s -> s.setOperator(Employee.UNASSIGNED));
        roster.remove(emp);
        updateTotalWages(-emp.getJob().getWage());
    }

    public void updateTotalBalance(double amount) {
        totalBalance.set(totalBalance.get() + amount);
    }

    public void updateTotalWages(double amount) {
        totalWages.set(totalWages.get() + amount);
    }

    public final StringProperty nameProperty() {
        return name;
    }

    public double getAppealBoostPerPerson() {
        return appealBoostPerPerson;
    }

    public int getCustomers() {
        return customers.get();
    }

    public final IntegerProperty customersProperty() {
        return customers;
    }

    private void setCustomers(int customers) {
        this.customers.set(customers);
    }

    public int getMaxCapacity() {
        return maxCapacity.get();
    }

    public final IntegerProperty maxCapacityProperty() {
        return maxCapacity;
    }

    public void setLastCheckOut() {
        lastCheckOut = now();
    }

    public void boostAppeal(double amount) {
        appeal += amount;
    }

    public ObservableList<Employee> getRoster() {
        return roster.get();
    }

    public final ListProperty<Employee> rosterProperty() {
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

    public final IntegerProperty donutStockProperty() {
        return donutStock;
    }

    public IngredientInventory getIngredients() {
        return ingredients;
    }

    public DonutInventory getDonuts() {
        return donuts;
    }

    public double getTotalBalance() {
        return totalBalance.get();
    }

    public final DoubleProperty totalBalanceProperty() {
        return totalBalance;
    }

    private double getTotalWages() {
        return totalWages.get();
    }

    public final DoubleProperty totalWagesProperty() {
        return totalWages;
    }

    public ObservableList<Account> getAccounts() {
        return accounts.get();
    }

    public final ListProperty<Account> accountsProperty() {
        return accounts;
    }

    private Account getWageSourceAccount() {
        return wageSourceAccount.get();
    }

    public final ObjectProperty<Account> wageSourceAccountProperty() {
        return wageSourceAccount;
    }

    public Account getDepositAccount() {
        return depositAccount.get();
    }

    public final ObjectProperty<Account> depositAccountProperty() {
        return depositAccount;
    }

    public ObservableList<TitledPane> getAccountPanes() {
        // todo move out of Location
        return accountPanes;
    }

    public String toString() {
        return name.get();
    }

    private void updateCustomers(Moment now, Period delta) {
        boolean haveCustomers = getCustomers() > 0;
        boolean recentCheckout = now.since(lastCheckOut).lessThan(slowServiceTolerance);
        if (customersLeaving) {
            if (!haveCustomers) {
                customersLeaving = false;
                canHaveCustomers.pushBack(randomDuration(3, 8));
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
    }

    private void updateStations() {
        registers.update();
        fryers.update();
    }

    private void payEmployees() {
        Account account = getWageSourceAccount();
        account.updateBalance(-getTotalWages());
    }

    private void depositInterest() {
        accounts.forEach(Account::addInterest);
    }

    private boolean customerCanEnter() {
        return canHaveCustomers.hasPassed()
                && occupancy.get() < 1
                && donutStock.get() >= lowStockTolerance;
    }

    private void addAccount(Account acc) {
        accounts.add(acc);
        accountPanes.add(TitledPaneFactory.buildAccountPane(acc));
    }

}

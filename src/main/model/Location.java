package main.model;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.control.TitledPane;
import main.Game;
import main.RNG;
import main.ui.TitledPaneFactory;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public class Location {

    public static final Location NONE = new Location("--", 0);

    private StringProperty name;
    private double appeal = 1;                       // modifies rate of customer entry
    private int lowStockTolerance = 5;               // min donuts before customers will enter
    private long slowServiceTolerance = (long) 6e9;  // min nanoseconds w/o service before customers walk out
    private double baseEnterChance = .002;           // base chance of customer entry per tick
    private double leaveChance = .006;               // base chance of customer walking out per tick
    private double appealBoostPerPerson = .02;       // effect of one happy customer on appeal

    private IntegerProperty customers;
    private IntegerProperty maxCapacity;
    private DoubleBinding occupancy;
    private long lastCheckOut;
    private long noCustomerTimeRemaining;
    private boolean customersLeaving;

    private IntegerProperty donutStock;
    private DonutInventory donuts;
    private ListProperty<Ingredient> ingredients;
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

        donutStock = new SimpleIntegerProperty();
        donuts = new DonutInventory(DonutType.PLAIN);
        ingredients = new SimpleListProperty<>(observableArrayList(
                new Ingredient(IngredientType.FLOUR, 1000),
                new Ingredient(IngredientType.SUGAR, 1000)
        ));
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

    public void update(long now, long last, boolean isInterestDay, boolean isPayday) {
        updateCustomers(now, last);
        updateStations();
        if (isInterestDay) {
            depositInterest();
        }
        if (isPayday) {
            payEmployees();
        }
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
        customers.set(customers.get() - 1);
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

    public String getName() {
        return name.get();
    }

    public final StringProperty nameProperty() {
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

    public final IntegerProperty customersProperty() {
        return customers;
    }

    public int getMaxCapacity() {
        return maxCapacity.get();
    }

    public final IntegerProperty maxCapacityProperty() {
        return maxCapacity;
    }

    public void setLastCheckOut() {
        lastCheckOut = System.nanoTime();
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

    public ObservableList<Ingredient> getIngredients() {
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

    public double getTotalWages() {
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

    private void updateCustomers(long now, long last) {
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
    }

    private void updateStations() {
        registers.update();
        fryers.update();
    }

    private void payEmployees() {
        Account account = wageSourceAccount.get();
        account.updateBalance(-totalWages.get());
    }

    private void depositInterest() {
        accounts.forEach(Account::addInterest);
    }

    private boolean customerCanEnter() {
        return noCustomerTimeRemaining <= 0
                && occupancy.get() < 1
                && donutStock.get() >= lowStockTolerance;
    }

    private void addAccount(Account acc) {
        accounts.add(acc);
        accountPanes.add(TitledPaneFactory.buildAccountPane(acc));
    }

}

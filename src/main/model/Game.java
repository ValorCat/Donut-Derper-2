package main.model;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.text.NumberFormat;
import java.util.Random;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public final class Game {

    public static final Game game = new Game();
    public static final Random random = new Random();

    private ListProperty<Location> locations;
    private ObjectProperty<Location> currentLocation;
    private LongProperty grossDonuts;

    private Game() {
        this.locations = new SimpleListProperty<>(observableArrayList(
                new Location("Tony's Bakery", 8)
        ));
        this.currentLocation = new SimpleObjectProperty<>(locations.get(0));
        this.grossDonuts = new SimpleLongProperty(0);

        currentLocation.get().getRegisters().add(CashRegister.INITIAL);
        currentLocation.get().getRegisters().assignToPlayer(CashRegister.INITIAL);
        currentLocation.get().getFryers().add(Fryer.INITIAL);
        currentLocation.get().getFryers().assignToPlayer(Fryer.INITIAL);
        CashRegister.INITIAL.setOperator(Employee.PLAYER);
        Fryer.INITIAL.setOperator(Employee.PLAYER);

        // temp
        currentLocation.get().getFryers().add(new Fryer(5, 45));
    }

    public void addDonuts(int amount) {
        grossDonuts.set(grossDonuts.get() + amount);
    }

    public ObservableList<Location> getLocations() {
        return locations.get();
    }

    public ListProperty<Location> locationsProperty() {
        return locations;
    }

    public Location getCurrentLocation() {
        return currentLocation.get();
    }

    public ObjectProperty<Location> currentLocationProperty() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation.set(currentLocation);
    }

    public long getGrossDonuts() {
        return grossDonuts.get();
    }

    public LongProperty grossDonutsProperty() {
        return grossDonuts;
    }

    public static Location location() {
        return game.currentLocation.get();
    }

    public static String formatMoney(double amount) {
        return NumberFormat.getCurrencyInstance().format(amount);
    }

}

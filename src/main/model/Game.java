package main.model;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import main.Controller;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public final class Game {

    public static final Game game = new Game();

    private ListProperty<Location> locations;
    private ObjectProperty<Location> currentLocation;
    private LongProperty grossDonuts;

    private Game() {
        this.locations = new SimpleListProperty<>(observableArrayList(
                new Location("Tony's Bakery", 8),
                new Location("Second Place", 12)
        ));
        this.currentLocation = new SimpleObjectProperty<>(locations.get(0));
        this.grossDonuts = new SimpleLongProperty(0);

        currentLocation.get().getRegisters().add(CashRegister.INITIAL);
        currentLocation.get().getFryers().add(Fryer.INITIAL);
        currentLocation.get().getFryers().assignToPlayer(Fryer.INITIAL);
        Fryer.INITIAL.setOperator(Employee.PLAYER);
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

    public long getGrossDonuts() {
        return grossDonuts.get();
    }

    public LongProperty grossDonutsProperty() {
        return grossDonuts;
    }

    public static Location location() {
        return game.currentLocation.get();
    }

}

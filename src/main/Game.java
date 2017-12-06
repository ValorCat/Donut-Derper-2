package main;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import main.model.CashRegister;
import main.model.Employee;
import main.model.Fryer;
import main.model.Location;

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
        locations = new SimpleListProperty<>(observableArrayList(
                new Location("Tony's Bakery", 8),
                new Location("Second Place", 12)
        ));
        currentLocation = new SimpleObjectProperty<>(locations.get(0));
        grossDonuts = new SimpleLongProperty();

        getCurrentLocation().getRegisters().add(CashRegister.INITIAL);
        getCurrentLocation().getFryers().add(Fryer.INITIAL);
        getCurrentLocation().getFryers().assignToPlayer(Fryer.INITIAL);
        Fryer.INITIAL.setOperator(Employee.PLAYER);
    }

    private Location getCurrentLocation() {
        return currentLocation.get();
    }

    public void addDonuts(int amount) {
        setGrossDonuts(getGrossDonuts() + amount);
    }

    private long getGrossDonuts() {
        return grossDonuts.get();
    }

    private void setGrossDonuts(long grossDonuts) {
        this.grossDonuts.set(grossDonuts);
    }

    public ObservableList<Location> getLocations() {
        return locations.get();
    }

    public ListProperty<Location> locationsProperty() {
        return locations;
    }

    public ObjectProperty<Location> currentLocationProperty() {
        return currentLocation;
    }

    public LongProperty grossDonutsProperty() {
        return grossDonuts;
    }

    public static Location location() {
        return game.getCurrentLocation();
    }

}

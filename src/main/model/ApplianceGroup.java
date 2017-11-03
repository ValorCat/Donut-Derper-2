package main.model;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.control.TitledPane;
import main.TitledPaneFactory;

import java.util.Optional;

import static javafx.collections.FXCollections.*;

/**
 * @author Anthony Morrell
 * @since 10/30/2017
 */
public class ApplianceGroup<A extends Appliance> {

    private Location location;
    private ListProperty<A> list;
    private ListProperty<TitledPane> panes;
    private ObjectProperty<A> playerOperated;
    private BooleanProperty playerHasAppliance;

    public ApplianceGroup(Location location) {
        this.location = location;
        this.list = new SimpleListProperty<>(observableArrayList());
        this.panes = new SimpleListProperty<>(observableArrayList());
        this.playerOperated = new SimpleObjectProperty<>(null);
        this.playerHasAppliance = new SimpleBooleanProperty(false);
    }

    public Optional<A> getPlayerOperated() {
        return Optional.ofNullable(playerOperated.get());
    }

    public void assignToPlayer(A app) {
        assert list.contains(app);
        playerOperated.set(app);
        playerHasAppliance.set(true);
    }

    public void unassignPlayer() {
        playerOperated.set(null);
        playerHasAppliance.set(false);
    }

    public void add(A newApp) {
        newApp.setLocation(location);
        list.add(newApp);
        panes.add(TitledPaneFactory.buildAppliancePane(newApp));
    }

    public ObservableList getList() {
        return list.get();
    }

    public ListProperty<A> listProperty() {
        return list;
    }

    public ObservableList<TitledPane> getPanes() {
        return panes.get();
    }

    public ListProperty<TitledPane> panesProperty() {
        return panes;
    }

    public boolean playerHasAppliance() {
        return playerHasAppliance.get();
    }

    public BooleanProperty playerHasApplianceProperty() {
        return playerHasAppliance;
    }
}

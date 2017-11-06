package main.model;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.control.TitledPane;
import main.TitledPaneFactory;

import java.util.Optional;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * @author Anthony Morrell
 * @since 10/30/2017
 */
public class StationGroup<S extends Station> {

    private Location location;
    private ListProperty<S> list;
    private ListProperty<TitledPane> panes;
    private ObjectProperty<S> playerOperated;
    private BooleanProperty playerHasStation;
    private BooleanProperty playerStationInUse;

    public StationGroup(Location location) {
        this.location = location;
        this.list = new SimpleListProperty<>(observableArrayList());
        this.panes = new SimpleListProperty<>(observableArrayList());
        this.playerOperated = new SimpleObjectProperty<>();
        this.playerHasStation = new SimpleBooleanProperty();
        this.playerStationInUse = new SimpleBooleanProperty();
    }

    public void update() {
        list.forEach(Station::update);
    }

    public Optional<S> getPlayerOperated() {
        return Optional.ofNullable(playerOperated.get());
    }

    public void assignToPlayer(S station) {
        assert list.contains(station);
        playerOperated.set(station);
        playerHasStation.set(true);
        playerStationInUse.bind(station.inUseProperty());
    }

    public void unassignPlayer() {
        playerOperated.set(null);
        playerHasStation.set(false);
        playerStationInUse.unbind();
    }

    public void add(S station) {
        station.setLocation(location);
        list.add(station);
        panes.add(TitledPaneFactory.buildStationPane(station));
    }

    public ObservableList getList() {
        return list.get();
    }

    public ListProperty<S> listProperty() {
        return list;
    }

    public ObservableList<TitledPane> getPanes() {
        return panes.get();
    }

    public ListProperty<TitledPane> panesProperty() {
        return panes;
    }

    public BooleanProperty playerHasStationProperty() {
        return playerHasStation;
    }

    public boolean isPlayerStationInUse() {
        return playerStationInUse.get();
    }

    public BooleanProperty playerStationInUseProperty() {
        return playerStationInUse;
    }
}

package main.model;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.control.TitledPane;
import main.ui.TitledPaneFactory;

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
        list = new SimpleListProperty<>(observableArrayList());
        panes = new SimpleListProperty<>(observableArrayList());
        playerOperated = new SimpleObjectProperty<>();
        playerHasStation = new SimpleBooleanProperty();
        playerStationInUse = new SimpleBooleanProperty();
    }

    public void update() {
        list.forEach(Station::update);
    }

    public Optional<S> getPlayerOperated() {
        return Optional.ofNullable(playerOperated.get());
    }

    public void assignToPlayer(S station) {
        assert list.contains(station);
        setPlayerOperated(station);
        setPlayerHasStation(true);
        playerStationInUse.bind(station.inUseProperty());
    }

    public void unassignPlayer() {
        setPlayerOperated(null);
        setPlayerHasStation(false);
        playerStationInUse.unbind();
    }

    public void add(S station) {
        station.setLocation(location);
        list.add(station);
        panes.add(TitledPaneFactory.buildStationPane(station));
    }

    public ObservableList<TitledPane> getPanes() {
        return panes.get();
    }

    public BooleanProperty playerHasStationProperty() {
        return playerHasStation;
    }

    public BooleanProperty playerStationInUseProperty() {
        return playerStationInUse;
    }

    public void setPlayerOperated(S playerOperated) {
        this.playerOperated.set(playerOperated);
    }

    public void setPlayerHasStation(boolean playerHasStation) {
        this.playerHasStation.set(playerHasStation);
    }

}

package main.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TitledPane;

import java.io.IOException;
import java.util.Optional;

import static javafx.collections.FXCollections.*;

/**
 * @author Anthony Morrell
 * @since 10/30/2017
 */
public class ApplianceGroup<A extends Appliance> {

    private TitledPane model;
    private Location location;
    private ListProperty<A> appliances;
    private ListProperty<TitledPane> titledPanes;
    private ObjectProperty<A> playerAppliance;

    public ApplianceGroup(String source, Location location) {
        this.model = loadFXML(source);
        this.location = location;
        this.appliances = new SimpleListProperty<>(observableArrayList());
        this.titledPanes = new SimpleListProperty<>(observableArrayList());
        this.playerAppliance = new SimpleObjectProperty<>(null);
    }

    private static TitledPane loadFXML(String fileName) {
        try {
            return FXMLLoader.load(ApplianceGroup.class.getResource(fileName));
        } catch (IOException e) {
            throw new IllegalArgumentException("no FXML file " + fileName);
        }
    }

    public Optional<A> getPlayerAppliance() {
        return Optional.ofNullable(playerAppliance.get());
    }

    public void assignToPlayer(A app) {
        assert appliances.contains(app);
        playerAppliance.set(app);
        app.setOperator(Employee.PLAYER);
    }

    public void addAppliance(A newApp) {
        newApp.setLocation(location);
        appliances.add(newApp);
        TitledPane newPane = new TitledPane(model.getText(), model.getContent());
        newPane.setGraphic(model.getGraphic());
        newApp.initialize(newPane);
        titledPanes.add(newPane);
    }

    public ObservableList getAppliances() {
        return appliances.get();
    }

    public ListProperty<A> appliancesProperty() {
        return appliances;
    }

    public ObservableList<TitledPane> getTitledPanes() {
        return titledPanes.get();
    }

    public ListProperty<TitledPane> titledPanesProperty() {
        return titledPanes;
    }

}

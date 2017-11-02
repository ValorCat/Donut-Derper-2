package main.model;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.control.TitledPane;

import static javafx.collections.FXCollections.*;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public abstract class Appliance {

    private static final String SELL_FORMAT = "Sell - %s";

    private Location location;
    private ObjectProperty<Employee> operator;
    private DoubleProperty progress;
    private DoubleProperty sellValue;

    public Appliance(double sellValue) {
        this.operator = new SimpleObjectProperty<>(Employee.UNASSIGNED);
        this.progress = new SimpleDoubleProperty(0);
        this.sellValue = new SimpleDoubleProperty(sellValue);
    }

    public abstract void initialize(TitledPane pane);
    public abstract void operate();

    public StringBinding getOperatorBinding() {
        return Bindings.createStringBinding(() -> operator.get().getName(), operator);
    }

    public StringBinding getSellTextBinding() {
        return Bindings.createStringBinding(() ->
                String.format(SELL_FORMAT, Game.formatMoney(sellValue.get())),
        sellValue);
    }

    public ObjectBinding<ObservableList<Employee>> getPossibleOperatorsBinding() {
        return Bindings.createObjectBinding(() -> {
            ObservableList<Employee> options = observableArrayList(location.getRoster());
            options.add(Employee.UNASSIGNED);
            return options;
        }, location.rosterProperty());
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Employee getOperator() {
        return operator.get();
    }

    public ObjectProperty<Employee> operatorProperty() {
        return operator;
    }

    public void setOperator(Employee operator) {
        this.operator.set(operator);
    }

    public double getProgress() {
        return progress.get();
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress.set(progress);
    }

    public double getSellValue() {
        return sellValue.get();
    }

    public DoubleProperty sellValueProperty() {
        return sellValue;
    }

    public void setSellValue(double sellValue) {
        this.sellValue.set(sellValue);
    }

}

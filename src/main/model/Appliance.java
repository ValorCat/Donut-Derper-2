package main.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public abstract class Appliance {

    private ObjectProperty<Employee> operator;
    private DoubleProperty progress;
    private DoubleProperty sellValue;
    protected Location location;

    public Appliance(double sellValue) {
        this.operator = new SimpleObjectProperty<>(Employee.UNASSIGNED) {
            public void set(Employee newValue) {
                setOperatorImpl(operator.get(), newValue);
                super.set(newValue);
            }
        };
        this.progress = new SimpleDoubleProperty(0);
        this.sellValue = new SimpleDoubleProperty(sellValue);
    }

    public abstract void operate();
    protected abstract void assignPlayer();
    protected abstract void unassignPlayer();

    public Location getLocation() {
        return location;
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

    private void unassign() {
        operator.set(Employee.UNASSIGNED);
    }

    private void setOperatorImpl(Employee oldOperator, Employee newOperator) {
        if (oldOperator == Employee.PLAYER) {
            unassignPlayer();
        }
        if (newOperator != Employee.UNASSIGNED) {
            newOperator.getStation().ifPresent(Appliance::unassign);
            newOperator.assign(this);
        }
        if (newOperator == Employee.PLAYER) {
            assignPlayer();
        }
    }

}

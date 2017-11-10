package main.model;

import javafx.beans.property.*;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public abstract class Station {

    private ObjectProperty<Employee> operator;
    private DoubleProperty progress;
    private DoubleProperty sellValue;
    private double speed;
    protected BooleanProperty inUse;
    protected boolean automatic;
    protected Location location;
    protected Job.Skill skill;

    public Station(double speed, double sellValue) {
        this.operator = new SimpleObjectProperty<>(Employee.UNASSIGNED);
        this.operator.addListener((obs, oldValue, newValue) -> setOperatorImpl(oldValue, newValue));
        this.speed = speed;
        this.progress = new SimpleDoubleProperty();
        this.sellValue = new SimpleDoubleProperty(sellValue);
        this.inUse = new SimpleBooleanProperty();
    }

    protected abstract void assignPlayer();
    protected abstract void unassignPlayer();

    public void begin() {
        inUse.set(true);
        progress.set(0);
    }

    public void finish() {
        inUse.set(false);
        progress.set(0);
    }

    public void update() {
        if (inUse.get() && (operator.get() != Employee.UNASSIGNED)) {
            progress.set(progress.get() + speed / 60);
            if (progress.get() >= 1) {
                finish();
            }
        }
    }

    public boolean isInUse() {
        return inUse.get();
    }

    public BooleanProperty inUseProperty() {
        return inUse;
    }

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

    public Job.Skill getSkill() {
        return skill;
    }

    private void unassign() {
        operator.set(Employee.UNASSIGNED);
    }

    private void setOperatorImpl(Employee oldOperator, Employee newOperator) {
        if (oldOperator == Employee.PLAYER) {
            unassignPlayer();
        }
        boolean isAssigned = newOperator != Employee.UNASSIGNED;
        boolean isPlayer = newOperator == Employee.PLAYER;
        if (isAssigned) {
            newOperator.getStation().ifPresent(Station::unassign);
            newOperator.assign(this);
        }
        if (isPlayer) {
            assignPlayer();
        }
        automatic = isAssigned && !isPlayer;
    }

}

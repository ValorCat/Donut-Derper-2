package main.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableValue;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public abstract class Station {

    private ObjectProperty<Employee> operator;
    private DoubleProperty progress;
    private DoubleProperty sellValue;
    private ObservableDoubleValue operatorSpeed;
    private ObservableDoubleValue speed;
    private double baseSpeed;
    protected BooleanProperty inUse;
    protected boolean automatic;
    protected Location location;
    protected Job.Skill skill;

    public Station(double baseSpeed, double sellValue) {
        this.operator = new SimpleObjectProperty<>(Employee.UNASSIGNED);
        this.operator.addListener((obs, oldValue, newValue) -> setOperatorImpl(oldValue, newValue));
        this.baseSpeed = baseSpeed;
        setOperatorSpeed(operator);
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
            progress.set(progress.get() + speed.get() / 60);
            if (progress.get() >= 1) {
                finish();
            }
        }
    }

    public void setOperatorSpeed(ObservableValue<Employee> newOperator) {
        this.operatorSpeed = Bindings.createDoubleBinding(
                () -> newOperator.getValue().getSkill(skill),
                newOperator, newOperator.getValue().jobProperty());
        this.speed = Bindings.createDoubleBinding(() -> baseSpeed * operatorSpeed.get(), operatorSpeed);
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
        boolean wasAssigned = oldOperator != Employee.UNASSIGNED;
        boolean wasPlayer = oldOperator == Employee.PLAYER;
        boolean isAssigned = newOperator != Employee.UNASSIGNED;
        boolean isPlayer = newOperator == Employee.PLAYER;
        if (wasAssigned) {
            oldOperator.unsetStation();
            if (wasPlayer) {
                unassignPlayer();
            }
        }
        if (isAssigned) {
            newOperator.getStation().ifPresent(Station::unassign);
            newOperator.setStation(this);
            setOperatorSpeed(operator);
            if (isPlayer) {
                assignPlayer();
            }
        }
        automatic = isAssigned && !isPlayer;
    }

}

package main.model;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableValue;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public abstract class Station {

    protected BooleanProperty inUse;
    protected boolean automatic;
    protected Location location;
    protected Job.Skill skill;
    private ObjectProperty<Employee> operator;
    private DoubleProperty progress;
    private DoubleProperty sellValue;
    private ObservableDoubleValue operatorSpeed;
    private ObservableDoubleValue speed;
    private double baseSpeed;

    public Station(double baseSpeed, double sellValue) {
        operator = new SimpleObjectProperty<>(Employee.UNASSIGNED);
        operator.addListener((obs, oldValue, newValue) -> setOperatorImpl(oldValue, newValue));
        this.baseSpeed = baseSpeed;
        setOperatorSpeed(operator);
        progress = new SimpleDoubleProperty();
        this.sellValue = new SimpleDoubleProperty(sellValue);
        inUse = new SimpleBooleanProperty();
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

    protected abstract void unassignPlayer();

    protected abstract void assignPlayer();

    protected abstract boolean canBegin();

    public void attemptToBegin() {
        if (canBegin()) {
            begin();
        }
    }

    public void begin() {
        setInUse(true);
        setProgress(0);
    }

    public void finish() {
        setInUse(false);
        setProgress(0);
    }

    public void update() {
        if (isInUse() && getOperator() != Employee.UNASSIGNED) {
            setProgress(getProgress() + getSpeed() / 60);
            if (getProgress() >= 1) {
                finish();
            }
        } else if (!isInUse() && automatic) {
            attemptToBegin();
        }
    }

    public double getProgress() {
        return progress.get();
    }

    private double getSpeed() {
        return speed.get();
    }

    public void setProgress(double progress) {
        this.progress.set(progress);
    }

    public boolean isInUse() {
        return inUse.get();
    }

    private void setInUse(boolean inUse) {
        this.inUse.set(inUse);
    }

    public final BooleanProperty inUseProperty() {
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

    public void setOperator(Employee operator) {
        this.operator.set(operator);
    }

    public final ObjectProperty<Employee> operatorProperty() {
        return operator;
    }

    public final DoubleProperty progressProperty() {
        return progress;
    }

    public double getSellValue() {
        return sellValue.get();
    }

    public final DoubleProperty sellValueProperty() {
        return sellValue;
    }

    public Job.Skill getSkill() {
        return skill;
    }

    private double getOperatorSpeed() {
        return operatorSpeed.get();
    }

    public void setOperatorSpeed(ObservableValue<Employee> newOperator) {
        DoubleBinding operatorSpeed = Bindings.createDoubleBinding(
                () -> newOperator.getValue().getSkill(skill),
                newOperator, newOperator.getValue().jobProperty());
        this.operatorSpeed = operatorSpeed;
        speed = operatorSpeed.multiply(baseSpeed);
    }

    private void unassign() {
        operator.set(Employee.UNASSIGNED);
    }

}

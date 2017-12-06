package main.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import main.ui.UILinker;

import java.util.Optional;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public class Employee {

    public static final Employee PLAYER = new Employee("You", Job.FOUNDER, Location.NONE);
    public static final Employee UNASSIGNED = new Employee("Unassigned", Job.NONE, Location.NONE);

    private StringProperty name;
    private ObjectProperty<Job> job;
    private ObjectProperty<Location> location;
    private StringProperty pay;
    private Station station;

    public Employee(String name, Job job, Location location) {
        this.name = new SimpleStringProperty(name);
        this.job = new SimpleObjectProperty<>(job);
        this.location = new SimpleObjectProperty<>(location);
        this.pay = new SimpleStringProperty(UILinker.asMoney(job.WAGE));
        this.job.addListener((obs, oldValue, newValue) -> pay.set(UILinker.asMoney(newValue.WAGE)));
        if (location != Location.NONE) {
            location.updateTotalWages(job.WAGE);
        }
    }

    public void promote() {
        assert isPromotable();
        location.get().updateTotalWages(job.get().SUPERIOR.WAGE - job.get().WAGE);
        job.set(job.get().SUPERIOR);
        getStation().ifPresent(s -> s.setOperatorSpeed(s.operatorProperty()));
    }

    public double getSkill(Job.Skill skill) {
        return job.get().getSkill(skill);
    }

    public boolean isPromotable() {
        return job.get().SUPERIOR != null;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public Job getJob() {
        return job.get();
    }

    public ObjectProperty<Job> jobProperty() {
        return job;
    }

    public Location getLocation() {
        return location.get();
    }

    public ObjectProperty<Location> locationProperty() {
        return location;
    }

    public void setLocation(Location location) {
        this.location.set(location);
    }

    public String getPay() {
        return pay.get();
    }

    public StringProperty payProperty() {
        return pay;
    }

    public Optional<Station> getStation() {
        return Optional.ofNullable(this.station);
    }

    public void setStation(Station newStation) {
        this.station = newStation;
    }

    public void unsetStation() {
        this.station = null;
    }

    public String toString() {
        return name.get();
    }

}

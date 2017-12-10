package main.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import main.model.station.Station;
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
        this.pay = new SimpleStringProperty(UILinker.asMoney(job.getWage()));
        this.job.addListener((obs, oldValue, newValue) -> pay.set(UILinker.asMoney(newValue.getWage())));
        if (location != Location.NONE) {
            location.updateTotalWages(job.getWage());
        }
    }

    public void promote() {
        assert isPromotable();
        location.get().updateTotalWages(job.get().getSuperior().getWage() - job.get().getWage());
        setJob(getJob().getSuperior());
        getStation().ifPresent(s -> s.setOperatorSpeed(s.operatorProperty()));
    }

    public boolean isPromotable() {
        return job.get().getSuperior() != null;
    }

    public Optional<Station> getStation() {
        return Optional.ofNullable(this.station);
    }

    public void setStation(Station newStation) {
        this.station = newStation;
    }

    public double getSkill(Job.Skill skill) {
        return job.get().getSkill(skill);
    }

    public String getName() {
        return name.get();
    }

    public final StringProperty nameProperty() {
        return name;
    }

    public Job getJob() {
        return job.get();
    }

    public final ObjectProperty<Job> jobProperty() {
        return job;
    }

    public Location getLocation() {
        return location.get();
    }

    public void setLocation(Location location) {
        this.location.set(location);
    }

    public final ObjectProperty<Location> locationProperty() {
        return location;
    }

    public String getPay() {
        return pay.get();
    }

    public final StringProperty payProperty() {
        return pay;
    }

    public void unsetStation() {
        this.station = null;
    }

    public String toString() {
        return name.get();
    }

    private void setJob(Job job) {
        this.job.set(job);
    }

}

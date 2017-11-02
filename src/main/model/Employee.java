package main.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public class Employee {

    public static final Employee PLAYER = new Employee("You", Job.FOUNDER);
    public static final Employee UNASSIGNED = new Employee("Unassigned", Job.NONE);

    private StringProperty name;
    private ObjectProperty<Job> job;

    public Employee(String name, Job job) {
        this.name = new SimpleStringProperty(name);
        this.job = new SimpleObjectProperty<>(job);
    }

    public void promote() {
        assert isPromotable();
        job.set(job.get().SUPERIOR);
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

    public void setJob(Job job) {
        this.job.set(job);
    }

    public String toString() {
        if (job.get() == Job.NONE) {
            return name.get();
        } else {
            return String.format("%s (%s)", name.get(), job.get());
        }
    }

}

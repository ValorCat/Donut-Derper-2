package main.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Set;

import static javafx.collections.FXCollections.observableArrayList;
import static main.model.Job.Skill.*;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public class Job {

    private static final Job JOB_TREE;
    private static final ListProperty<Job> ENTRY_LEVEL_JOBS;

    public static final Job FOUNDER = new Job("Founder", 0, null, Set.of(values()));
    public static final Job NONE = new Job("--", 0, null, Set.of(values()));

    static {
        final Set<Skill> CAN_FRY_AND_SELL = Set.of(USE_FRYER, USE_REGISTER);

        Job assistMan = new Job("Assistant Manager", 20, null, CAN_FRY_AND_SELL);
        Job snrCook = new Job("Senior Fry Cook", 17, assistMan, CAN_FRY_AND_SELL);
        Job cook = new Job("Fry Cook", 14, snrCook, USE_FRYER);
        Job jnrCook = new Job("Junior Fry Cook", 10, cook, USE_FRYER);
        Job cashier = new Job("Cashier", 12, assistMan, USE_REGISTER);

        JOB_TREE = assistMan;
        ENTRY_LEVEL_JOBS = new SimpleListProperty<>(observableArrayList(List.of(cashier, jnrCook)));
    }

    public enum Skill { USE_FRYER, USE_REGISTER }

    public final String NAME;
    public final Job SUPERIOR;
    public final Set<Skill> SKILLS;
    public final double WAGE;

    public Job(String name, double wage, Job superior, Skill skill) {
        this(name, wage, superior, Set.of(skill));
    }

    public Job(String name, double wage, Job superior, Set<Skill> skills) {
        this.NAME = name;
        this.SKILLS = skills;
        this.SUPERIOR = superior;
        this.WAGE = wage;
    }

    public String toString() {
        return NAME;
    }

    public static Job getJobTree() {
        return JOB_TREE;
    }

    public static ObservableList<Job> getEntryLevelJobs() {
        return ENTRY_LEVEL_JOBS.get();
    }

    public static ListProperty<Job> entryLevelJobsProperty() {
        return ENTRY_LEVEL_JOBS;
    }

}

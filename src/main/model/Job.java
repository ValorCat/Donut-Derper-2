package main.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static javafx.collections.FXCollections.observableArrayList;
import static main.model.Job.Skill.USE_FRYER;
import static main.model.Job.Skill.USE_REGISTER;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public class Job {

    private static final ListProperty<Job> ENTRY_LEVEL_JOBS;

    public static final Job FOUNDER = new Job("Founder", 0, null,
            skills(USE_FRYER, 1, USE_REGISTER, 1));
    public static final Job NONE = new Job("--", 0, null, skills());

    static {
        Job mngr = new Job("Branch Manager", 15, null, skills(USE_FRYER, .35, USE_REGISTER, 1));
        Job assistMan = new Job("Assistant Manager", 10, mngr, skills(USE_FRYER, .5, USE_REGISTER, .7));
        Job snrCook = new Job("Senior Fry Cook", 8, assistMan, skills(USE_FRYER, .7, USE_REGISTER, .2));
        Job cook = new Job("Fry Cook", 5, snrCook, skills(USE_FRYER, .45));
        Job jnrCook = new Job("Junior Fry Cook", 2, cook, skills(USE_FRYER, .2));
        Job snrCashier = new Job("Senior Cashier", 8, assistMan, skills(USE_REGISTER, .5));
        Job cashier = new Job("Cashier", 4, snrCashier, skills(USE_REGISTER, .3));

        ENTRY_LEVEL_JOBS = new SimpleListProperty<>(observableArrayList(List.of(cashier, jnrCook)));
    }

    public enum Skill { USE_FRYER, USE_REGISTER }

    public final String NAME;
    public final Job SUPERIOR;
    public final Map<Skill,Double> SKILLS;
    public final double WAGE;

    public Job(String name, double wage, Job superior, Map<Skill,Double> skills) {
        this.NAME = name;
        this.SUPERIOR = superior;
        this.SKILLS = skills;
        this.WAGE = wage;
    }

    public boolean hasSkill(Skill skill) {
        return getSkill(skill) > 0;
    }

    public boolean isAssignable(Skill skill) {
        return this == Job.NONE || hasSkill(skill);
    }

    public double getSkill(Skill skill) {
        return SKILLS.getOrDefault(skill, 0d);
    }

    public String toString() {
        return NAME;
    }

    public static ObservableList<Job> getEntryLevelJobs() {
        return ENTRY_LEVEL_JOBS.get();
    }

    public static ListProperty<Job> entryLevelJobsProperty() {
        return ENTRY_LEVEL_JOBS;
    }

    private static Map<Skill,Double> skills(Object... values) {
        Map<Skill,Double> skills = new EnumMap<>(Skill.class);
        for (int i = 0; i < values.length; i += 2) {
            Skill skill = (Skill) values[i];
            double level;
            if (values[i + 1] instanceof Double) {
                level = (double) values[i + 1];
            } else {
                level = (double) (int) values[i + 1];
            }
            skills.put(skill, level);
        }
        return skills;
    }

}

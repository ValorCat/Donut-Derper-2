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

    public static final Job FOUNDER = new Job("Founder", 0, null,
            skills(USE_FRYER, 1, USE_REGISTER, 1));
    public static final Job NONE = new Job("--", 0, null, skills());
    private static final ListProperty<Job> ENTRY_LEVEL_JOBS;

    static {
        Job mngr = new Job("Branch Manager", 15, null, skills(USE_FRYER, .5, USE_REGISTER, 1));
        Job assistMan = new Job("Assistant Manager", 10, mngr, skills(USE_FRYER, .6, USE_REGISTER, .7));
        Job snrCook = new Job("Senior Fry Cook", 8, assistMan, skills(USE_FRYER, .9, USE_REGISTER, .2));
        Job cook = new Job("Fry Cook", 5, snrCook, skills(USE_FRYER, .6));
        Job jnrCook = new Job("Junior Fry Cook", 2, cook, skills(USE_FRYER, .4));
        Job snrCashier = new Job("Senior Cashier", 8, assistMan, skills(USE_REGISTER, .5));
        Job cashier = new Job("Cashier", 4, snrCashier, skills(USE_REGISTER, .3));

        ENTRY_LEVEL_JOBS = new SimpleListProperty<>(observableArrayList(List.of(cashier, jnrCook)));
    }

    private String name;
    private Job superior;
    private Map<Skill,Double> skills;
    private double wage;

    public Job(String name, double wage, Job superior, Map<Skill,Double> skills) {
        this.name = name;
        this.superior = superior;
        this.skills = skills;
        this.wage = wage;
    }

    public String getName() {
        return name;
    }

    public Job getSuperior() {
        return superior;
    }

    public double getWage() {
        return wage;
    }

    public boolean isAssignable(Skill skill) {
        return this == NONE || getSkill(skill) > 0;
    }

    public double getSkill(Skill skill) {
        return skills.getOrDefault(skill, 0d);
    }

    public String toString() {
        return name;
    }

    public static ObservableList<Job> getEntryLevelJobs() {
        return ENTRY_LEVEL_JOBS.get();
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

    public enum Skill { USE_FRYER, USE_REGISTER }

}

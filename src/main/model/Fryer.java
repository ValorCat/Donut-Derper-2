package main.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public class Fryer extends Station {

    public static final Fryer INITIAL = new Fryer(0.6, 1, 0);
    public static final String OUTPUT_FORMAT = "%d / %d %s Donuts";

    private ObjectProperty<DonutType> donutType;
    private IntegerProperty maxDonutOutput;
    private IntegerProperty currentDonutOutput;

    public Fryer(double baseSpeed, int donutsPerBatch, double sellValue) {
        super(baseSpeed, sellValue);
        donutType = new SimpleObjectProperty<>(DonutType.PLAIN);
        maxDonutOutput = new SimpleIntegerProperty(donutsPerBatch);
        currentDonutOutput = new SimpleIntegerProperty();
        skill = Job.Skill.USE_FRYER;
    }

    protected boolean canBegin() {
        return getDonutType().getRecipe().stream()
                .allMatch(recipePart -> location.hasIngredient(recipePart));
    }

    public void begin() {
        super.begin();
        setCurrentDonutOutput(computeBatchSize());
        for (IngredientBatch recipePart : getDonutType().getRecipe()) {
            location.removeIngredient(recipePart.times(getCurrentDonutOutput()));
        }
    }

    public void finish() {
        super.finish();
        location.addDonuts(new DonutBatch(getDonutType(), getMaxDonutOutput()));
        setCurrentDonutOutput(0);
    }

    public void assignPlayer() {
        location.getFryers().assignToPlayer(this);
    }

    public void unassignPlayer() {
        location.getFryers().unassignPlayer();
    }

    public DonutType getDonutType() {
        return donutType.get();
    }

    public ObjectProperty<DonutType> donutTypeProperty() {
        return donutType;
    }

    public int getMaxDonutOutput() {
        return maxDonutOutput.get();
    }

    public IntegerProperty maxDonutOutputProperty() {
        return maxDonutOutput;
    }

    public int getCurrentDonutOutput() {
        return currentDonutOutput.get();
    }

    public final IntegerProperty currentDonutOutputProperty() {
        return currentDonutOutput;
    }

    private void setCurrentDonutOutput(int currentDonutOutput) {
        this.currentDonutOutput.set(currentDonutOutput);
    }

    private int computeBatchSize() {
        int actualSize = getMaxDonutOutput();
        for (IngredientBatch recipeItem : getDonutType().getRecipe()) {
            int amountNeeded = recipeItem.getAmount() * actualSize;
            int amountInStock = location.getIngredientAmount(recipeItem.getType());
            actualSize = Math.min(actualSize, amountInStock / amountNeeded);
        }
        return actualSize;
    }

}

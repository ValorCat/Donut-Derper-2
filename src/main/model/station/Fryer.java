package main.model.station;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import main.model.Job;
import main.model.Location;
import main.model.donut.DonutBatch;
import main.model.donut.DonutType;
import main.model.ingredient.IngredientBatch;
import main.model.ingredient.IngredientInventory;

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
    private IngredientInventory ingredients;

    public Fryer(double baseSpeed, int donutsPerBatch, double sellValue) {
        super(baseSpeed, sellValue);
        donutType = new SimpleObjectProperty<>(DonutType.PLAIN);
        maxDonutOutput = new SimpleIntegerProperty(donutsPerBatch);
        currentDonutOutput = new SimpleIntegerProperty();
        skill = Job.Skill.USE_FRYER;
    }

    protected boolean canBegin() {
        return getDonutType().getRecipe().stream().allMatch(ingredients::hasAtLeast);
    }

    public void begin() {
        super.begin();
        setCurrentDonutOutput(computeBatchSize());
        ingredients.remove(getDonutType().getRecipe(), getCurrentDonutOutput());
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

    private DonutType getDonutType() {
        return donutType.get();
    }

    public ObjectProperty<DonutType> donutTypeProperty() {
        return donutType;
    }

    private int getMaxDonutOutput() {
        return maxDonutOutput.get();
    }

    public IntegerProperty maxDonutOutputProperty() {
        return maxDonutOutput;
    }

    private int getCurrentDonutOutput() {
        return currentDonutOutput.get();
    }

    public final IntegerProperty currentDonutOutputProperty() {
        return currentDonutOutput;
    }

    @Override
    public void setLocation(Location location) {
        super.setLocation(location);
        ingredients = location.getIngredients();
    }

    private void setCurrentDonutOutput(int currentDonutOutput) {
        this.currentDonutOutput.set(currentDonutOutput);
    }

    private int computeBatchSize() {
        int actualSize = getMaxDonutOutput();
        for (IngredientBatch recipeItem : getDonutType().getRecipe()) {
            int amountNeeded = recipeItem.getAmount() * actualSize;
            int amountInStock = ingredients.getAmount(recipeItem.getType());
            actualSize = Math.min(actualSize, amountInStock / amountNeeded);
        }
        return actualSize;
    }

}

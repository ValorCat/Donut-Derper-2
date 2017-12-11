package main.model.station;

import javafx.beans.property.*;
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

    public static final Fryer INITIAL = new Fryer(0.15, 4, 0);
    public static final String OUTPUT_FORMAT = "%d / %d %s Donuts";
    public static final String DESCRIPTION
            = "Consumes recipe ingredients and produces %d donuts/batch while operated by a fry cook.";

    private ObjectProperty<DonutType> donutType;
    private IntegerProperty maxDonutOutput;
    private IntegerProperty currentDonutOutput;
    private BooleanProperty isMissingIngredients;
    private IngredientInventory ingredients;

    public Fryer(double baseSpeed, int donutsPerBatch, double sellValue) {
        super(baseSpeed, sellValue);
        donutType = new SimpleObjectProperty<>(DonutType.PLAIN);
        maxDonutOutput = new SimpleIntegerProperty(donutsPerBatch);
        currentDonutOutput = new SimpleIntegerProperty();
        isMissingIngredients = new SimpleBooleanProperty();
        skill = Job.Skill.USE_FRYER;
    }

    protected boolean canBegin() {
        boolean hasAllIngredients = getDonutType().getRecipe().stream().allMatch(ingredients::hasAtLeast);
        setIsMissingIngredients(!hasAllIngredients);
        return hasAllIngredients;
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

    public final BooleanProperty isMissingIngredientsProperty() {
        return isMissingIngredients;
    }

    private void setIsMissingIngredients(boolean isMissingIngredients) {
        this.isMissingIngredients.set(isMissingIngredients);
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

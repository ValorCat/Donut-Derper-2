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
    public static final String OUTPUT_FORMAT = "%d %s Donut(s)";

    private ObjectProperty<DonutType> outputType;
    private IntegerProperty outputAmount;

    public Fryer(double baseSpeed, int donutsPerBatch, double sellValue) {
        super(baseSpeed, sellValue);
        outputType = new SimpleObjectProperty<>(DonutType.PLAIN);
        outputAmount = new SimpleIntegerProperty(donutsPerBatch);
        skill = Job.Skill.USE_FRYER;
    }

    public void finish() {
        super.finish();
        location.addDonuts(new DonutBatch(getOutputType(), getOutputAmount()));
    }

    public void update() {
        super.update();
        if (!isInUse() && automatic) {
            inUse.set(true);
        }
    }

    public void assignPlayer() {
        location.getFryers().assignToPlayer(this);
    }

    public void unassignPlayer() {
        location.getFryers().unassignPlayer();
    }

    public DonutType getOutputType() {
        return outputType.get();
    }

    public ObjectProperty<DonutType> outputTypeProperty() {
        return outputType;
    }

    public int getOutputAmount() {
        return outputAmount.get();
    }

    public IntegerProperty outputAmountProperty() {
        return outputAmount;
    }

}

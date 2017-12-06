package main.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public class Fryer extends Station {

    public static final Fryer INITIAL = new Fryer(0.6, 1, 0);
    public static final String OUTPUT_FORMAT = "%d %s Donut(s)";

    // output must be an ObservableValue, and not an ObjectProperty, for its
    // binding to work in the constructor
    private ObservableValue<DonutType> output;
    private ObjectProperty<DonutTypeDescription> outputType;
    private IntegerProperty outputAmount;

    public Fryer(double baseSpeed, int donutsPerBatch, double sellValue) {
        super(baseSpeed, sellValue);
        outputType = new SimpleObjectProperty<>(DonutType.PLAIN);
        outputAmount = new SimpleIntegerProperty(donutsPerBatch);
        output = Bindings.createObjectBinding(
                () -> new DonutType(getOutputType(), getOutputAmount()),
                outputType, outputAmount);
        skill = Job.Skill.USE_FRYER;
    }

    public void finish() {
        super.finish();
        location.updateDonuts(getOutput());
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

    public DonutTypeDescription getOutputType() {
        return outputType.get();
    }

    public ObjectProperty<DonutTypeDescription> outputTypeProperty() {
        return outputType;
    }

    public int getOutputAmount() {
        return outputAmount.get();
    }

    public IntegerProperty outputAmountProperty() {
        return outputAmount;
    }

    private DonutType getOutput() {
        return new DonutType(output.getValue());
    }



}

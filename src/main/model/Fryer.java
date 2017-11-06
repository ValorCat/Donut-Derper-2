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

    public static final Fryer INITIAL = new Fryer(0.8, 2, 0);
    public static final String OUTPUT_FORMAT = "%d %s Donut(s)";

    // output must be an ObservableValue, and not an ObjectProperty, for its
    // binding to work in the constructor
    private ObservableValue<DonutType> output;
    private ObjectProperty<DonutTypeDescription> outputType;
    private IntegerProperty outputAmount;

    public Fryer(double speed, int donutsPerBatch, double sellValue) {
        super(speed, sellValue);
        this.outputType = new SimpleObjectProperty<>(DonutType.PLAIN);
        this.outputAmount = new SimpleIntegerProperty(donutsPerBatch);
        this.output = Bindings.createObjectBinding(
                () -> new DonutType(outputType.get(), outputAmount.get()),
                outputType, outputAmount);
    }

    public void finish() {
        super.finish();
        location.updateDonuts(getOutput());
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

    public void setOutputType(DonutTypeDescription outputType) {
        this.outputType.set(outputType);
    }

    public int getOutputAmount() {
        return outputAmount.get();
    }

    public IntegerProperty outputAmountProperty() {
        return outputAmount;
    }

    public void setOutputAmount(int outputAmount) {
        this.outputAmount.set(outputAmount);
    }

    private DonutType getOutput() {
        return new DonutType(output.getValue());
    }

}

package main.model;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

/**
 * @author Anthony Morrell
 * @since 10/29/2017
 */
public class Fryer extends Appliance {

    public static final Fryer INITIAL = new Fryer(1, 0);
    private static final String OUTPUT_FORMAT = "%d %s Donut(s)";

    // output must be an ObservableValue, and not an ObjectProperty, for its
    // binding to work in the constructor
    private ObservableValue<DonutType> output;
    private ObjectProperty<DonutTypeDescription> outputType;
    private IntegerProperty outputAmount;

    public Fryer(int donutsPerBatch, double sellValue) {
        super(sellValue);
        this.outputType = new SimpleObjectProperty<>(DonutType.PLAIN);
        this.outputAmount = new SimpleIntegerProperty(donutsPerBatch);
        this.output = Bindings.createObjectBinding(
                () -> new DonutType(outputType.get(), outputAmount.get()),
                outputType, outputAmount);
    }

    public void initialize(TitledPane pane) {
        ObservableList<Node> header = ((Pane) pane.getGraphic()).getChildren();
        ObservableList<Node> data = ((Pane) pane.getContent()).getChildren();

        Label operatorName = (Label) (header.get(0));
        ProgressBar cookTimer = (ProgressBar) ((Labeled) (header.get(0))).getGraphic();
        Label donutOutput = (Label) (header.get(1));
        Button sellButton = (Button) data.get(3);

        // suppress the (unavoidable?) warning on the cast from Node -> ChoiceBox<Employee>
        @SuppressWarnings("unchecked") ChoiceBox<Employee> operator
                = (ChoiceBox<Employee>) ((Labeled) data.get(0)).getGraphic();
        @SuppressWarnings("unchecked") ChoiceBox<DonutTypeDescription> outputType
                = (ChoiceBox<DonutTypeDescription>) ((Labeled) data.get(1)).getGraphic();

        operatorName.textProperty().bind(getOperatorBinding());
        cookTimer.progressProperty().bind(progressProperty());
        donutOutput.textProperty().bind(getOutputBinding());
        operator.itemsProperty().bind(getPossibleOperatorsBinding());
        operator.valueProperty().bindBidirectional(operatorProperty());
        outputType.itemsProperty().bind(DonutType.DONUT_TYPES);
        outputType.valueProperty().bindBidirectional(outputTypeProperty());
        sellButton.textProperty().bind(getSellTextBinding());
    }

    public void operate() {
        Game.location().updateDonuts(getOutput());
    }

    public DonutTypeDescription getOutputType() {
        return outputType.get();
    }

    public StringBinding getOutputBinding() {
        return new StringBinding() {
            {
                super.bind(outputType, outputAmount);
            }
            protected String computeValue() {
                return String.format(OUTPUT_FORMAT, outputAmount.get(), outputType.get());
            }
        };
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

    public DonutType getOutput() {
        return new DonutType(output.getValue());
    }

}

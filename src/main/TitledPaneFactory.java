package main;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import main.model.*;

import java.io.IOException;

/**
 * @author Anthony Morrell
 * @since 11/2/2017
 */
public final class TitledPaneFactory {

    private TitledPaneFactory() {}

    public static TitledPane buildAppliancePane(Appliance app) {
        if (app instanceof Fryer) {
            return buildFryerPane((Fryer) app);
        } else if (app instanceof CashRegister) {
            return buildRegisterPane((CashRegister) app);
        } else {
            throw new IllegalArgumentException("Unrecognized appliance type: " + app.getClass().getName());
        }
    }

    private static TitledPane buildFryerPane(Fryer fryer) {
        // cook timer
        ProgressBar cookTimer = new ProgressBar();
        cookTimer.progressProperty().bind(fryer.progressProperty());
        cookTimer.setPrefWidth(150);

        // operator name
        Label cookName = new Label("", cookTimer);
        cookName.textProperty().bind(fryer.getOperatorBinding());
        cookName.setGraphicTextGap(8);
        cookName.setContentDisplay(ContentDisplay.RIGHT);
        setAnchors(cookName, 0d, 0d, 0d, null);

        // donut output
        Label output = new Label();
        output.textProperty().bind(fryer.getOutputBinding());
        setAnchors(output, 0d, 0d, null, 0d);

        // operator selector
        ChoiceBox<Employee> cookSelect = new ChoiceBox<>();
        cookSelect.itemsProperty().bind(fryer.getPossibleOperatorsBinding());
        cookSelect.valueProperty().bindBidirectional(fryer.operatorProperty());
        Label cook = new Label("Fry Cook:", cookSelect);
        cook.setGraphicTextGap(8);
        cook.setContentDisplay(ContentDisplay.RIGHT);

        // donut type selector
        ChoiceBox<DonutTypeDescription> typeSelect = new ChoiceBox<>();
        typeSelect.itemsProperty().bind(DonutType.DONUT_TYPES);
        typeSelect.valueProperty().bindBidirectional(fryer.outputTypeProperty());
        Label type = new Label("Output:", typeSelect);
        type.setGraphicTextGap(8);
        type.setContentDisplay(ContentDisplay.RIGHT);

        // description
        Label description = new Label("Produces 1 donut every 3 seconds while operated by a fry cook.");
        description.setPrefWidth(420);
        description.setWrapText(true);

        // sell button
        Button sellButton = new Button();
        sellButton.textProperty().bind(fryer.getSellTextBinding());

        AnchorPane header = new AnchorPane(cookName, output);
        header.setPrefSize(407, 20);
        VBox body = new VBox(7, cook, type, description, sellButton);

        TitledPane pane = new TitledPane("", body);
        pane.setGraphic(header);
        return pane;
    }

    private static TitledPane buildRegisterPane(CashRegister register) {
        // checkout timer
        ProgressBar checkoutTimer = new ProgressBar();
        checkoutTimer.progressProperty().bind(register.progressProperty());
        checkoutTimer.setPrefWidth(150);

        // operator name
        Label cashierName = new Label("", checkoutTimer);
        cashierName.textProperty().bind(register.getOperatorBinding());
        cashierName.setGraphicTextGap(8);
        cashierName.setContentDisplay(ContentDisplay.RIGHT);
        setAnchors(cashierName, 0d, 0d, 0d, null);

        // collect button
        Button collectButton = new Button("Collect");
        collectButton.setOnAction(a -> register.collect());
        collectButton.setFont(Font.font(9));

        // balance
        Label balance = new Label("", collectButton);
        balance.textProperty().bind(register.getBalanceBinding());
        balance.setGraphicTextGap(8);
        balance.setContentDisplay(ContentDisplay.RIGHT);
        setAnchors(balance, 0d, 0d, null, -8d);

        // operator selector
        ChoiceBox<Employee> cashierSelect = new ChoiceBox<>();
        cashierSelect.itemsProperty().bind(register.getPossibleOperatorsBinding());
        cashierSelect.valueProperty().bindBidirectional(register.operatorProperty());
        Label cashier = new Label("Cashier:", cashierSelect);
        cashier.setGraphicTextGap(8);
        cashier.setContentDisplay(ContentDisplay.RIGHT);

        // description
        Label description = new Label("Checks out one customer per second while operated by a cashier.");
        description.setPrefWidth(420);
        description.setWrapText(true);

        // sell button
        Button sellButton = new Button();
        sellButton.textProperty().bind(register.getSellTextBinding());

        AnchorPane header = new AnchorPane(cashierName, balance);
        header.setPrefSize(407, 20);
        VBox body = new VBox(7, cashier, description, sellButton);

        TitledPane pane = new TitledPane("", body);
        pane.setGraphic(header);
        return pane;
    }

    private static void setAnchors(Node node, Double top, Double bottom, Double left, Double right) {
        if (top != null)
            AnchorPane.setTopAnchor(node, top);
        if (bottom != null)
            AnchorPane.setBottomAnchor(node, bottom);
        if (left != null)
            AnchorPane.setLeftAnchor(node, left);
        if (right != null)
            AnchorPane.setRightAnchor(node, right);
    }

}

package main;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import main.model.*;

import static main.UILinker.*;

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
        // todo move repeated code in buildRegisterPane into buildAppliancePane

        // cook timer
        ProgressBar cookTimer = new ProgressBar();
        cookTimer.setPrefWidth(150);
        link(cookTimer.progressProperty(), getProgress(fryer));

        // operator name
        Label cookName = new Label("", cookTimer);
        cookName.setGraphicTextGap(8);
        cookName.setContentDisplay(ContentDisplay.RIGHT);
        setAnchors(cookName, 0d, 0d, 0d, null);
        linkText(cookName, getOperatorName(fryer));

        // donut output
        Label output = new Label();
        setAnchors(output, 0d, 0d, null, 0d);
        linkText(output, getOutputText(fryer));

        // operator selector
        ChoiceBox<Employee> cookSelect = new ChoiceBox<>();
        Label cook = new Label("Fry Cook:", cookSelect);
        cook.setGraphicTextGap(8);
        cook.setContentDisplay(ContentDisplay.RIGHT);
        linkItems(cookSelect, getPossibleOperators(fryer));
        linkChoice(cookSelect, getOperator(fryer));

        // assign self button
        Button assignSelfButton = new Button("Assign Self");
        assignSelfButton.setOnAction(e -> fryer.setOperator(Employee.PLAYER));
        link(assignSelfButton.visibleProperty(), getAssignSelfVisible(fryer));

        // donut type selector
        ChoiceBox<DonutTypeDescription> typeSelect = new ChoiceBox<>();
        Label type = new Label("Output:", typeSelect);
        type.setGraphicTextGap(8);
        type.setContentDisplay(ContentDisplay.RIGHT);
        linkItems(typeSelect, DonutType.DONUT_TYPES);
        linkChoice(typeSelect, getOutputType(fryer));

        // description
        Label description = new Label("Produces 1 donut every 3 seconds while operated by a fry cook.");
        description.setPrefWidth(420);
        description.setWrapText(true);

        // sell button
        Button sellButton = new Button();
        link(sellButton.textProperty(), getSellButtonText(fryer));

        AnchorPane header = new AnchorPane(cookName, output);
        header.setPrefSize(407, 20);
        VBox body = new VBox(7, new HBox(10, cook, assignSelfButton), type, description, sellButton);

        TitledPane pane = new TitledPane("", body);
        pane.setGraphic(header);
        return pane;
    }

    private static TitledPane buildRegisterPane(CashRegister register) {
        // checkout timer
        ProgressBar checkoutTimer = new ProgressBar();
        checkoutTimer.setPrefWidth(150);
        link(checkoutTimer.progressProperty(), getProgress(register));

        // operator name
        Label cashierName = new Label("", checkoutTimer);
        cashierName.setGraphicTextGap(8);
        cashierName.setContentDisplay(ContentDisplay.RIGHT);
        setAnchors(cashierName, 0d, 0d, 0d, null);
        linkText(cashierName, getOperatorName(register));

        // collect button
        Button collectButton = new Button("Collect");
        collectButton.setOnAction(a -> register.collect());
        collectButton.setFont(Font.font(9));
        link(collectButton.disableProperty(), getCollectButtonDisable(register));

        // balance
        Label balance = new Label("", collectButton);
        balance.setGraphicTextGap(8);
        balance.setContentDisplay(ContentDisplay.RIGHT);
        setAnchors(balance, 0d, 0d, null, -8d);
        linkText(balance, getBalance(register));

        // operator selector
        ChoiceBox<Employee> cashierSelect = new ChoiceBox<>();
        Label cashier = new Label("Cashier:", cashierSelect);
        cashier.setGraphicTextGap(8);
        cashier.setContentDisplay(ContentDisplay.RIGHT);
        linkItems(cashierSelect, getPossibleOperators(register));
        linkChoice(cashierSelect, getOperator(register));

        // assign self button
        Button assignSelfButton = new Button("Assign Self");
        assignSelfButton.setOnAction(e -> register.setOperator(Employee.PLAYER));
        link(assignSelfButton.visibleProperty(), getAssignSelfVisible(register));

        // description
        Label description = new Label("Checks out one customer per second while operated by a cashier.");
        description.setPrefWidth(420);
        description.setWrapText(true);

        // sell button
        Button sellButton = new Button();
        link(sellButton.textProperty(), getSellButtonText(register));

        AnchorPane header = new AnchorPane(cashierName, balance);
        header.setPrefSize(407, 20);
        VBox body = new VBox(7, new HBox(10, cashier, assignSelfButton), description, sellButton);

        TitledPane pane = new TitledPane("", body);
        pane.setGraphic(header);
        return pane;
    }

    public static TitledPane buildAccountPane(Account account) {
        Label header = new Label();
        linkText(header, getAccountHeader(account));

        Label interest = new Label();
        linkText(interest, getInterest(account));

        VBox body = new VBox(7, interest);

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

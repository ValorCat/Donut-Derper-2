package main.ui;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import main.model.Account;
import main.model.Employee;
import main.model.donut.DonutType;
import main.model.station.CashRegister;
import main.model.station.Fryer;
import main.model.station.Station;

import static main.ui.Controller.*;
import static main.ui.UILinker.*;

/**
 * @author Anthony Morrell
 * @since 11/2/2017
 */
public final class TitledPaneFactory {

    private static final int HEADER_WIDTH = 310;
    private static final int HEADER_HEIGHT = 20;
    private static final int TIMER_WIDTH = 130;
    private static final int TIMER_HEIGHT = 12;
    private static final int TEXT_GAP = 8;
    private static final int BODY_SPACING = 7;
    private static final String FRYER_TIMER_COLOR = "darkorange";
    private static final String REGISTER_TIMER_COLOR = "forestgreen";
    private static final String REGISTER_FULL_COLOR = "forestgreen";
    private static final String ASSIGN_SELF = "< Assign Self";
    private static final String MISSING_INGREDIENTS = "Missing Ingredients";
    private static final Font COLLECT_FONT = Font.font(9);

    private TitledPaneFactory() {}

    public static TitledPane buildStationPane(Station sta) {
        TitledPane pane = new TitledPane();

        ProgressBar timer = new ProgressBar();
        timer.setPrefWidth(TIMER_WIDTH);
        timer.setMinHeight(TIMER_HEIGHT);
        timer.setMaxHeight(TIMER_HEIGHT);
        timer.setMouseTransparent(true);
        linkProgress(timer, getProgress(sta));
        link(timer.visibleProperty(), getTimerVisible(sta));

        Label operatorName = new Label("", timer);
        operatorName.setGraphicTextGap(TEXT_GAP);
        operatorName.setContentDisplay(ContentDisplay.RIGHT);
        setAnchors(operatorName, 0d, 0d, 0d, null);
        linkText(operatorName, getOperatorName(sta));

        ChoiceBox<Employee> operatorSelect = new ChoiceBox<>();
        operatorSelect.setCursor(Cursor.HAND);
        Label operator = new Label("", operatorSelect);
        operator.setGraphicTextGap(TEXT_GAP);
        operator.setContentDisplay(ContentDisplay.RIGHT);
        linkItems(operatorSelect, getPossibleOperators(sta));
        linkChoice(operatorSelect, getOperator(sta));

        Button assignSelfButton = new Button(ASSIGN_SELF);
        assignSelfButton.setOnAction(e -> sta.setOperator(Employee.PLAYER));
        assignSelfButton.setFont(Font.font(9));
        assignSelfButton.setCursor(Cursor.HAND);
        link(assignSelfButton.visibleProperty(), getAssignSelfVisible(sta));

        Label description = new Label();
        description.setWrapText(true);

        Button sellButton = new Button();
        sellButton.setCursor(Cursor.HAND);
        link(sellButton.textProperty(), getSellButtonText(sta));

        AnchorPane header = new AnchorPane(operatorName);
        header.setPrefSize(HEADER_WIDTH, HEADER_HEIGHT);
        HBox operatorRow = new HBox(TEXT_GAP, operator, assignSelfButton);
        operatorRow.setAlignment(Pos.CENTER_LEFT);
        VBox body = new VBox(BODY_SPACING, operatorRow, description, sellButton);
        pane.setContent(body);
        pane.setGraphic(header);

        if (sta instanceof Fryer) {
            setupFryer((Fryer) sta, header, body, timer, operator, description);
        } else if (sta instanceof CashRegister) {
            setupRegister((CashRegister) sta, header, timer, operator, description);
        }

        return pane;
    }

    private static void setupFryer(Fryer fryer, Pane header, VBox body, ProgressBar timer, Label operator, Label description) {
        setCSS(timer, "accent", FRYER_TIMER_COLOR);
        operator.setText("Fry Cook:");
        description.textProperty().bind(getFryerDescription(fryer));

        Label output = new Label();
        setAnchors(output, 0d, 0d, null, 0d);
        linkText(output, getOutputText(fryer));
        header.getChildren().add(output);

        ChoiceBox<DonutType> typeSelect = new ChoiceBox<>();
        typeSelect.setCursor(Cursor.HAND);
        Label type = new Label("Output:", typeSelect);
        type.setGraphicTextGap(TEXT_GAP);
        type.setContentDisplay(ContentDisplay.RIGHT);
        linkItems(typeSelect, DonutType.typesProperty());
        linkChoice(typeSelect, getOutputType(fryer));

        Label missingIngredients = new Label(MISSING_INGREDIENTS);
        missingIngredients.setStyle("-fx-text-fill: orangered");
        missingIngredients.visibleProperty().bind(fryer.isMissingIngredientsProperty());

        HBox outputRow = new HBox(12, type, missingIngredients);
        outputRow.setAlignment(Pos.CENTER_LEFT);
        body.getChildren().add(1, outputRow);
    }

    private static void setupRegister(CashRegister register, Pane header, ProgressBar timer, Label operator, Label description) {
        setCSS(timer, "accent", REGISTER_TIMER_COLOR);
        operator.setText("Cashier:");
        description.setText(getRegisterDescription(register));

        Button collectButton = new Button("Collect");
        collectButton.setOnAction(a -> register.collect());
        collectButton.setFont(COLLECT_FONT);
        collectButton.setCursor(Cursor.HAND);
        linkDisable(collectButton, getCollectButtonDisable(register));

        Label balance = new Label("", collectButton);
        balance.setGraphicTextGap(TEXT_GAP);
        balance.setContentDisplay(ContentDisplay.RIGHT);
        setAnchors(balance, 0d, 0d, null, -8d);
        linkText(balance, getBalance(register));
        onUpdate(register.balanceProperty(), bal -> setCSS(balance, "text-fill",
                bal.doubleValue() > 0 ? REGISTER_FULL_COLOR : DEFAULT_TEXT_COLOR));
        header.getChildren().add(balance);
    }

    public static TitledPane buildAccountPane(Account account) {
        Label header = new Label();
        linkText(header, getAccountHeader(account));

        Label interest = new Label();
        linkText(interest, getInterest(account));

        VBox body = new VBox(BODY_SPACING, interest);
        TitledPane pane = new TitledPane("", body);
        pane.setGraphic(header);
        return pane;
    }

    @SuppressWarnings("SameParameterValue")
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

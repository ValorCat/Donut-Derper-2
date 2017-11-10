package main;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import main.model.*;

import static main.UILinker.*;

/**
 * @author Anthony Morrell
 * @since 11/2/2017
 */
public final class TitledPaneFactory {

    private static final int HEADER_WIDTH = 407;
    private static final int HEADER_HEIGHT = 20;
    private static final int TIMER_WIDTH = 150;
    private static final int TIMER_HEIGHT = 12;
    private static final int TEXT_GAP = 8;
    private static final int BODY_SPACING = 7;
    private static final String ASSIGN_SELF = "Assign Self";
    private static final int DESCRIPTION_MAX_WIDTH = 420;
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
        assignSelfButton.setCursor(Cursor.HAND);
        link(assignSelfButton.visibleProperty(), getAssignSelfVisible(sta));

        Label description = new Label();
        description.setPrefWidth(DESCRIPTION_MAX_WIDTH);
        description.setWrapText(true);

        Button sellButton = new Button();
        sellButton.setCursor(Cursor.HAND);
        link(sellButton.textProperty(), getSellButtonText(sta));

        AnchorPane header = new AnchorPane(operatorName);
        header.setPrefSize(HEADER_WIDTH, HEADER_HEIGHT);
        VBox body = new VBox(BODY_SPACING, new HBox(TEXT_GAP, operator, assignSelfButton), description, sellButton);
        pane.setContent(body);
        pane.setGraphic(header);

        if (sta instanceof Fryer) {
            setupFryer((Fryer) sta, header, body, operator, description);
        } else if (sta instanceof CashRegister) {
            setupRegister((CashRegister) sta, header, operator, description);
        }

        return pane;
    }

    private static void setupFryer(Fryer fryer, Pane header, VBox body, Label operator, Label description) {
        operator.setText("Fry Cook:");
        description.setText("Produces 0 donuts/second while operated.");

        Label output = new Label();
        setAnchors(output, 0d, 0d, null, 0d);
        linkText(output, getOutputText(fryer));
        header.getChildren().add(output);

        ChoiceBox<DonutTypeDescription> typeSelect = new ChoiceBox<>();
        typeSelect.setCursor(Cursor.HAND);
        Label type = new Label("Output:", typeSelect);
        type.setGraphicTextGap(TEXT_GAP);
        type.setContentDisplay(ContentDisplay.RIGHT);
        linkItems(typeSelect, DonutType.DONUT_TYPES);
        linkChoice(typeSelect, getOutputType(fryer));
        body.getChildren().add(1, type);
    }

    private static void setupRegister(CashRegister register, Pane header, Label operator, Label description) {
        operator.setText("Cashier:");
        description.setText("Checks out 0 customers/second while operated.");

        Button collectButton = new Button("Collect");
        collectButton.setOnAction(a -> register.collect());
        collectButton.setFont(COLLECT_FONT);
        collectButton.setCursor(Cursor.HAND);
        link(collectButton.disableProperty(), getCollectButtonDisable(register));

        Label balance = new Label("", collectButton);
        balance.setGraphicTextGap(TEXT_GAP);
        balance.setContentDisplay(ContentDisplay.RIGHT);
        setAnchors(balance, 0d, 0d, null, -8d);
        linkText(balance, getBalance(register));
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

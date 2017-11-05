package main;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Accordion;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import main.model.*;

import java.text.NumberFormat;
import java.util.function.Consumer;
import java.util.function.Function;

import static javafx.beans.binding.Bindings.*;
import static javafx.collections.FXCollections.observableArrayList;

/**
 * @author Anthony Morrell
 * @since 11/4/2017
 */
public final class UILinker {

    private UILinker() {}

    public static <T> void link(Property<T> uiElement, ObservableValue<? extends T> source) {
        uiElement.bind(source);
    }

    public static void linkText(Label uiElement, StringExpression source) {
        link(uiElement.textProperty(), source);
    }

    public static <T> void linkItems(ChoiceBox<T> uiElement, ObservableValue<ObservableList<T>> source) {
        link(uiElement.itemsProperty(), source);
    }

    public static <T> void linkChoice(ChoiceBox<T> uiElement, Property<T> modelElement) {
        linkChoice(uiElement, modelElement, null, (x) -> {});
    }

    public static <T> void linkChoice(ChoiceBox<T> uiElement, Property<T> modelElement, T initial, Consumer<T> action) {
        uiElement.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, curr) -> action.accept(curr));
        uiElement.valueProperty().bindBidirectional(modelElement);
        if (initial != null) {
            uiElement.setValue(initial);
        }
    }

    public static void linkPanes(Accordion uiElement, ObservableList<TitledPane> source) {
        ObservableList<TitledPane> panes = uiElement.getPanes();
        panes.clear();
        panes.addAll(source);
        source.addListener((ListChangeListener<TitledPane>) change -> update(panes, change));
    }

    public static StringExpression getAccountHeader(Account a) {
        return format("%s  |  %s", a.nameProperty(), wrap(a.balanceProperty(), UILinker::asMoney));
    }

    public static ObservableValue<ObservableList<Account>> getAccounts(Location l) {
        return l.accountsProperty();
    }

    public static BooleanBinding getAssignSelfVisible(Appliance a) {
        return a.operatorProperty().isEqualTo(Employee.UNASSIGNED);
    }

    public static StringBinding getBalance(CashRegister r) {
        return wrap(r.balanceProperty(), UILinker::asMoney);
    }

    public static BooleanBinding getCheckoutButtonDisable(Location l) {
        return l.getRegisters().playerHasApplianceProperty()
                .not()
                .or(l.customersProperty().isEqualTo(0));
    }

    public static BooleanBinding getCollectButtonDisable(CashRegister r) {
        return r.balanceProperty().isEqualTo(0);
    }

    public static StringExpression getCustomerCount(Location l) {
        return format("%d / %d", l.customersProperty(), l.maxCapacityProperty());
    }

    public static ObjectProperty<Account> getDepositAccount(Location l) {
        return l.depositAccountProperty();
    }

    public static BooleanBinding getFryButtonDisable(Location l) {
        return l.getFryers().playerHasApplianceProperty().not();
    }

    public static StringBinding getGrossDonuts() {
        return Game.game.grossDonutsProperty().asString();
    }

    public static StringExpression getInterest(Account a) {
        return format("Interest: %.2f%%  (+%s)",
                a.interestRateProperty().multiply(100),
                createStringBinding(
                        () -> asMoney(a.getBalance() * a.getInterestRate()),
                        a.balanceProperty(), a.interestRateProperty()));
    }

    public static ObjectProperty<Location> getLocation() {
        return Game.game.currentLocationProperty();
    }

    public static ObservableValue<ObservableList<Location>> getLocations() {
        return Game.game.locationsProperty();
    }

    public static ObjectProperty<Employee> getOperator(Appliance a) {
        return a.operatorProperty();
    }

    public static StringBinding getOperatorName(Appliance a) {
        return wrap(a.operatorProperty(), Employee::getName);
    }

    public static StringExpression getOutputText(Fryer f) {
        return format(Fryer.OUTPUT_FORMAT, f.outputAmountProperty(), f.outputTypeProperty());
    }

    public static ObjectProperty<DonutTypeDescription> getOutputType(Fryer f) {
        return f.outputTypeProperty();
    }

    public static ObjectBinding<ObservableList<Employee>> getPossibleOperators(Appliance a) {
        return createObjectBinding(() -> {
            ObservableList<Employee> options = observableArrayList(a.getLocation().getRoster());
            options.add(Employee.UNASSIGNED);
            return options;
        }, a.getLocation().rosterProperty());
    }

    public static DoubleProperty getProgress(Appliance a) {
        return a.progressProperty();
    }

    public static StringBinding getSellButtonText(Appliance a) {
        return wrap(a.sellValueProperty(), val -> "Sell - " + asMoney(val));
    }

    public static StringBinding getStockedDonuts(Location l) {
        return l.donutStockProperty().asString();
    }

    public static StringBinding getTotalBalance(Location l) {
        return wrap(l.totalBalanceProperty(), UILinker::asMoney);
    }

    private static String asMoney(Number amount) {
        return NumberFormat.getCurrencyInstance().format(amount);
    }

    private static <T> StringBinding wrap(ObservableValue<T> source, Function<T,String> wrapper) {
        return createStringBinding(() -> wrapper.apply(source.getValue()), source);
    }

    private static void update(ObservableList<TitledPane> panes, ListChangeListener.Change<? extends TitledPane> change) {
        while (change.next()) {
            if (change.wasPermutated()) {
                throw new RuntimeException("List permutation was unhandled at "
                        + change.getPermutation(change.getFrom()));
            } else if (change.wasUpdated()) {
                throw new RuntimeException("TitledPane was modified in list: "
                        + change.getList().get(change.getFrom()));
            } else {
                panes.removeAll(change.getRemoved());
                panes.addAll(change.getAddedSubList());
            }
        }
    }

}

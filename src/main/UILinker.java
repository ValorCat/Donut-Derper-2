package main;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.*;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import main.model.*;

import java.text.NumberFormat;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;

import static javafx.beans.binding.Bindings.*;

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

    public static void linkProgress(ProgressIndicator uiElement, ObservableDoubleValue source) {
        link(uiElement.progressProperty(), source);
    }

    public static <T> void linkItems(ChoiceBox<T> uiElement, ObservableValue<ObservableList<T>> source) {
        link(uiElement.itemsProperty(), source);
    }

    public static <T> void linkItems(TableView<T> uiElement, ObservableValue<ObservableList<T>> source) {
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

    public static BooleanBinding getAssignSelfVisible(Station s) {
        return s.operatorProperty().isNotEqualTo(Employee.PLAYER);
    }

    public static StringBinding getBalance(CashRegister r) {
        return wrap(r.balanceProperty(), UILinker::asMoney);
    }

    public static BooleanBinding getCheckoutButtonDisable(Location l) {
        return l.getRegisters().playerHasStationProperty().not()
                .or(l.getRegisters().playerStationInUseProperty())
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

    public static BooleanBinding getEmployeesSelected(TableView<Employee> employees) {
        return employees.getSelectionModel().selectedItemProperty().isNull();
    }

    public static BooleanBinding getFryButtonDisable(Location l) {
        return l.getFryers().playerHasStationProperty().not()
                .or(l.getFryers().playerStationInUseProperty());
    }

    public static StringBinding getGrossDonuts() {
        return Game.game.grossDonutsProperty().asString();
    }

    public static BooleanBinding getHireButtonDisable(ObjectProperty<Job> selected, Location l) {
        return createBooleanBinding(
                () -> l.getTotalBalance() < selected.get().WAGE,
                selected, l.totalBalanceProperty()
        );
    }

    public static StringBinding getHireButtonText(ObjectProperty<Job> selected) {
        return createStringBinding(
                () -> String.format("Hire - %s/pp", asMoney(selected.get().WAGE)),
                selected);
    }

    public static ListProperty<Job> getHireableJobs() {
        ObservableList<Job> jobs = Job.getEntryLevelJobs();
        SortedList<Job> sorted = jobs.sorted();
        return new SimpleListProperty<>(sorted);
    }

    public static StringExpression getInterest(Account a) {
        return format("Interest: %.2f%%  (%s)",
                a.interestRateProperty().multiply(100),
                createStringBinding(
                        () -> asMoneySigned(a.getInterest()),
                        a.balanceProperty(), a.interestRateProperty()));
    }

    public static ObjectProperty<Location> getLocation() {
        return Game.game.currentLocationProperty();
    }

    public static ObservableValue<ObservableList<Location>> getLocations() {
        return Game.game.locationsProperty();
    }

    public static ObjectProperty<Employee> getOperator(Station s) {
        return s.operatorProperty();
    }

    public static StringBinding getOperatorName(Station s) {
        return wrap(s.operatorProperty(), Employee::getName);
    }

    public static StringExpression getOutputText(Fryer f) {
        return format(Fryer.OUTPUT_FORMAT, f.outputAmountProperty(), f.outputTypeProperty());
    }

    public static ObjectProperty<DonutTypeDescription> getOutputType(Fryer f) {
        return f.outputTypeProperty();
    }

    public static ObservableValue<ObservableList<Employee>> getPossibleOperators(Station s) {
        FilteredList<Employee> list = s.getLocation().rosterProperty()
                .filtered(e -> e.getJob().SKILLS.contains(s.getSkill()));
        SortedList<Employee> sorted = list.sorted((o1, o2) -> {
            if (o1 == Employee.UNASSIGNED) {
                return Integer.MAX_VALUE;
            } else if (o2 == Employee.UNASSIGNED) {
                return Integer.MIN_VALUE;
            } else {
                return o1.getJob().NAME.compareTo(o2.getJob().NAME);
            }
        });
        return new SimpleListProperty<>(sorted);
    }

    public static DoubleProperty getProgress(Station s) {
        return s.progressProperty();
    }

    public static ListProperty<Employee> getRoster(Location loc) {
        FilteredList<Employee> roster = loc.getRoster().filtered(e -> e != Employee.UNASSIGNED);
        SortedList<Employee> sorted = roster.sorted(Comparator.comparing(Employee::getName));
        return new SimpleListProperty<>(sorted);
    }

    public static StringBinding getSellButtonText(Station s) {
        return wrap(s.sellValueProperty(), val -> "Sell - " + asMoney(val));
    }

    public static StringBinding getStockedDonuts(Location l) {
        return l.donutStockProperty().asString();
    }

    public static StringExpression getTimeToPayday() {
        return format("Payday in %d seconds", Account.payPeriodSecondsProperty());
    }

    public static BooleanBinding getTimerVisible(Station s) {
        return s.progressProperty().isNotEqualTo(0);
    }

    public static StringBinding getTotalBalance(Location l) {
        return wrap(l.totalBalanceProperty(), UILinker::asMoney);
    }

    public static StringBinding getTotalWages(Location l) {
        return wrap(l.totalWagesProperty(), amount -> "-" + asMoney(amount));
    }

    public static ObjectProperty<Account> getWageSourceAccount(Location l) {
        return l.wageSourceAccountProperty();
    }

    public static String asMoney(Number amount) {
        return NumberFormat.getCurrencyInstance().format(amount);
    }
    public static String asMoneySigned(Number amount) {
        String value = asMoney(amount);
        return value.startsWith("-") ? value : "+" + value;
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

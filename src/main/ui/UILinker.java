package main.ui;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.*;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.Game;
import main.model.Account;
import main.model.Employee;
import main.model.Job;
import main.model.Location;
import main.model.donut.DonutType;
import main.model.ingredient.IngredientBatch;
import main.model.station.CashRegister;
import main.model.station.Fryer;
import main.model.station.Station;

import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
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

    public static void linkDisable(Node uiElement, ObservableBooleanValue modelElement) {
        link(uiElement.disableProperty(), modelElement);
    }

    public static void linkColumns(TableView<?> table, String... properties) {
        for (int i = 0; i < properties.length; i++) {
            table.getColumns().get(i).setCellValueFactory(new PropertyValueFactory<>(properties[i]));
        }
    }

    public static void linkPanes(Accordion uiElement, ObservableList<TitledPane> source) {
        ObservableList<TitledPane> panes = uiElement.getPanes();
        panes.clear();
        panes.addAll(source);
        source.addListener((ListChangeListener<TitledPane>) change -> update(panes, change));
        if (!source.isEmpty() && uiElement.getExpandedPane() == null) {
            uiElement.setExpandedPane(source.get(0));
        }
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

    public static StringExpression getFryerDescription(Fryer f) {
        return format(Fryer.DESCRIPTION, f.maxDonutOutputProperty());
    }

    public static StringBinding getGrossDonuts() {
        return Game.game.grossDonutsProperty().asString();
    }

    public static BooleanBinding getHireButtonDisable(ObjectProperty<Job> selected, Location l) {
        return createBooleanBinding(
                () -> l.getTotalBalance() < selected.get().getWage(),
                selected, l.totalBalanceProperty()
        );
    }

    public static StringBinding getHireButtonText(ObjectProperty<Job> selected) {
        return createStringBinding(
                () -> String.format("Hire - %s/pp", asMoney(selected.get().getWage())),
                selected);
    }

    public static ListProperty<Job> getHireableJobs() {
        ObservableList<Job> jobs = Job.getEntryLevelJobs();
        SortedList<Job> sorted = jobs.sorted();
        return new SimpleListProperty<>(sorted);
    }

    public static StringExpression getIngredientAmount(IngredientBatch i) {
        return createStringBinding(
                () -> getScaledUnitValue(i.getAmount(), i.getType().getUnits(), i.getType().getDecimalThreshold()),
                i.amountProperty()
        );
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
        return format(Fryer.OUTPUT_FORMAT, f.currentDonutOutputProperty(),
                f.maxDonutOutputProperty(), f.donutTypeProperty());
    }

    public static ObjectProperty<DonutType> getOutputType(Fryer f) {
        return f.donutTypeProperty();
    }

    public static DoubleProperty getPayPeriodProgress() {
        return Account.payPeriodProgressProperty();
    }

    public static ObservableValue<ObservableList<Employee>> getPossibleOperators(Station s) {
        FilteredList<Employee> list = s.getLocation().rosterProperty()
                .filtered(e -> e.getJob().isAssignable(s.getSkill()));
        SortedList<Employee> sorted = list.sorted((o1, o2) -> {
            if (o1 == Employee.UNASSIGNED) {
                return Integer.MAX_VALUE;
            } else if (o2 == Employee.UNASSIGNED) {
                return Integer.MIN_VALUE;
            } else {
                return o1.getJob().getName().compareTo(o2.getJob().getName());
            }
        });
        return new SimpleListProperty<>(sorted);
    }

    public static DoubleProperty getProgress(Station s) {
        return s.progressProperty();
    }

    public static String getRegisterDescription(CashRegister r) {
        return CashRegister.DESCRIPTION;
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
    private static String asMoneySigned(Number amount) {
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

    private static String getScaledUnitValue(int value, SortedMap<Integer,String> units, int decimalThreshold) {
        Map.Entry<Integer, String> last = null;
        for (Map.Entry<Integer, String> mapping : units.entrySet()) {
            if (value < mapping.getKey()) {
                break;
            }
            last = mapping;
        }
        assert last != null;
        double computedValue = value == 0 || last.getKey() == 0
                ? value
                : (double) value / last.getKey();
        boolean canRoundToInt = computedValue - (int) computedValue < 0.1;
        return (value < decimalThreshold) || canRoundToInt
                ? String.format("%d %s", (int) computedValue, last.getValue())
                : String.format("%.1f %s", computedValue, last.getValue());
    }

}

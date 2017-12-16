package main.ui;

import javafx.beans.property.ListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import main.Game;
import main.RNG;
import main.model.Account;
import main.model.Employee;
import main.model.Job;
import main.model.Location;
import main.model.ingredient.IngredientOffer;
import main.model.ingredient.IngredientStock;
import main.model.ingredient.IngredientSupplier;
import main.model.ingredient.IngredientType;
import main.model.station.CashRegister;
import main.model.station.Fryer;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static javafx.collections.FXCollections.observableList;
import static main.ui.UILinker.*;

/**
 * @author Anthony Morrell
 * @since 10/28/2017
 */
public class Controller implements Initializable {

    public static final String DEFAULT_TEXT_COLOR = "black";

    private static final String FULL_STORE_COLOR = "forestgreen";
    private static final String HIGH_WAGE_COLOR = "firebrick";

    // general
    @FXML public ChoiceBox<Location> currentLocation;
    @FXML public Label grossDonutCount;

    // store
    @FXML public Button manualSellButton;
    @FXML public Button manualFryButton;
    @FXML public Label customerCount;
    @FXML public Label donutCount;
    @FXML public Accordion registerList;
    @FXML public Accordion fryerList;

    // inventory
    @FXML public TableView<IngredientStock> ingredientList;
    @FXML public TitledPane ingredientOfferPane;
    @FXML public Label shownIngredient;
    @FXML public Label ingredientMinBound;
    @FXML public Label ingredientMaxBound;
    @FXML public Slider ingredientBoundSlider;
    @FXML public TableView<IngredientOffer> ingredientOfferList;
    @FXML public Label donutCountDetailed;
    @FXML public Accordion productList;
    @FXML public Button addProductButton;

    // finances
    @FXML public ProgressIndicator percentToPayday;
    @FXML public Label timeToPayday;
    @FXML public Label totalWages;
    @FXML public ChoiceBox<Account> salarySource;
    @FXML public Label totalBalance;
    @FXML public ChoiceBox<Account> depositAccount;
    @FXML public Accordion accountList;

    @FXML public TableView<Employee> employeeList;
    @FXML public Button dismissEmployeeButton;
    @FXML public Button promoteEmployeeButton;
    @FXML public ChoiceBox<Job> hireList;
    @FXML public Button hireButton;

    // ordering
    @FXML public ChoiceBox<IngredientType> orderItem;
    @FXML public Button orderButton;
    @FXML public Label orderAmount;
    @FXML public Label orderCost;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // finances tab
        linkProgress(percentToPayday, getPayPeriodProgress());
        linkText(timeToPayday, getTimeToPayday());
        initializeEmployeeManager();

        // location selector
        linkItems(currentLocation, getLocations());
        linkChoice(currentLocation, getLocation(), Game.location(), this::bindLocationSpecific);

        // other
        linkColumns(ingredientList, "name", "amountText", "quality", "brand");
        linkText(shownIngredient, getShownIngredient(ingredientList, ingredientOfferPane));
        linkText(ingredientMaxBound, getIngredientSearchMax(ingredientBoundSlider, ingredientList));
        linkColumns(ingredientOfferList, "supplierName", "amountText", "quality", "priceText");
        linkItems(orderItem, IngredientType.typesProperty());
        linkText(grossDonutCount, getGrossDonuts());
    }

    private void initializeEmployeeManager() {
        // hire employee selector
        linkItems(hireList, getHireableJobs());
        hireList.setValue(Job.getEntryLevelJobs().get(0));
        link(hireButton.textProperty(), getHireButtonText(hireList.valueProperty()));

        // employee table and buttons
        linkColumns(employeeList, "name", "job", "location", "pay");
        employeeList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        linkDisable(promoteEmployeeButton, getEmployeesSelected(employeeList));
        linkDisable(dismissEmployeeButton, getEmployeesSelected(employeeList));
    }

    private void bindLocationSpecific(Location loc) {
        setupStoreTab(loc);
        setupInventoryTab(loc);
        setupFinancesTab(loc);
    }

    private void setupStoreTab(Location loc) {
        linkDisable(manualSellButton, getCheckoutButtonDisable(loc));
        linkDisable(manualFryButton, getFryButtonDisable(loc));
        linkText(customerCount, getCustomerCount(loc));
        onUpdate(loc.customersProperty(), num -> setCSS(customerCount, "text-fill",
                num.intValue() > 0 ? FULL_STORE_COLOR : DEFAULT_TEXT_COLOR));
        linkText(donutCount, getStockedDonuts(loc));
        linkPanes(registerList, loc.getRegisters().getPanes());
        linkPanes(fryerList, loc.getFryers().getPanes());
    }

    private void setupInventoryTab(Location loc) {
        ingredientList.setItems(loc.getIngredients().getStock());
    }

    private void setupFinancesTab(Location loc) {
        linkText(totalWages, getTotalWages(loc));
        onUpdate(canPayWages(loc), canPay -> setCSS(totalWages, "text-fill",
                canPay ? DEFAULT_TEXT_COLOR : HIGH_WAGE_COLOR));
        linkText(totalBalance, getTotalBalance(loc));
        Account firstAccount = getAccounts(loc).getValue().get(0);
        linkItems(depositAccount, getAccounts(loc));
        linkChoice(depositAccount, getDepositAccount(loc), firstAccount);
        linkItems(salarySource, getAccounts(loc));
        linkChoice(salarySource, getWageSourceAccount(loc), firstAccount);
        linkPanes(accountList, loc.getAccountPanes());
        setupRoster(loc);
    }

    private void setupRoster(Location loc) {
        linkDisable(hireButton, getHireButtonDisable(hireList.valueProperty(), loc));
        ListProperty<Employee> roster = getRoster(loc);
        linkItems(employeeList, roster);
        ((SortedList<Employee>) roster.get()).comparatorProperty().bind(employeeList.comparatorProperty());
    }

    public void onManualCheckout() {
        Game.location().getRegisters().getPlayerOperated().ifPresent(CashRegister::attemptToBegin);
    }

    public void onManualFry() {
        Game.location().getFryers().getPlayerOperated().ifPresent(Fryer::attemptToBegin);
    }

    public void onSearchIngredients() {
        int min = 1;
        int max = getMaxIngredients();
        IngredientStock selected = ingredientList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            ingredientOfferList.setItems(observableList(
                    IngredientSupplier.getAllOffers(selected.getType(), min, max)));
        }
    }

    public void onCreateProduct() {

    }

    public void onPromoteSelectedEmployees() {
        List<Job> jobs = Job.getEntryLevelJobs();
        for (Employee emp : employeeList.getSelectionModel().getSelectedItems()) {
            if (emp.isPromotable()) {
                emp.promote();
                Job superior = emp.getJob().getSuperior();
                if (superior != null && !jobs.contains(superior)) {
                    jobs.add(superior);
                }
            }
        }
    }

    public void onDismissSelectedEmployees() {
        Employee[] selected = employeeList.getSelectionModel().getSelectedItems().toArray(new Employee[0]);
        for (Employee emp : selected) {
            if (emp != Employee.PLAYER) {
                Game.location().dismiss(emp);
            }
        }
    }

    public void onHireNewEmployee() {
        Job job = hireList.getValue();
        Job superior = job.getSuperior();
        List<Job> jobs = Job.getEntryLevelJobs();
        Employee emp = new Employee(RNG.name(), job, Game.location());
        Game.location().getRoster().add(emp);
        if (superior != null && !jobs.contains(superior)) {
            jobs.add(superior);
        }
    }

    private int getMaxIngredients() {
        IngredientStock stock = ingredientList.getSelectionModel().getSelectedItem();
        return stock == null ? 0 : (int) ingredientBoundSlider.getValue();
    }

    public static <T> void onUpdate(ObservableValue<T> observable, Consumer<T> action) {
        observable.addListener((obs, oldValue, newValue) -> action.accept(newValue));
    }

    public static <T> void onUpdate(ObservableValue<T> observable, BiConsumer<T,T> action) {
        observable.addListener((obs, oldValue, newValue) -> action.accept(oldValue, newValue));
    }

    static void setCSS(Node uiElement, String property, String value) {
        uiElement.setStyle("-fx-" + property + ": " + value);
    }

}

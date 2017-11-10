package main;

import javafx.beans.property.ListProperty;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.model.*;

import java.net.URL;
import java.util.ResourceBundle;

import static main.UILinker.*;

/**
 * @author Anthony Morrell
 * @since 10/28/2017
 */
public class Controller implements Initializable {

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
    @FXML public TableView<Ingredient> ingredientList;
    @FXML public Label donutCountDetailed;
    @FXML public Accordion productList;
    @FXML public Button addProductButton;

    // finances
    @FXML public ProgressIndicator timeToPayday;
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
    @FXML public ChoiceBox<IngredientDescription> orderItem;
    @FXML public Button orderButton;
    @FXML public Label orderAmount;
    @FXML public Label orderCost;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        linkItems(currentLocation, getLocations());
        linkText(grossDonutCount, getGrossDonuts());
        linkChoice(currentLocation, getLocation(), Game.location(), this::bindLocationSpecific);
        linkProgress(timeToPayday, Account.payPeriodProgressProperty());
        linkItems(hireList, Job.entryLevelJobsProperty());
        hireList.setValue(Job.getEntryLevelJobs().get(0));
    }

    private void bindLocationSpecific(Location loc) {
        link(manualSellButton.disableProperty(), getCheckoutButtonDisable(loc));
        link(manualFryButton.disableProperty(), getFryButtonDisable(loc));
        linkText(customerCount, getCustomerCount(loc));
        linkText(donutCount, getStockedDonuts(loc));
        linkPanes(registerList, loc.getRegisters().getPanes());
        linkPanes(fryerList, loc.getFryers().getPanes());
        linkText(totalWages, getTotalWages(loc));
        linkText(totalBalance, getTotalBalance(loc));
        linkItems(depositAccount, getAccounts(loc));
        linkChoice(depositAccount, getDepositAccount(loc), getAccounts(loc).getValue().get(0), (a) -> {});
        linkItems(salarySource, getAccounts(loc));
        linkChoice(salarySource, getWageSourceAccount(loc), getAccounts(loc).getValue().get(0), (a) -> {});
        linkPanes(accountList, loc.getAccountPanes());
        setupRoster(loc);
    }

    private void setupRoster(Location loc) {
        ListProperty<Employee> roster = getRoster(loc);
        linkItems(employeeList, roster);
        ((SortedList<Employee>) roster.get()).comparatorProperty().bind(employeeList.comparatorProperty());
        employeeList.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        employeeList.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("job"));
        employeeList.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("location"));
        employeeList.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("pay"));
    }

    public void onManualCheckout(ActionEvent event) {
        Game.location().getRegisters().getPlayerOperated().ifPresent(CashRegister::begin);
    }

    public void onManualFry(ActionEvent event) {
        Game.location().getFryers().getPlayerOperated().ifPresent(Fryer::begin);
    }

    public void onCreateProduct(ActionEvent event) {

    }

    public void onPromoteSelectedEmployees(ActionEvent event) {

    }

    public void onDismissSelectedEmployees(ActionEvent event) {

    }

    public void onHireNewEmployee(ActionEvent event) {
        Job job = hireList.getValue();
        Employee emp = new Employee(RNG.name(), job, Game.location());
        Game.location().getRoster().add(emp);
    }

}

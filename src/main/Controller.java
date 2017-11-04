package main;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import main.model.*;

import java.net.URL;
import java.util.ResourceBundle;

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
    @FXML public HBox paydayTracker;
    @FXML public ChoiceBox<Account> salarySource;
    @FXML public Label totalBalance;
    @FXML public ChoiceBox<Account> depositAccount;
    @FXML public Accordion accountList;
    @FXML public Button hireEmployeeButton;
    @FXML public Button dismissEmployeeButton;
    @FXML public Button promoteEmployeeButton;
    @FXML public TableView<Employee> employeeList;

    // ordering
    @FXML public ChoiceBox<IngredientDescription> orderItem;
    @FXML public Button orderButton;
    @FXML public Label orderAmount;
    @FXML public Label orderCost;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Game.controller = this;

        currentLocation.setOnAction(this::onLocationChange);
        currentLocation.itemsProperty().bind(Game.game.locationsProperty());
        currentLocation.valueProperty().bind(Game.game.currentLocationProperty());
        grossDonutCount.textProperty().bind(Game.game.grossDonutsProperty().asString());

        manualSellButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> !Game.location().getRegisters().playerHasAppliance()
                    || Game.location().getCustomers() == 0,
                Game.game.currentLocationProperty(),
                Game.location().getRegisters().playerHasApplianceProperty(),
                Game.location().customersProperty()
        ));

        manualFryButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> !Game.location().getFryers().playerHasAppliance(),
                Game.game.currentLocationProperty(),
                Game.location().getFryers().playerHasApplianceProperty()
        ));

        customerCount.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("%d / %d",
                        Game.location().customersProperty().getValue(),
                        Game.location().maxCapacityProperty().getValue()),
                Game.game.currentLocationProperty(),
                Game.location().customersProperty(),
                Game.location().maxCapacityProperty()
        ));

        donutCount.textProperty().bind(Bindings.createStringBinding(
                () -> Game.location().donutStockProperty().getValue().toString(),
                Game.game.currentLocationProperty(), Game.location().donutStockProperty()
                // todo donutCount only updates for first location
        ));

        bindAppliances(registerList, Game.location().getRegisters());
        bindAppliances(fryerList, Game.location().getFryers());

        totalBalance.textProperty().bind(Bindings.createStringBinding(
                () -> Game.formatMoney(Game.location().getTotalBalance()),
                Game.game.currentLocationProperty(),
                Game.location().totalBalanceProperty()
        ));

        accountList.getPanes().clear();
        accountList.getPanes().addAll(Game.location().getAccountPanes());
        Game.location().getAccountPanes().addListener((ListChangeListener<? super TitledPane>) change -> {
            while (change.next()) {
                if (change.wasPermutated()) {
                    throw new RuntimeException("Account list permutation was unhandled: " + change);
                } else if (change.wasUpdated()) {
                    throw new RuntimeException("TitledPane was modified in Account list: " + change.getList().get(change.getFrom()));
                } else {
                    accountList.getPanes().removeAll(change.getRemoved());
                    accountList.getPanes().addAll(change.getAddedSubList());
                }
            }
        });

        depositAccount.itemsProperty().bind(Game.location().accountsProperty());
        depositAccount.valueProperty().bindBidirectional(Game.location().depositAccountProperty());
    }

    private <A extends Appliance> void bindAppliances(Accordion uiNode, ApplianceGroup<A> appliances) {
        uiNode.getPanes().clear();
        uiNode.getPanes().addAll(appliances.getPanes());
        appliances.panesProperty().addListener(getApplianceListener(uiNode));
    }

    private ListChangeListener<TitledPane> getApplianceListener(Accordion uiNode) {
        return change -> {
            while (change.next()) {
                if (change.wasPermutated()) {
                    throw new RuntimeException("Appliance list permutation was unhandled: " + change);
                } else if (change.wasUpdated()) {
                    throw new RuntimeException("TitledPane was modified in ApplianceGroup list: " + change.getList().get(change.getFrom()));
                } else {
                    uiNode.getPanes().removeAll(change.getRemoved());
                    uiNode.getPanes().addAll(change.getAddedSubList());
                }
            }
        };
    }

    public void onLocationChange(ActionEvent event) {

    }

    public void onManualCheckout(ActionEvent event) {
        Game.location().getRegisters().getPlayerOperated().ifPresent(CashRegister::operate);
    }

    public void onManualFry(ActionEvent event) {
        Game.location().getFryers().getPlayerOperated().ifPresent(Fryer::operate);
    }

    public void onCreateProduct(ActionEvent event) {

    }

    public void onHireNewEmployee(ActionEvent event) {

    }

    public void onPromoteSelectedEmployees(ActionEvent event) {

    }

    public void onDismissSelectedEmployees(ActionEvent event) {

    }

}

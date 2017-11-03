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
    @FXML public Accordion flavorList;
    @FXML public Button addFlavorButton;

    // finances
    @FXML public HBox paydayTracker;
    @FXML public ChoiceBox<Account> salarySource;
    @FXML public Label totalBalance;
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
        currentLocation.setOnAction(this::onBranchChange);
        currentLocation.itemsProperty().bind(Game.game.locationsProperty());
        currentLocation.valueProperty().bind(Game.game.currentLocationProperty());
        grossDonutCount.textProperty().bind(Game.game.grossDonutsProperty().asString());

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
    }

    private <A extends Appliance> void bindAppliances(Accordion uiNode, ApplianceGroup<A> appliances) {
        uiNode.getPanes().clear();
        uiNode.getPanes().addAll(appliances.getPanes());
        appliances.panesProperty().addListener(getApplianceListener());
    }

    private ListChangeListener<TitledPane> getApplianceListener() {
        return change -> {
            while (change.next()) {
                if (change.wasPermutated()) {
                    throw new RuntimeException("Appliance list permutation was unhandled: " + change);
                } else if (change.wasUpdated()) {
                    throw new RuntimeException("TitledPane was modified in ApplianceGroup list: " + change.getList().get(change.getFrom()));
                } else {
                    fryerList.getPanes().removeAll(change.getRemoved());
                    fryerList.getPanes().addAll(change.getAddedSubList());
                }
            }
        };
    }

    public void onBranchChange(ActionEvent event) {

    }

    public void onManualCheckOut(ActionEvent event) {
        Game.location().getRegisters().getPlayerOperated().ifPresent(CashRegister::operate);
    }

    public void onManualCook(ActionEvent event) {
        Game.location().getFryers().getPlayerOperated().ifPresent(Fryer::operate);
    }

    public void onAddFlavor(ActionEvent event) {

    }

    public void onHireNewEmployee(ActionEvent event) {

    }

    public void onPromoteSelectedEmployees(ActionEvent event) {

    }

    public void onDismissSelectedEmployees(ActionEvent event) {

    }

}

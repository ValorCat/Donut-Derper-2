package main;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.model.*;

import java.awt.*;
import java.io.IOException;

/**
 * @author Anthony Morrell
 * @since 10/22/2017
 */
public class Main extends Application {

    private final static String WINDOW_TITLE = "Donut Derper II: Back to the Bakery";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            AnchorPane window = FXMLLoader.load(Main.class.getResource("donut-derper-2.fxml"));
            primaryStage.setScene(new Scene(window));
            primaryStage.setTitle(WINDOW_TITLE);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Couldn't find FXML data:");
            e.printStackTrace();
        }
    }
}

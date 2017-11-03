package main;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.model.Game;
import main.model.Location;

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

            AnimationTimer loop = new AnimationTimer() {
                public void handle(long now) {
                    tick();
                }
            };
            loop.start();
        } catch (IOException e) {
            System.out.println("Couldn't find FXML data:");
            e.printStackTrace();
        }
    }

    private static void tick() {
        for (Location l : Game.game.getLocations()) {
            l.update();
        }
    }

}

package main;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.model.Account;
import main.model.DonutType;
import main.model.Game;
import main.model.Location;

import java.io.IOException;

/**
 * @author Anthony Morrell
 * @since 10/22/2017
 */
public class Main extends Application {

    private static final String WINDOW_TITLE = "Donut Derper II: Back to the Bakery";
    public static final String NAME_LIST = "src/names.txt";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            AnchorPane window = FXMLLoader.load(Main.class.getResource("donut-derper-2.fxml"));
            Scene scene = new Scene(window);
            scene.setOnKeyPressed(e -> {
                switch (e.getCode()) {
                    case D:
                        Game.location().updateDonuts(new DonutType("Plain", 1));
                        break;
                }
            });
            primaryStage.setScene(scene);
            primaryStage.setTitle(WINDOW_TITLE);
            primaryStage.show();

            AnimationTimer loop = new AnimationTimer() {

                private long lastTimeStamp = -1;

                public void handle(long now) {
                    if (lastTimeStamp == -1) {
                        lastTimeStamp = now;
                    }
                    tick(now, lastTimeStamp);
                    lastTimeStamp = now;
                }

            };

            loop.start();
        } catch (IOException e) {
            System.out.println("Couldn't find FXML data:");
            e.printStackTrace();
        }
    }

    private static void tick(long now, long last) {
        boolean isInterestDay = Account.readyForInterestDeposit(now - last);
        boolean isPayday = Account.readyForPayday(now - last);
        for (Location l : Game.game.getLocations()) {
            l.update(now, last, isInterestDay, isPayday);
        }
    }

}

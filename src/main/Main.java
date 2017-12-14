package main;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.model.Account;
import main.model.Location;
import main.model.donut.DonutBatch;
import main.model.donut.DonutType;

import java.io.IOException;

/**
 * @author Anthony Morrell
 * @since 10/22/2017
 */
public class Main extends Application {

    private static final String WINDOW_TITLE = "Donut Derper II: Back to the Bakery";
    private static final String FXML_SOURCE = "ui/donut-derper-2.fxml";

    @Override
    public void start(Stage primaryStage) {
        try {
            AnchorPane window = FXMLLoader.load(Main.class.getResource(FXML_SOURCE));
            Scene scene = new Scene(window);
            scene.setOnKeyPressed(e -> {
                switch (e.getCode()) {
                    case C:
                        Game.location().enterCustomer();
                        break;
                    case D:
                        Game.location().addDonuts(new DonutBatch(DonutType.PLAIN, 1));
                        break;
                    case M:
                        Game.location().getDepositAccount().updateBalance(1);
                        break;
                }
            });
            primaryStage.setScene(scene);
            primaryStage.setTitle(WINDOW_TITLE);
            primaryStage.setResizable(false);
            primaryStage.show();

            AnimationTimer loop = new AnimationTimer() {
                private long lastTimeStamp = -1;
                public void handle(long now) {
                    tick(now, lastTimeStamp == -1 ? now : lastTimeStamp);
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

    public static void main(String[] args) {
        launch(args);
    }

}

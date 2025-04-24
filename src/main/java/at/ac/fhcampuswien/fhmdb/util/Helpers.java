package at.ac.fhcampuswien.fhmdb.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Label;

public class Helpers {

    private static Stage primaryStage;

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static void showToast(String message) {
        if (primaryStage == null) {
            System.err.println("Helpers not initialized with stage.");
            return;
        }

        Label toastLabel = new Label(message);
        toastLabel.setStyle("-fx-background-color: rgba(220, 53, 69, 0.9); -fx-text-fill: white; -fx-padding: 10px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        Popup popup = new Popup();
        popup.centerOnScreen();
        popup.getContent().add(toastLabel);
        popup.setAutoHide(true);
        popup.show(primaryStage);

        new Timeline(
                new KeyFrame(Duration.seconds(5), ae -> popup.hide())
        ).play();
    }
}



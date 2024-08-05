package org.dashboard.dashboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class DashboardApplication extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DashboardApplication.class.getResource("dashboard-view.fxml"));
        Pane dashboardPane = fxmlLoader.load();

        // Create a wrapper Pane to center the dashboardPane
        StackPane wrapperPane = new StackPane();
        wrapperPane.getChildren().add(dashboardPane);
        wrapperPane.setStyle("-fx-background-color: black;");

        // Set the preferred size of the dashboardPane
        dashboardPane.setPrefSize(800, 480);

        // Create the scene with the wrapperPane
        Scene scene = new Scene(wrapperPane, Color.BLACK);

        // Add stylesheets
        scene.getStylesheets().add(DashboardApplication.class.getResource("style.css").toExternalForm());

        // Configure the stage
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint(""); // Optionally remove the exit hint
        stage.show();

        // Add event filter to close the application with 'Q' key
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.Q) {
                stage.close();
            }
        });
    }
}

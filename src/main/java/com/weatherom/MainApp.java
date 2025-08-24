package com.weatherom;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                    "/com/weatherom/main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Linking CSS styling file to the MainApp class
            String css = this.getClass().getResource("/com/weatherom/style.css").toExternalForm();
            scene.getStylesheets().add(css);

            stage.setTitle("WeatherOM!");
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
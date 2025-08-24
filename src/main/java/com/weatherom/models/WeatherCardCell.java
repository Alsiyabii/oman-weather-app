package com.weatherom.models;

// Creating a class to display a weatherCard in a ListView in javaFX

import com.weatherom.controllers.MainController;
import com.weatherom.fileUtil.FileUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.*;

public class WeatherCardCell extends ListCell<WeatherCard> {
    private HBox content;
    private Label cityLabel;
    private Label currentTempLabel;
    private Label descriptionLabel;
    private Button deleteButton;

    // Constructor method
    public WeatherCardCell() {
        super();
        createCell();

    }

    public void createCell() {
        content = new HBox();

        content.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-color: lightgrey; " +
                        "-fx-border-radius: 15; " +
                        "-fx-border-width: 4; " +
                        "-fx-padding: 5;"
        );
        content.setPrefHeight(65);
        content.setMinHeight(65);
        content.setAlignment(Pos.CENTER_LEFT);

        // City name label (Left)
        cityLabel = new Label();
        cityLabel.setPrefWidth(100);
        cityLabel.setPrefHeight(26);
        cityLabel.setStyle(
                "-fx-font-family: Cochin; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 25; " +
                        "-fx-text-fill: white;"
        );
        cityLabel.setMaxWidth(Double.MAX_VALUE);
        cityLabel.setPadding(new Insets(0,0,0,5));

        // Spacer (Middle region)
        Region spacer = new Region();
        spacer.setPrefWidth(24);
        spacer.setPrefHeight(38);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Temperature Container / VBox (Right)
        VBox tempVBox = new VBox();
        tempVBox.setPrefHeight(38);
        tempVBox.setPrefWidth(64);
        tempVBox.setAlignment(Pos.CENTER);

        // Current temperature (Top Right)
        VBox currentTempContainer = new VBox();
        currentTempContainer.setAlignment(Pos.CENTER);
        currentTempContainer.setPrefHeight(28);
        currentTempContainer.setPrefWidth(48);

        currentTempLabel = new Label();
        currentTempLabel.setStyle(
                "-fx-font-family: Cochin; " +
                        "-fx-font-size: 24; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: white;"
        );

        // Temperature range label (Bottom Right)
        descriptionLabel = new Label();
        descriptionLabel.setStyle(
                "-fx-font-size: 10; " +
                        "-fx-font-family: Cochin; " +
                        "-fx-text-fill: white;"
        );

        // Creating the delete container to place the delete button in
        VBox deleteContainer = new VBox();
        deleteContainer.setAlignment(Pos.TOP_RIGHT);
        deleteContainer.setMinWidth(20);
        deleteContainer.setMaxWidth(20);

        // Delete card button
        deleteButton = new Button("X");
        deleteButton.setStyle("-fx-background-color: lightgrey; " +
                "-fx-border-color: transparent; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 11; " +
                "-fx-text-fill: red; " +
                "-fx-padding: 0; " +
                "-fx-background-radius: 50em; " +
                "-fx-border-radius: 50; ");
        deleteButton.setPrefSize(20, 20);
        deleteButton.setMinWidth(17);
        deleteButton.setMaxWidth(17);
        deleteButton.setPadding(Insets.EMPTY);
        deleteButton.setVisible(false);

        // Adding nodes to containers
        currentTempContainer.getChildren().addAll(currentTempLabel);
        tempVBox.getChildren().addAll(currentTempContainer, descriptionLabel);
        deleteContainer.getChildren().add(deleteButton);
        content.getChildren().addAll(cityLabel, spacer, tempVBox, deleteContainer);

    }

    @Override
    protected void updateItem(WeatherCard weather, boolean empty) {
        super.updateItem(weather, empty);

        if (empty || weather == null) {
            setGraphic(null);
        } else {
            if (getListView() != null) {
                // Bind the content width to the ListView width
                content.minWidthProperty().bind(getListView().widthProperty().subtract(20));
                content.prefWidthProperty().bind(getListView().widthProperty().subtract(20));
            }

            cityLabel.setText(weather.getCityName());
            currentTempLabel.setText(weather.getCurrentTemp() + "°c");
            descriptionLabel.setText(weather.getDescription());
            setGraphic(content);

            // Changing the weatherCard bg colour based on the weather condition
            int conditionID = Integer.parseInt(weather.getConditionID());
            String bgString = "white";
            if (conditionID == 800) {
             bgString = "from 100% 0% to 0% 100%, #e8e38e,#36adcf";
            } else if (conditionID >= 801 && conditionID <= 804) {
                bgString = "to right, #4a4e4f, #828485";
            } else if ((conditionID >= 500 && conditionID <= 532) ||
                    (conditionID >= 300 && conditionID <= 321)) {
                bgString = "to bottom, #414242, #646666";
            } else if (conditionID >= 200 && conditionID <= 232) {
                bgString = "to bottom, #414242, #646666";
            } else if (conditionID >= 600 && conditionID <= 622) {
                bgString = "to bottom, #5e6061, #c5c9c9";
            } else if (conditionID >= 701 && conditionID <= 781) {
                bgString = "to left, #8c4d06, #d17711";
            }

            String currentBg =  "-fx-background-color: linear-gradient(" + bgString + ");";

            // Adding hover effects on the weather card
            // Normal Style
            String normalStyle = currentBg +
                    "-fx-background-radius: 15; " +
                    "-fx-border-color: lightgrey; " +
                    "-fx-border-radius: 15; " +
                    "-fx-border-width: 4; " +
                    "-fx-padding: 5;";

            // Hover Style
            String hoverStyle = currentBg +
                    "-fx-background-radius: 15; " +
                    "-fx-border-color: #606263; " +
                    "-fx-border-radius: 15; " +
                    "-fx-border-width: 6; " +
                    "-fx-padding: 3;";

            // Applying normal style
            content.setStyle(normalStyle);

            // Apply hover effect (Mouse entrance and exit)
            content.setOnMouseEntered(e -> {
                content.setStyle(hoverStyle);
                deleteButton.setVisible(true);
            });

            content.setOnMouseExited(e -> {
                content.setStyle(normalStyle);
                deleteButton.setVisible(false);
            });

            // Setting up delete button action
            deleteButton.setOnAction(e -> {
                e.consume(); // Do not mix between this button and the weather card

                // Check if this is the currently selected item
                boolean wasSelected = getListView().getSelectionModel().getSelectedItem() == getItem();

                // Remove city name from the file
                FileUtil.removeFromFile(weather.getCityName(), "src/main/resources/com/weatherom/cityNames.txt");
                // Remove weather card from the list
                getListView().getItems().remove(getItem());

                // Check if the listView is empty or the selected card was deleted
                if (getListView().getItems().isEmpty() || wasSelected) {
                    // Access controller through the ListView's user data
                    Object controller = getListView().getScene().getWindow().getUserData();
                    if (controller instanceof MainController) {
                        ((MainController) controller).resetWeatherPanel();
                    }
                }
                System.out.println("Delete requested for: " + weather.getCityName());
            });

            // Setting up delete button hover effect
            deleteButton.setOnMouseEntered(e -> {
                deleteButton.setStyle("-fx-background-color: #606263; " +
                        "-fx-border-color: transparent; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 14; " +
                        "-fx-text-fill: red; " +
                        "-fx-padding: 0; " +
                        "-fx-background-radius: 50em; " +
                        "-fx-border-radius: 50; ");
            });

            deleteButton.setOnMouseExited(e -> {
                deleteButton.setStyle("-fx-background-color: lightgrey; " +
                        "-fx-border-color: transparent; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 14; " +
                        "-fx-text-fill: red; " +
                        "-fx-padding: 0; " +
                        "-fx-background-radius: 50em; " +
                        "-fx-border-radius: 50; ");
            });

        }
    }


}

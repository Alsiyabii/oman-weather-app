package com.weatherom.models;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

// Creating a main weather panel that holds the weather info regarding a city (VBox)
public class WeatherPanel extends VBox {

    // Specifying the main (variables) to be used in this class
    // Initial weather panel screen
    private Label weatherInfoLabel;
    private ImageView omanMap;

    // Weather description screen
    private Label cityNameLabel;
    private Label currentTempLabel;
    private Label descriptionLabel;
    private Label feelsLikeTempLabel;
    private Label humidityLabel;
    private Label windLabel;
    private Label pressureLabel;
    private Label visibilityLabel;
    private Label cloudsLabel;
    private Label sunriseLabel;
    private Label sunsetLabel;
    private ImageView weatherIcon;

    // Creating a lock so that setupMainPanel() method runs only once when launching the app
    private boolean isShowingWeather = false;


    // Class constructor
    public WeatherPanel() {
        setupInitialPanel();
        setupLayout();
    }

    // Creating the basic components inside the weather panel
    private void setupInitialPanel() {
        // Creating the two initial nodes
        weatherInfoLabel = new Label("Weather information displayed here");
        omanMap = new ImageView(new Image(getClass().getResourceAsStream("/com/weatherom/icons/oman-map.png")));

        // Styling the label
        weatherInfoLabel.setStyle("-fx-text-fill: lightgrey; " +
                "-fx-font-family: Cochin; " +
                "-fx-font-size: 24; " +
                "-fx-font-weight: bold; ");

        weatherInfoLabel.setWrapText(true);
        weatherInfoLabel.setMaxWidth(275);
        weatherInfoLabel.setTextAlignment(TextAlignment.CENTER);

        // Styling the omanMap imageView
        omanMap.setOpacity(0.8);
        omanMap.setFitHeight(250);
        omanMap.setFitWidth(400);
        omanMap.setPreserveRatio(true);

        // Adding the nodes to the VBox
        this.getChildren().addAll(weatherInfoLabel, omanMap);

    }

    // Setting up the main weather panel that displays actual weather info
    private void setupMainPanel() {
        // Creating the weather description nodes
        cityNameLabel = new Label();
        currentTempLabel = new Label();
        descriptionLabel = new Label();
        feelsLikeTempLabel = new Label();
        humidityLabel = new Label();
        windLabel = new Label();
        pressureLabel = new Label();
        visibilityLabel = new Label();
        cloudsLabel = new Label();
        sunriseLabel = new Label();
        sunsetLabel = new Label();
        weatherIcon = new ImageView();

        // Styling the labels
        cityNameLabel.setStyle("-fx-text-fill: white; " +
                "-fx-font-family: Cochin; " +
                "-fx-font-size: 24; " +
                "-fx-font-weight: bold;");

        currentTempLabel.setStyle("-fx-text-fill: white; " +
                "-fx-font-family: Cochin; " +
                "-fx-font-size: 48; " +
                "-fx-font-weight: bold;");

        descriptionLabel.setStyle("-fx-text-fill: white; " +
                "-fx-font-family: Cochin; " +
                "-fx-font-size: 20; ");

        // Styling multiple labels at once
        applyLabelStyle(feelsLikeTempLabel, cloudsLabel, humidityLabel, windLabel, pressureLabel, visibilityLabel, sunriseLabel, sunsetLabel);

        // Creating and adding node to the two main sections of the weather panel
        VBox headerSection = new VBox();
        headerSection.setAlignment(Pos.CENTER);
        headerSection.getChildren().addAll(cityNameLabel, currentTempLabel);

        VBox iconSection = new VBox();
        iconSection.setAlignment(Pos.CENTER);
        descriptionLabel.setOpacity(0.8);
        iconSection.getChildren().addAll(weatherIcon, descriptionLabel);

        // Creating a grid to organise weather info
        GridPane grid = new GridPane();

        // Configuring grid properties
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        // Adding labels to the grid
        grid.add(feelsLikeTempLabel, 0, 0);
        grid.add(humidityLabel, 1, 0);
        grid.add(windLabel, 0, 1);
        grid.add(pressureLabel, 1, 1);
        grid.add(visibilityLabel, 0, 2);
        grid.add(cloudsLabel, 1, 2);
        grid.add(sunriseLabel, 0, 3);
        grid.add(sunsetLabel, 1, 3);

        this.getChildren().addAll(headerSection, iconSection, grid);

    }

    public void showInitialPanel () {
        this.getChildren().clear();
        setupInitialPanel();
        isShowingWeather = false;
    }
    public void showWeatherInfo(String city, String temp, String description, String feelsLikeTemp,
                                String humidity, String wind, String pressure, String visibility,  String clouds,
                                String sunrise, String sunset, String iconURL) {
        // Making sure the code inside the if condition won't run unless the lock is false
        if (!isShowingWeather) {
            // Clear initial content in the panel
            this.getChildren().clear();
            // Add weather content
            setupMainPanel();

            // Now the panel is showing weather info, so the lock must be true
            isShowingWeather = true;
        }
        // If the lock is true, then the panel is already showing the weather, so we only have to update the labels
        updateWeather(city, temp, description, feelsLikeTemp, humidity, wind, pressure, visibility, clouds, sunrise, sunset, iconURL);
    }

    // Setting up the layout of the weather panel (VBox)
    private void setupLayout() {
        this.setSpacing(10);
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.CENTER);
        this.setMaxWidth(350);
    }

    public void updateWeather(String cityName, String temperature, String description, String feelsLikeTemp,
    String humidity, String wind, String pressure, String visibility, String clouds, String sunrise, String sunset, String iconURL) {
        cityNameLabel.setText(cityName);
        currentTempLabel.setText(temperature + "°c");
        descriptionLabel.setText(description);
        feelsLikeTempLabel.setText("Feels Like: " + feelsLikeTemp + "°c");
        humidityLabel.setText("Humidity: " + humidity + "%");
        windLabel.setText("Wind: " + wind + "m/s");
        pressureLabel.setText("Pressure: " + pressure + "hPa");
        visibilityLabel.setText("Visibility: " + visibility + "km");
        cloudsLabel.setText("Clouds: " + clouds + "%");
        sunriseLabel.setText("Sunrise: " + sunrise);
        sunsetLabel.setText("Sunset: " + sunset);

        // Loading weather icon
        try {
            Image weatherIconImage = new Image(iconURL);
            weatherIcon.setImage(weatherIconImage);
            weatherIcon.setFitWidth(64);
            weatherIcon.setFitHeight(64);
            weatherIcon.setPreserveRatio(true);
        } catch (Exception e) {
            System.out.println("Could not load weather icon: " + e.getMessage());
        }

    }

    // A method to style multiple labels
    private void applyLabelStyle(Label... labels) {
        String style = "-fx-text-fill: white; " +
                "-fx-font-family: Cochin; " +
                "-fx-font-size: 18; ";

        for (Label label : labels) {
            label.setStyle(style);
        }
    }
}

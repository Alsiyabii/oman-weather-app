package com.weatherom.controllers;

import com.weatherom.fileUtil.FileUtil;
import com.weatherom.models.WeatherCard;
import com.weatherom.models.WeatherCardCell;
import com.weatherom.models.WeatherPanel;
import com.weatherom.models.WeatherService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;


public class MainController implements Initializable {
    @FXML
    private Button settingsButton;

    @FXML
    private Button showMapButton;

    @FXML
    private ImageView logoImageView;

    @FXML
    private Region panelRegion;

    @FXML
    private VBox weatherPanelContainer;

    @FXML
    private ComboBox<String> searchCombo;

    @FXML
    private ListView<WeatherCard> weatherListView;

    private ObservableList<WeatherCard> weatherData = FXCollections.observableArrayList();

    private ObservableList<String> omaniStates = FXCollections.observableArrayList(
            "Muscat", "Salalah", "Nizwa", "Sohar", "Sur", "Ibra", "Duqm", "Quriyat", "Bahla", "Rustaq"
            , "Khasab", "Bidbid", "Nakhal", "Taqah", "Ibri", "Manah"
    );

    private ObservableList<String> filteredOmaniStates = FXCollections.observableArrayList();

    private WeatherPanel weatherPanel;

    // declaring required variables / nodes
    Timeline timeline = new Timeline();
    List<Image> frames = new ArrayList<>();

    // Adding a flag to prevent rapid calls
    private final boolean[] isProcessing = {false};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Importing the settings' icon's image
        ImageView settingsIcon = new ImageView(new Image(getClass().getResourceAsStream(
                "/com/weatherom/icons/settings.png")));
        settingsIcon.setFitHeight(24);
        settingsIcon.setFitWidth(24);
        settingsIcon.setPreserveRatio(true);

        // Setting the settings' icon as a graphic of the settings button
        settingsButton.setGraphic(settingsIcon);

        // Implementing the app's logo and it's animation using frames
        // Loading frames
        for (int i = 0; i < 32; i++) {
            String framepath = getClass().getResource("/com/weatherom/frames/frame_" + i + ".png").toExternalForm();
            frames.add(new Image(framepath));
        }

        // Create a list of reversed frames for a reversed effect
        List<Image> allFrames = new ArrayList<>(frames);
        Collections.reverse(frames);
        allFrames.addAll(frames); // adding both normal and reversed frames

        int frmecount = allFrames.size();

        for (int i = 0; i < frmecount; i++) {
            int index = i;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(i * 100),
                    e -> {logoImageView.setImage(allFrames.get(index));});

            timeline.getKeyFrames().add(keyFrame);
        }

        // Setting the animation time to be indefinite
        timeline.setCycleCount(Animation.INDEFINITE);
        // Playing the logo animation
        timeline.play();

        // Calling the two listView methods
        setupWeatherListView();
        loadWeatherData();

        // Calling the comboBox setAutoComplete method to take care of the drop-down menu of the searchCombo comboBox
        setAutoComplete();

        // Creating and showing the initial weather panel
        setupWeatherPanel();
        Platform.runLater(() -> {
            weatherListView.getScene().getWindow().setUserData(this);
        });

        // Setting up the mouse click listener on weather cards
        setupWeatherCardClickListener();
    }

    // Setting up the weather list view with the custom weather cards
    private void setupWeatherListView () {
        weatherListView.setCellFactory(listView -> new WeatherCardCell());
        weatherListView.setStyle("-fx-background-color: transparent;");

        System.out.println("listview setup complete");
    }

    private void loadWeatherData () {
        // Creating initial weather data in the list view
        List<String> addedCities = FileUtil.readFile("src/main/resources/com/weatherom/cityNames.txt");

        for (String city : addedCities) {
            addWeatherCard(city);
        }
        // Giving the data to the ListView
        weatherListView.setItems(weatherData);
    }

    // Dynamically adding a weather card tot the weather ListView
    private void addWeatherCard (String cityName) {
        // Duplicate city checking
        for (WeatherCard weatherCard : weatherData) {
            if (weatherCard.getCityName().equalsIgnoreCase(cityName)) {
                System.out.println("City " + cityName + " already exists");
                return;
            }
        }
        JSONArray geoData = WeatherService.getGeoData(cityName);
        JSONObject jsonObj = WeatherService.getWeatherData(geoData);

        if (jsonObj == null) {
            System.out.println("No city found with name " + cityName);
            return;
        } else {
            try {
                // Obtaining label info from the OpenWeatherMap API
            double currentTemp = jsonObj.getJSONObject("main").getDouble("temp");
            JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);
            String conditionID = Integer.toString(weather.getInt("id"));
            String description = weather.getString("main");
            String detailedDescription = weather.getString("description");
            String iconCode = weather.getString("icon");

            String lowTemp = Integer.toString((int) Math.round((jsonObj.getJSONObject("main").getDouble("temp_min") * 10) / 10));
            String highTemp = Integer.toString((int) Math.round((jsonObj.getJSONObject("main").getDouble("temp_max") * 10) / 10));
            String feelsLikeTemp = Integer.toString((int) Math.round((jsonObj.getJSONObject("main").getDouble("feels_like") * 10) / 10));
            String humidity = Integer.toString((int) jsonObj.getJSONObject("main").getDouble("humidity"));
            String pressure = Integer.toString((int) jsonObj.getJSONObject("main").getDouble("pressure"));

            String wind = Integer.toString((int) Math.round((jsonObj.getJSONObject("wind").getDouble("speed") * 10) / 10));
            String cloud = Integer.toString((int) Math.round((jsonObj.getJSONObject("clouds").getDouble("all") * 10) / 10));
            String visibility = Integer.toString((int) (jsonObj.getDouble("visibility"))/1000);

            // Dealing with sunrise and sunset times using a separate library
            long sunriseTimeStamp = jsonObj.getJSONObject("sys").getLong("sunrise");
            long sunsetTimeStamp = jsonObj.getJSONObject("sys").getLong("sunset");
            int timezoneOffset = jsonObj.getInt("timezone");

            // Converting to Instant (UTC time)
            Instant sunriseInstant = Instant.ofEpochSecond(sunriseTimeStamp);
            Instant sunsetInstant = Instant.ofEpochSecond(sunsetTimeStamp);

            // Converting to local time using the timezone offset
            ZoneOffset offset = ZoneOffset.ofTotalSeconds(timezoneOffset);
            LocalDateTime sunriseLocal = LocalDateTime.ofInstant(sunriseInstant, offset);
            LocalDateTime sunsetLocal = LocalDateTime.ofInstant(sunsetInstant, offset);

            // Formatting for display
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            // Round temperatures to the nearest 1 decimal place and convert them to strings
            String roundedCurrentTemp = Integer.toString((int) Math.round(currentTemp));

            WeatherCard newWeatherCard = new WeatherCard(cityName, roundedCurrentTemp, description, detailedDescription,
                    conditionID, lowTemp, highTemp, feelsLikeTemp, humidity, wind, pressure, visibility, cloud,
                    sunriseLocal.format(formatter), sunsetLocal.format(formatter), iconCode);



            weatherData.add(newWeatherCard);

            FileUtil.writeOnFile("src/main/resources/com/weatherom/cityNames.txt", cityName);

            // Fixing the lagging and crashing issue
            // Clearing selection AFTER the current UI update is completely finished
            Platform.runLater(() -> {
                weatherListView.getSelectionModel().clearSelection();
            });
        } catch (Exception e) {
                System.out.println("Error adding weather card for " + cityName + ": " + e.getMessage());
                e.printStackTrace();
            }
        }


    }

    // Setting up the auto complete feature for the text field above the weather cards
    private void setAutoComplete() {
        // All the Omani states are show before typing anything
        searchCombo.setItems(omaniStates);

        // Listen for text changes in the text field
        searchCombo.getEditor().textProperty().addListener((obs, oldText, newText)
                -> {
            if (isProcessing[0]) {return;}

            if ((newText == null) || newText.trim().isEmpty()) {
                searchCombo.setItems(omaniStates);
            } else {
                // Filter states based on input (add a city to "filteredOmaniStates" observable list
                filteredOmaniStates.clear();
                for (String state : omaniStates) {
                    if (state.toLowerCase().startsWith(newText.toLowerCase().trim())) {
                        filteredOmaniStates.add(state);
                    }
                }
                searchCombo.setItems(filteredOmaniStates);
            }

            // Show the drop-down if there are matches
            if(!filteredOmaniStates.isEmpty() && !newText.trim().isEmpty()) {
                if (!searchCombo.isShowing()) {
                    searchCombo.show();
                }

            }

        });

        // When a user selects a city from the drop-down menu
        searchCombo.setOnAction(e -> {
            if (isProcessing[0]) {return;}
            isProcessing[0] = true;

            try {
                String selectedCity = searchCombo.getValue();
                if (selectedCity != null && !selectedCity.isEmpty()) {
                    addWeatherCard(selectedCity.trim());

                    // Clear the comboBox properly
                    Platform.runLater( () -> {
                                searchCombo.getEditor().clear();
                                searchCombo.setValue(null);
                                searchCombo.setItems(omaniStates);
                                filteredOmaniStates.clear();
                                searchCombo.hide();

                                isProcessing[0] = false;
                            }
                    );
                } else {
                    isProcessing[0] = false;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                isProcessing[0] = false;
            }

        });
    }

    // A method to set up the initial weather panel when loading and launching the app
    private void setupWeatherPanel() {
        weatherPanel = new WeatherPanel();
        weatherPanelContainer.getChildren().add(weatherPanel);

    }

    // A method to set up a weatherCard click listener, which in turn displays weather info in the weather panel
    private void setupWeatherCardClickListener() {
        weatherListView.setOnMouseClicked(e -> {
            WeatherCard selectedCard = weatherListView.getSelectionModel().getSelectedItem();
            System.out.println("Card clicked");
            if (selectedCard != null) {
                System.out.println("Selected city: " + selectedCard.getCityName());

                String iconurl = "https://openweathermap.org/img/wn/" + selectedCard.getIconCode() + "@2x.png";

                weatherPanel.showWeatherInfo(
                        selectedCard.getCityName(),
                        selectedCard.getCurrentTemp(),
                        selectedCard.getDetailedDescription(),
                        selectedCard.getFeelsLikeTemp(),
                        selectedCard.getHumidity(),
                        selectedCard.getWind(),
                        selectedCard.getPressure(),
                        selectedCard.getVisibility(),
                        selectedCard.getClouds(),
                        selectedCard.getSunrise(),
                        selectedCard.getSunset(),
                        iconurl
                );
            } else {
                System.out.println("Selected card is null");
            }
        });
    }

    public void resetWeatherPanel () {
        if (weatherPanel != null) {
            weatherPanel.showInitialPanel();
        }
    }



}
package com.weatherom.models;

public class WeatherCard {
    private String cityName;
    private String currentTemp;
    private String description;
    private String detailedDescription;
    private String conditionID;
    private String iconCode;
    private String feelsLikeTemp;
    private String humidity;
    private String wind;
    private String pressure;
    private String visibility;
    private String clouds;
    private String sunrise;
    private String sunset;

    // Constructor method
    public WeatherCard (String cityName, String currentTemp, String description, String detailedDescription,  String conditionID, String lowTemp,
                        String highTemp, String feelsLikeTemp, String humidity, String wind, String pressure,
                        String visibility, String clouds, String sunrise, String sunset, String iconCode) {
        this.cityName = cityName;
        this.currentTemp = currentTemp;
        this.conditionID = conditionID;
        this.description = description;
        this.detailedDescription = detailedDescription;
        this.feelsLikeTemp = feelsLikeTemp;
        this.humidity = humidity;
        this.wind = wind;
        this.pressure = pressure;
        this.visibility = visibility;
        this.clouds = clouds;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.iconCode = iconCode;

    }

    // Getters
    public String getCityName() {
        return cityName;
    }

    public String getCurrentTemp() {
        return currentTemp;
    }

    public String getDescription() {
        return description;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public String getConditionID() {
        return conditionID;
    }

    public String getFeelsLikeTemp() {
        return feelsLikeTemp;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getWind() {
        return wind;
    }

    public String getPressure() {
        return pressure;
    }

    public String getVisibility() {
        return visibility;
    }

    public String getClouds() {
        return clouds;
    }

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public String getIconCode() {return iconCode;}
}

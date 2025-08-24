package com.weatherom.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class WeatherService {
    private static final String API_KEY= loadAPIKey();

    private static String loadAPIKey () {
        try {
            Properties props = new Properties();
            props.load(WeatherService.class.getResourceAsStream("/com/weatherom/config.properties"));
            return props.getProperty("api.key");
        } catch (Exception e) {
            System.out.println("Could not load the API key from config.properties");
            return "";
        }
    }

    public static JSONObject getWeatherData (JSONArray jarr) {
        try {
            if (jarr == null || jarr.isEmpty()) {
                System.out.println("No locations found in geocoding response");
                return null;
            }
            JSONObject location = jarr.getJSONObject(0);
            String lat = Double.toString(location.getDouble("lat"));
            String lon = Double.toString(location.getDouble("lon"));

            String weatherUrlString = "https://api.openweathermap.org/data/2.5/weather?lat=" +
            lat + "&lon=" + lon + "&appid=" + API_KEY + "&units=metric";

            URL weatherUrl = new URL(weatherUrlString);
            HttpURLConnection connection = (HttpURLConnection) weatherUrl.openConnection();

            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseStr = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                responseStr.append(line);
            }
            reader.close();

            // Parse the response
            return new JSONObject(responseStr.toString());
        }

        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONArray getGeoData (String city) {
        try {
            String geoUrlString = "http://api.openweathermap.org/geo/1.0/direct?q=" + city + ",OM" +
                    "&limit=1&appid=" + API_KEY;

            URL geoUrl = new URL(geoUrlString);
            HttpURLConnection geoConn = (HttpURLConnection) geoUrl.openConnection();

            geoConn.setRequestMethod("GET");
            geoConn.connect();

            int geoResponseCode = geoConn.getResponseCode();

            if (geoResponseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + geoResponseCode);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(geoConn.getInputStream()));
            StringBuilder responseStr = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                responseStr.append(line);
            }
            reader.close();

            // Parse the response
            return new JSONArray(responseStr.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

package tech.khash.weathercompare.utilities;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 *
 * Responsible for creating the Url using different parameters for OpenWeather
 */

public class OpenWeatherUtils {

    private final static String TAG = OpenWeatherUtils.class.getSimpleName();

    static final String OPENWEATHER_WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/weather?id=";

    static final String OPENWEATHER_FORECAST_BASE_URL = "https://api.openweathermap.org/data/2.5/forecast?id=";

    static final String API_ID = "&appid=";

    static final String API_KEY = "470cd029b949095fcc602ed656262f8b";

    static final String UNIT = "&units=";

    static final String METRIC = "metric";




    /**
     * This creates the URL using the city ID for the current weather
     * @param id : OpenWeatherMap city ID
     * @return URL : query URL (metric)
     */
    public static URL createWeatherUrlId(int id) {

        String url = OPENWEATHER_WEATHER_BASE_URL + id + UNIT + METRIC + API_ID + API_KEY;
        Log.d(TAG, "URL: " + url );

        URL queryUrl = null;
        try {
            queryUrl = new URL(url);
        } catch (MalformedURLException e) {

            Log.e(TAG, "Error creating URL from string", e);
        }
        return queryUrl;
    }//createWeatherUrlId

    /**
     * This creates the URL using the city ID for the current weather
     * @param id : OpenWeatherMap city ID
     * @return URL : query URL (metric)
     */
    public static URL createForecastUrlId(int id) {

        String url = OPENWEATHER_FORECAST_BASE_URL + id + UNIT + METRIC + API_ID + API_KEY;
        Log.d(TAG, "URL: " + url );

        URL queryUrl = null;
        try {
            queryUrl = new URL(url);
        } catch (MalformedURLException e) {

            Log.e(TAG, "Error creating URL from string", e);
        }
        return queryUrl;
    }//createWeatherUrlId

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        //establish the connection
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            //start reading the input
            InputStream in = urlConnection.getInputStream();

            //pass the inputstream to the scanner
            Scanner scanner = new Scanner(in);

            //Delimiter \A means next token, read until the next token (refer to GitHub page in the bookmarks of InputStream/Scanner)
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                //close scanner
                scanner.close();
                return null;
            }
        } finally {
            //finally close the url connection
            urlConnection.disconnect();
        }
    }//getResponseFromHttpUrl




}//main - class

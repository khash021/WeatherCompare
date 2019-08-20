package tech.khash.weathercompare.utilities;


import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Responsible for creating the Url using different parameters for AccuWeather
 */

public class AccuWeatherUtils {


    private final static String TAG = AccuWeatherUtils.class.getSimpleName();

    static final String ACCU_WEATHER_BASE_URL = "http://dataservice.accuweather.com/currentconditions/v1/";

    static final String API_ID = "?apikey=";

    static final String API_KEY = "Lxds8cj5vJGWk7n1XBe8McAhJhyFnCaw";

    static final String DETAILS_TRUE = "&details=true";


    /**
     * This creates the URL using the city ID for the current weather
     * @param id : city's id
     * @return : URL
     */
    public static URL createWeatherUrlId(String id) {

        String url = ACCU_WEATHER_BASE_URL + id + API_ID + API_KEY + DETAILS_TRUE;
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
}//class

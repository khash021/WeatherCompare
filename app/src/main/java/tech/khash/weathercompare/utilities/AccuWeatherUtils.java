package tech.khash.weathercompare.utilities;


import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

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

    private static final String ACCU_WEATHER_BASE_URL_CURRENT =
            "http://dataservice.accuweather.com/currentconditions/v1/";

    private static final String QUERY = "&q=";

    static final String ACCU_WEATHER_BASE_URL_LOCATION =
            "http://dataservice.accuweather.com/locations/v1/cities/geoposition/search";

    private static final String API_ID = "?apikey=";

    private static final String API_KEY = "Lxds8cj5vJGWk7n1XBe8McAhJhyFnCaw";

    private static final String DETAILS_TRUE = "&details=true";


    /**
     *  This creates a URL for getting location code from LatLng
     * @param latLng : LatLng of the location
     * @return : URL for getting the location code
     */
    public static URL createLocationCodeUrl (LatLng latLng) {
        String latLngString = latLng.latitude + "," + latLng.longitude;
        String urlString = ACCU_WEATHER_BASE_URL_LOCATION + API_ID + API_KEY + QUERY + latLngString;

        URL url = null;
        try {
            url = new URL(urlString);
            Log.v(TAG, "Location URL: " + url.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error creating location URL from string", e);
        }
        return url;
    }//createLocationCodeUrl


    /**
     * This creates the URL for current weather using city ID
     * @param id : city's id
     * @return : URL
     */
    public static URL createCurrentWeatherUrlId(String id) {

        String url = ACCU_WEATHER_BASE_URL_CURRENT + id + API_ID + API_KEY + DETAILS_TRUE;
        Log.v(TAG, "current weather URL: " + url );

        URL queryUrl = null;
        try {
            queryUrl = new URL(url);
            Log.v(TAG, "generated current url: " + queryUrl.toString());
        } catch (MalformedURLException e) {

            Log.e(TAG, "Error creating current weather URL from string", e);
        }
        return queryUrl;
    }//createCurrentWeatherUrlId

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

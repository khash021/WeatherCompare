package tech.khash.weathercompare.utilities;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class DarkSkyUtils {

    private static final String TAG = DarkSkyUtils.class.getSimpleName();

    /**
     * The general outline is:
     * "https://api.darksky.net/forecast/" + apiKey + "/" + lat + "," + long + "?" + exclude + "?" + unit;
     *
     * where exclude=[block] the block should be a comma-delimeted list (without spaces):
     *  currently, minutely, hourly, daily,alerts, flags
     *
     */

    private static final String DARK_SKY_BASE_URL = "https://api.darksky.net/forecast/";

    private static final String API_KEY = "f5e4285ed1e89d653fd1d99b04e375b7";

    private static final String LOCATION = "%s,%s";

    private static final String UNITS = "units=";

    private static final String UNITS_METRIC = "ca"; //same as si except speed is km/h

    private static final String EXCLUDE = "exclude=";

    private static final String EXCLUDE_BLOCK_CURRENT = "minutely,hourly,daily,alerts,flags";


    /**
     * This creates the URL for current weather (notice exclude parameters) using LatLng
     * @param latLng : LatLng
     * @return : URL
     */
    public static URL createCurrentUrl(LatLng latLng) {
        String latLngString = String.format(LOCATION, latLng.latitude, latLng.longitude);

        String url = DARK_SKY_BASE_URL + API_KEY + "/" + latLngString + "?" + EXCLUDE +
                EXCLUDE_BLOCK_CURRENT + "?" + UNITS + UNITS_METRIC;

        Log.d(TAG, "URL: " + url );

        URL queryUrl = null;
        try {
            queryUrl = new URL(url);
        } catch (MalformedURLException e) {

            Log.e(TAG, "Error creating URL from string", e);
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

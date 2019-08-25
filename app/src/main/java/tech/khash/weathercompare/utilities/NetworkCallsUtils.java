package tech.khash.weathercompare.utilities;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 *
 * Main class for making network calls using AsyncTask on the background
 *
 * It contains network calls for all weather providers as inner classes
 *
 * AW = AccuWeather ; DS = DarkSky ; OW = OpenWeather
 *
 */

public class NetworkCallsUtils {

    private static final String TAG = NetworkCallsUtils.class.getSimpleName();


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl (URL url) throws IOException {
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
        } catch (Exception e) {
            //Log error
            Log.e(TAG, "Error getting http response", e);
            return null;
        } finally {
            //finally close the url connection
            urlConnection.disconnect();
        }
    }//getResponseFromHttpUrl




    /*
        ----------------------------- AW --------------------------------------
     */

    /**
     * This is for getting the AW location code
     */
    public static class NetworkCallAccuWeatherCode extends AsyncTask<URL, Void, String> {

        private final String TAG = NetworkCallAccuWeatherCode.class.getSimpleName();

        public interface AsyncResponse {
            void processFinish(String output);
        }//AsyncResponse

        private AsyncResponse delegate = null;


        public NetworkCallAccuWeatherCode (AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected String doInBackground(URL... urls) {
            //Make the request
            try {
                String locationCodeResponse = AccuWeatherUtils.getResponseFromHttpUrl(urls[0]);
                return locationCodeResponse;
            } catch (IOException e) {
                Log.e(TAG, "Error establishing connection - AccuWeather - location ", e);
                return null;
            }
        }//doInBackground

        @Override
        protected void onPostExecute(String s) {
            //parse the response and get the code
            try {
                //parse response
                String locationCode = ParseJSON.parseAccuLocationCode(s);

                //pass data to the interface so we get it in the main activity
                delegate.processFinish(locationCode);

            } catch (JSONException e) {
                Log.e(TAG, "Error parsing location code response - AccuWeather ", e);
            }
        }//onPostExecute
    }//NetworkCallAccuWeatherCode


    /*
        ----------------------------- DS --------------------------------------
     */





    /*
        ----------------------------- OW --------------------------------------
     */

}

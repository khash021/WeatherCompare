package tech.khash.weathercompare.utilities;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import tech.khash.weathercompare.model.Weather;

/**
 * Main class for making network calls using AsyncTask on the background
 * <p>
 * It contains network calls for all weather providers as inner classes
 * <p>
 * AW = AccuWeather ; DS = DarkSky ; OW = OpenWeather
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


        public NetworkCallAccuWeatherCode(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected String doInBackground(URL... urls) {
            //Make the request
            try {
                String locationCodeResponse = NetworkCallsUtils.getResponseFromHttpUrl(urls[0]);
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
                delegate.processFinish(null);
            }
        }//onPostExecute
    }//NetworkCallAccuWeatherCode


    /**
     * For now we are using an AsyncTask loader class for getting the JSON response from AccuWeather
     */
    public static class AccuWeatherQueryTask extends AsyncTask<URL, Void, String> {

        private final String TAG = AccuWeatherQueryTask.class.getSimpleName();

        public interface AsyncResponse {
            void processFinish(Weather output);
        }

        private AsyncResponse delegate = null;

        public AccuWeatherQueryTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected String doInBackground(URL... urls) {
            //Make the request
            try {
                //get the response using the class, passing in our url
                String httpResponse = NetworkCallsUtils.getResponseFromHttpUrl(urls[0]);
                Log.d(TAG, "AccuWeather - JSON response: " + httpResponse);
                return httpResponse;
            } catch (IOException e) {
                Log.e(TAG, "Error establishing connection - AccuWeather ", e);
                return null;
            }
        }//doInBackground

        //return results
        @Override
        protected void onPostExecute(String s) {
            if (TextUtils.isEmpty(s)) {
                Log.e(TAG, "AccuWeatherQueryTask - postExecute - response = null");

            }
            //parse response and get the code
            Weather current = null;
            try {
                current = ParseJSON.parseAccuWeatherCurrent(s);
                //pass data to interface
                delegate.processFinish(current);
            } catch (JSONException e) {
                Log.e(TAG, "AccuWeatherQueryTask - postExecute - error parsing response");
                delegate.processFinish(null);
            }//try-catch
        }//onPostExecute
    }//AccuWeatherQueryTask - class

    /*
        ----------------------------- DS --------------------------------------
     */

    public static class DarkSkyQueryTask extends AsyncTask<URL, Void, String> {

        private final String TAG = DarkSkyQueryTask.class.getSimpleName();

        public interface AsyncResponse {
            void processFinish(Weather output);
        }

        private AsyncResponse delegate = null;

        public DarkSkyQueryTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected String doInBackground(URL... urls) {
            //Make the request
            try {
                //get the response using the class, passing in our url
                String httpResponse = NetworkCallsUtils.getResponseFromHttpUrl(urls[0]);
                Log.d(TAG, "DarkSkyQueryTask - JSON response: " + httpResponse);
                return httpResponse;
            } catch (IOException e) {
                Log.e(TAG, "Error establishing connection - DarkSkyQueryTask ", e);
                return null;
            }
        }//doInBackground


        //return results
        @Override
        protected void onPostExecute(String s) {
            if (TextUtils.isEmpty(s)) {
                Log.e(TAG, "DarkSkyQueryTask - postExecute - response = null");

            }
            //parse response and get the code
            Weather current = null;
            try {
                current = ParseJSON.parseDarkSkyCurrent(s);
                //pass data to interface
                delegate.processFinish(current);
            } catch (JSONException e) {
                Log.e(TAG, "DarkSkyQueryTask - postExecute - error parsing response");
                delegate.processFinish(null);
            }//try-catch
        }//onPostExecute

    }//DarkSkyQueryTask

    /*
        ----------------------------- OW --------------------------------------
     */

    public static class OpenWeatherQueryTask extends AsyncTask<URL, Void, String> {

        private final String TAG = OpenWeatherQueryTask.class.getSimpleName();

        public interface AsyncResponse {
            void processFinish(Weather output);
        }

        private AsyncResponse deletegate = null;

        public OpenWeatherQueryTask(AsyncResponse deletegate) {
            this.deletegate = deletegate;
        }

        @Override
        protected String doInBackground(URL... urls) {
            //Make the request
            try {
                //get the response using the class, passing in our url
                String httpResponse = NetworkCallsUtils.getResponseFromHttpUrl(urls[0]);
                Log.d(TAG, "OpenWeatherQueryTask - JSON response: " + httpResponse);
                return httpResponse;
            } catch (IOException e) {
                Log.e(TAG, "Error establishing connection - OpenWeatherQueryTask ", e);
                return null;
            }
        }//doInBackground

        @Override
        protected void onPostExecute(String s) {
            if (TextUtils.isEmpty(s)) {
                Log.e(TAG, "OpenWeatherQueryTask - postExecute - response = null");
            }
            //parse response and get the code
            Weather current = null;

            try {
                current = ParseJSON.parseOpenWeatherCurrent(s);
                //pass data to interface
                deletegate.processFinish(current);
            } catch (JSONException e) {
                Log.e(TAG, "OpenWeatherQueryTask - postExecute - error parsing response");
                deletegate.processFinish(null);
            }//try-catch
        }//onPostExecute
    }//OpenWeatherQueryTask

}//NetworkCallsUtils - class
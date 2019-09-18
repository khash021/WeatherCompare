package tech.khash.weathercompare.utilities;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
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
    private static String getResponseFromHttpUrl(URL url) throws IOException {
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
            return null;
        } finally {
            //finally close the url connection
            urlConnection.disconnect();
        }
    }//getResponseFromHttpUrl

    /**
     * This method returns the entire result from the HTTP response with specified header
     * (Used for Weather Unlocked)
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    private static String getResponseFromHttpUrlWithHeader(URL url) throws IOException {
        //establish the connection
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        //set the header for JSON
        urlConnection.setRequestProperty("Accept", "application/json");
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
            return null;
        } finally {
            //finally close the url connection
            urlConnection.disconnect();
        }
    }//getResponseFromHttpUrl

    /**
     * In all of these AsyncTasks, we create an interface called AsyncResponse which has the
     * function processFinishe which has an input parameter of String. This is our result (i.e.
     * JSON response).
     *
     * We use this interface to send results back to the host activity, so we can do what we want
     * in the host activity, rather thatn passing in context and running into problems here.
     *
     * Then once we get the results in onPostExecute, we pass that to the interface.
     *
     * In our host activities (current, forecast), we implement theis interface and override its
     * processFinish method. Whenever the background task finishes, we get the results in that
     * method in the host activity and we can process results and update db and UI from there.
     */


    /*
        ----------------------------- AW --------------------------------------
     */

    /**
     * This is for getting the AW location code
     */
    public static class NetworkCallAccuWeatherCode extends AsyncTask<URL, Void, String> {

        private final String TAG = NetworkCallAccuWeatherCode.class.getSimpleName();

        public interface AsyncResponse {
            void processFinish(HashMap<String, String> output);
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
                return null;
            }
        }//doInBackground

        @Override
        protected void onPostExecute(String s) {
            //parse the response and get the code
            try {
                //parse response
                HashMap<String, String> responseHashMap = ParseJSON.parseAccuLocationCode(s);

                //pass data to the interface so we get it in the main activity
                delegate.processFinish(responseHashMap);

            } catch (JSONException e) {
                delegate.processFinish(null);
            }
        }//onPostExecute
    }//NetworkCallAccuWeatherCode


    public static class AccuWeatherTodayTask extends AsyncTask<URL, Void, String> {

        private final String TAG = AccuWeatherTodayTask.class.getSimpleName();

        public interface AsyncResponse {
            void processFinish(Weather output);
        }

        private AsyncResponse delegate = null;

        public AccuWeatherTodayTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected String doInBackground(URL... urls) {
            //Make the request
            try {
                //get the response using the class, passing in our url
                String httpResponse = NetworkCallsUtils.getResponseFromHttpUrl(urls[0]);
                return httpResponse;
            } catch (IOException e) {
                return null;
            }
        }//doInBackground

        //return results
        @Override
        protected void onPostExecute(String s) {
            //parse response and get the code
            Weather current = null;
            try {
                current = ParseJSON.parseAccuWeatherToday(s);
                //pass data to interface
                delegate.processFinish(current);
            } catch (JSONException e) {
                delegate.processFinish(null);
            }//try-catch
        }//onPostExecute
    }//AccuWeatherTodayTask - class

    /**
     * Gets the AW forecast response from web.
     * It does not parse data here, it is done in the parent activity
     */
    public static class AccuWeatherForecastTask extends AsyncTask<URL, Void, String> {

        private static final String TAG = AccuWeatherForecastTask.class.getSimpleName();

        public interface AsyncResponse {
            void processFinish(String jsonResponse);
        }//interface

        private AsyncResponse delegate = null;

        //constructor
        public AccuWeatherForecastTask (AsyncResponse delegate) {
            this.delegate = delegate;
        }//constructor

        @Override
        protected String doInBackground(URL... urls) {
            //make the request
            try {
                //get the response using the class, passing in our url
                String httpResponse = NetworkCallsUtils.getResponseFromHttpUrl(urls[0]);
                return httpResponse;
            } catch (IOException e) {
                return null;
            }
        }//doInBackground

        @Override
        protected void onPostExecute(String s) {
            if (TextUtils.isEmpty(s)) {
                delegate.processFinish(null);
            } else {
                delegate.processFinish(s);
            }
        }//onPostExecute
    }//AccuWeatherForecastTask



    /*
        ----------------------------- DS --------------------------------------
     */

    public static class DarkSkyTodayTask extends AsyncTask<URL, Void, String> {

        private final String TAG = DarkSkyTodayTask.class.getSimpleName();

        public interface AsyncResponse {
            void processFinish(Weather output);
        }

        private AsyncResponse delegate = null;

        public DarkSkyTodayTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected String doInBackground(URL... urls) {
            //Make the request
            try {
                //get the response using the class, passing in our url
                String httpResponse = NetworkCallsUtils.getResponseFromHttpUrl(urls[0]);
                return httpResponse;
            } catch (IOException e) {
                return null;
            }
        }//doInBackground


        //return results
        @Override
        protected void onPostExecute(String s) {
            if (TextUtils.isEmpty(s)) {
                delegate.processFinish(null);
            }
            //parse response and get the code
            Weather current = null;
            try {
                current = ParseJSON.parseDarkSkyToday(s);
                //pass data to interface
                delegate.processFinish(current);
            } catch (JSONException e) {
                delegate.processFinish(null);
            }//try-catch
        }//onPostExecute
    }//DarkSkyTodayTask


    /**
     * Gets the DS forecast response from web.
     * It does not parse data here, it is done in the parent activity
     */
    public static class DarkSkyForecastTask extends AsyncTask<URL, Void, String> {

        private static final String TAG = DarkSkyForecastTask.class.getSimpleName();

        public interface AsyncResponse {
            void processFinish(String jsonResponse);
        }//interface

        private AsyncResponse delegate = null;

        //constructor
        public DarkSkyForecastTask (AsyncResponse delegate) {
            this.delegate = delegate;
        }//constructor

        @Override
        protected String doInBackground(URL... urls) {
            //make the request
            try {
                //get the response using the class, passing in our url
                String httpResponse = NetworkCallsUtils.getResponseFromHttpUrl(urls[0]);
                return httpResponse;
            } catch (IOException e) {
                return null;
            }
        }//doInBackground

        @Override
        protected void onPostExecute(String s) {
            if (TextUtils.isEmpty(s)) {
                return;
            }
            delegate.processFinish(s);
        }//onPostExecute
    }//DarkSkyForecastTask


    /*
        ----------------------------- WB --------------------------------------
     */

    public static class WeatherBitTodayTask extends AsyncTask<URL, Void, String> {

        private final String TAG = WeatherBitTodayTask.class.getSimpleName();

        public interface AsyncResponse {
            void processFinish(Weather output);
        }

        private AsyncResponse delegate = null;

        public WeatherBitTodayTask(AsyncResponse deletegate) {
            this.delegate = deletegate;
        }

        @Override
        protected String doInBackground(URL... urls) {
            //Make the request
            try {
                //get the response using the class, passing in our url
                String httpResponse = NetworkCallsUtils.getResponseFromHttpUrl(urls[0]);
                return httpResponse;
            } catch (IOException e) {
                return null;
            }
        }//doInBackground

        @Override
        protected void onPostExecute(String s) {
            if (TextUtils.isEmpty(s)) {
                delegate.processFinish(null);
            }
            //parse response and get the code
            Weather current = null;

            try {
                current = ParseJSON.parseWeatherBitToday(s);
                //pass data to interface
                delegate.processFinish(current);
            } catch (JSONException e) {
                delegate.processFinish(null);
            }//try-catch
        }//onPostExecute
    }//WeatherBitTodayTask

    /**
     * Gets the DS forecast response from web.
     * It does not parse data here, it is done in the parent activity
     */
    public static class WeatherBitForecastTask extends AsyncTask<URL, Void, String> {

        private static final String TAG = WeatherBitForecastTask.class.getSimpleName();

        public interface AsyncResponse {
            void processFinish(String jsonResponse);
        }//interface

        private AsyncResponse delegate = null;

        //constructor
        public WeatherBitForecastTask (AsyncResponse delegate) {
            this.delegate = delegate;
        }//constructor

        @Override
        protected String doInBackground(URL... urls) {
            //make the request
            try {
                //get the response using the class, passing in our url
                String httpResponse = NetworkCallsUtils.getResponseFromHttpUrl(urls[0]);
                return httpResponse;
            } catch (IOException e) {
                return null;
            }
        }//doInBackground

        @Override
        protected void onPostExecute(String s) {
            if (TextUtils.isEmpty(s)) {
                delegate.processFinish(null);
                return;
            }
            delegate.processFinish(s);
        }//onPostExecute
    }//WeatherBitForecastTask

    /*
        ----------------------------- WU --------------------------------------
     */

    public static class WeatherUnlockedTodayTask extends AsyncTask<URL, Void, String> {

        private final String TAG = WeatherUnlockedTodayTask.class.getSimpleName();

        public interface AsyncResponse {
            void processFinish(Weather output);
        }

        private AsyncResponse delegate = null;

        public WeatherUnlockedTodayTask(AsyncResponse deletegate) {
            this.delegate = deletegate;
        }

        @Override
        protected String doInBackground(URL... urls) {
            //Make the request
            try {
                //get the response using the class, passing in our url
                String httpResponse = NetworkCallsUtils.getResponseFromHttpUrlWithHeader(urls[0]);
                return httpResponse;
            } catch (IOException e) {
                return null;
            }
        }//doInBackground

        @Override
        protected void onPostExecute(String s) {
            if (TextUtils.isEmpty(s)) {
                delegate.processFinish(null);
            }
            //parse response and get the code
            Weather current = null;

            try {
                current = ParseJSON.parseWeatherUnlockedToday(s);
                //pass data to interface
                delegate.processFinish(current);
            } catch (JSONException e) {
                delegate.processFinish(null);
            }//try-catch
        }//onPostExecute
    }//WeatherUnlockedTodayTask

    /**
     * Gets the WU forecast response from web.
     * It does not parse data here, it is done in the parent activity
     */
    public static class WeatherUnlockedForecastTask extends AsyncTask<URL, Void, String> {

        private final String TAG = WeatherUnlockedForecastTask.class.getSimpleName();

        public interface AsyncResponse {
            void processFinish(String jsonResponse);
        }

        private AsyncResponse delegate = null;

        public WeatherUnlockedForecastTask(AsyncResponse deletegate) {
            this.delegate = deletegate;
        }

        @Override
        protected String doInBackground(URL... urls) {
            //Make the request
            try {
                //get the response using the class, passing in our url
                String httpResponse = NetworkCallsUtils.getResponseFromHttpUrlWithHeader(urls[0]);
                return httpResponse;
            } catch (IOException e) {
                return null;
            }
        }//doInBackground

        @Override
        protected void onPostExecute(String s) {
            if (TextUtils.isEmpty(s)) {
                delegate.processFinish(null);
            }
            delegate.processFinish(s);
        }//onPostExecute
    }//WeatherUnlockedForecastTask

    /*
        ----------------------------- IP Geolocation --------------------------------------
     */

    /**
     * Gets the respose from IP Geolocation API used for sunset/sunrise data
     */

    public static class SunriseSunsetTask extends AsyncTask<URL, Void, String> {
        private static final String TAG = SunriseSunsetTask.class.getSimpleName();


        public interface AsyncResponse {
            void processFinished(Boolean result);
        }

        private AsyncResponse delegate = null;

        public SunriseSunsetTask (AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected String doInBackground(URL... urls) {
            if (urls == null) {
                return null;
            }
            String response = null;

            try {
                response = NetworkCallsUtils.getResponseFromHttpUrl(urls[0]);
            } catch (IOException e) {
            }
            return response;
        }//doInBackground

        @Override
        protected void onPostExecute(String s) {
            Boolean isday = null;
                try {
                    isday = ParseJSON.parseSunriseSunset(s);
                } catch (JSONException e) {
                }
            delegate.processFinished(isday);
        }//onPostExecute
    }//SunriseSunsetTask

}//NetworkCallsUtils - class
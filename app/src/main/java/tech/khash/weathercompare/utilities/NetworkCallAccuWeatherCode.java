package tech.khash.weathercompare.utilities;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

/**
 * This AsyncTask makes the network call off UI thread to get the location code (AW) for loc that
 * was just created.
 *
 * In order to get the results back from postExecute, we need to make an interface, then implement
 * it in the MainActivity to get the results (location code)
 *
 */
public class NetworkCallAccuWeatherCode extends AsyncTask<URL, Void, String> {

    private final String TAG = NetworkCallAccuWeatherCode.class.getSimpleName();

    public interface AsyncResponse {
        void processFinish(String output);
    }//AsyncResponse

    public AsyncResponse delegate = null;

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


package app.khash.weathertry;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import app.khash.weathertry.utilities.AccuWeatherUtils;
import app.khash.weathertry.utilities.DarkSkyUtils;
import app.khash.weathertry.utilities.OpenWeatherUtils;
import app.khash.weathertry.utilities.ParseJSON;

//TODO: extensive cleaning, commenting, and re-thinking the entire architecture of the desing and flow


public class MainActivity extends AppCompatActivity {

    //TODO: search for location, and automatic find me
    //TODO: move everything away from inner classes and use task loader
    //TODO: do the forecast
    //TODO: options for changing units
    //TODO: sunrise and sunset

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String CANMORE = "canmore";
    private static final int CANMORE_ID_OPEN_WEATHER = 7871396;
    private static final String CANMORE_ID_ACCU_WEATHER = "52903_PC";
    static final String CANMORE_LAT_LONG = "/51.09,-115.35";
    private TextView mResultsText;
    private ImageView mIconImage;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultsText = findViewById(R.id.text_results);
        mProgress = findViewById(R.id.progress_bar);
        mIconImage = findViewById(R.id.image_icon);

        //Getting Open Weather
        Button canmoreOpenWeather = findViewById(R.id.button_canmore_current_open_weather);
        canmoreOpenWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URL canmoreUrl = OpenWeatherUtils.createWeatherUrlId(CANMORE_ID_OPEN_WEATHER);

                if (canmoreUrl == null) {
                    showError();
                } else {
                    //instantiate a OpenWeatherQueryTask object and then passing in our URL
                    OpenWeatherQueryTask openWeatherQueryTask = new OpenWeatherQueryTask();
                    openWeatherQueryTask.execute(canmoreUrl);
                }

            }
        });

        //Getting Accu Weather
        Button canmoreAccuWeather = findViewById(R.id.button_canmore_forecast_accu_weather);
        canmoreAccuWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                URL canmoreUrl = AccuWeatherUtils.createWeatherUrlId(CANMORE_ID_ACCU_WEATHER);

                if (canmoreUrl == null) {
                    showError();
                } else {
                    //instantiate a AccuWeatherQueryTask object and then passing in our URL
                    AccuWeatherQueryTask accuWeatherQueryTask = new AccuWeatherQueryTask();
                    accuWeatherQueryTask.execute(canmoreUrl);
                }

            }
        });

        //Getting Dark Sky
        Button canmoreDarkSky = findViewById(R.id.button_canmore_current_dark_sky);
        canmoreDarkSky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                URL url = DarkSkyUtils.createWeatherUrlId(CANMORE_LAT_LONG);

                if (url == null) {
                    showError();
                } else {

                }

            }
        });

    }//onCreate

    /**
     * Helper methods for showing the result or error
     */
    private void showResults(String results) {

        if (results.contains(";")) {
            //get the index of ;
            int index = results.indexOf(";");
            //get thr forecast
            String forecast = results.substring(0, index);
            mResultsText.setText(forecast);
            //get the uri
            String iconUrl = results.substring(index + 1);
            Uri iconUri = createUri(iconUrl);
            //use glide to set it
            Glide.with(this).load(iconUri).into(mIconImage);
        } else {
            mResultsText.setText(results);
        }

    }//showResults

    private void showError() {
        mResultsText.setText("Error");
    }//showError

    //helper method for creating Uri
    private Uri createUri(String s) {
        return Uri.parse(s);
    }//createUri

    /**
     * For now we are using an AsyncTask loader class for getting the JSON response from Open Weather
     */

    public class OpenWeatherQueryTask extends AsyncTask<URL, Void, String> {

        //show the progress bar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgress.setVisibility(View.VISIBLE);
        }//onPreExecute

        //http query
        @Override
        protected String doInBackground(URL... urls) {

            //Make the request
            try {
                //get the response using the class, passing in our url
                String httpResponse = OpenWeatherUtils.getResponseFromHttpUrl(urls[0]);
                Log.d(TAG, "OpenWeather JSON response: " + httpResponse);
                return httpResponse;
            } catch (IOException e) {
                Log.e(TAG, "Error establishing connection - OpenWeather ", e);
                return null;
            }
        }//doInBackground

        //update UI
        @Override
        protected void onPostExecute(String s) {
            mProgress.setVisibility(View.INVISIBLE);

            //dummy check
            if (s == null || TextUtils.isEmpty(s)) {
                //show error if the response is empty or null
                showError();
            } else {
                //pass in the response to be decoded
                String forecast = null;
                try {
                    forecast = ParseJSON.parseOpenWeatherCurrent(s);
                } catch (JSONException e) {
                    Log.e(TAG, "Error getting results decoded - OpenWeather", e);
                }//try-catch
                //check the results for null or empty
                if (forecast == null || TextUtils.isEmpty(forecast)) {
                    showError();
                } else {
                    showResults(forecast);
                }

            }//if-else
        }//onPostExecute
    }//OpenWeatherQueryTask

    /**
     * For now we are using an AsyncTask loader class for getting the JSON response from AccuWeather
     */
    public class AccuWeatherQueryTask extends AsyncTask<URL, Void, String> {

        //show the progress bar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgress.setVisibility(View.VISIBLE);
        }//onPreExecute

        @Override
        protected String doInBackground(URL... urls) {
            //Make the request
            try {
                //get the response using the class, passing in our url
                String httpResponse = AccuWeatherUtils.getResponseFromHttpUrl(urls[0]);
                Log.d(TAG, "AccuWeather - JSON response: " + httpResponse);
                return httpResponse;
            } catch (IOException e) {
                Log.e(TAG, "Error establishing connection - AccuWeather ", e);
                return null;
            }
        }//doInBackground

        //update UI
        @Override
        protected void onPostExecute(String s) {
            mProgress.setVisibility(View.INVISIBLE);

            //dummy check
            if (s == null || TextUtils.isEmpty(s)) {
                //show error if the response is empty or null
                showError();
            } else {
                //pass in the response to be decoded
                String forecast = null;
                try {
                    forecast = ParseJSON.parseAccuWeatherCurrent(s);
                } catch (JSONException e) {
                    Log.e(TAG, "Error getting results decoded - AccuWeather", e);
                }//try-catch
                //check the results for null or empty
                if (forecast == null || TextUtils.isEmpty(forecast)) {
                    showError();
                } else {
                    showResults(forecast);
                }

            }//if-else
        }//onPostExecute
    }//AccuWeatherQueryTask - class

    /**
     * For now we are using an AsyncTask loader class for getting the JSON response from DarkSkay
     */
    public class DarkSkyQueryTask extends AsyncTask<URL, Void, String> {

        //show the progress bar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgress.setVisibility(View.VISIBLE);
        }//onPreExecute

        @Override
        protected String doInBackground(URL... urls) {
            //Make the request
            try {
                //get the response using the class, passing in our url
                String httpResponse = DarkSkyUtils.getResponseFromHttpUrl(urls[0]);
                Log.d(TAG, "DarkSky - JSON response: " + httpResponse);
                return httpResponse;
            } catch (IOException e) {
                Log.e(TAG, "Error establishing connection - DarkSky ", e);
                return null;
            }
        }//doInBackground

        //update UI
        @Override
        protected void onPostExecute(String s) {
            mProgress.setVisibility(View.INVISIBLE);

            //dummy check
            if (s == null || TextUtils.isEmpty(s)) {
                //show error if the response is empty or null
                showError();
            } else {
                //pass in the response to be decoded
                String forecast = null;
                try {
                    forecast = ParseJSON.parseDarkSkyCurrent(s);
                } catch (JSONException e) {
                    Log.e(TAG, "Error getting results decoded - DarkSky", e);
                }//try-catch
                //check the results for null or empty
                if (forecast == null || TextUtils.isEmpty(forecast)) {
                    showError();
                } else {
                    showResults(forecast);
                }

            }//if-else
        }//onPostExecute
    }//AccuWeatherQueryTask - class


}//main-class

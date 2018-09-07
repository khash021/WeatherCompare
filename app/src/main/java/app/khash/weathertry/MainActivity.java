package app.khash.weathertry;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import app.khash.weathertry.utilities.NetworkUtils;
import app.khash.weathertry.utilities.ParseJSON;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String CANMORE = "canmore";
    private static final int CANMORE_ID = 7871396;
    private TextView mResultsText;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultsText = findViewById(R.id.text_results);
        mProgress = findViewById(R.id.progress_bar);

        Button canmoreNow = findViewById(R.id.button_canmore);
        canmoreNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                URL canmoreUrl = NetworkUtils.createWeatherUrlId(CANMORE_ID);

                if (canmoreUrl == null) {
                    showError();
                } else {
                    //instantiate a OpenWeatherQueryTask object and then passing in our URL
                    OpenWeatherQueryTask openWeatherQueryTask = new OpenWeatherQueryTask();
                    openWeatherQueryTask.execute(canmoreUrl);
                }

            }
        });

        Button canmoreForecast = findViewById(R.id.button_canmore_forecast);
        canmoreForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                URL canmoreUrl = NetworkUtils.createForecastUrlId(CANMORE_ID);

                if (canmoreUrl == null) {
                    showError();
                } else {
                    //instantiate a OpenWeatherQueryTask object and then passing in our URL
                    OpenWeatherQueryTask openWeatherQueryTask = new OpenWeatherQueryTask();
                    openWeatherQueryTask.execute(canmoreUrl);
                }

            }
        });

    }//onCreate

    /**
     * Helper methods for showing the result or error
     */
    private void showResults(String results) {
        mResultsText.setText(results);
    }//showResults

    private void showError() {
        mResultsText.setText("Error");
    }//showError

    /**
     * For now we are using an AsyncTask loader class for getting the JSON response
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
                String httpResponse = NetworkUtils.getResponseFromHttpUrl(urls[0]);
                Log.d(TAG, "JSON response: " + httpResponse);
                return httpResponse;
            } catch (IOException e) {
                Log.e(TAG, "Error establishing connection ", e);
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
                    Log.e(TAG, "Error getting results decoded", e);
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


}//main-class

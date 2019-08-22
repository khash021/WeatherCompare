package tech.khash.weathercompare;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import tech.khash.weathercompare.model.Loc;
import tech.khash.weathercompare.model.Weather;
import tech.khash.weathercompare.utilities.AccuWeatherUtils;
import tech.khash.weathercompare.utilities.DarkSkyUtils;
import tech.khash.weathercompare.utilities.OpenWeatherUtils;
import tech.khash.weathercompare.utilities.ParseJSON;
import tech.khash.weathercompare.utilities.SaveLoadList;

//TODO: extensive cleaning, commenting, and re-thinking the entire architecture of the desing and flow


public class CompareActivity extends AppCompatActivity {

    //TODO: remove ad for now

    //TODO: search for location, and automatic find me
    //TODO: move everything away from inner classes and use task loader
    //TODO: do the forecast
    //TODO: options for changing units
    //TODO: sunrise and sunset
    //TODO: make these buttons into fragments so we can swipe right and left
    //TODO: create a comparison table
    //TODO: use divider for the results
    //TODO: use somehow manage progress bar to go off once everything is loaded

    private final static String TAG = CompareActivity.class.getSimpleName();

    private final static String CANMORE = "canmore";
    private static final int CANMORE_ID_OPEN_WEATHER = 7871396;
    private static final String CANMORE_ID_ACCU_WEATHER = "52903_PC";
    static final String CANMORE_LAT_LONG = "/51.09,-115.35";
    private ImageView mIconImage;
    private ProgressBar progressBar;

    private Loc currentLoc;

    //this is to be used for tracking the progress bar (it will be set to -1 if it a single update; otherwise
    //it will be incremented so the last query to get finished will remove it
    private int tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        Log.v(TAG, "onCreate Called");
        Log.v(TAG, "Package name: " + getPackageName());

        tracker = 0;

        progressBar = findViewById(R.id.progress_bar);
//        mIconImage = findViewById(R.id.image_icon);

        //get the loc id from intent extra
        if (getIntent().hasExtra(MainActivity.COMPARE_EXTRA_LOC_ID)) {
            String id = getIntent().getStringExtra(MainActivity.COMPARE_EXTRA_LOC_ID);
            if (!TextUtils.isEmpty(id)) {
                //get the corresponding loc
                Loc loc = SaveLoadList.getLocFromDb(this, id);
                if (loc != null) {
                    currentLoc = loc;
                }//null-loc
            }//empty string
        }//has extra

        //Getting Open Weather
        Button buttonOpenWeather = findViewById(R.id.button_open_weather);
        buttonOpenWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check current loc
                if (currentLoc != null) {
                    //get the current weather URL
                    URL latLngUrl = OpenWeatherUtils.createCurrentUrlLatLng(currentLoc.getLatLng());

                    if (latLngUrl == null) {
                        showOpenWeatherError();
                        Log.v(TAG, "OW - LatLng URL null");
                    } else {
                        OpenWeatherQueryTask openWeatherQueryTask = new OpenWeatherQueryTask();
                        //set the tracker to -1 so we know it is a single load
                        tracker = -1;
                        openWeatherQueryTask.execute(latLngUrl);
                    }//if-else null url
                }//null loc
            }//onClick
        });//onClickListener

        //Getting Accu Weather
        Button buttonAccuWeather = findViewById(R.id.button_accu_weather);
        buttonAccuWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: later we should just get the code from Loc
                //check for null loc
                if (currentLoc != null) {

                    //get location code url
                    //TODO: later we should just get the code from Loc
                    if (!currentLoc.hasKey()) {
                        //doesn't have key, so we need to get it
                        //get the location code
                        URL locationCodeUrl = AccuWeatherUtils.createLocationCodeUrl(currentLoc.getLatLng());

                        if (locationCodeUrl == null) {
                            showAccuWeatherError();
                            Log.v(TAG, "AW - Location code URL null");
                        } else {
                            //instantiate AccuWeatherLocationQueryTask to get the location key
                            AccuWeatherLocationQueryTask locationQueryTask = new AccuWeatherLocationQueryTask(getApplicationContext());
                            //set the tracker to -1 so we know it is a single load
                            tracker = -1;
                            locationQueryTask.execute(locationCodeUrl);
                        }//null-loc
                    } else {
                        //this means we already have a key, so just start query for weather
                        //create weather URL
                        URL url = AccuWeatherUtils.createCurrentWeatherUrlId(currentLoc.getKey());
                        if (url == null) {
                            showAccuWeatherError();
                            Log.v(TAG, "AW - weather URL null");
                        } else {
                            //instantiate a AccuWeatherQueryTask object and then passing in our URL
                            AccuWeatherQueryTask accuWeatherQueryTask = new AccuWeatherQueryTask();
                            //set the tracker to -1 so we know it is a single load
                            tracker = -1;
                            accuWeatherQueryTask.execute(url);
                        }//if-else null url
                    }//if-else loc.hasKey
                }//current loc-null
            }//onClick
        });//onClickListener

        //Getting Dark Sky
        Button buttonDarkSky = findViewById(R.id.button_dark_sky);
        buttonDarkSky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                URL url = DarkSkyUtils.createWeatherUrlId(CANMORE_LAT_LONG);

                if (url == null) {
                    showDarkSkyError();
                } else {
                    DarkSkyQueryTask darkSkyQueryTask = new DarkSkyQueryTask();
                    //set the tracker to -1 so we know it is a single load
                    tracker = -1;
                    darkSkyQueryTask.execute(url);
                }
            }
        });

    }//onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compare_menu, menu);

        //return true since we have managed it
        return true;
    }//onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_refresh) {

            //open weather
            URL openWeatherUrl = OpenWeatherUtils.createCurrentUrlId(CANMORE_ID_OPEN_WEATHER);

            if (openWeatherUrl == null) {
                showOpenWeatherError();
            } else {
                //instantiate a OpenWeatherQueryTask object and then passing in our URL
                OpenWeatherQueryTask openWeatherQueryTask = new OpenWeatherQueryTask();
                openWeatherQueryTask.execute(openWeatherUrl);
            }

            //Accu Weather
            URL accuUrl = AccuWeatherUtils.createCurrentWeatherUrlId(CANMORE_ID_ACCU_WEATHER);

            if (accuUrl == null) {
                showAccuWeatherError();
            } else {
                //instantiate a AccuWeatherQueryTask object and then passing in our URL
                AccuWeatherQueryTask accuWeatherQueryTask = new AccuWeatherQueryTask();
                accuWeatherQueryTask.execute(accuUrl);
            }

            //DarkSky
            URL darkSkyUrl = DarkSkyUtils.createWeatherUrlId(CANMORE_LAT_LONG);

            if (darkSkyUrl == null) {
                showDarkSkyError();
            } else {
                DarkSkyQueryTask darkSkyQueryTask = new DarkSkyQueryTask();
                darkSkyQueryTask.execute(darkSkyUrl);
            }
        }//if

        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected

    /**
     * For now we are using an AsyncTask loader class for getting the JSON response from Open Weather
     */

    public class OpenWeatherQueryTask extends AsyncTask<URL, Void, String> {

        //show the progress bar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }//onPreExecute

        //http query
        @Override
        protected String doInBackground(URL... urls) {

            //Make the request
            try {
                //get the response using the class, passing in our url
                String httpResponse = OpenWeatherUtils.getResponseFromHttpUrl(urls[0]);
                Log.v(TAG, "OpenWeather JSON response: " + httpResponse);
                return httpResponse;
            } catch (IOException e) {
                Log.e(TAG, "Error establishing connection - OpenWeather ", e);
                return null;
            }
        }//doInBackground

        //update UI
        @Override
        protected void onPostExecute(String s) {
            //if it was a single load, then remove progress bar and set it back to 0; otherwise
            //if it is not 2, just increment
            if (tracker == -1 || tracker == 2) {

                //remove progress bar and reset the tracker
                progressBar.setVisibility(View.INVISIBLE);
                tracker = 0;
            } else {
                //this means this is part the group load and just increment
                tracker++;
            }

            //dummy check
            if (s == null || TextUtils.isEmpty(s)) {
                //show error if the response is empty or null
                showOpenWeatherError();
            } else {
                Weather current = null;
                try {
                    current = ParseJSON.parseOpenWeatherCurrent(s);
                } catch (JSONException e) {
                    Log.e(TAG, "Error getting results decoded - OpenWeather", e);
                }//try-catch
                //check the results for null
                if (current == null) {
                    showOpenWeatherError();
                } else {
                    showOpenWeatherResults(current);
                }

            }//if-else
        }//onPostExecute
    }//OpenWeatherQueryTask

    public class AccuWeatherLocationQueryTask extends AsyncTask<URL, Void, String> {

        private Context context;

        public AccuWeatherLocationQueryTask(Context context) {
            this.context = context;
        }

        //show progress bar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }//onPreExecute

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
                String locationCode = ParseJSON.parseAccuLocationCode(s);
                //set the loc
                if (currentLoc != null) {
                    currentLoc.setKey(locationCode);
                    //TODO: testing
                    //update the Loc in list
                    SaveLoadList.replaceLocInDb(context, currentLoc);
                }//loc null
                //create weather URL
                URL url = AccuWeatherUtils.createCurrentWeatherUrlId(locationCode);
                if (url == null) {
                    showAccuWeatherError();
                    Log.v(TAG, "AW - weather URL null");
                } else {
                    //instantiate a AccuWeatherQueryTask object and then passing in our URL
                    AccuWeatherQueryTask accuWeatherQueryTask = new AccuWeatherQueryTask();
                    //set the tracker to -1 so we know it is a single load
                    tracker = -1;
                    accuWeatherQueryTask.execute(url);
                }//if-else null url

            } catch (JSONException e) {
                Log.e(TAG, "Error parsing location code response - AccuWeather ", e);
            }
        }//onPostExecute
    }//AccuWeatherLocationQueryTask

    /**
     * For now we are using an AsyncTask loader class for getting the JSON response from AccuWeather
     */
    public class AccuWeatherQueryTask extends AsyncTask<URL, Void, String> {

        //show the progress bar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }//onPreExecute

        @Override
        protected String doInBackground(URL... urls) {
            //Make the request
            try {
                //get the response using the class, passing in our url
                String httpResponse = AccuWeatherUtils.getResponseFromHttpUrl(urls[0]);
                Log.v(TAG, "AccuWeather - JSON response: " + httpResponse);
                return httpResponse;
            } catch (IOException e) {
                Log.e(TAG, "Error establishing connection - AccuWeather ", e);
                return null;
            }
        }//doInBackground

        //update UI
        @Override
        protected void onPostExecute(String s) {
            //if it was a single load, then remove progress bar and set it back to 0; otherwise
            //if it is not 2, just increment
            if (tracker == -1 || tracker == 2) {

                //remove progress bar and reset the tracker
                progressBar.setVisibility(View.INVISIBLE);
                tracker = 0;
            } else {
                //this means this is part the group load and just increment
                tracker++;
            }

            //dummy check
            if (s == null || TextUtils.isEmpty(s)) {
                //show error if the response is empty or null
                showAccuWeatherError();
            } else {
                Weather current = null;
                try {
                    current = ParseJSON.parseAccuWeatherCurrent(s);
                } catch (JSONException e) {
                    Log.e(TAG, "Error getting results decoded - AccuWeather", e);
                }//try-catch
                //check the results for null
                if (current == null) {
                    showAccuWeatherError();
                } else {
                    showAccuWeatherResults(current);
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
            progressBar.setVisibility(View.VISIBLE);
        }//onPreExecute

        @Override
        protected String doInBackground(URL... urls) {
            //Make the request
            try {
                //get the response using the class, passing in our url
                String httpResponse = DarkSkyUtils.getResponseFromHttpUrl(urls[0]);
                Log.v(TAG, "DarkSky - JSON response: " + httpResponse);
                return httpResponse;
            } catch (IOException e) {
                Log.e(TAG, "Error establishing connection - DarkSky ", e);
                return null;
            }
        }//doInBackground

        //update UI
        @Override
        protected void onPostExecute(String s) {
            //if it was a single load, then remove progress bar and set it back to 0; otherwise
            //if it is not 2, just increment
            if (tracker == -1 || tracker == 2) {

                //remove progress bar and reset the tracker
                progressBar.setVisibility(View.INVISIBLE);
                tracker = 0;
            } else {
                //this means this is part the group load and just increment
                tracker++;
            }

            //dummy check
            if (s == null || TextUtils.isEmpty(s)) {
                //show error if the response is empty or null
                showDarkSkyError();
            } else {
                Weather current = null;
                try {
                    current = ParseJSON.parseDarkSkyCurrent(s);
                } catch (JSONException e) {
                    Log.e(TAG, "Error getting results decoded - DarkSky", e);
                }//try-catch
                //check the results for null or empty
                if (current == null) {
                    showDarkSkyError();
                } else {
                    showDarkSkyResults(current);
                }

            }//if-else
        }//onPostExecute
    }//AccuWeatherQueryTask - class


    /**
     * Helper methods for displaying the results and Errors
     */
    private void showOpenWeatherResults(Weather weather) {

        //TODO: icons
        if (weather == null) {
            showOpenWeatherError();
            return;
        }
        //find views
        TextView summary, temp, dew, press, humidity, wind, gust, cloud, visibility;

        summary = findViewById(R.id.text_summary_open_weather);
        temp = findViewById(R.id.text_temp_open_weather);
        dew = findViewById(R.id.text_dew_open_weather);
        press = findViewById(R.id.text_press_open_weather);
        humidity = findViewById(R.id.text_humidity_open_weather);
        wind = findViewById(R.id.text_wind_open_weather);
        gust = findViewById(R.id.text_gust_open_weather);
        cloud = findViewById(R.id.text_cloud_open_weather);
        visibility = findViewById(R.id.text_visibility_open_weather);

        //set values
        summary.setText(weather.getSummary());
        temp.setText(weather.getTemperature());
        dew.setText(weather.getDewPoint());
        press.setText(weather.getPressure());
        humidity.setText(weather.getHumidity());
        wind.setText(weather.getWindSpeed() + " " + weather.getWindDirection());
        gust.setText(weather.getWindGust());
        cloud.setText(weather.getCloudCoverage());
        visibility.setText(weather.getVisibility());
    }//showOpenWeatherResults

    private void showOpenWeatherError() {

        TextView summary, temp, dew, press, humidity, wind, gust, cloud, visibility;

        summary = findViewById(R.id.text_summary_open_weather);
        temp = findViewById(R.id.text_temp_open_weather);
        dew = findViewById(R.id.text_dew_open_weather);
        press = findViewById(R.id.text_press_open_weather);
        humidity = findViewById(R.id.text_humidity_open_weather);
        wind = findViewById(R.id.text_wind_open_weather);
        gust = findViewById(R.id.text_gust_open_weather);
        cloud = findViewById(R.id.text_cloud_open_weather);
        visibility = findViewById(R.id.text_visibility_open_weather);

        String error = "Error";
        String empty = "";
        //set values
        summary.setText(error);
        temp.setText(empty);
        dew.setText(empty);
        press.setText(empty);
        humidity.setText(empty);
        wind.setText(empty);
        gust.setText(empty);
        cloud.setText(empty);
        visibility.setText(empty);
    }//showOpenWeatherError

    private void showDarkSkyResults(Weather weather) {

        //TODO: icons
        if (weather == null) {
            showOpenWeatherError();
            return;
        }
        //find views
        TextView summary, temp, dew, press, humidity, wind, gust, cloud, visibility;

        summary = findViewById(R.id.text_summary_dark_sky);
        temp = findViewById(R.id.text_temp_dark_sky);
        dew = findViewById(R.id.text_dew_dark_sky);
        press = findViewById(R.id.text_press_dark_sky);
        humidity = findViewById(R.id.text_humidity_dark_sky);
        wind = findViewById(R.id.text_wind_dark_sky);
        gust = findViewById(R.id.text_gust_dark_sky);
        cloud = findViewById(R.id.text_cloud_dark_sky);
        visibility = findViewById(R.id.text_visibility_dark_sky);

        //set values
        summary.setText(weather.getSummary());
        temp.setText(weather.getTemperature());
        dew.setText(weather.getDewPoint());
        press.setText(weather.getPressure());
        humidity.setText(weather.getHumidity());
        wind.setText(weather.getWindSpeed() + " " + weather.getWindDirection());
        gust.setText(weather.getWindGust());
        cloud.setText(weather.getCloudCoverage());
        visibility.setText(weather.getVisibility());
    }//showOpenWeatherResults

    private void showDarkSkyError() {

        TextView summary, temp, dew, press, humidity, wind, gust, cloud, visibility;

        summary = findViewById(R.id.text_summary_dark_sky);
        temp = findViewById(R.id.text_temp_dark_sky);
        dew = findViewById(R.id.text_dew_dark_sky);
        press = findViewById(R.id.text_press_dark_sky);
        humidity = findViewById(R.id.text_humidity_dark_sky);
        wind = findViewById(R.id.text_wind_dark_sky);
        gust = findViewById(R.id.text_gust_dark_sky);
        cloud = findViewById(R.id.text_cloud_dark_sky);
        visibility = findViewById(R.id.text_visibility_dark_sky);

        String error = "Error";
        String empty = "";
        //set values
        summary.setText(error);
        temp.setText(empty);
        dew.setText(empty);
        press.setText(empty);
        humidity.setText(empty);
        wind.setText(empty);
        gust.setText(empty);
        cloud.setText(empty);
        visibility.setText(empty);
    }//showOpenWeatherError

    private void showAccuWeatherResults(Weather weather) {

        //TODO: icons
        if (weather == null) {
            showOpenWeatherError();
            return;
        }
        //find views
        TextView summary, temp, dew, press, humidity, wind, gust, cloud, visibility;

        summary = findViewById(R.id.text_summary_accu);
        temp = findViewById(R.id.text_temp_accu);
        dew = findViewById(R.id.text_dew_accu);
        press = findViewById(R.id.text_press_accu);
        humidity = findViewById(R.id.text_humidity_accu);
        wind = findViewById(R.id.text_wind_accu);
        gust = findViewById(R.id.text_gust_accu);
        cloud = findViewById(R.id.text_cloud_accu);
        visibility = findViewById(R.id.text_visibility_accu);

        //set values
        summary.setText(weather.getSummary());
        temp.setText(weather.getTemperature());
        dew.setText(weather.getDewPoint());
        press.setText(weather.getPressure());
        humidity.setText(weather.getHumidity());
        wind.setText(weather.getWindSpeed() + " " + weather.getWindDirection());
        gust.setText(weather.getWindGust());
        cloud.setText(weather.getCloudCoverage());
        visibility.setText(weather.getVisibility());
    }//showOpenWeatherResults

    private void showAccuWeatherError() {

        TextView summary, temp, dew, press, humidity, wind, gust, cloud, visibility;

        summary = findViewById(R.id.text_summary_accu);
        temp = findViewById(R.id.text_temp_accu);
        dew = findViewById(R.id.text_dew_accu);
        press = findViewById(R.id.text_press_accu);
        humidity = findViewById(R.id.text_humidity_accu);
        wind = findViewById(R.id.text_wind_accu);
        gust = findViewById(R.id.text_gust_accu);
        cloud = findViewById(R.id.text_cloud_accu);
        visibility = findViewById(R.id.text_visibility_accu);

        String error = "Error";
        String empty = "";
        //set values
        summary.setText(error);
        temp.setText(empty);
        dew.setText(empty);
        press.setText(empty);
        humidity.setText(empty);
        wind.setText(empty);
        gust.setText(empty);
        cloud.setText(empty);
        visibility.setText(empty);
    }//showOpenWeatherError



}//main-class

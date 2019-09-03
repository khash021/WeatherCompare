package tech.khash.weathercompare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.net.URL;
import java.util.ArrayList;

import tech.khash.weathercompare.model.Constant;
import tech.khash.weathercompare.model.Loc;
import tech.khash.weathercompare.model.Weather;
import tech.khash.weathercompare.utilities.HelperFunctions;
import tech.khash.weathercompare.utilities.NetworkCallsUtils;
import tech.khash.weathercompare.utilities.SaveLoadList;

//TODO: extensive cleaning, commenting, and re-thinking the entire architecture of the desing and flow


public class CompareActivity extends AppCompatActivity {

    //TODO: search for location, and automatic find me
    //TODO: move everything away from inner classes and use task loader


    //TODO: use divider for the results

    //TODO: right now the int tracker is not working when we load them all (it works for single ones) - do something about that

    private final static String TAG = CompareActivity.class.getSimpleName();

    private ImageView mIconImage;
    private ProgressBar progressBar;

    //for sending and receiving location from add location activity
    private static final int FORECAST_REQUEST_CODE = 1;

    private Loc currentLoc;

    //this is to be used for tracking the progress bar (it will be set to -1 if it a single update; otherwise
    //it will be incremented so the last query to get finished will remove it
    private int tracker;

    private TextView textCityName;
    private ArrayList<Integer> menuIdList;
    ArrayList<Loc> locArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        Log.d(TAG, "onCreate Called");
        Log.d(TAG, "Package name: " + getPackageName());

        tracker = 0;

        progressBar = findViewById(R.id.progress_bar);
//        mIconImage = findViewById(R.id.image_icon);
        textCityName = findViewById(R.id.text_city_name);

        //get the loc id from intent extra
        if (getIntent().hasExtra(Constant.INTENT_EXTRA_LOC_NAME)) {
            String id = getIntent().getStringExtra(Constant.INTENT_EXTRA_LOC_NAME);
            if (!TextUtils.isEmpty(id)) {
                //get the corresponding loc
                Loc loc = SaveLoadList.getLocFromDb(this, id);
                if (loc != null) {
                    currentLoc = loc;
                    textCityName.setText(loc.getName());
                }//null-loc
            }//empty string
        }//has extra


        locArrayList = SaveLoadList.loadLocList(this);

        setClickListeners();

        getAllWeather();
    }//onCreate



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult called");
        //check to make sure it is the right one
        if (requestCode == FORECAST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //get the name of the loc
            String nameLoc = data.getStringExtra(Constant.INTENT_EXTRA_LOC_NAME);
            if (nameLoc == null || nameLoc.isEmpty()) {
                Log.d(TAG, "onActivityResult - Loc Name = null/empty");
                return;
            }//null name
            //get the corresponding Loc object
            Loc loc = SaveLoadList.getLocFromDb(this, nameLoc);
            //set the current loc only if it is not null
            if (loc == null) {
                Log.d(TAG, "onActivityResult - Loc =null ; name: " + nameLoc);
                return;
            }//loc-null
            //we set the current loc, set the name, and get the weather again
            currentLoc = loc;
            textCityName.setText(loc.getName());
            locArrayList = SaveLoadList.loadLocList(this);
            getAllWeather();
        }//if result OK
        super.onActivityResult(requestCode, resultCode, data);
    }//onActivityResult


    /**
     * Gets called every time the user presses the menu button.
     * Use if your menu is dynamic.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //TODO: consider using a spinner for saved location compared to this
        Log.d(TAG, "onPrepareOptionsMenu Called");
        super.onPrepareOptionsMenu(menu);
        menu.clear();
        //TODO: TESTING
        //load database
        int i = 0;
        menuIdList = new ArrayList<>();
        for (Loc loc : locArrayList) {
            menu.add(0, i, i + 1, loc.getName());
            //add id to list
            menuIdList.add(i);
            i++;
        }//for
        menu.add(0, R.id.action_refresh, 20, "Refresh");
        return true;
    }//onPrepareOptionsMenu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu Called");
        getMenuInflater().inflate(R.menu.compare_menu, menu);

        //return true since we have managed it
        return true;
    }//onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get the size of array
        //TODO: maybe use a dialog for showing all saved locations
        switch (item.getItemId()) {
            case R.id.action_refresh:
                getAllWeather();
                return true;
            case 0:
                currentLoc = locArrayList.get(0);
                getAllWeather();
                return true;
            case 1:
                currentLoc = locArrayList.get(1);
                getAllWeather();
                return true;
            case 2:
                currentLoc = locArrayList.get(2);
                getAllWeather();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }//switch
    }//onOptionsItemSelected


    /*------------------------------------------------------------------------------------------
                    ---------------    HELPER METHODS    ---------------
    ------------------------------------------------------------------------------------------*/


    private void getAllWeather() {
        if (currentLoc == null) {
            Log.d(TAG, "getAllWeather - currentLoc is null");
            return;
        }//null loc

        //set the name
        textCityName.setText(currentLoc.getName());

        //make the progress bar visible
        progressBar.setVisibility(View.VISIBLE);

        kickOffAccuWeather();
        kickOffDarkSky();
        kickOffOpenWeather();
        kickOffWeatherBit();
        kickOffWeatherUnlocked();
    }//getAllWeather

    private void kickOffDarkSky() {
        //check for null loc
        if (currentLoc == null) {
            Log.d(TAG, "kickOffDarkSky - currentLoc = null");
            return;
        }

        //get the location code
        URL currentUrlDS = currentLoc.getCurrentUrlDS();

        if (currentUrlDS == null) {
            Log.d(TAG, "kickOffDarkSky - currentUrl = null");
            showDarkSkyError();
        } else {
            NetworkCallsUtils.DarkSkyQueryTask darkSkyQueryTask = new
                    NetworkCallsUtils.DarkSkyQueryTask(new NetworkCallsUtils.DarkSkyQueryTask.AsyncResponse() {
                @Override
                public void processFinish(Weather output) {
                    //if it is 3 (meaning all tasks are finished), remove, otherwise increment
                    if (tracker == 4) {
                        //remove progress bar and reset the tracker
                        progressBar.setVisibility(View.INVISIBLE);
                        tracker = 0;
                    } else {
                        //this means this is part the group load and just increment
                        tracker++;
                    }

                    //if the call fails, it returns null, so check that first
                    if (output == null) {
                        Log.v(TAG, "kickOffDarkSky - processFinish - Weather = null");
                        showDarkSkyError();
                    } else {
                        showDarkSkyResults(output);
                    }

                }//processFinish
            });
            darkSkyQueryTask.execute(currentUrlDS);
        }////if-else URL
    }//kickOffDarkSky

    private void kickOffWeatherBit() {
        //check for null loc
        if (currentLoc == null) {
            Log.d(TAG, "kickOffWeatherBit - currentLoc = null");
            return;
        }

        //get the location code
        URL currentUrlWB = currentLoc.getCurrentUrlWB();

        if (currentUrlWB == null) {
            Log.d(TAG, "kickOffWeatherBit - currentUrl = null");
            showWeatherBitError();
        } else {
            NetworkCallsUtils.WeatherBitCurrentTask weatherBitCurrentTask = new
                    NetworkCallsUtils.WeatherBitCurrentTask(new NetworkCallsUtils.WeatherBitCurrentTask.AsyncResponse() {
                @Override
                public void processFinish(Weather output) {
                    //if it is 3 (meaning all tasks are finished), remove, otherwise increment
                    if (tracker == 4) {
                        //remove progress bar and reset the tracker
                        progressBar.setVisibility(View.INVISIBLE);
                        tracker = 0;
                    } else {
                        //this means this is part the group load and just increment
                        tracker++;
                    }

                    //if the call fails, it returns null, so check that first
                    if (output == null) {
                        Log.v(TAG, "kickOffWeatherBit - processFinish - Weather = null");
                        showDarkSkyError();
                    } else {
                        showWeatherBitResults(output);
                    }

                }//processFinish
            });
            weatherBitCurrentTask.execute(currentUrlWB);
        }////if-else URL



    }//kickOffWeatherBit

    private void kickOffAccuWeather() {
        //check for null loc
        if (currentLoc == null) {
            Log.d(TAG, "kickOffAccuWeather - currentLoc = null");
            return;
        }

        if (!currentLoc.hasKeyAW()) {
            Log.d(TAG, "kickOffAccuWeather - currentLoc.hasKey = false");
            return;
        }

        //get the location code
        URL currentUrlAW = currentLoc.getCurrentUrlAW();

        if (currentUrlAW == null) {
            Log.d(TAG, "kickOffAccuWeather - currentUrl = null");
            showAccuWeatherError();
        } else {
            NetworkCallsUtils.AccuWeatherCurrentTask accuWeatherQueryTask = new
                    NetworkCallsUtils.AccuWeatherCurrentTask(new NetworkCallsUtils.AccuWeatherCurrentTask.AsyncResponse() {
                @Override
                public void processFinish(Weather output) {
                    //if it is 3 (meaning all tasks are finished), remove, otherwise increment
                    if (tracker == 4) {
                        //remove progress bar and reset the tracker
                        progressBar.setVisibility(View.INVISIBLE);
                        tracker = 0;
                    } else {
                        //this means this is part the group load and just increment
                        tracker++;
                    }

                    //if the call fails, it returns null, so check that first
                    if (output == null) {
                        Log.v(TAG, "kickOffAccuWeather - processFinish - Weather = null");
                        showAccuWeatherError();
                    } else {
                        showAccuWeatherResults(output);
                    }

                }//processFinish
            });
            accuWeatherQueryTask.execute(currentUrlAW);
        }//if-else URL
    }//kickOffAccuWeather

    private void kickOffOpenWeather() {
        //check for null loc
        if (currentLoc == null) {
            Log.d(TAG, "kickOffOpenWeather - currentLoc = null");
            return;
        }

        //get the location code
        URL currentUrlOW = currentLoc.getCurrentUrlOW();

        if (currentUrlOW == null) {
            Log.d(TAG, "kickOffOpenWeather - currentUrl = null");
            showOpenWeatherError();
        } else {
            NetworkCallsUtils.OpenWeatherCurrentTask openWeatherQueryTask = new
                    NetworkCallsUtils.OpenWeatherCurrentTask(new NetworkCallsUtils.OpenWeatherCurrentTask.AsyncResponse() {
                @Override
                public void processFinish(Weather output) {
                    //if it is 3 (meaning all tasks are finished), remove, otherwise increment
                    if (tracker == 4) {
                        //remove progress bar and reset the tracker
                        progressBar.setVisibility(View.INVISIBLE);
                        tracker = 0;
                    } else {
                        //this means this is part the group load and just increment
                        tracker++;
                    }

                    //if the call fails, it returns null, so check that first
                    if (output == null) {
                        Log.v(TAG, "kickOffOpenWeather - processFinish - Weather = null");
                        showOpenWeatherError();
                    } else {
                        showOpenWeatherResults(output);
                    }
                }
            });
            openWeatherQueryTask.execute(currentUrlOW);
        }//if-else URL null
    }//kickOffOpenWeather

    private void kickOffWeatherUnlocked() {
        //check for null loc
        if (currentLoc == null) {
            Log.d(TAG, "kickOffWeatherUnlocked - currentLoc = null");
            return;
        }

        //get the location code
        URL currentUrlWU = currentLoc.getCurrentUrlWU();

        if (currentUrlWU == null) {
            Log.d(TAG, "kickOffWeatherUnlocked - currentUrl = null");
            showWeatherUnlockedError();
        } else {
            NetworkCallsUtils.WeatherUnlockedCurrentTask weatherUnlockedCurrentTask = new
                    NetworkCallsUtils.WeatherUnlockedCurrentTask(new NetworkCallsUtils.WeatherUnlockedCurrentTask.AsyncResponse() {
                @Override
                public void processFinish(Weather output) {
                    //if it is 3 (meaning all tasks are finished), remove, otherwise increment
                    if (tracker == 4) {
                        //remove progress bar and reset the tracker
                        progressBar.setVisibility(View.INVISIBLE);
                        tracker = 0;
                    } else {
                        //this means this is part the group load and just increment
                        tracker++;
                    }

                    //if the call fails, it returns null, so check that first
                    if (output == null) {
                        Log.v(TAG, "kickOffWeatherUnlocked - processFinish - Weather = null");
                        showWeatherUnlockedError();
                    } else {
                        showWeatherUnlockedResults(output);
                    }
                }
            });
            weatherUnlockedCurrentTask.execute(currentUrlWU);
        }//if-else URL null
    }//kickOffWeatherUnlocked


    /*------------------------------------------------------------------------------------------
                    ---------------    HELPER METHODS    ---------------
                    ---------------       UPDATE UI      ---------------
    ------------------------------------------------------------------------------------------*/

    /**
     * Set all the click listeners for opening the forecast
     */
    private void setClickListeners() {
        //set the click listeners for the title views
        //OW
        ((TextView) findViewById(R.id.text_title_open_weather)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLoc != null) {
                    Intent owIntent = new Intent(getApplicationContext(), OpenWeatherForecastActivity.class);
                    owIntent.putExtra(Constant.INTENT_EXTRA_LOC_NAME, currentLoc.getName());
                    startActivityForResult(owIntent, FORECAST_REQUEST_CODE);
                } else {
                    Log.d(TAG, "Forecast Intent - OW : current loc null");
                    HelperFunctions.showToast(getApplicationContext(), "current loc null");
                }
            }
        });

        //AW
        ((TextView) findViewById(R.id.text_title_accu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLoc != null) {
                    Intent awIntent = new Intent(getApplicationContext(), AccuWeatherForecastActivity.class);
                    awIntent.putExtra(Constant.INTENT_EXTRA_LOC_NAME, currentLoc.getName());
                    startActivityForResult(awIntent, FORECAST_REQUEST_CODE);
                } else {
                    Log.d(TAG, "Forecast Intent - AW : current loc null");
                    HelperFunctions.showToast(getApplicationContext(), "current loc null");
                }
            }
        });

        //DS
        ((TextView) findViewById(R.id.text_title_dark_sky)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLoc != null) {
                    Intent dsIntent = new Intent(getApplicationContext(), DarkSkyForecastActivity.class);
                    dsIntent.putExtra(Constant.INTENT_EXTRA_LOC_NAME, currentLoc.getName());
                    startActivityForResult(dsIntent, FORECAST_REQUEST_CODE);
                } else {
                    Log.d(TAG, "Forecast Intent - DS : current loc null");
                    HelperFunctions.showToast(getApplicationContext(), "current loc null");
                }
            }
        });

        //WB
        ((TextView) findViewById(R.id.text_title_weather_bit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLoc != null) {
                    Intent wbIntent = new Intent(getApplicationContext(), WeatherBitForecastActivity.class);
                    wbIntent.putExtra(Constant.INTENT_EXTRA_LOC_NAME, currentLoc.getName());
                    startActivityForResult(wbIntent, FORECAST_REQUEST_CODE);
                } else {
                    Log.d(TAG, "Forecast Intent - WB : current loc null");
                    HelperFunctions.showToast(getApplicationContext(), "current loc null");
                }
            }
        });

        //WU
        ((TextView) findViewById(R.id.text_title_weather_unlocked)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLoc != null) {
                    Intent wuIntent = new Intent(getApplicationContext(), WeatherUnlockedForecastActivity.class);
                    wuIntent.putExtra(Constant.INTENT_EXTRA_LOC_NAME, currentLoc.getName());
                    startActivityForResult(wuIntent, FORECAST_REQUEST_CODE);
                } else {
                    Log.d(TAG, "Forecast Intent - WU : current loc null");
                    HelperFunctions.showToast(getApplicationContext(), "current loc null");
                }
            }
        });
    }//setClickListeners


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
        TextView summary, temp,feelLike, dew, press, humidity, wind, gust, cloud, visibility;

        summary = findViewById(R.id.text_summary_open_weather);
        temp = findViewById(R.id.text_temp_open_weather);
        feelLike = findViewById(R.id.text_temp_feel_open_weather);
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
        feelLike.setText(weather.getTempFeel());
        dew.setText(weather.getDewPoint());
        press.setText(weather.getPressure());
        humidity.setText(weather.getHumidity());
        wind.setText(weather.getWindSpeed() + " " + weather.getWindDirection());
        gust.setText(weather.getWindGust());
        cloud.setText(weather.getCloudCoverage());
        visibility.setText(weather.getVisibility());
    }//showOpenWeatherResults

    private void showOpenWeatherError() {

        TextView summary, temp,feelLike, dew, press, humidity, wind, gust, cloud, visibility;

        summary = findViewById(R.id.text_summary_open_weather);
        temp = findViewById(R.id.text_temp_open_weather);
        feelLike = findViewById(R.id.text_temp_feel_open_weather);
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
        feelLike.setText(empty);
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
        TextView summary, temp, feelLike, dew, press, humidity, wind, gust, cloud, visibility;

        summary = findViewById(R.id.text_summary_dark_sky);
        temp = findViewById(R.id.text_temp_dark_sky);
        feelLike = findViewById(R.id.text_temp_feel_dark_sky);
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
        feelLike.setText(weather.getTempFeel());
        press.setText(weather.getPressure());
        humidity.setText(weather.getHumidity());
        wind.setText(weather.getWindSpeed() + " " + weather.getWindDirection());
        gust.setText(weather.getWindGust());
        cloud.setText(weather.getCloudCoverage());
        visibility.setText(weather.getVisibility());
    }//showOpenWeatherResults

    private void showDarkSkyError() {

        TextView summary, temp, feelLike, dew, press, humidity, wind, gust, cloud, visibility;

        summary = findViewById(R.id.text_summary_dark_sky);
        temp = findViewById(R.id.text_temp_dark_sky);
        feelLike = findViewById(R.id.text_temp_feel_dark_sky);
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
        feelLike.setText(empty);
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
        TextView summary, temp, feelLike, dew, press, humidity, wind, gust, cloud, visibility;

        summary = findViewById(R.id.text_summary_accu);
        temp = findViewById(R.id.text_temp_accu);
        feelLike = findViewById(R.id.text_temp_feel_accu);
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
        feelLike.setText(weather.getTempFeel());
        dew.setText(weather.getDewPoint());
        press.setText(weather.getPressure());
        humidity.setText(weather.getHumidity());
        wind.setText(weather.getWindSpeed() + " " + weather.getWindDirection());
        gust.setText(weather.getWindGust());
        cloud.setText(weather.getCloudCoverage());
        visibility.setText(weather.getVisibility());
    }//showOpenWeatherResults

    private void showAccuWeatherError() {

        TextView summary, temp, feelLike, dew, press, humidity, wind, gust, cloud, visibility;

        summary = findViewById(R.id.text_summary_accu);
        temp = findViewById(R.id.text_temp_accu);
        feelLike = findViewById(R.id.text_temp_feel_accu);
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
        feelLike.setText(empty);
        dew.setText(empty);
        press.setText(empty);
        humidity.setText(empty);
        wind.setText(empty);
        gust.setText(empty);
        cloud.setText(empty);
        visibility.setText(empty);
    }//showOpenWeatherError

    private void showWeatherBitResults(Weather weather) {

        //TODO: icons
        if (weather == null) {
            showWeatherBitError();
            return;
        }
        //find views
        TextView summary, temp, feelLike, dew, press, humidity, wind, gust, cloud, visibility;

        summary = findViewById(R.id.text_summary_wb);
        temp = findViewById(R.id.text_temp_wb);
        feelLike = findViewById(R.id.text_temp_feel_wb);
        dew = findViewById(R.id.text_dew_wb);
        press = findViewById(R.id.text_press_wb);
        humidity = findViewById(R.id.text_humidity_wb);
        wind = findViewById(R.id.text_wind_wb);
        gust = findViewById(R.id.text_gust_wb);
        cloud = findViewById(R.id.text_cloud_wb);
        visibility = findViewById(R.id.text_visibility_wb);

        //set values
        summary.setText(weather.getSummary());
        temp.setText(weather.getTemperature());
        feelLike.setText(weather.getTempFeel());
        dew.setText(weather.getDewPoint());
        press.setText(weather.getPressure());
        humidity.setText(weather.getHumidity());
        wind.setText(weather.getWindSpeed() + " " + weather.getWindDirection());
        gust.setText(weather.getWindGust());
        cloud.setText(weather.getCloudCoverage());
        visibility.setText(weather.getVisibility());
    }//showWeatherBitResults

    private void showWeatherBitError() {

        TextView summary, temp, feelLike, dew, press, humidity, wind, gust, cloud, visibility;

        summary = findViewById(R.id.text_summary_wb);
        temp = findViewById(R.id.text_temp_wb);
        feelLike = findViewById(R.id.text_temp_feel_wb);
        dew = findViewById(R.id.text_dew_wb);
        press = findViewById(R.id.text_press_wb);
        humidity = findViewById(R.id.text_humidity_wb);
        wind = findViewById(R.id.text_wind_wb);
        gust = findViewById(R.id.text_gust_wb);
        cloud = findViewById(R.id.text_cloud_wb);
        visibility = findViewById(R.id.text_visibility_wb);

        String error = "Error";
        String empty = "";
        //set values
        summary.setText(error);
        temp.setText(empty);
        feelLike.setText(empty);
        dew.setText(empty);
        press.setText(empty);
        humidity.setText(empty);
        wind.setText(empty);
        gust.setText(empty);
        cloud.setText(empty);
        visibility.setText(empty);
    }//showWeatherBitError

    private void showWeatherUnlockedResults(Weather weather) {

        //TODO: icons
        if (weather == null) {
            showWeatherUnlockedError();
            return;
        }
        //find views
        TextView summary, temp, feelLike, dew, press, humidity, wind, gust, cloud, visibility;

        summary = findViewById(R.id.text_summary_wu);
        temp = findViewById(R.id.text_temp_wu);
        feelLike = findViewById(R.id.text_temp_feel_wu);
        dew = findViewById(R.id.text_dew_wu);
        press = findViewById(R.id.text_press_wu);
        humidity = findViewById(R.id.text_humidity_wu);
        wind = findViewById(R.id.text_wind_wu);
        gust = findViewById(R.id.text_gust_wu);
        cloud = findViewById(R.id.text_cloud_wu);
        visibility = findViewById(R.id.text_visibility_wu);

        //set values
        summary.setText(weather.getSummary());
        temp.setText(weather.getTemperature());
        feelLike.setText(weather.getTempFeel());
        dew.setText(weather.getDewPoint());
        press.setText(weather.getPressure());
        humidity.setText(weather.getHumidity());
        wind.setText(weather.getWindSpeed() + " " + weather.getWindDirection());
        gust.setText(weather.getWindGust());
        cloud.setText(weather.getCloudCoverage());
        visibility.setText(weather.getVisibility());
    }//showWeatherBitResults

    private void showWeatherUnlockedError() {

        TextView summary, temp, feelLike, dew, press, humidity, wind, gust, cloud, visibility;

        summary = findViewById(R.id.text_summary_wu);
        temp = findViewById(R.id.text_temp_wu);
        feelLike = findViewById(R.id.text_temp_feel_wu);
        dew = findViewById(R.id.text_dew_wu);
        press = findViewById(R.id.text_press_wu);
        humidity = findViewById(R.id.text_humidity_wu);
        wind = findViewById(R.id.text_wind_wu);
        gust = findViewById(R.id.text_gust_wu);
        cloud = findViewById(R.id.text_cloud_wu);
        visibility = findViewById(R.id.text_visibility_wu);

        String error = "Error";
        String empty = "";
        //set values
        summary.setText(error);
        temp.setText(empty);
        feelLike.setText(empty);
        dew.setText(empty);
        press.setText(empty);
        humidity.setText(empty);
        wind.setText(empty);
        gust.setText(empty);
        cloud.setText(empty);
        visibility.setText(empty);
    }//showWeatherBitError

}//main-class

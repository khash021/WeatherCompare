package tech.khash.weathercompare;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import tech.khash.weathercompare.fragment.PagerFragmentAdapter;
import tech.khash.weathercompare.model.Constant;
import tech.khash.weathercompare.model.Loc;
import tech.khash.weathercompare.model.Weather;
import tech.khash.weathercompare.utilities.NetworkCallsUtils;
import tech.khash.weathercompare.utilities.ParseJSON;
import tech.khash.weathercompare.utilities.SaveLoadList;

/**
 * Created by Khashayar "Khash" Mortazavi
 * <p>
 * Responsible for showing the 3 day forecast using tabs and fragments
 */

public class ForecastActivity extends AppCompatActivity {

    private static final String TAG = ForecastActivity.class.getSimpleName();

    private Loc currentLoc;

    private ArrayList<Weather> day1WeatherList;
    private ArrayList<Weather> day2WeatherList;
    private ArrayList<Weather> day3WeatherList;

    private ArrayList<Weather> weatherAW;
    private ArrayList<Weather> weatherDS;
    private ArrayList<Weather> weatherWB;
    private ArrayList<Weather> weatherWU;

    private TabLayout tabLayout;

    private ProgressBar progressBar;
    private ViewPager viewPager;

    private String day1Date, day2Date, day3Date;

    private int tracker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate caleed");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar actionBar = getSupportActionBar();

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);


        //get the loc id from intent extra
        if (getIntent().hasExtra(Constant.INTENT_EXTRA_LOC_NAME)) {
            String id = getIntent().getStringExtra(Constant.INTENT_EXTRA_LOC_NAME);
            if (!TextUtils.isEmpty(id)) {
                //get the corresponding loc
                Loc loc = SaveLoadList.getLocFromDb(this, id);
                if (loc != null) {
                    currentLoc = loc;
                    //set the title
                    actionBar.setTitle(currentLoc.getName() + " " + getString(R.string.title_activity_forecast));
                }//null-loc
            }//empty string
        }//has extra

        progressBar = findViewById(R.id.progress_bar);
        tracker = 0;

        getForecasts(currentLoc);


    }//onCreate


    /*------------------------------------------------------------------------------------------
                    ---------------    HELPER METHODS    ---------------
    ------------------------------------------------------------------------------------------*/

    /**
     * Helper method for getting all the forecasts from all providers, parse them, and then
     * place each day's forecast from all providers in one arraylist to be used with fragments
     *
     * @param loc
     */
    private void getForecasts(Loc loc) {
        Log.d(TAG, "getForecasts called");
        if (loc == null) {
            Log.d(TAG, "getForecasts - current loc = null");
            return;
        }

        //show progress bar
        progressBar.setVisibility(View.VISIBLE);

        //fetch all the data

        //------------------------------- AW ----------------------------------
        URL forecastUrlAW = loc.getForecastUrlAW();
        if (forecastUrlAW == null) {
            Log.d(TAG, "forecastUrlAW = null");
        } else {
            getResponseAW(forecastUrlAW);
        }//url null

        //------------------------------- DS ----------------------------------
        URL forecastUrlDS = loc.getForecastUrlDS();
        if (forecastUrlDS == null) {
            Log.d(TAG, "forecastUrlDS = null");
        } else {
            getResponseDS(forecastUrlDS);
        }//url null

        //------------------------------- WB ----------------------------------
        URL forecastUrlWB = loc.getForecastUrlWB();
        if (forecastUrlWB == null) {
            Log.d(TAG, "forecastUrlWB = null");
        } else {
            getResponseWB(forecastUrlWB);
        }//url null

        //------------------------------- WB ----------------------------------
        URL forecastUrlWU = loc.getForecastUrlWU();
        if (forecastUrlWU == null) {
            Log.d(TAG, "forecastUrlWU = null");
        } else {
            getResponseWU(forecastUrlWU);
        }//url null

        //Now we need to go through all of these arrays and get the separate them by date
//        createDayArrayLists();
    }//getForecasts

    private void createDayArrayLists() {
        Log.d(TAG, "createDayArrayLists called");
        if (tracker != 4) {
            Log.d(TAG, "createDayArrayLists - tracker is not 4. Tracker: " + tracker);
            return;
        }
        day1WeatherList = new ArrayList<>();
        day2WeatherList = new ArrayList<>();
        day3WeatherList = new ArrayList<>();

        //Create day strings
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM.dd", Locale.getDefault());
        final long DAY_MILLI = 24 * 60 * 60 * 1000; //one day in millis
        long nowMillis = Calendar.getInstance().getTimeInMillis();
        long day1Milli = nowMillis + DAY_MILLI;
        long day2Milli = day1Milli + DAY_MILLI;
        long day3Milli = day2Milli + DAY_MILLI;
        day1Date = formatter.format(new Date(day1Milli));
        day2Date = formatter.format(new Date(day2Milli));
        day3Date = formatter.format(new Date(day3Milli));

        //create our days

        //if we get here, it means all the fetching of the data is done
        //---------------- AW -----------------------
        if (weatherAW == null || weatherAW.size() < 1) {
            Log.d(TAG, "weatherAW - null/empty");
        } else {
            for (Weather weather : weatherAW) {
                String date = weather.getDate();
                if (date.equalsIgnoreCase(day1Date)) {
                    day1WeatherList.add(weather);
                } else if (date.equalsIgnoreCase(day2Date)) {
                    day2WeatherList.add(weather);
                } else if (date.equalsIgnoreCase(day3Date)) {
                    day3WeatherList.add(weather);
                }
            }//for
        }//if/else

        //---------------- DS -----------------------
        if (weatherDS == null || weatherDS.size() < 1) {
            Log.d(TAG, "weatherDS - null/empty");
        } else {
            for (Weather weather : weatherDS) {
                String date = weather.getDate();
                if (date.equalsIgnoreCase(day1Date)) {
                    day1WeatherList.add(weather);
                } else if (date.equalsIgnoreCase(day2Date)) {
                    day2WeatherList.add(weather);
                } else if (date.equalsIgnoreCase(day3Date)) {
                    day3WeatherList.add(weather);
                }
            }//for
        }//if/else

        //---------------- WB -----------------------
        if (weatherWB == null || weatherWB.size() < 1) {
            Log.d(TAG, "weatherWB - null/empty");
        } else {
            for (Weather weather : weatherWB) {
                String date = weather.getDate();
                if (date.equalsIgnoreCase(day1Date)) {
                    day1WeatherList.add(weather);
                } else if (date.equalsIgnoreCase(day2Date)) {
                    day2WeatherList.add(weather);
                } else if (date.equalsIgnoreCase(day3Date)) {
                    day3WeatherList.add(weather);
                }
            }//for
        }//if/else

        //---------------- WU -----------------------
        if (weatherWU == null || weatherWU.size() < 1) {
            Log.d(TAG, "weatherWB - null/empty");
        } else {
            for (Weather weather : weatherWU) {
                String date = weather.getDate();
                if (date.equalsIgnoreCase(day1Date)) {
                    day1WeatherList.add(weather);
                } else if (date.equalsIgnoreCase(day2Date)) {
                    day2WeatherList.add(weather);
                } else if (date.equalsIgnoreCase(day3Date)) {
                    day3WeatherList.add(weather);
                }
            }//for
        }//if/else

        //kick off the fragments
        setupFragments();
    }//createDayArrayLists

    private void setupFragments() {
        Log.d(TAG, "setupFragments called");

        //set the tab names
        tabLayout.addTab(tabLayout.newTab().setText(day1Date));
        tabLayout.addTab(tabLayout.newTab().setText(day2Date));
        tabLayout.addTab(tabLayout.newTab().setText(day3Date));
        // Set the tabs to fill the entire layout.
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Use PagerAdapter to manage page views in fragments.
        PagerFragmentAdapter adapter = new PagerFragmentAdapter
                (this, getSupportFragmentManager(), tabLayout.getTabCount(),
                        day1WeatherList, day2WeatherList, day3WeatherList);
        viewPager.setAdapter(adapter);
        // Setting a listener for clicks.
        viewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }//setupFragments

    //------------------------------- AW ----------------------------------
    private void getResponseAW(URL url) {
        Log.d(TAG, "getResponseAW called");
        //get the response
        NetworkCallsUtils.AccuWeatherForecastTask forecastTaskAW = new
                NetworkCallsUtils.AccuWeatherForecastTask(new NetworkCallsUtils.AccuWeatherForecastTask.AsyncResponse() {
            @Override
            public void processFinish(String jsonResponse) {
                if (TextUtils.isEmpty(jsonResponse)) {
                    Log.d(TAG, "getWeather - processFinish callback - response : null/empty");
                    tracker++;
                    return;
                }
                getWeathersAW(jsonResponse);
            }
        });
        forecastTaskAW.execute(url);
    }//getResponseAW

    private void getWeathersAW(String jsonResponse) {
        Log.d(TAG, "getWeathersAW called");
        weatherAW = new ArrayList<>();
        try {
            weatherAW = ParseJSON.parseAccuWeatherForecast(jsonResponse);
            Log.d(TAG, "getWeathersAW - size: " + weatherAW.size());
        } catch (JSONException e) {
            Log.e(TAG, "Error getting arraylist from ParseJSON.parseAccuWeatherForecast", e);
        } finally {
            tracker++;
            createDayArrayLists();
        }
    }//getWeathersAW

    //------------------------------- DS ----------------------------------
    private void getResponseDS(URL url) {
        Log.d(TAG, "getResponseDS called");
        //get response
        NetworkCallsUtils.DarkSkyForecastTask forecastTask = new
                NetworkCallsUtils.DarkSkyForecastTask(new NetworkCallsUtils.DarkSkyForecastTask.AsyncResponse() {
            @Override
            public void processFinish(String jsonResponse) {
                if (TextUtils.isEmpty(jsonResponse)) {
                    Log.d(TAG, "getWeather - processFinish callback - response : null/empty");
                    tracker++;
                    return;
                }
                //send the data to be parsed
                getWeathersDS(jsonResponse);
            }
        });
        forecastTask.execute(url);
    }//getResponseDS

    private void getWeathersDS(String jsonResponse) {
        Log.d(TAG, "getWeathersDS called");
        weatherDS = new ArrayList<>();
        try {
            weatherDS = ParseJSON.parseDarkSkyForecast(jsonResponse);
            Log.d(TAG, "createWeatherArrayList - size: " + weatherDS.size());
            //start the adapter
        } catch (JSONException e) {
            Log.e(TAG, "Error getting arraylist from ParseJSON.parseAccuWeatherForecast", e);
        } finally {
            tracker++;
            createDayArrayLists();
        }
    }//getWeathersDS

    //------------------------------- WB ----------------------------------
    private void getResponseWB(URL url) {
        Log.d(TAG, "getResponseWB called");
        //get the response
        NetworkCallsUtils.WeatherBitForecastTask forecastTask = new
                NetworkCallsUtils.WeatherBitForecastTask(new NetworkCallsUtils.WeatherBitForecastTask.AsyncResponse() {
            @Override
            public void processFinish(String jsonResponse) {
                if (TextUtils.isEmpty(jsonResponse)) {
                    Log.d(TAG, "getWeather - processFinish callback - response : null/empty");
                    tracker++;
                    return;
                }
                //send the data to be parsed
                getWeathersWB(jsonResponse);
            }
        });
        forecastTask.execute(url);
    }//getResponseWB

    private void getWeathersWB(String jsonResponse) {
        Log.d(TAG, "getWeathersWB called");
        weatherWB = new ArrayList<>();
        try {
            weatherWB = ParseJSON.parseWeatherBitForecast(jsonResponse);
            Log.d(TAG, "createWeatherArrayList - size: " + weatherWB.size());
        } catch (JSONException e) {
            Log.e(TAG, "Error getting arraylist from ParseJSON.parseAccuWeatherForecast", e);
        } finally {
            tracker++;
            createDayArrayLists();
        }
    }//getWeathersWB

    //------------------------------- WU ----------------------------------
    private void getResponseWU(URL url) {
        Log.d(TAG, "getResponseWU called");
        //get the response
        NetworkCallsUtils.WeatherUnlockedForecastTask forecastTask = new
                NetworkCallsUtils.WeatherUnlockedForecastTask(new NetworkCallsUtils.WeatherUnlockedForecastTask.AsyncResponse() {
            @Override
            public void processFinish(String jsonResponse) {
                if (TextUtils.isEmpty(jsonResponse)) {
                    Log.d(TAG, "getWeather - processFinish callback - response : null/empty");
                    tracker++;
                    return;
                }
                //send the data to be parsed
                getWeathersWU(jsonResponse);
            }
        });
        forecastTask.execute(url);
    }//getResponseWB

    private void getWeathersWU(String jsonResponse) {
        Log.d(TAG, "getWeathersWU called");
        weatherWU = new ArrayList<>();
        try {
            weatherWU = ParseJSON.parseWeatherUnlockedForecast(jsonResponse);
            Log.d(TAG, "createWeatherArrayList - size: " + weatherWU.size());
        } catch (JSONException e) {
            Log.e(TAG, "Error getting arraylist from ParseJSON.parseWeatherUnlockedForecast", e);
        } finally {
            tracker++;
            createDayArrayLists();
        }
    }//getWeathersWB
}//ForecastActivity

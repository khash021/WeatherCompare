package tech.khash.weathercompare;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URL;
import java.util.ArrayList;

import tech.khash.weathercompare.adapter.WeatherListAdapterToday;
import tech.khash.weathercompare.model.Constant;
import tech.khash.weathercompare.model.Loc;
import tech.khash.weathercompare.model.Weather;
import tech.khash.weathercompare.utilities.NetworkCallsUtils;
import tech.khash.weathercompare.utilities.SaveLoadList;

public class TodayActivity extends AppCompatActivity {

    private static final String TAG = TodayActivity.class.getSimpleName();

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private WeatherListAdapterToday adapter;
    private Loc currentLoc;

    private ArrayList<Weather> weatherArrayList;

    private Boolean isDay;
    private int tracker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recycler_view);

        //get the loc id from intent extra
        if (getIntent().hasExtra(Constant.INTENT_EXTRA_LOC_NAME)) {
            String id = getIntent().getStringExtra(Constant.INTENT_EXTRA_LOC_NAME);
            if (!TextUtils.isEmpty(id)) {
                //get the corresponding loc
                Loc loc = SaveLoadList.getLocFromDb(this, id);
                if (loc != null) {
                    currentLoc = loc;
                    setTitle(currentLoc.getName());
                }//null-loc
            }//empty string
        }//has extra

        tracker = 0;
        weatherArrayList = new ArrayList<>();

        calculateIsDay();

    }//onCreate

    /*------------------------------------------------------------------------------------------
                    ---------------    HELPER METHODS    ---------------
    ------------------------------------------------------------------------------------------*/


    private void calculateIsDay() {
        if (currentLoc == null) {
            Log.d(TAG, "calculateIsDay - currentLoc is null");
            return;
        }//null loc

        //make the progress bar visible
        progressBar.setVisibility(View.VISIBLE);

        URL sinriseSunsetUrl = currentLoc.getSunriseSunsetUrl();
        if (sinriseSunsetUrl == null) {
            Log.d(TAG, "calculateIsDay - currentUrl = null");
            //TODO: manage this case
        } else {
            NetworkCallsUtils.SunriseSunsetTask sunriseSunsetTask = new
                    NetworkCallsUtils.SunriseSunsetTask(new NetworkCallsUtils.SunriseSunsetTask.AsyncResponse() {
                @Override
                public void processFinished(Boolean result) {
                    isDay = result;
                    getAllWeather();
                }
            });
            sunriseSunsetTask.execute(sinriseSunsetUrl);
        }

    }//calculateIsDay

    private void getAllWeather() {

        if (currentLoc == null) {
            Log.d(TAG, "getAllWeather - currentLoc is null");
            return;
        }//null loc

        kickOffAccuWeather();
        kickOffDarkSky();
        kickOffWeatherBit();
        kickOffWeatherUnlocked();

    }//getAllWeather

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
        URL todayUrlAW = currentLoc.getTodayUrlAW();

        if (todayUrlAW == null) {
            Log.d(TAG, "kickOffAccuWeather - currentUrl = null");
        } else {
            Log.d(TAG, "Current URL - AW : " + todayUrlAW.toString());
            NetworkCallsUtils.AccuWeatherTodayTask accuWeatherTodayTask = new
                    NetworkCallsUtils.AccuWeatherTodayTask(new NetworkCallsUtils.AccuWeatherTodayTask.AsyncResponse() {
                @Override
                public void processFinish(Weather output) {
                    weatherArrayList.add(output);
                    //if it is 3 (meaning all tasks are finished), remove, otherwise increment
                    if (tracker == 3) {
                        //remove progress bar and reset the tracker
                        progressBar.setVisibility(View.GONE);
                        tracker = 0;
                        //start adapter
                        updateAdapter();
                    } else {
                        //this means this is part the group load and just increment
                        tracker++;
                    }

                }//processFinish
            });
            accuWeatherTodayTask.execute(todayUrlAW);
        }//if-else URL
    }//kickOffAccuWeather

    private void kickOffDarkSky() {
        //check for null loc
        if (currentLoc == null) {
            Log.d(TAG, "kickOffDarkSky - currentLoc = null");
            return;
        }

        URL todayUrlDS = currentLoc.getForecastUrlDS();

        if (todayUrlDS == null) {
            Log.d(TAG, "kickOffDarkSky - forecastUrl = null");
        } else {
            Log.d(TAG, "Forecast URL - DS : " + todayUrlDS.toString());
            NetworkCallsUtils.DarkSkyTodayTask darskSkyTodayTask = new
                    NetworkCallsUtils.DarkSkyTodayTask(new NetworkCallsUtils.DarkSkyTodayTask.AsyncResponse() {
                @Override
                public void processFinish(Weather output) {
                    weatherArrayList.add(output);
                    //if it is 3 (meaning all tasks are finished), remove, otherwise increment
                    if (tracker == 3) {
                        //remove progress bar and reset the tracker
                        progressBar.setVisibility(View.GONE);
                        tracker = 0;
                        //start adapter
                        updateAdapter();
                    } else {
                        //this means this is part the group load and just increment
                        tracker++;
                    }

                }//processFinish
            });
            darskSkyTodayTask.execute(todayUrlDS);
        }//if-else URL
    }//kickOffDarkSky

    private void kickOffWeatherBit() {
        //check for null loc
        if (currentLoc == null) {
            Log.d(TAG, "kickOffWeatherBit - currentLoc = null");
            return;
        }

        URL todayUrlWB = currentLoc.getForecastUrlWB();

        if (todayUrlWB == null) {
            Log.d(TAG, "kickOffWeatherBit - todayURL = null");
        } else {
            Log.d(TAG, "Today URL - WB : " + todayUrlWB.toString());
            NetworkCallsUtils.WeatherBitTodayTask weatherBitTodayTask = new
                    NetworkCallsUtils.WeatherBitTodayTask(new NetworkCallsUtils.WeatherBitTodayTask.AsyncResponse() {
                @Override
                public void processFinish(Weather output) {
                    weatherArrayList.add(output);
                    //if it is 3 (meaning all tasks are finished), remove, otherwise increment
                    if (tracker == 3) {
                        //remove progress bar and reset the tracker
                        progressBar.setVisibility(View.GONE);
                        tracker = 0;
                        //start adapter
                        updateAdapter();
                    } else {
                        //this means this is part the group load and just increment
                        tracker++;
                    }

                }//processFinish
            });
            weatherBitTodayTask.execute(todayUrlWB);
        }//if-else URL
    }//kickOffWeatherBit

    private void kickOffWeatherUnlocked() {
        //check for null loc
        if (currentLoc == null) {
            Log.d(TAG, "kickOffWeatherUnlocked - currentLoc = null");
            return;
        }

        URL todayUrlWU = currentLoc.getForecastUrlWU();

        if (todayUrlWU == null) {
            Log.d(TAG, "kickOffWeatherUnlocked - todayURL = null");
        } else {
            Log.d(TAG, "Today URL - WU : " + todayUrlWU.toString());
            NetworkCallsUtils.WeatherUnlockedTodayTask weatherUnlockedTodayTask = new
                    NetworkCallsUtils.WeatherUnlockedTodayTask(new NetworkCallsUtils.WeatherUnlockedTodayTask.AsyncResponse() {
                @Override
                public void processFinish(Weather output) {
                    weatherArrayList.add(output);
                    //if it is 3 (meaning all tasks are finished), remove, otherwise increment
                    if (tracker == 3) {
                        //remove progress bar and reset the tracker
                        progressBar.setVisibility(View.GONE);
                        tracker = 0;
                        //start adapter
                        updateAdapter();
                    } else {
                        //this means this is part the group load and just increment
                        tracker++;
                    }

                }//processFinish
            });
            weatherUnlockedTodayTask.execute(todayUrlWU);
        }//if-else URL
    }//kickOffWeatherUnlocked






    private void updateAdapter() {
        if (weatherArrayList == null || weatherArrayList.size() < 1) {
            Log.d(TAG, "updateAdapter - weatherArrayList null/empty");
            return;
        }

        if (isDay == null) {
            Log.d(TAG, "updateAdapter - isDay is null");
            adapter = new WeatherListAdapterToday(this, weatherArrayList, true);
        } else {
            adapter = new WeatherListAdapterToday(this, weatherArrayList, isDay);
        }

        // Connect the adapter with the RecyclerView.
        recyclerView.setAdapter(adapter);
        // Give the RecyclerView a horizontal layout manager.
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        //Add divider between items using the DividerItemDecoration
        DividerItemDecoration decoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.HORIZONTAL);
        recyclerView.addItemDecoration(decoration);
    }//updateAdapter



}//TodayActivity

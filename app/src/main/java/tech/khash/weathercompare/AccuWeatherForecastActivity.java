package tech.khash.weathercompare;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;

import tech.khash.weathercompare.adapter.WeatherListAdapterAW;
import tech.khash.weathercompare.model.Constant;
import tech.khash.weathercompare.model.Loc;
import tech.khash.weathercompare.model.Weather;
import tech.khash.weathercompare.utilities.NetworkCallsUtils;
import tech.khash.weathercompare.utilities.ParseJSON;
import tech.khash.weathercompare.utilities.SaveLoadList;

public class AccuWeatherForecastActivity extends AppCompatActivity implements
        WeatherListAdapterAW.ListItemClickListener {

    private static final String TAG = AccuWeatherForecastActivity.class.getSimpleName();

    private Loc currentLoc;
    private ArrayList<Weather> weatherArrayList;
    private RecyclerView recyclerView;
    private WeatherListAdapterAW adapter;
    //TODO: add progress bar
    //TODO: send currentLoc data back when you do finish()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        TextView textCityName = findViewById(R.id.text_city_name);
        recyclerView = findViewById(R.id.recycler_view);

        //get the loc id from intent extra
        if (getIntent().hasExtra(Constant.INTENT_EXTRA_LOC_NAME)) {
            String id = getIntent().getStringExtra(Constant.INTENT_EXTRA_LOC_NAME);
            if (!TextUtils.isEmpty(id)) {
                //get the corresponding loc
                Loc loc = SaveLoadList.getLocFromDb(this, id);
                if (loc != null) {
                    currentLoc = loc;
                    textCityName.setText(loc.getName());
                    getWeather();
                }//null-loc
            }//empty string
        }//has extra




    }//onCreate

    //TODO: this does not work, for now I change the parent activity to Main.
    //TODO: we need to make an app bar, then do this when android up is clicked (like the way we show nav drawer in main activity
    //Here wen send the current Loc name back to CompareActivity
    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(this, CompareActivity.class);
        backIntent.putExtra(Constant.INTENT_EXTRA_LOC_NAME, currentLoc.getName());
        setResult(RESULT_OK, backIntent);
        finish();
    }//onBackPressed



    /*------------------------------------------------------------------------------------------
                    ---------------    HELPER METHODS    ---------------
    ------------------------------------------------------------------------------------------*/

    //Helper method for making the network call to get the JSON response for the weather forecast
    private void getWeather() {
        if (currentLoc == null) {
            Log.d(TAG, "getWeather - currentLoc = null");
            return;
        }

        URL forecastUrl = currentLoc.getForecastUrlAW();
        if (forecastUrl == null) {
            Log.d(TAG, "getWeather - forecastUrl = null");
            return;
        }

        //get the response
        NetworkCallsUtils.AccuWeatherForecastTask forecastTask = new
                NetworkCallsUtils.AccuWeatherForecastTask(new NetworkCallsUtils.AccuWeatherForecastTask.AsyncResponse() {
            @Override
            public void processFinish(String jsonResponse) {
                if (TextUtils.isEmpty(jsonResponse)) {
                    Log.d(TAG, "getWeather - processFinish callback - response : null/empty");
                    return;
                }

                //send the data to be parsed
                createWeatherArrayList(jsonResponse);
            }
        });
        forecastTask.execute(forecastUrl);

    }//getWeather

    //this methods parse the json response, and populates the Weather ArrayList
    private void createWeatherArrayList (String jsonResponse) {
        weatherArrayList = new ArrayList<>();
        try {
            weatherArrayList = ParseJSON.parseAccuWeatherForecast(jsonResponse);
            Log.d(TAG, "createWeatherArrayList - size: " + weatherArrayList.size());
            //start the adapter
            updateAdapter(weatherArrayList);
        } catch (JSONException e) {
            Log.e(TAG, "Error getting arraylist from ParseJSON.parseAccuWeatherForecast", e);
        }
    }//createWeatherArrayList

    private void updateAdapter(ArrayList<Weather> weatherArrayList) {
        if (weatherArrayList == null || weatherArrayList.size() < 1) {
            Log.d(TAG, "updateAdapter - null or empty");
            return;
        }
        // Get a handle to the RecyclerView.
        recyclerView = findViewById(R.id.recycler_view);
        // Create an adapter and supply the data to be displayed.
        adapter = new WeatherListAdapterAW(this, weatherArrayList, this);
        // Connect the adapter with the RecyclerView.
        recyclerView.setAdapter(adapter);
        // Give the RecyclerView a horizontal layout manager.
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        //Add divider between items using the DividerItemDecoration
        DividerItemDecoration decoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.HORIZONTAL);
        recyclerView.addItemDecoration(decoration);
    }


    @Override
    public void onListItemClick(int clickedItemIndex) {

    }//onListItemClick
}//OpenWeatherForecast

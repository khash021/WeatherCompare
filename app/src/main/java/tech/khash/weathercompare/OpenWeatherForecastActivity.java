package tech.khash.weathercompare;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URL;
import java.util.ArrayList;

import tech.khash.weathercompare.adapter.WeatherListAdapterOW;
import tech.khash.weathercompare.model.Constant;
import tech.khash.weathercompare.model.Loc;
import tech.khash.weathercompare.model.Weather;
import tech.khash.weathercompare.utilities.NetworkCallsUtils;
import tech.khash.weathercompare.utilities.ParseJSON;
import tech.khash.weathercompare.utilities.SaveLoadList;

public class OpenWeatherForecastActivity extends AppCompatActivity implements
        WeatherListAdapterOW.ListItemClickListener {

    private static final String TAG = OpenWeatherForecastActivity.class.getCanonicalName();

    private Loc currentLoc;
    private ArrayList<Weather> weatherArrayList;
    private RecyclerView recyclerView;
    private WeatherListAdapterOW adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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



    /*------------------------------------------------------------------------------------------
                    ---------------    HELPER METHODS    ---------------
    ------------------------------------------------------------------------------------------*/

    //Helper method for making the network call to get the JSON response for the weather forecast
    private void getWeather() {
        if (currentLoc == null) {
            Log.d(TAG, "getWeather - currentLoc = null");
            return;
        }

        URL forecastUrl = currentLoc.getForecastUrlOW();
        if (forecastUrl == null) {
            Log.d(TAG, "getWeather - forecastUrl = null");
            return;
        }

        //get the response
        NetworkCallsUtils.OpenWeatherForecastTask forecastTask = new
                NetworkCallsUtils.OpenWeatherForecastTask(new NetworkCallsUtils.OpenWeatherForecastTask.AsyncResponse() {
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


    private void createWeatherArrayList(String jsonResponse) {
        if (jsonResponse == null) {
            Log.d(TAG, "getForecastOW - response = null");
            return;
        }


        weatherArrayList = ParseJSON.parseOpenWeatherForecast(jsonResponse);
        updateAdapter(weatherArrayList);

    }//createWeatherArrayList

    private void updateAdapter(ArrayList<Weather> weatherArrayList) {
        if (weatherArrayList == null || weatherArrayList.size() < 1) {
            Log.d(TAG, "updateAdapter - null or empty");
            return;
        }
        // Get a handle to the RecyclerView.
        recyclerView = findViewById(R.id.recycler_view);
        // Create an adapter and supply the data to be displayed.
        adapter = new WeatherListAdapterOW(this, weatherArrayList, this);
        // Connect the adapter with the RecyclerView.
        recyclerView.setAdapter(adapter);
        // Give the RecyclerView a horizontal layout manager.
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        //Add divider between items using the DividerItemDecoration
        DividerItemDecoration decoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.HORIZONTAL);
        recyclerView.addItemDecoration(decoration);

    }//updateAdapter


    @Override
    public void onListItemClick(int clickedItemIndex) {

    }//onListItemClick
}//OpenWeatherForecastActivity

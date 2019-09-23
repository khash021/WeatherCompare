package tech.khash.weathercompare;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import tech.khash.weathercompare.fragment.PagerFragmentAdapter;
import tech.khash.weathercompare.model.Constant;
import tech.khash.weathercompare.model.Loc;
import tech.khash.weathercompare.model.Weather;
import tech.khash.weathercompare.utilities.HelperFunctions;
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

    //for sending and receiving location from add location activity
    private static final int ADD_LOCATION_REQUEST_CODE = 3;

    private Loc currentLoc;

    private LinearLayout noConnectionLayout;
    private LinearLayout noLocationLayout;
    private LinearLayout noLocDefaultLayout;

    private ArrayList<Weather> day1WeatherList;
    private ArrayList<Weather> day2WeatherList;
    private ArrayList<Weather> day3WeatherList;

    private ArrayList<Weather> weatherAW;
    private ArrayList<Weather> weatherDS;
    private ArrayList<Weather> weatherWB;
    private ArrayList<Weather> weatherWU;

    private ArrayList<Loc> locArrayList;

    private TabLayout tabLayout;

    private ProgressBar progressBar;
    private ViewPager viewPager;

    private ActionBar actionBar;

    private boolean deviceLocation = false;

    private String day1Date, day2Date, day3Date;

    private int tracker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Get a support ActionBar corresponding to this toolbar
        actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);

        noConnectionLayout = findViewById(R.id.no_internet_view);
        noLocationLayout = findViewById(R.id.no_location_view);
        noLocDefaultLayout = findViewById(R.id.no_loc_default_view);

        locArrayList = SaveLoadList.loadLocList(this);

        //check internet connection
        if (!HelperFunctions.isNetworkAvailable(this)) {
            noConnectionLayout.setVisibility(View.VISIBLE);
            return;
        }


        //get the loc id from intent extra
        if (getIntent().hasExtra(Constant.INTENT_EXTRA_LOC_NAME)) {
            String id = getIntent().getStringExtra(Constant.INTENT_EXTRA_LOC_NAME);
            if (!TextUtils.isEmpty(id)) {
                //get the corresponding loc
                Loc loc = SaveLoadList.getLocFromDb(this, id);
                if (loc != null) {
                    currentLoc = loc;
                    //set the title
                    actionBar.setTitle(currentLoc.getName());
                } else {
                    //TODO: handle in case it is not in the db
                }
            }//empty string
        } else if (getIntent().hasExtra(Constant.INTENT_EXTRA_DEVICE_LOCATION)) {
            String json = getIntent().getStringExtra(Constant.INTENT_EXTRA_DEVICE_LOCATION);
            if (json != null) {
                //convert json back to Loc
                Gson gson = new Gson();
                Loc loc = gson.fromJson(json, Loc.class);
                if (loc != null) {
                    currentLoc = loc;
                } else {
                    //TODO: handle this
                }//if/else : null loc
            } else {
                //TODO: handle this
            }

            String title = getIntent().getStringExtra(Constant.INTENT_EXTRA_DEVICE_LOCATION_NAME);
            if (!TextUtils.isEmpty(title)) {
                actionBar.setTitle(title);
            }
        } else {
            noLocDefaultLayout.setVisibility(View.VISIBLE);
        }
        progressBar = findViewById(R.id.progress_bar);

        getForecasts(currentLoc);
    }//onCreate

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_LOCATION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //get the name of the loc
            String nameLoc = data.getStringExtra(Constant.INTENT_EXTRA_LOC_NAME);
            if (TextUtils.isEmpty(nameLoc)) {
                return;
            }
            //get the corresponding Loc object
            Loc loc = SaveLoadList.getLocFromDb(this, nameLoc);
            if (loc == null) {
                return;
            }
            locArrayList = SaveLoadList.loadLocList(this);
            currentLoc = loc;
            actionBar.setTitle(loc.getName());
            getForecasts(currentLoc);
        }

    }//onActivityResult

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast_menu, menu);

        //find the search item
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            setupSearch(searchItem);
        }//if

        //return true since we have managed it
        return true;
    }//onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get the size of array
        switch (item.getItemId()) {
            case R.id.action_find_me:
                findMe();
                return true;
            case R.id.action_saved_locations:
                showSavedLocations();
                return true;
            case R.id.action_add_locations:
                Intent intent = new Intent(ForecastActivity.this, AddLocationActivity.class);
                startActivityForResult(intent, ADD_LOCATION_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }//switch
    }//onOptionsItemSelected

    private void showSavedLocations() {
        showLocListDialog(this, locArrayList);
    }//showSavedLocations



    /*------------------------------------------------------------------------------------------
                    ---------------    HELPER METHODS    ---------------
    ------------------------------------------------------------------------------------------*/

    private void findMe() {
        //check internet connection
        if (!HelperFunctions.isNetworkAvailable(this)) {
            noConnectionLayout.setVisibility(View.VISIBLE);
            return;
        }

        //check for permission first and ask it if needed
        if (HelperFunctions.checkLocationPermission(this)) {
            //we have permission, get the user's location
            getDeviceLocation();
        } else {
            //don't have permission, ask for it
            askLocationPermission(this, this);
        }
    }//findMe

    /**
     * When we request permission, this fets called back with the results.
     * We figure out if the user has granted the permission, or not and act accordingly
     * @param requestCode : request code int we used when requesting the permission
     * @param permissions : the permission we requested
     * @param grantResults : results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constant.LOCATION_PERMISSION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted,
                    getDeviceLocation();
                    return;
                } else {
                    // permission denied, show the message
                    noLocationLayout.setVisibility(View.VISIBLE);
                    return;
                }
        }//switch
    }//onRequestPermissionsResult

    private void getDeviceLocation() {
        //remove the warning views if present
        if (noLocationLayout.getVisibility() != View.GONE) {
            noLocationLayout.setVisibility(View.GONE);
        }

        if (noLocDefaultLayout.getVisibility() != View.GONE) {
            noLocDefaultLayout.setVisibility(View.GONE);
        }
        // Construct a FusedLocationProviderClient.
        FusedLocationProviderClient fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this);
        try {
            if (HelperFunctions.checkLocationPermission(this)) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            //get the result and save it
                            Location location = task.getResult();
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            setUserLocation(latLng);
                        } else {
                            showAddLocationDialog(Constant.ALERT_CODE_UNABLE_FIND_DEVICE);
                        }
                    }
                })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showAddLocationDialog(Constant.ALERT_CODE_UNABLE_FIND_DEVICE);
                            }
                        });
            } else {
                return;
            }

        } catch (Exception e) {
        }//try-catch

    }//getDeviceLocation

    private void setUserLocation(LatLng latLng) {
        if (latLng == null) {
            return;
        }

        //remove the warning views if present
        if (noLocationLayout.getVisibility() != View.GONE) {
            noLocationLayout.setVisibility(View.GONE);
        }

        if (noLocDefaultLayout.getVisibility() != View.GONE) {
            noLocDefaultLayout.setVisibility(View.GONE);
        }

        //create a new Loc
        final Loc loc = new Loc(latLng);

        //get the AW code and set all URLs
        progressBar.setVisibility(View.VISIBLE);

        //first we need to get the codeURL and then get the code
        URL locationCodeUrl = loc.getLocationCodeUrlAW();
        if (locationCodeUrl == null) {
            return;
        }

        NetworkCallsUtils.NetworkCallAccuWeatherCode networkCallAccuWeatherCode = new
                NetworkCallsUtils.NetworkCallAccuWeatherCode(new NetworkCallsUtils.NetworkCallAccuWeatherCode.AsyncResponse() {
            /**
             *  This gets called when the code is ready from the background network
             *  call and we set the data on loc
             * @param output : HashMap containing key and name
             */
            @Override
            public void processFinish(HashMap<String, String> output) {
                if (output == null) {
                    loc.setAllUrls();

                    currentLoc = loc;
                } else {
                    //set key and name
                    String key = output.get(Constant.AW_KEY);

                    //set the key
                    loc.setKeyAW(key);

                    //set all urls
                    loc.setAllUrls();

                    //set current loc
                    currentLoc = loc;
                }
                deviceLocation = true;

                //get weather
                getForecasts(currentLoc);
            }//processFinish

        });
        networkCallAccuWeatherCode.execute(locationCodeUrl);

    }//setUserLocation

    private void setupSearch(MenuItem searchItem) {
        //create a SearchView object using the search menu item
        SearchView searchView = (SearchView) searchItem.getActionView();
        //add hint
        searchView.setQueryHint(getString(R.string.enter_address_hint));
        //closes the keyboard when the user clicks the search button
        searchView.setIconifiedByDefault(true);
        //get a reference to the search box, so we can change the input type to cap words
        int id1 = searchView.getContext().getResources().
                getIdentifier("android:id/search_src_text", null, null);
        EditText searchEditText = searchView.findViewById(id1);
        searchEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        // use this method for search process
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /* Called when the user submits the query. This could be due to a key press on the
                keyboard or due to pressing a submit button. The listener can override the standard
                behavior by returning true to indicate that it has handled the submit request.
                Otherwise return false to let the SearchView handle the submission by launching
                any associated intent. */
            @Override
            public boolean onQueryTextSubmit(String query) {
                // use this method when query submitted
                searchAddress(query);
                return false;
            }

            //Called when the query text is changed by the user.
            @Override
            public boolean onQueryTextChange(String newText) {
                // use this method for auto complete search process
                return false;
            }
        });//query text change listener
    }//setupSearch

    private void searchAddress(String query) {
        //TODO: show dialog for going to add location if no result or more than 1
        //check for geocoder availability
        if (!Geocoder.isPresent()) {
            showAddLocationDialog(Constant.ALERT_CODE_NO_GEOCODER);
            return;
        }
        //Now we know it is available, Create geocoder to retrieve the location
        // responses will be localized for the given Locale. (A Locale object represents a specific geographical,
        // political, or cultural region. An operation that requires a Locale to perform its task is called locale-sensitive )

        //create localized geocoder
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            //the second parameter is the number of max results, here we set it to 3
            List<Address> addresses = geocoder.getFromLocationName(query, 3);
            //check to make sure we got results
            if (addresses.size() < 1) {
                showAddLocationDialog(Constant.ALERT_CODE_NO_RESULT);
                return;
            }//if

            //go through all the results and put them on map
            int counter = 0;
            for (Address result : addresses) {
                LatLng latLng = new LatLng(result.getLatitude(), result.getLongitude());
                counter++;
            }//for

            //don't need to set bounds if there is only one result. Just move the camera
            if (counter <= 1) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                //pass to helper method to set it up
                setUserLocation(latLng);
            } else {
                //more than one result
                showAddLocationDialog(Constant.ALERT_CODE_MULTIPLE_RESULTS);
            }

        } catch (IOException e) {
        }//try/catch

    }//openSearch



    /**
     * This creates a custom dialog showing all the saved city names for the user to choose, and
     * return the corresponding Loc object
     *
     * @param activity     : host activity
     * @param locArrayList : list of Locs
     */
    private void showLocListDialog(final Activity activity, final ArrayList<Loc> locArrayList) {
        //check for empty list
        if (locArrayList == null || locArrayList.size() < 1) {
            HelperFunctions.showToast(activity.getBaseContext(), getResources().getString(R.string.no_saved_location));
            return;
        }//if

        //create our name list
        ArrayList<String> namesArrayList = new ArrayList<>();
        for (Loc loc : locArrayList) {
            namesArrayList.add(loc.getName());
        }

        if (namesArrayList == null || locArrayList.size() < 1) {
            return;
        }

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.custom_dialog);


        ListView listView = dialog.findViewById(R.id.list_view);
        ArrayAdapter arrayAdapter = new ArrayAdapter(activity, R.layout.dialog_list_item,
                R.id.text_city_name, namesArrayList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //set current loc
                currentLoc = locArrayList.get(position);
                //get the weather
                getForecasts(currentLoc);
                //set the title
                actionBar.setTitle(currentLoc.getName());
                //close dialog
                dialog.dismiss();
            }
        });

        dialog.show();
    }//showLocListDialog

    private void showAddLocationDialog(int alertCode) {

        //Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builderQuestion = new AlertDialog.Builder(this);

        //set the title based on alert code
        switch (alertCode) {
            case Constant.ALERT_CODE_NO_RESULT:
                builderQuestion.setMessage(getResources().getString(R.string.no_searhc_results))
                        .setTitle(getResources().getString(R.string.no_results));
                break;
            case Constant.ALERT_CODE_MULTIPLE_RESULTS:
                builderQuestion.setMessage(getResources().getString(R.string.multiple_search_results))
                        .setTitle(getResources().getString(R.string.multiple_results));
                break;
            case Constant.ALERT_CODE_NO_GEOCODER:
                builderQuestion.setMessage(getResources().getString(R.string.unable_finish_search))
                        .setTitle(getResources().getString(R.string.error));
                break;
            case Constant.ALERT_CODE_UNABLE_FIND_DEVICE:
                builderQuestion.setMessage(getResources().getString(R.string.unable_to_locate_device))
                        .setTitle(getResources().getString(R.string.error));
                break;
            default:
                return;

        }//switch

        // Add the buttons. We can call helper methods from inside the onClick if we need to
        builderQuestion.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(ForecastActivity.this, AddLocationActivity.class);
                //TODO: add extra and do startActivityForResults
                startActivity(intent);
            }
        });
        builderQuestion.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //what happens on this click goes here.
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builderQuestion.create();
        dialog.show();
    }//showAddLocationDialog

    /**
     * Helper method for getting all the forecasts from all providers, parse them, and then
     * place each day's forecast from all providers in one arraylist to be used with fragments
     *
     * @param loc
     */
    private void getForecasts(Loc loc) {
        if (loc == null) {
            return;
        }

        //check internet connection
        if (!HelperFunctions.isNetworkAvailable(this)) {
            noConnectionLayout.setVisibility(View.VISIBLE);
            return;
        }

        //reset tracker
        tracker = 0;

        //show progress bar
        progressBar.setVisibility(View.VISIBLE);

        //fetch all the data

        //------------------------------- AW ----------------------------------
        URL forecastUrlAW = loc.getForecastUrlAW();
        if (forecastUrlAW == null) {
            tracker++;
        } else {
            getResponseAW(forecastUrlAW);
        }//url null

        //------------------------------- DS ----------------------------------
        URL forecastUrlDS = loc.getForecastUrlDS();
        if (forecastUrlDS == null) {
            tracker++;
        } else {
            getResponseDS(forecastUrlDS);
        }//url null

        //------------------------------- WB ----------------------------------
        URL forecastUrlWB = loc.getForecastUrlWB();
        if (forecastUrlWB == null) {
            tracker++;
        } else {
            getResponseWB(forecastUrlWB);
        }//url null

        //------------------------------- WU ----------------------------------
        URL forecastUrlWU = loc.getForecastUrlWU();
        if (forecastUrlWU == null) {
            tracker++;
        } else {
            getResponseWU(forecastUrlWU);
        }//url null
    }//getForecasts

    private void createDayArrayLists() {
        if (tracker != 4) {
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
        //first remove all tabs
        tabLayout.removeAllTabs();

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
        //get the response
        NetworkCallsUtils.AccuWeatherForecastTask forecastTaskAW = new
                NetworkCallsUtils.AccuWeatherForecastTask(new NetworkCallsUtils.AccuWeatherForecastTask.AsyncResponse() {
            @Override
            public void processFinish(String jsonResponse) {
                if (TextUtils.isEmpty(jsonResponse)) {
                    tracker++;
                    return;
                }
                getWeathersAW(jsonResponse);
            }
        });
        forecastTaskAW.execute(url);
    }//getResponseAW

    private void getWeathersAW(String jsonResponse) {
        weatherAW = new ArrayList<>();
        try {
            weatherAW = ParseJSON.parseAccuWeatherForecast(jsonResponse);
        } catch (JSONException e) {
        } finally {
            tracker++;
            createDayArrayLists();
        }
    }//getWeathersAW

    //------------------------------- DS ----------------------------------
    private void getResponseDS(URL url) {
        //get response
        NetworkCallsUtils.DarkSkyForecastTask forecastTask = new
                NetworkCallsUtils.DarkSkyForecastTask(new NetworkCallsUtils.DarkSkyForecastTask.AsyncResponse() {
            @Override
            public void processFinish(String jsonResponse) {
                if (TextUtils.isEmpty(jsonResponse)) {
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
        weatherDS = new ArrayList<>();
        try {
            weatherDS = ParseJSON.parseDarkSkyForecast(jsonResponse);
            //start the adapter
        } catch (JSONException e) {
        } finally {
            tracker++;
            createDayArrayLists();
        }
    }//getWeathersDS

    //------------------------------- WB ----------------------------------
    private void getResponseWB(URL url) {
        //get the response
        NetworkCallsUtils.WeatherBitForecastTask forecastTask = new
                NetworkCallsUtils.WeatherBitForecastTask(new NetworkCallsUtils.WeatherBitForecastTask.AsyncResponse() {
            @Override
            public void processFinish(String jsonResponse) {
                if (TextUtils.isEmpty(jsonResponse)) {
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
        weatherWB = new ArrayList<>();
        try {
            weatherWB = ParseJSON.parseWeatherBitForecast(jsonResponse);
        } catch (JSONException e) {
        } finally {
            //set the name
            if (deviceLocation) {
                for (Weather w : weatherWB) {
                    String name = w.getCityName();
                    if (!TextUtils.isEmpty(name)) {
                        actionBar.setTitle(name);
                        break;
                    }//if
                }//for
            }//if device

            tracker++;
            createDayArrayLists();
        }
    }//getWeathersWB

    //------------------------------- WU ----------------------------------
    private void getResponseWU(URL url) {
        //get the response
        NetworkCallsUtils.WeatherUnlockedForecastTask forecastTask = new
                NetworkCallsUtils.WeatherUnlockedForecastTask(new NetworkCallsUtils.WeatherUnlockedForecastTask.AsyncResponse() {
            @Override
            public void processFinish(String jsonResponse) {
                if (TextUtils.isEmpty(jsonResponse)) {
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
        weatherWU = new ArrayList<>();
        try {
            weatherWU = ParseJSON.parseWeatherUnlockedForecast(jsonResponse);
        } catch (JSONException e) {
        } finally {
            tracker++;
            createDayArrayLists();
        }
    }//getWeathersWB

    /**
     * Helper method for showing a message to the user informing them about the benefits of turning on their
     * location. and also can direct them to the location settings of their phone
     */
    private void askLocationPermission(final Context context, final Activity activity) {
        //Create a dialog to inform the user about this feature's permission
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //Chain together various setter methods to set the dialogConfirmation characteristics
        builder.setMessage(R.string.permission_required_text_dialog).setTitle(R.string.permission_required_title_dialog);
        // Add the buttons. We can call helper methods from inside the onClick if we need to
        builder.setPositiveButton(R.string.permission_required_yes_dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                /*  We need to differentiate if it is the first time we are asking or not
                    If it is, we just ask permission,
                    If it is not, then we will check rationale (it returns false the very first time
                 */
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean firstTime = sharedPreferences.getBoolean(Constant.PREF_KEY_FIRST_TIME_LOCATION, true);
                if (firstTime) {
                    //we dont need to check rationale, just ask
                    if (ContextCompat.checkSelfPermission(context,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(activity,
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                Constant.LOCATION_PERMISSION_REQUEST_CODE);
                        //set the boolean to false, we only run this the very first time
                        sharedPreferences.edit().putBoolean(Constant.PREF_KEY_FIRST_TIME_LOCATION, false).apply();
                    }//need permission
                } else {
                    //this is not the first time anymore, so we check rationale
                    if (ContextCompat.checkSelfPermission(context,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                        /*
                        Here we check to see if they have selected "never ask again". If that is the
                        case, then shouldShowRequestPermissionRationale will return false. If that
                        is false, and the build version is higher than 23 (that feature is only
                        available to >= 23 then send them to the
                         */
                        if (Build.VERSION.SDK_INT >= 23 && !(activity.shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION))) {
                            //This is the case when the user checked the box, so we send them to the settings
                            HelperFunctions.openPermissionSettings(activity);
                        } else {
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    Constant.LOCATION_PERMISSION_REQUEST_CODE);
                        }

                    } else {
                        //this is the case that the user has never denied permission, so we ask for it
                        ActivityCompat.requestPermissions(activity,
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                Constant.LOCATION_PERMISSION_REQUEST_CODE);
                    }//if-else build version
                }//if-else first time
            }//positive button
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //set the layout to visible
                noLocationLayout.setVisibility(View.VISIBLE);
            }
        });
        //build and show dialog
        builder.create().show();
    }//askLocationPermission
}//ForecastActivity

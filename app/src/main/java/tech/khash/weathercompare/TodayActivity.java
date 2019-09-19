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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import tech.khash.weathercompare.adapter.WeatherListAdapterToday;
import tech.khash.weathercompare.model.Constant;
import tech.khash.weathercompare.model.Loc;
import tech.khash.weathercompare.model.Weather;
import tech.khash.weathercompare.utilities.HelperFunctions;
import tech.khash.weathercompare.utilities.NetworkCallsUtils;
import tech.khash.weathercompare.utilities.SaveLoadList;

public class TodayActivity extends AppCompatActivity {

    private static final String TAG = TodayActivity.class.getSimpleName();

    //for sending and receiving location from add location activity
    private static final int ADD_LOCATION_REQUEST_CODE = 2;

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private WeatherListAdapterToday adapter;
    private Loc currentLoc;

    private ArrayList<Weather> weatherArrayList;
    private ArrayList<Loc> locArrayList;

    private Boolean isDay;
    private boolean deviceLocation = false;
    private int tracker;

    private LinearLayout noConnectionLayout;
    private LinearLayout noLocationLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recycler_view);

        noConnectionLayout = findViewById(R.id.no_internet_view);
        noLocationLayout = findViewById(R.id.no_location_view);

        tracker = 0;

        locArrayList = SaveLoadList.loadLocList(this);

        //check internet connection
        if (!HelperFunctions.isNetworkAvailable(this)) {
            noConnectionLayout.setVisibility(View.VISIBLE);
            return;
        }


        //get the loc id from intent extra
        if (getIntent().hasExtra(Constant.INTENT_EXTRA_LOC_NAME)) {
            //set device location to false
            String id = getIntent().getStringExtra(Constant.INTENT_EXTRA_LOC_NAME);
            if (!TextUtils.isEmpty(id)) {
                //get the corresponding loc
                Loc loc = SaveLoadList.getLocFromDb(this, id);
                if (loc != null) {
                    //set the current loc
                    currentLoc = loc;
                    //set title
                    setTitle(currentLoc.getName());
                    //start getting results
                    calculateIsDay();
                }//null-loc
            }//empty string
        } else if (getIntent().hasExtra(Constant.INTENT_EXTRA_DEVICE_LOCATION)) {
            if (getIntent().getBooleanExtra(Constant.INTENT_EXTRA_DEVICE_LOCATION, false)) {
                //set device location to true
                deviceLocation = true;
                findMe();
            }
        }
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
            calculateIsDay();
        }
    }//onActivityResult

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.today_menu, menu);

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
            case R.id.action_forecast:
                //create intent
                Intent forecastIntent = new Intent(TodayActivity.this, ForecastActivity.class);
                //figure out if it is device location, or Loc
                if (deviceLocation) {
                    //convert our Loc object to Gson to pass it in extra
                    Gson gson = new Gson();
                    String json = gson.toJson(currentLoc);
                    forecastIntent.putExtra(Constant.INTENT_EXTRA_DEVICE_LOCATION, json);
                    forecastIntent.putExtra(Constant.INTENT_EXTRA_DEVICE_LOCATION_NAME, getTitle());
                    startActivity(forecastIntent);
                } else {
                    forecastIntent.putExtra(Constant.INTENT_EXTRA_LOC_NAME, currentLoc.getName());
                    startActivity(forecastIntent);
                }
                return true;
            case R.id.action_add_locations:
                Intent intent = new Intent(TodayActivity.this, AddLocationActivity.class);
                startActivityForResult(intent, ADD_LOCATION_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }//switch
    }//onOptionsItemSelected

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

            //go through all the results
            int counter = addresses.size();

            if (counter == 1) {
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
        //remove the warning
        if (noLocationLayout.getVisibility() != View.GONE) {
            noLocationLayout.setVisibility(View.GONE);
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
                //permission denied (should never happen since we have already checked it before this call
                return;
            }

        } catch (Exception e) {
        }//try-catch

    }//getDeviceLocation

    private void setUserLocation(LatLng latLng) {
        if (latLng == null) {
            return;
        }

        //set the boolean to true
        deviceLocation = true;

        //create a new Loc
        final Loc loc = new Loc(latLng);

        //get the AW code and set all URLs
        progressBar.setVisibility(View.VISIBLE);

        //first we need to get the codeURL and then get the code
        URL locationCodeUrl = loc.getLocationCodeUrlAW();
        if (locationCodeUrl == null) {
            currentLoc = loc;
            calculateIsDay();
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

                //check to see if we need to calculate isDay first, otherwise get all weather
                calculateIsDay();

            }//processFinish

        });
        networkCallAccuWeatherCode.execute(locationCodeUrl);

    }//setUserLocation

    private void showSavedLocations() {
        showLocListDialog(this, locArrayList);
    }//showSavedLocations

    /*------------------------------------------------------------------------------------------
                    ---------------    HELPER METHODS    ---------------
    ------------------------------------------------------------------------------------------*/


    private void calculateIsDay() {
        if (currentLoc == null) {
            return;
        }//null loc

        //check internet connection
        if (!HelperFunctions.isNetworkAvailable(this)) {
            noConnectionLayout.setVisibility(View.VISIBLE);
            return;
        }

        //make the progress bar visible
        progressBar.setVisibility(View.VISIBLE);

        URL sunriseSunsetUrl = currentLoc.getSunriseSunsetUrl();
        if (sunriseSunsetUrl == null) {
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
            sunriseSunsetTask.execute(sunriseSunsetUrl);
        }

    }//calculateIsDay

    private void getAllWeather() {

        if (currentLoc == null) {
            //remove progress bar if it is running
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }
            return;
        }//null loc

        //check internet connection
        if (!HelperFunctions.isNetworkAvailable(this)) {
            noConnectionLayout.setVisibility(View.VISIBLE);
            return;
        }

        //show progress bar if it is not visible
        if (progressBar.getVisibility() != View.VISIBLE) {
            progressBar.setVisibility(View.VISIBLE);
        }

        //refresh the array list
        weatherArrayList = new ArrayList<>();

        kickOffAccuWeather();
        kickOffDarkSky();
        kickOffWeatherBit();
        kickOffWeatherUnlocked();

    }//getAllWeather

    private void kickOffAccuWeather() {
        //check for null loc
        if (currentLoc == null) {
            return;
        }

        if (!currentLoc.hasKeyAW()) {
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
            return;
        }

        //get the location code
        URL todayUrlAW = currentLoc.getTodayUrlAW();

        if (todayUrlAW == null) {
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
        } else {
            NetworkCallsUtils.AccuWeatherTodayTask accuWeatherTodayTask = new
                    NetworkCallsUtils.AccuWeatherTodayTask(new NetworkCallsUtils.AccuWeatherTodayTask.AsyncResponse() {
                @Override
                public void processFinish(Weather output) {
                    //only add if it is not null
                    if (output != null) {
                        weatherArrayList.add(output);
                    }
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
            return;
        }

        URL todayUrlDS = currentLoc.getForecastUrlDS();

        if (todayUrlDS == null) {
        } else {
            NetworkCallsUtils.DarkSkyTodayTask darskSkyTodayTask = new
                    NetworkCallsUtils.DarkSkyTodayTask(new NetworkCallsUtils.DarkSkyTodayTask.AsyncResponse() {
                @Override
                public void processFinish(Weather output) {
                    //only add if it is not null
                    if (output != null) {
                        weatherArrayList.add(output);
                    }
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
            return;
        }

        URL todayUrlWB = currentLoc.getForecastUrlWB();

        if (todayUrlWB == null) {
        } else {
            NetworkCallsUtils.WeatherBitTodayTask weatherBitTodayTask = new
                    NetworkCallsUtils.WeatherBitTodayTask(new NetworkCallsUtils.WeatherBitTodayTask.AsyncResponse() {
                @Override
                public void processFinish(Weather output) {
                    //only add if it is not null
                    if (output != null) {
                        weatherArrayList.add(output);
                    }

                    //is this is from device location, set the name
                    //TODO: testing
                    if (deviceLocation) {
                        setTitle(output.getCityName());
                    }

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
            return;
        }

        URL todayUrlWU = currentLoc.getForecastUrlWU();

        if (todayUrlWU == null) {
        } else {
            NetworkCallsUtils.WeatherUnlockedTodayTask weatherUnlockedTodayTask = new
                    NetworkCallsUtils.WeatherUnlockedTodayTask(new NetworkCallsUtils.WeatherUnlockedTodayTask.AsyncResponse() {
                @Override
                public void processFinish(Weather output) {
                    //only add if it is not null
                    if (output != null) {
                        weatherArrayList.add(output);
                    }
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
            return;
        }

        //set the name
        for (Weather w : weatherArrayList) {
            String name = w.getCityName();
            if (!TextUtils.isEmpty(name)) {
                setTitle(name);
                break;
            }
        }


        if (isDay == null) {
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
                calculateIsDay();
                //set the title
                activity.setTitle(currentLoc.getName());
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
                Intent intent = new Intent(TodayActivity.this, AddLocationActivity.class);
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


}//TodayActivity

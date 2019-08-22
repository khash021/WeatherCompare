package tech.khash.weathercompare;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import tech.khash.weathercompare.model.Loc;
import tech.khash.weathercompare.utilities.HelperFunctions;
import tech.khash.weathercompare.utilities.SaveLoadList;

public class AddLocationActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener {

    private static final String TAG = AddLocationActivity.class.getSimpleName();

    private GoogleMap mMap;
    private LatLng latLngCamera;
    private LatLng latLngUser;
    private EditText editTextName;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    //That is, the last-known location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    //DEFAULTS FOR NOW
    private final LatLng DEFAULT_LAT_LNG_VANCOUVER = new LatLng(49.273367, -123.102950);
    private final float DEFAULT_ZOOM = 14.0f;
    private final int SEARCH_MAX_RESULTS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        editTextName = findViewById(R.id.edit_text_name);
        Button saveButton = findViewById(R.id.button_save_location);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLocation();
            }
        });

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }//onCreate


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.v(TAG, "onMapReady Called");
        mMap = googleMap;

        //set the camera idle listener to get the location
        mMap.setOnCameraIdleListener(this);

        //disable map toolbar
        UiSettings uiSettings = mMap.getUiSettings();
//        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setZoomControlsEnabled(true); //for emulator since cant zoom out with mouse
        uiSettings.setCompassEnabled(true);

        //for testing, we are moving camera to Vancouver, Science center
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LAT_LNG_VANCOUVER, DEFAULT_ZOOM));
    }//onMapReady

    @Override
    public void onCameraIdle() {
        Log.v(TAG, "onCameraIdle Called");
        //get LatLng
         if (isMapReady()) {
             latLngCamera = mMap.getCameraPosition().target;
         } else {
             Log.v(TAG, "Map is null from onCameraIdle");
         }
    }//onCameraIdle

    /**
     * Inflates the menu, and adds items to the action bar if it is present.
     *
     * @param menu Menu to inflate.
     * @return Returns true if the menu inflated.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_location_menu, menu);

        //find the search item
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            setupSearch(searchItem);
        }//if
        return true;
    }//onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_find_me:
                findMeMap();
                return true;
            case R.id.action_search:
                //TODO:
                HelperFunctions.showToast(this, "Search");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }//switch
    }//onOptionsItemSelected

    /*------------------------------------------------------------------------------------------
                    ---------------    HELPER METHODS    ---------------
    ------------------------------------------------------------------------------------------*/



    //Helper method for find me
    private void findMeMap() {
        //check for permission first and ask it if needed
        if (HelperFunctions.checkLocationPermission(this)) {
            //we have permission. Check map first and enable my location if necessary
            if (isMapReady()) {
                getDeviceLocation();
            } else {
                Log.v(TAG, "Map not ready from findMeMap");
                HelperFunctions.showToast(this, "Map not ready");
            }
        } else {
            //don't have permission, ask for it
            HelperFunctions.askLocationPermission(this, this);
        }
    }//findMeMap

    //Helper method for getting my location (Permission has already been checked)
    private void getDeviceLocation() {
        try {
            if (HelperFunctions.checkLocationPermission(this)) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            //get the result and save it
                            mLastKnownLocation = task.getResult();
                            setUserLocation();
                            moveCamera(latLngUser, 14.0f);
                        } else {
                            Log.v(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(DEFAULT_LAT_LNG_VANCOUVER, DEFAULT_ZOOM));
                        }
                    }
                })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Failed to get the last know location", e);
                            }
                        });
            } else {
                //permission denied (should never happen since we have already checked it before this call
                Log.wtf(TAG, "Location permission denied from getDeviceLocation");
                return;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error finding my location from getDeviceLocation", e);
        }//try-catch
    }//getDeviceLocation

    //Sets the latlng of the user from get last location
    private void setUserLocation() {
        if (mLastKnownLocation == null) {
            return;
        }
        try {
            latLngUser = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        } catch (Exception e) {
            Log.e(TAG, "Error setting user lat, lng from setUserLocation", e);
        }//try-catch
    }//setUserLocation

    //Helper method for moving camera to specific location, with specific zoom
    private void moveCamera(LatLng latLng, float zoom) {
        //check map first
        if (!isMapReady()) {
            HelperFunctions.showToast(this, "Map not ready");
            Log.v(TAG, "Map not ready from moveCamera");
            return;
        }//map not ready
        //use try catch in case there is something wrong with the input
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        } catch (Exception e) {
            Log.e(TAG, "Error movign camera from moveCamera", e);
        }
    }//moveCamera

    //helper method for saving the location
    private void saveLocation() {
        String name = editTextName.getText().toString().trim();
        if (name == null || name.isEmpty()) {
            HelperFunctions.showToast(this, getResources().getString(R.string.name_required_toast));
            return;
        }

        if (latLngCamera == null) {
            HelperFunctions.showToast(this, getResources().getString(R.string.error_getting_location_toast));
            Log.v(TAG, "latLngCamera is null from saveLocation method");
            return;
        }

        //create Loc object
        Loc location = new Loc(name, latLngCamera);
        //add it to the list
        SaveLoadList.addToLocList(this, location);

        HelperFunctions.showToast(this, "\"" + name + "\"" + " " + getString(R.string.location_added_successfully_toast));
        Log.v(TAG, "Location added.\nName: " + name + "\nLatLng: " + latLngCamera);

        //return to sender
        Intent returnIntent = new Intent();
        //we return the string name of the loc
        returnIntent.putExtra(MainActivity.FENCE_EDIT_EXTRA_INTENT_LOC_NAME, name);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }//saveLocation

    //Helper method for setting up app bar search item
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

    //Helper method for searching the address
    private void searchAddress(String query) {
        //check for geocoder availability
        if (!Geocoder.isPresent()) {
            Log.v(TAG, "Geocoder not available - searchAddress");
            HelperFunctions.showToast(this, getString(R.string.geocoder_not_available_toast));
            return;
        }
        //Now we know it is available, Create geocoder to retrieve the location
        // responses will be localized for the given Locale. (A Locale object represents a specific geographical,
        // political, or cultural region. An operation that requires a Locale to perform its task is called locale-sensitive )

        //create localized geocoder
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());


        try {
            //the second parameter is the number of max results, here we set it to 3
            List<Address> addresses = geocoder.getFromLocationName(query, SEARCH_MAX_RESULTS);
            //check to make sure we got results
            if (addresses.size() < 1) {
                HelperFunctions.showToast(this, getString(R.string.no_results_found_toast));
                Log.v(TAG, "No results - searchAddress");
                return;
            }//if

            //check the map first
            if (mMap == null) {
                Log.v(TAG, "Map not ready - searchAddress");
                return;
            }

            //make a builder to include all points
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            //clear the map
            mMap.clear();

            //go through all the results and put them on map
            int counter = 0;
            for (Address result : addresses) {
                LatLng latLng = new LatLng(result.getLatitude(), result.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng));
                //include the marker
                builder.include(latLng);
                //TODO: for testing only
                Log.v(TAG, "Search results: " + "\nFeature: " + result.getFeatureName() +
                        "  ---   " + "Admin area: " + result.getAdminArea() + "  ---   " +
                        "Country code: " + result.getCountryCode() + "  ---   " +
                        "Country name: " + result.getCountryName() + "  ---   " +
                        "Postal code: " + result.getPostalCode() + "  ---   " +
                        "Subadmin area: " + result.getSubAdminArea() + "  ---   " +
                        "Locale: " + result.getLocale().toString() + "  ---   " +
                        "Latutude: " + result.getLatitude() + "  ---   " +
                        "Longitude: " + result.getLongitude());
                counter++;
            }//for

            //don't need to set bounds if there is only one result. Just move the camera
            if (counter <= 1) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                moveCamera(latLng, DEFAULT_ZOOM);
                //add the name to the filed
                String featureName = address.getFeatureName();
                if (featureName != null && !featureName.isEmpty()) {
                    editTextName.setText(featureName);
                }
                return;
            }

            //since we have more than one results, we want to show them all, so we need the builder
            //build the bounds builder
            LatLngBounds bounds = builder.build();
            //Setting the width and height of your screen (if not, sometimes the app crashes)
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.2); // offset from edges of the map 20% of screen

            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));//this is the pixel padding

        } catch (IOException e) {
            Log.e(TAG, "Error getting location", e);
        }//try/catch
    }//searchAddress

    private boolean isMapReady() {
        return mMap != null;
    }//isMapReady

}//AddLocationActivity

package tech.khash.weathercompare;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

import tech.khash.weathercompare.adapter.LocListAdapter;
import tech.khash.weathercompare.model.Constant;
import tech.khash.weathercompare.model.Loc;
import tech.khash.weathercompare.utilities.NetworkCallsUtils;
import tech.khash.weathercompare.utilities.SaveLoadList;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        LocListAdapter.ListItemClickListener,
        LocListAdapter.ListLongClickListener{

    //TODO: settings for units
    //TODO: sunrise and sunset (?)

    private final static String TAG = MainActivity.class.getSimpleName();

    //for sending and receiving location from add location activity
    private static final int ADD_LOCATION_REQUEST_CODE = 1;

    //drawer layout used for navigation drawer
    private DrawerLayout drawerLayout;
    private SeekBar seekBar;

    //adapter
    private ArrayList<Loc> locArrayList;
    private LocListAdapter adapter;
    private RecyclerView recyclerView;

    private SharedPreferences sharedPreferences;

    //for tracking changes that needs the list to be updated/recreated
    private boolean needsUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Set the tool bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //change the title
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.app_name));
        //Set the menu icon
        ActionBar actionbar = getSupportActionBar();
        //Enable app bar home button
        actionbar.setDisplayHomeAsUpEnabled(true);
        //Set the icon
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        //In order for the button to open the menu we need to override onOption Item selected (below onCreate)

        //get the drawer layout and navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        seekBar = findViewById(R.id.seekbar);

        //this gets the db, and set the corresponding empty view/recycler view
        updateRecyclerView();

        //find the fab and set it up
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //needs update
                needsUpdate = true;
                openAddLocation();
            }
        });
        //ask for rate (based on number of visits)
        rateApp();

        //add the add banner
        addBannerAd();

        //update Locs
        checkLocs();
    }//onCreate

    @Override
    protected void onStart() {
        //check for update boolean
        if (needsUpdate) {
            recreate();
        }
        super.onStart();
    }//onStart

    @Override
    protected void onPause() {
        super.onPause();
        //if the navigation drawer is open, we close it so when the user is directed back, it doesn't stay open
        if (drawerLayout != null) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        }
    }//onPause

    /**
     * We start AddLocation activity for results for adding new location. This gets
     * called when we return from addLocationActivity
     *
     * @param requestCode : request code we sent with the original intent
     * @param resultCode  : result code that the activity has set
     * @param data        : the data being sent back
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //check to make sure it is the right one
        if (requestCode == ADD_LOCATION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //get the name of the loc
            String nameLoc = data.getStringExtra(Constant.INTENT_EXTRA_LOC_NAME);
            if (nameLoc == null || nameLoc.isEmpty()) {
                return;
            }//null name
            //get the corresponding Loc object
            Loc loc = SaveLoadList.getLocFromDb(this, nameLoc);
            //set the current loc only if it is not null
            if (loc == null) {
                return;
            }//loc-null
            //we setup the loc info (setting URLs, and AW location code) and update db
            setLocInfo(this, loc);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }//onActivityResult


    /**
     * Handles the Back button: closes the nav drawer.
     */
    @Override
    public void onBackPressed() {
        //If the user clicks the systems back button and if the navigation drawer is open, it closes it
        if (drawerLayout != null) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }//onBackPressed

    /**
     * Inflates the menu, and adds items to the action bar if it is present.
     *
     * @param menu Menu to inflate.
     * @return Returns true if the menu inflated.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }//onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //open navigation drawer
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_find_me:
                //start today activity with user's location
                Intent deviceTodayIntent = new Intent(MainActivity.this, TodayActivity.class);
                deviceTodayIntent.putExtra(Constant.INTENT_EXTRA_DEVICE_LOCATION, true);
                startActivity(deviceTodayIntent);
                return true;
            case R.id.action_sort_name_ascending:
                sortNameAscending();
                return true;
            case R.id.action_sort_name_descending:
                sortNameDescending();
                return true;
            case R.id.action_refresh:
                recreate();
                return true;
            case R.id.action_delete_all:
                showDeleteAllDialog(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }//switch
    }//onOptionsItemSelected

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_add_location:
                openAddLocation();
                return true;
            case R.id.nav_settings:
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.nav_contact:
                //send email. Use Implicit intent so the user can choose their preferred app
                //create uri for email
                String email = getString(R.string.contact_email);
                Uri emailUri = Uri.parse("mailto:" + email);
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, emailUri);
                //make sure the device can handle the intent before sending
                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(emailIntent);
                    return true;
                }
                return false;
            case R.id.nav_rate:
                rateOnGooglePlay();
                return true;
            case R.id.nav_share:
                ShareCompat.IntentBuilder.from(this)
                        .setType("text/plain")
                        .setChooserTitle(R.string.share_intent_title)
                        .setSubject(getResources().getString(R.string.share_dialog_title))
                        .setText(getResources().getString(R.string.google_play_address))
                        .startChooser();
                return true;
            case R.id.nav_about:
                //TODO:
                return true;
            case R.id.nav_privacy_policy:
                try {
                    //TODO: url
                    Uri addressUri = Uri.parse("");
                    Intent privacyIntent = new Intent(Intent.ACTION_VIEW, addressUri);
                    startActivity(privacyIntent);
                } catch (Exception e) {
                }
                return true;
            default:
                return false;
        }//switch
    }//onNavigationItemSelected



    @Override
    public void onListItemClick(int clickedItemIndex, int buttonClick) {
        //get the corresponding fence object
        Loc loc = locArrayList.get(clickedItemIndex);
        String id = loc.getName();

        //if it is a click on the view, we show the old original current, otherwise we send them to
        //newer current/forecast classes
        switch (buttonClick) {
            //TODO: remove one of them
            //for now, both are doing the same thing,
            case -1:
            case LocListAdapter.TODAY_BUTTON:
                //start compare activity, passing in the loc object
                Intent todayIntent = new Intent(MainActivity.this, TodayActivity.class);
                todayIntent.putExtra(Constant.INTENT_EXTRA_LOC_NAME, id);
                startActivity(todayIntent);
                break;

            case LocListAdapter.FORECAST_BUTTON:
                Intent forecastIntent = new Intent(MainActivity.this, ForecastActivity.class);
                forecastIntent.putExtra(Constant.INTENT_EXTRA_LOC_NAME, id);
                startActivity(forecastIntent);
                break;

        }//switch
    }//onListItemClick

    @Override
    public void onLongClick(int position) {
        //get the corresponding Loc object
        Loc loc = locArrayList.get(position);

        //show a dialog for deleting
        showLongClickDialog(this, loc);
    }//onLongClick

    /*------------------------------------------------------------------------------------------
                    ---------------    HELPER METHODS    ---------------
    ------------------------------------------------------------------------------------------*/

    /**
     * This gets called only from onResultResults when we return from add location.
     * This helper method, sets the Loc's info such as create URLs, and make the network call
     * to get the AW location code.
     * Then it updates the db so all consequent weather calls could be done using saved URls/codes
     *
     */
    private void setLocInfo(final Context context, final Loc loc) {
        if (loc == null) {
            return;
        }

        //set the URLS
        loc.setAllUrls();

        //update database here
        SaveLoadList.replaceLocInDb(context, loc);

        //first we need to get the codeURL and then get the code
        URL locationCodeUrl = loc.getLocationCodeUrlAW();
        if (locationCodeUrl == null) {
            return;
        }
        //show seekbar
        seekBar.setVisibility(View.VISIBLE);
        NetworkCallsUtils.NetworkCallAccuWeatherCode networkCallAccuWeatherCode = new
                NetworkCallsUtils.NetworkCallAccuWeatherCode(new NetworkCallsUtils.NetworkCallAccuWeatherCode.AsyncResponse() {
            /**
             *  This gets called when the code is ready from the background network
             *  call and we set the data on loc
             * @param output : HashMap containing name and code
             */
            @Override
            public void processFinish(HashMap<String, String> output) {
                //remove seekbar
                seekBar.setVisibility(View.GONE);

                if (output == null) {
                    updateRecyclerView();
                    return;
                }
                //we only need the key here,
                String key = output.get(Constant.AW_KEY);
                //set the key
                loc.setKeyAW(key);

                //update database here
                SaveLoadList.replaceLocInDb(context, loc);

                //update list
                updateRecyclerView();

            }//processFinish
        });
        networkCallAccuWeatherCode.execute(locationCodeUrl);
    }//setLocInfo

    //Helper method for getting the arrayList and set the recycler view
    private void updateRecyclerView() {
        //view containing the empty view
        LinearLayout emptyView = findViewById(R.id.empty_view);

        //get the arrayList, and set the visibility of empty view accordingly
        locArrayList = SaveLoadList.loadLocList(this);
        if (locArrayList == null || locArrayList.size() < 1) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }//if/else - empty view

        // Get a handle to the RecyclerView.
        recyclerView = findViewById(R.id.recycler_view);
        // Create an adapter and supply the data to be displayed.
        adapter = new LocListAdapter(this, locArrayList, this, this);
        // Connect the adapter with the RecyclerView.
        recyclerView.setAdapter(adapter);
        // Give the RecyclerView a default layout manager.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Add divider between items using the DividerItemDecoration
        DividerItemDecoration decoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);
    }//updateRecyclerView

    //open add location activity for results
    private void openAddLocation() {
        Intent addLocationIntent = new Intent(MainActivity.this, AddLocationActivity.class);
        startActivityForResult(addLocationIntent, ADD_LOCATION_REQUEST_CODE);
    }//openAddLocation

    //Helper method for sorting list based on their name (ascending)
    private void sortNameAscending() {
        Collections.sort(locArrayList, new Comparator<Loc>() {
            @Override
            public int compare(Loc o1, Loc o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        //notify the adapter that the data has changed, and it should update
        adapter.notifyDataSetChanged();
    }//sortNameAscending

    //Helper method for sorting list based on their name (ascending)
    private void sortNameDescending() {
        Collections.sort(locArrayList, new Comparator<Loc>() {
            @Override
            public int compare(Loc o1, Loc o2) {
                return o2.getName().compareTo(o1.getName());
            }
        });
        //notify the adapter that the data has changed, and it should update
        adapter.notifyDataSetChanged();
    }//sortNameAscending

    //Helper method for showing the dialog for deleting all data
    private void showDeleteAllDialog(final Context context) {
        //create the builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        //add message and button functionality
        builder.setMessage(R.string.delete_all_dialog_msg)
                .setPositiveButton(R.string.delete_all, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Delete all
                        SaveLoadList.deleteDb(context);
                        recreate();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close the dialog
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }//showUnsavedChangesDialog

    private void showLongClickDialog(final Context context, final Loc loc) {
        //create the builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //set the title
        String title = getResources().getString(R.string.warning);
        builder.setTitle(title);

        //set the body
        String message = getResources().getString(R.string.delete_confirmation) + loc.getName() + "\"?" ;
        builder.setMessage(message);

        //set the buttons
        builder.setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //delete the loc
                SaveLoadList.deleteLoc(context, loc);

                //update the arraylist
                updateRecyclerView();
            }
        })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close dialog
                        dialog.dismiss();
                    }
                });

        //create and show dialog
        builder.create().show();
    }//showLongClickDialog

    /**
     * Helper method for checking the number of times the user has opened the app
     * Once it hits 10, it will ask the user to rate, if the accept, they never get this again.
     * Otherwise it will show twice more after another 10 visits
     */
    private void rateApp() {
        boolean haveRated = sharedPreferences.getBoolean(getResources().getString(R.string.rated_app_pref_key), false);
        if (haveRated) {
            return;
        }

        int count = sharedPreferences.getInt(getResources().getString(R.string.app_open_count_pref_key), 0);
        if (count == 10 || count == 20 || count == 30) {
            //increase the count
            count++;
            sharedPreferences.edit().putInt(getResources().getString(R.string.app_open_count_pref_key), count).apply();
            //show dialog
            showRateDialog();
        } else {
            count++;
            sharedPreferences.edit().putInt(getResources().getString(R.string.app_open_count_pref_key), count).apply();
        }
    }//rateApp


    private void showRateDialog() {
        //create the builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //set the title
        String title = getResources().getString(R.string.rate_us);
        builder.setTitle(title);

        //set the body
        String message = getResources().getString(R.string.rate_us_body);
        builder.setMessage(message);

        //set the buttons
        builder.setPositiveButton(getResources().getString(R.string.rate_us), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //set the boolean to true so it never asks the user again
                sharedPreferences.edit().putBoolean(getResources().getString(R.string.rated_app_pref_key), true).apply();
                //direct to google play
                rateOnGooglePlay();
            }
        })
                .setNegativeButton(getResources().getString(R.string.not_now), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close dialog
                        dialog.dismiss();
                    }
                });

        //create and show dialog
        builder.create().show();
    }//showRateDialog

    private void rateOnGooglePlay() {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }//rateOnGooglePlay

    private void checkLocs() {
        for (Loc loc : locArrayList) {
            if (loc.hasKeyAW()) {
                continue;
            }
            setLocInfo(this, loc);
        }//for
    }//checkLocs

    //Adds the banner Ad
    private void addBannerAd() {
        //initialize the Mobile Ads SDK
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        //find the ad view
        AdView adView = findViewById(R.id.ad_view);
        //create adRequest
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        //load the ad request into the ad view
        adView.loadAd(adRequest);
    }//addBannerAd


}//MainActivity

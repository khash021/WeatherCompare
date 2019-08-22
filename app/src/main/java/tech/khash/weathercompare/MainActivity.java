package tech.khash.weathercompare;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import tech.khash.weathercompare.adapter.LocListAdapter;
import tech.khash.weathercompare.model.Loc;
import tech.khash.weathercompare.utilities.AccuWeatherUtils;
import tech.khash.weathercompare.utilities.HelperFunctions;
import tech.khash.weathercompare.utilities.SaveLoadList;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        LocListAdapter.ListItemClickListener {

    //TODO: check for duplicate name

    private final static String TAG = MainActivity.class.getSimpleName();

    //for sending and receiving location from add location activity
    private static final int ADD_LOCATION_REQUEST_CODE = 1;
    public final static String FENCE_EDIT_EXTRA_INTENT_LOC_NAME = "fence-edit-extra-intent-loc_name";
    public final static String COMPARE_EXTRA_LOC_ID = "compare-extra-loc-id";

    //drawer layout used for navigation drawer
    private DrawerLayout drawerLayout;

    //for holding the current location
    private Loc locCurrent;

    //adapter
    private ArrayList<Loc> locArrayList;
    private LocListAdapter adapter;
    private RecyclerView recyclerView;

    //for tracking changes that needs the list to be updated/recreated
    private boolean needsUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate Called");
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

        //view containing the empty view
        LinearLayout emptyView = findViewById(R.id.empty_view);

        //get the arrayList, and set the visibility of empty view accordingly
        locArrayList = SaveLoadList.loadLocList(this);
        if (locArrayList == null || locArrayList.size() < 1) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }

        //TODO: testing
        logList(locArrayList);

        // Get a handle to the RecyclerView.
        recyclerView = findViewById(R.id.recycler_view);
        // Create an adapter and supply the data to be displayed.
        adapter = new LocListAdapter(this, locArrayList, this);
        // Connect the adapter with the RecyclerView.
        recyclerView.setAdapter(adapter);
        // Give the RecyclerView a default layout manager.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Add divider between items using the DividerItemDecoration
        DividerItemDecoration decoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);

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


        Loc loc = new Loc();
        String key = loc.getKey();
        boolean hasKey = loc.hasKey();

        Log.v(TAG, "Has Key: " + String.valueOf(loc.hasKey()) + "\nKey: " + key);
    }//onCreate

    @Override
    protected void onResume() {
        Log.v(TAG, "onResume Called");
        super.onResume();
    }//onResume

    @Override
    protected void onStart() {
        Log.v(TAG, "onStart Called");
        //check for update boolean
        if (needsUpdate) {
            recreate();
        }
        super.onStart();
//        if (!mGoogleApiClient.isConnecting() || !mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.connect();
//        }
    }//onStart

    @Override
    protected void onPause() {
        Log.v(TAG, "onPause Called");
        super.onPause();
        //if the navigation drawer is open, we close it so when the user is directed back, it doesn't stay open
        if (drawerLayout != null) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        }
    }//onPause

    @Override
    protected void onStop() {
        Log.v(TAG, "onStop Called");
        super.onStop();
//        if (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
    }//onStop

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy Called");
        super.onDestroy();
    }//onDestroy

    /**
     * We start AddLocation activity for results to get the location data. This gets
     * called when we return from that.
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
            String nameLoc = data.getStringExtra(FENCE_EDIT_EXTRA_INTENT_LOC_NAME);
            if (nameLoc == null || nameLoc.isEmpty()) {
                return;
            }//null name
            //get the corresponding Loc object
            Loc loc = SaveLoadList.getLocFromDb(this, nameLoc);
            //set the current loc only if it is not null
            if (loc != null) {
                locCurrent = loc;
            } else {
                return;
            }
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
                //TODO:
                //Testing only
                for (Loc loc : locArrayList) {
                    AccuWeatherUtils.createLocationCodeUrl(loc.getLatLng());
                }
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
                //TODO:
                HelperFunctions.showToast(this, "Settings");
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
                HelperFunctions.showToast(this, "About");
                return true;
            case R.id.nav_privacy_policy:
                try {
                    //TODO: url
                    Uri addressUri = Uri.parse("");
                    Intent privacyIntent = new Intent(Intent.ACTION_VIEW, addressUri);
                    startActivity(privacyIntent);
                } catch (Exception e) {
                    Log.e(TAG, "Error opening Privacy Policy page", e);

                }
                return true;
            default:
                return false;
        }//switch
    }//onNavigationItemSelected

    @Override
    public void onListItemClick(int clickedItemIndex) {
        //get the corresponding fence object
        Loc loc = locArrayList.get(clickedItemIndex);
//        HelperFunctions.showToast(this, "\"" + loc.getId() + "\"" + " clicked");

        //start compare activity, passing in the loc object
        Intent compareIntent = new Intent(MainActivity.this, CompareActivity.class);
        String id = loc.getId();
        compareIntent.putExtra(COMPARE_EXTRA_LOC_ID, id);
        startActivity(compareIntent);

    }//onListItemClick

    /*------------------------------------------------------------------------------------------
                    ---------------    HELPER METHODS    ---------------
    ------------------------------------------------------------------------------------------*/

    //open add location activity for results
    private void openAddLocation() {
        Intent addLocationIntent = new Intent(MainActivity.this, AddLocationActivity.class);
        startActivityForResult(addLocationIntent, ADD_LOCATION_REQUEST_CODE);
    }//openAddLocation

    //for testing
    private void logList(ArrayList<Loc> locArrayList) {
        if (locArrayList == null || locArrayList.size() < 1) {
            return;
        }
        int counter = 1;
        for (Loc loc : locArrayList) {
            Log.v(TAG, counter + ": " + "\n" + "Name: " + loc.getId() + " ----- " + "LatLng: " + loc.getLatLng().toString());
        }
    }//logList

    //Helper method for sorting list based on their name (ascending)
    private void sortNameAscending() {
        Collections.sort(locArrayList, new Comparator<Loc>() {
            @Override
            public int compare(Loc o1, Loc o2) {
                return o1.getId().compareTo(o2.getId());
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
                return o2.getId().compareTo(o1.getId());
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

}//MainActivity

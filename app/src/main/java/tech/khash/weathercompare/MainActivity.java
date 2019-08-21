package tech.khash.weathercompare;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

import tech.khash.weathercompare.model.Constant;
import tech.khash.weathercompare.model.Loc;
import tech.khash.weathercompare.utilities.SaveLoadList;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    //for sending and receiving location from add location activity
    private static final int ADD_LOCATION_REQUEST_CODE = 1;
    public final static String FENCE_EDIT_EXTRA_INTENT_LOC_NAME = "fence-edit-extra-intent-loc_name";

    //drawer layout used for navigation drawer
    private DrawerLayout mDrawerLayout;

    //for holding the current location
    private Loc locCurrent;

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
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }
    }//onCreate

    @Override
    protected void onResume() {
        Log.v(TAG, "onResume Called");
        super.onResume();
    }//onResume

    @Override
    protected void onStart() {
        Log.v(TAG, "onStart Called");
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
        if (mDrawerLayout != null) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
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
     *          We start AddLocation activity for results to get the location data. This gets
     *          called when we return from that.
     * @param requestCode : request code we sent with the original intent
     * @param resultCode : result code that the activity has set
     * @param data : the data being sent back
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //check to make sure it is the right one
        if (requestCode == ADD_LOCATION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //get the name of the loc
            String nameLoc = data.getStringExtra(FENCE_EDIT_EXTRA_INTENT_LOC_NAME);
            if (nameLoc ==null || nameLoc.isEmpty()) {
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
        if (mDrawerLayout != null) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
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
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_find_me:
                //TODO:
                showToast(this, "Find Me");
                return true;
            case R.id.action_search:
                //TODO:
                showToast(this, "Search");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }//switch
    }//onOptionsItemSelected

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_add_location:
                Intent addLocationIntent = new Intent(MainActivity.this, AddLocationActivity.class);
                startActivityForResult(addLocationIntent, ADD_LOCATION_REQUEST_CODE);
                return true;
            case R.id.nav_settings:
                //TODO:
                showToast(this, "Settings");
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
                showToast(this, "About");
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




    /*------------------------------------------------------------------------------------------
                    ---------------    HELPER METHODS    ---------------
    ------------------------------------------------------------------------------------------*/

    //Display Toast
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }//showToast

    //checks location permission
    public static boolean checkLocationPermission(Context context) {
        //check for location permission and ask for it
        return ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }//checkLocationPermission

    /**
     * Helper method for showing a message to the user informing them about the benefits of turning on their
     * location. and also can direct them to the location settings of their phone
     */
    public static void askLocationPermission(final Context context, final Activity activity) {
        //Create a dialog to inform the user about this feature's permission
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //Chain together various setter methods to set the dialogConfirmation characteristics
        builder.setMessage(R.string.permission_required_text_dialog).setTitle(R.string.permission_required_title_dialog);
        // Add the buttons. We can call helper methods from inside the onClick if we need to
        builder.setPositiveButton(R.string.permission_required_yes_dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                //first check to see if the user has denied permission before
                if (ContextCompat.checkSelfPermission(context,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    //here we check to see if they have selected "never ask again". If that is the case, then
                    // shouldShowRequestPermissionRationale will return false. If that is false, and
                    //the build version is higher than 23 (that feature is only available to >= 23
                    //then send them to the
                    //TODO: this is still weird with the second condition. I removed ! but still needs work
                    if (Build.VERSION.SDK_INT >= 23 && !(activity.shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION))) {
                        //This is the case when the user checked the box, so we send them to the settings
                        openPermissionSettings(activity);
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
                }

            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //build and show dialog
        builder.create().show();
    }//askLocationPermission

    /**
     * Helper method for directing the user to the app's setting in their phone to turn on the permission
     */
    private static void openPermissionSettings(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }//openPermissionSettings

}//MainActivity

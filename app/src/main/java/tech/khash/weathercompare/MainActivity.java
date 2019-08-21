package tech.khash.weathercompare;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    private static final int ADD_LOCATION_REQUEST_CODE = 1;

    //drawer layout used for navigation drawer
    private DrawerLayout mDrawerLayout;

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
}//MainActivity
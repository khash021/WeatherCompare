package tech.khash.weathercompare.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import tech.khash.weathercompare.R;
import tech.khash.weathercompare.model.Constant;

/**
 * Created by Khashayar "Khash" Mortazavi
 * <p>
 * This is a class hosts our helper functions (such as permissions and dialogs, etc)
 * to keep the main classes clean.
 *
 * All of the functions are static.
 */

public class HelperFunctions {

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
                    }//if-else build version
                }//if-else first time

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

    //checks location permission
    public static boolean checkLocationPermission(Context context) {
        //check for location permission and ask for it
        return ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }//checkLocationPermission

    //Display Toast
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }//showToast

    public static int getIconInteger(String icon) {
        switch (icon) {
            case "01d":
                return R.drawable.i01d;
            case "01n":
                return R.drawable.i01n;
            case "02d":
                return R.drawable.i02d;
            case "02n":
                return R.drawable.i02n;
            case "03d":
                return R.drawable.i03d;
            case "03n":
                return R.drawable.i03n;
            case "04d":
                return R.drawable.i04d;
            case "04n":
                return R.drawable.i04n;
            case "05d":
                return R.drawable.i05d;
            case "05n":
                return R.drawable.i05n;
            case "06d":
                return R.drawable.i06d;
            case "06n":
                return R.drawable.i06n;
            case "07d":
                return R.drawable.i07d;
            case "07n":
                return R.drawable.i07n;
            case "08d":
                return R.drawable.i08d;
            case "08n":
                return R.drawable.i08n;
            case "09d":
                return R.drawable.i09d;
            case "09n":
                return R.drawable.i09n;
            default:
                return -1;
        }
    }//getIconInteger

}//HelperFunctions

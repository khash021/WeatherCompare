package tech.khash.weathercompare.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import tech.khash.weathercompare.R;

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
    public static void openPermissionSettings(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }//openPermissionSettings

    //Checks for network connection
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }//isNetworkAvailable


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

package tech.khash.weathercompare.model;

/**
 * Created by Khashayar "Khash" Mortazavi
 *
 * This final class holds constants used throughout the app such as preference keys
 *
 */

public final class Constant {

    //Preference keys
    public static final String PREF_KEY_ARRAY = "pref_key_array";
    public static final String PREF_KEY_METRIC = "pref_key_metric";


    //location permission
    public final static int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public static final String PREF_KEY_FIRST_TIME_LOCATION = "pref_key_first_time_location";

    //Intents
    public static final String INTENT_EXTRA_LOC_NAME = "intent-extra-loc-name";
    public static final String INTENT_EXTRA_DEVICE_LOCATION = "intent-extra-device-location";
    public static final String INTENT_EXTRA_DEVICE_LOCATION_NAME = "intent-extra-device-location-name";

    //Loc AW code and name
    public static final String AW_KEY = "code";
    public static final String AW_NAME = "name";

    public static final int ALERT_CODE_NO_RESULT = 1;
    public static final int ALERT_CODE_MULTIPLE_RESULTS = 2;
    public static final int ALERT_CODE_NO_GEOCODER = 3;
    public static final int ALERT_CODE_UNABLE_FIND_DEVICE = 4;


}//Constant

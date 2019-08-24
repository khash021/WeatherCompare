package tech.khash.weathercompare.model;

import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Khashayar "Khash" Mortazavi
 *
 * This class represents a location object. It is used for saving and retrieving locations for
 * weather queries
 *
 * We also create query URLs here in this class
 *
 * AccuWeather needs a location keyAW (cannot be done using LatLng), so the AW stands for AccuWeather
 */

//TODO: add methods for creating OW, AW, and DS URLs

public class Loc {

    private final static String TAG = Loc.class.getSimpleName();

    //constants for creating URLS
    //Accu Wethaer
    private static final String ACCU_WEATHER_BASE_URL_CURRENT =
            "http://dataservice.accuweather.com/currentconditions/v1/";
    private static final String ACCU_WEATHER_QUERY = "&q=";
    private static final String ACCU_WEATHER_BASE_URL_LOCATION =
            "http://dataservice.accuweather.com/locations/v1/cities/geoposition/search";
    private static final String ACCU_WEATHER_API_ID = "?apikey=";
    private static final String ACCU_WEATHER_API_KEY = "Lxds8cj5vJGWk7n1XBe8McAhJhyFnCaw";
    private static final String ACCU_WEATHER_DETAILS_TRUE = "&details=true";

    //Open Weather
    //for current
    private static final String OPEN_WEATHER_BASE_URL_CITY_CODE = "https://api.openweathermap.org/data/2.5/weather?id=";
    private static final String OPEN_WEATHER_BASE_URL_LAT_LNG = "https://api.openweathermap.org/data/2.5/weather?";
    //forecast
    private static final String OPEN_WEATHER_FORECAST_BASE_URL = "https://api.openweathermap.org/data/2.5/forecast?id=";
    private static final String OPEN_WEATHER_API_ID = "&appid=";
    private static final String OPEN_WEATHER_API_KEY = "470cd029b949095fcc602ed656262f8b";
    private static final String OPEN_WEATHER_LAT_LNG = "lat=%s&lon=%s";
    private static final String OPEN_WEATHER_UNIT = "&units=";
    private static final String OPEN_WEATHER_METRIC = "metric";

    //Dark Sky
    private static final String DARK_SKY_BASE_URL = "https://api.darksky.net/forecast/";
    private static final String DARK_SKY_API_KEY = "f5e4285ed1e89d653fd1d99b04e375b7";
    private static final String DARK_SKY_LOCATION = "%s,%s";
    private static final String DARK_SKY_UNITS = "units=";
    private static final String DARK_SKY_UNITS_METRIC = "ca"; //same as si except speed is km/h
    private static final String DARK_SKY_EXCLUDE = "exclude=";
    private static final String DARK_SKY_EXCLUDE_BLOCK_CURRENT = "minutely,hourly,daily,alerts,flags";

    //Variable
    private LatLng latLng;
    private String id;
    private String keyAW; //used only for AccuWeather
    private URL locationCodeUrlAW;
    private URL currentUrlAW;
    private URL currentUrlOW;
    private URL currentUrlDS;


    //default constructor
    public Loc(){}

    public Loc(String id, LatLng latLng) {
        this.latLng = latLng;
        this.id = id;
    }


    /*
        ------------------------ GETTER METHODS -----------------------------------------
     */

    //NOTE: These getter methods could return null, check when implementing

    public String getId() {
        return id;
    }//getId

    public LatLng getLatLng() {
        return latLng;
    }//getLatLng

    public String getKeyAW() {
        if (hasKeyAW()) {
            return keyAW;
        } else {
            return "";
        }
    }//getKeyAW

    public URL getLocationCodeUrlAW () {
        if (locationCodeUrlAW != null) {
            return locationCodeUrlAW;
        }

        URL url = createLocationCodeUrlAW(latLng);
        if (url == null) {
            Log.d(TAG, "getLocationCodeUrlAW - URL null......Name: " + id);
            return null;
        } else {
            locationCodeUrlAW = url;
            return locationCodeUrlAW;
        }//if-else
    }//getLocationCodeUrlAW

    public URL getCurrentUrlAW() {
        if (currentUrlAW != null) {
            return currentUrlAW;
        }

        if (hasKeyAW()) {
            //it already has a keyAW
            URL url = createCurrentUrlAW(keyAW);
            if (url == null) {
                Log.d(TAG, "getCurrentUrlAW - URL null......Name: " + id);
                return null;
            } else {
                currentUrlAW = url;
                return currentUrlAW;
            }//if-else
        } else {
            //we need keyAW
            //TODO:
            //get keyAW
            Log.d(TAG, "getCurrentUrlAW - no keyAW......Name: " + id);
            return null;
        }//if-else keyAW
    }//getCurrentUrlAW

    public URL getCurrentUrlOW() {
        if (currentUrlOW != null) {
            return currentUrlOW;
        }

        URL url = createCurrentUrlOW(latLng);
        if (url == null) {
            Log.d(TAG, "getCcurrentUrlOW - URL null......Name: " + id);
            return null;
        } else {
            currentUrlOW = url;
            return currentUrlOW;
        }//if-else
    }//currentUrlOW

    public URL getCurrentUrlDS() {
        if (currentUrlDS != null) {
            return currentUrlDS;
        }

        URL url = createCurrentUrlDS(latLng);
        if (url == null) {
            Log.d(TAG, "getCurrentUrlDS - URL null......Name: " + id);
            return null;
        } else {
            currentUrlDS = url;
            return currentUrlDS;
        }//if-else
    }//getCurrentUrlDS


    /*
        ------------------------ SETTER METHODS -----------------------------------------
     */

    public void setKeyAW(String keyAW) {
        this.keyAW = keyAW;
    }

    public void setId(String id) {
        this.id = id;
    }//setId

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }//setLatLng

    public void setAllUrls() {
        if (!hasLocationCodeUrlAW()) {
            getLocationCodeUrlAW();
        }

        if (!hasCurrentUrlAW()) {
            getCurrentUrlAW();
        }

        if (!hasCurrentUrlOW()) {
            getCurrentUrlOW();
        }

        if (!hasCurrentUrlDS()) {
            getCurrentUrlDS();
        }
    }//setAllUrls


    /*
        ------------------------ HELPER METHODS -----------------------------------------
     */

    public boolean hasKeyAW() {
        return !TextUtils.isEmpty(keyAW);
    }//hasKeyAW

    public boolean hasLocationCodeUrlAW() {
        return locationCodeUrlAW != null;
    }//hasLocationCodeUrlAW

    public boolean hasCurrentUrlAW() {
        return currentUrlAW != null;
    }//hasCurrentUrlAW

    public boolean hasCurrentUrlOW() {
        return currentUrlOW != null;
    }//hasCurrentUrlOW

    public boolean hasCurrentUrlDS() {
        return currentUrlDS != null;
    }//hasCurrentUrlDS

    /**
     *  This creates a URL for getting location code from LatLng
     * @param latLng : LatLng of the location
     * @return : URL for getting the location code
     */
    private static URL createLocationCodeUrlAW (LatLng latLng) {
        String latLngString = latLng.latitude + "," + latLng.longitude;
        String urlString = ACCU_WEATHER_BASE_URL_LOCATION + ACCU_WEATHER_API_ID +
                ACCU_WEATHER_API_KEY + ACCU_WEATHER_QUERY + latLngString;
        Log.d(TAG, "Code URL string - AW: " + urlString );

        URL url = null;
        try {
            url = new URL(urlString);
            Log.d(TAG, "Location URL - AW: " + url.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error creating location URL from string", e);
        }
        return url;
    }//createLocationCodeUrl

    /**
     * This creates the URL for current weather using city ID
     * @param awId : city's id
     * @return : URL
     */
    private static URL createCurrentUrlAW (String awId) {
        String urlString = ACCU_WEATHER_BASE_URL_CURRENT + awId + ACCU_WEATHER_API_ID +
                ACCU_WEATHER_API_KEY + ACCU_WEATHER_DETAILS_TRUE;
        Log.d(TAG, "Current URL string - AW: " + urlString );

        URL url = null;
        try {
            url = new URL(urlString);
            Log.d(TAG, "generated current url: " + url.toString());
        } catch (MalformedURLException e) {

            Log.e(TAG, "Error creating current weather URL from string", e);
        }
        return url;
    }//createCurrentWeatherUrlId

    /**
     * This creates the URL for current weather using LatLng
     * @param latLng : latlng
     * @return : URL
     */
    private static URL createCurrentUrlOW(LatLng latLng) {
        String latLngString = String.format(OPEN_WEATHER_LAT_LNG, latLng.latitude, latLng.longitude);

        String urlString = OPEN_WEATHER_BASE_URL_LAT_LNG + latLngString + OPEN_WEATHER_UNIT +
                OPEN_WEATHER_METRIC + OPEN_WEATHER_API_ID + OPEN_WEATHER_API_KEY;
        Log.d(TAG, "Current URL string - OW: " + urlString );

        URL url = null;
        try {
            url = new URL(urlString);
            Log.d(TAG, "generated current url: " + url.toString());
        } catch (MalformedURLException e) {

            Log.e(TAG, "Error creating current weather URL from string", e);
        }
        return url;
    }//createCurrentWeatherUrlId

    /**
     * This creates the URL for current weather (notice exclude parameters) using LatLng
     * @param latLng : LatLng
     * @return : URL
     */
    private static URL createCurrentUrlDS (LatLng latLng) {
        String latLngString = String.format(DARK_SKY_LOCATION, latLng.latitude, latLng.longitude);

        String urlString = DARK_SKY_BASE_URL + DARK_SKY_API_KEY + "/" + latLngString + "?" +
                DARK_SKY_EXCLUDE + DARK_SKY_EXCLUDE_BLOCK_CURRENT + "?" + DARK_SKY_UNITS +
                DARK_SKY_UNITS_METRIC;
        Log.d(TAG, "Current URL string - DS: " + urlString );

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {

            Log.e(TAG, "Error creating URL from string", e);
        }
        return url;
    }//createCurrentWeatherUrlId

}//Loc

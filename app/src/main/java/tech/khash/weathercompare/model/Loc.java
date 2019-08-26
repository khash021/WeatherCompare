package tech.khash.weathercompare.model;

import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Khashayar "Khash" Mortazavi
 * <p>
 * This class represents a location object. It is used for saving and retrieving locations for
 * weather queries
 * <p>
 * We also create query URLs here in this class
 * <p>
 * AccuWeather needs a location keyAW (cannot be done using LatLng).
 * <p>
 * AW = AccuWeather ; DS = DarkSky ; OW = OpenWeather
 */

//TODO: add methods for creating OW, AW, and DS URLs

public class Loc {

    private final static String TAG = Loc.class.getSimpleName();

    //constants for creating URLS
    //---------------------   Accu Wethaer  -----------------------------------
    private static final String BASE_URL_CURRENT_AW =
            "http://dataservice.accuweather.com/currentconditions/v1/";
    private static final String QUERY_AW = "&q=";
    private static final String BASE_URL_LOCATION_AW =
            "http://dataservice.accuweather.com/locations/v1/cities/geoposition/search";
    private static final String API_ID_AW = "?apikey=";
    private static final String API_KEY_AW = "Lxds8cj5vJGWk7n1XBe8McAhJhyFnCaw";
    private static final String DETAILS_TRUE_AW = "&details=true";
    //forecast (5-day)
    private static final String BASE_URL_FORECAST_AW =
            "http://dataservice.accuweather.com/forecasts/v1/daily/5day/";
    private static final String METRIC_TRUE_AW = "&metric=true";


    //---------------------   Dark Sky   ---------------------------------------
    private static final String BASE_URL_DS = "https://api.darksky.net/forecast/";
    private static final String API_KEY_DS = "f5e4285ed1e89d653fd1d99b04e375b7";
    private static final String LOCATION_DS = "%s,%s";
    private static final String UNITS_DS = "units=";
    private static final String UNITS_METRIC_DS = "ca"; //same as si except speed is km/h
    private static final String EXCLUDE_DS = "exclude=";
    private static final String EXCLUDE_BLOCK_CURRENT_DS = "minutely,hourly,daily,alerts,flags";
    private static final String EXCLUDE_BLOCK_FORECAST_DS = "currently,minutely,hourly,alerts,flags";

    //----------------------   Open Weather   -----------------------------------------------
    //current
    private static final String BASE_URL_CITY_CODE_OW = "https://api.openweathermap.org/data/2.5/weather?id=";
    private static final String BASE_URL_LAT_LNG_OW = "https://api.openweathermap.org/data/2.5/weather?";
    //forecast
    private static final String FORECAST_BASE_URL_OW = "https://api.openweathermap.org/data/2.5/forecast?";
    private static final String API_ID_OW = "&appid=";
    private static final String API_KEY_OW = "470cd029b949095fcc602ed656262f8b";
    private static final String LAT_LNG_OW = "lat=%s&lon=%s";
    private static final String UNIT_OW = "&units=";
    private static final String METRIC_OW = "metric";


    //Variable
    private LatLng latLng;
    private String name;

    private String keyAW; //used only for AccuWeather
    private URL locationCodeUrlAW;

    private URL currentUrlAW;
    private URL currentUrlOW;
    private URL currentUrlDS;

    private URL forecastUrlAW;
    private URL forecastUrlOW;
    private URL forecastUrlDS;


    //default constructor
    public Loc() {
    }

    public Loc(String name, LatLng latLng) {
        this.latLng = latLng;
        this.name = name;
    }


    /*
        ------------------------ GETTER METHODS -----------------------------------------
     */

    //NOTE: These getter methods could return null, check when implementing

    public String getName() {
        return name;
    }//getName

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

    public URL getLocationCodeUrlAW() {
        if (locationCodeUrlAW != null) {
            return locationCodeUrlAW;
        }

        URL url = createLocationCodeUrlAW(latLng);
        if (url == null) {
            Log.d(TAG, "getLocationCodeUrlAW - URL null......Name: " + name);
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
                Log.d(TAG, "getCurrentUrlAW - URL null......Name: " + name);
                return null;
            } else {
                currentUrlAW = url;
                return currentUrlAW;
            }//if-else
        } else {
            //we need keyAW
            //TODO:
            //get keyAW
            Log.d(TAG, "getCurrentUrlAW - no keyAW......Name: " + name);
            return null;
        }//if-else keyAW
    }//getCurrentUrlAW

    public URL getForecastUrlAW() {
        if (forecastUrlAW != null) {
            return forecastUrlAW;
        }

        if (hasKeyAW()) {
            URL url = createForecastUrlAW(keyAW);
            if (url == null) {
                Log.d(TAG, "getForecastUrlAW - URL null......Name: " + name);
                return null;
            } else {
                forecastUrlAW = url;
                return forecastUrlAW;
            }//url null
        } else {
            //we need keyAW
            //TODO:
            //get keyAW
            Log.d(TAG, "getForecastUrlAW - no keyAW......Name: " + name);
            return null;
        }//if-else keyAW
    }//getForecastUrlAW

    public URL getCurrentUrlOW() {
        if (currentUrlOW != null) {
            return currentUrlOW;
        }

        URL url = createCurrentUrlOW(latLng);
        if (url == null) {
            Log.d(TAG, "getCcurrentUrlOW - URL null......Name: " + name);
            return null;
        } else {
            currentUrlOW = url;
            return currentUrlOW;
        }//if-else
    }//currentUrlOW

    public URL getForecastUrlOW() {
        if (currentUrlOW != null) {
            return currentUrlOW;
        }

        URL url = createForecastUrlOW(latLng);
        if (url == null) {
            Log.d(TAG, "getForecastUrlOW - URL null......Name: " + name);
            return null;
        } else {
            forecastUrlAW = url;
            return forecastUrlAW;
        }//url null

    }//getForecastUrlOW

    public URL getCurrentUrlDS() {
        if (currentUrlDS != null) {
            return currentUrlDS;
        }

        URL url = createCurrentUrlDS(latLng);
        if (url == null) {
            Log.d(TAG, "getCurrentUrlDS - URL null......Name: " + name);
            return null;
        } else {
            currentUrlDS = url;
            return currentUrlDS;
        }//if-else
    }//getCurrentUrlDS

    public URL getForecastUrlDS() {
        if (forecastUrlDS != null) {
            return forecastUrlDS;
        }

        URL url = createForecastUrlDS(latLng);
        if (url == null) {
            Log.d(TAG, "getForecastUrlDS - URL null......Name: " + name);
            return null;
        } else {
            forecastUrlDS = url;
            return forecastUrlDS;
        }//url null

    }//getForecastUrlOW



    /*
        ------------------------ SETTER METHODS -----------------------------------------
     */

    public void setKeyAW(String keyAW) {
        this.keyAW = keyAW;
    }

    public void setName(String name) {
        this.name = name;
    }//setName

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }//setLatLng

    public void setAllUrls() {
        //code
        if (!hasLocationCodeUrlAW()) {
            getLocationCodeUrlAW();
        }

        //current
        if (!hasCurrentUrlAW()) {
            getCurrentUrlAW();
        }

        if (!hasCurrentUrlOW()) {
            getCurrentUrlOW();
        }

        if (!hasCurrentUrlDS()) {
            getCurrentUrlDS();
        }

        //forecast
        if (!hasForecastUrlAW()) {
            getForecastUrlAW();
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

    public boolean hasForecastUrlAW() {
        return forecastUrlAW != null;
    }//hasForecastUrlAW

    /**
     * This creates a URL for getting location code from LatLng
     *
     * @param latLng : LatLng of the location
     * @return : URL for getting the location code
     */
    private static URL createLocationCodeUrlAW(LatLng latLng) {
        String latLngString = latLng.latitude + "," + latLng.longitude;
        String urlString = BASE_URL_LOCATION_AW + API_ID_AW +
                API_KEY_AW + QUERY_AW + latLngString;
        Log.d(TAG, "Code URL string - AW: " + urlString);

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
     *
     * @param locCode : city's code
     * @return : URL
     */
    private static URL createCurrentUrlAW(String locCode) {
        String urlString = BASE_URL_CURRENT_AW + locCode + API_ID_AW +
                API_KEY_AW + DETAILS_TRUE_AW;
        Log.d(TAG, "Current URL string - AW: " + urlString);

        URL url = null;
        try {
            url = new URL(urlString);
            Log.d(TAG, "generated current url: " + url.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error creating current weather URL from string", e);
        }
        return url;
    }//createCurrentWeatherUrlId

    private static URL createForecastUrlAW(String locCode) {
        String urlString = BASE_URL_FORECAST_AW + locCode + API_ID_AW +
                API_KEY_AW + DETAILS_TRUE_AW + METRIC_TRUE_AW;
        Log.d(TAG, "Forecast URL String - AW: " + urlString);

        URL url = null;
        try {
            url = new URL(urlString);
            Log.d(TAG, "generated forecast url: " + url.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error creating forecast weather URL from string", e);
        }
        return url;
    }//createForecastUrlAW

    private static URL createForecastUrlOW(LatLng latLng) {
        String latLngString = String.format(LAT_LNG_OW, latLng.latitude, latLng.longitude);

        String urlString = FORECAST_BASE_URL_OW + latLngString + UNIT_OW +
                METRIC_OW + API_ID_OW + API_KEY_OW;
        Log.d(TAG, "Forecast URL string - OW: " + urlString);

        URL url = null;
        try {
            url = new URL(urlString);
            Log.d(TAG, "generated forecast url - OW: " + url.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error creating current weather URL from string", e);
        }
        return url;
    }//createForecastUrlOW

    /**
     * This creates the URL for current weather using LatLng
     *
     * @param latLng : latlng
     * @return : URL
     */
    private static URL createCurrentUrlOW(LatLng latLng) {
        String latLngString = String.format(LAT_LNG_OW, latLng.latitude, latLng.longitude);

        String urlString = BASE_URL_LAT_LNG_OW + latLngString + UNIT_OW +
                METRIC_OW + API_ID_OW + API_KEY_OW;
        Log.d(TAG, "Current URL string - OW: " + urlString);

        URL url = null;
        try {
            url = new URL(urlString);
            Log.d(TAG, "generated current url - OW: " + url.toString());
        } catch (MalformedURLException e) {

            Log.e(TAG, "Error creating current weather URL from string", e);
        }
        return url;
    }//createCurrentWeatherUrlId

    /**
     * This creates the URL for current weather (notice exclude parameters) using LatLng
     *
     * @param latLng : LatLng
     * @return : URL
     */
    private static URL createCurrentUrlDS(LatLng latLng) {
        String latLngString = String.format(LOCATION_DS, latLng.latitude, latLng.longitude);

        String urlString = BASE_URL_DS + API_KEY_DS + "/" + latLngString + "?" +
                EXCLUDE_DS + EXCLUDE_BLOCK_CURRENT_DS + "?" + UNITS_DS +
                UNITS_METRIC_DS;
        Log.d(TAG, "Current URL string - DS: " + urlString);

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {

            Log.e(TAG, "Error creating URL from string", e);
        }
        return url;
    }//createCurrentWeatherUrlId

    private static URL createForecastUrlDS(LatLng latLng) {
        String latLngString = String.format(LOCATION_DS, latLng.latitude, latLng.longitude);

        String urlString = BASE_URL_DS + API_KEY_DS + "/" + latLngString + "?" +
                EXCLUDE_DS + EXCLUDE_BLOCK_FORECAST_DS + "?" + UNITS_DS +
                UNITS_METRIC_DS;
        Log.d(TAG, "Forecast URL string - DS: " + urlString);

        URL url = null;
        try {
            url = new URL(urlString);
            Log.d(TAG, "generated forecast url - DS: " + url.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error creating current weather URL from string", e);
        }
        return url;
    }//createForecastUrlOW

}//Loc

package app.khash.weathertry.utilities;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is responsible for receiving the JSON and decode it and return the result text to be shown on UI
 */

public class ParseJSON {

    //TODO: add rain, sunrise and sunset, icons
    //TODO: feels like attribute

    private static final String TAG = ParseJSON.class.getSimpleName();

    //constants for OpenWeather json
    private static final String JSON_MAIN = "main";
    private static final String JSON_TEMP = "temp";
    private static final String JSON_PRESSURE = "pressure";
    private static final String JSON_HUMIDITY = "humidity";
    private static final String JSON_WIND = "wind";
    private static final String JSON_WIND_SPEED = "speed";
    private static final String JSON_WIND_DIRECTION = "deg";
    private static final String JSON_WEATHER = "weather";
    private static final String JSON_WEATHER_MAIN = "main";
    private static final String JSON_CLOUD = "clouds";
    private static final String JSON_CLOUDS_ALL = "all";
    private static final String JSON_ICON = "icon";

    //constants for AccuWeather json
    private static final String JSON_AW_DESCRIPTION = "WeatherText";
    private static final String JSON_AW_TEMPERATURE = "Temperature";
    private static final String JSON_AW_METRIC = "Metric";
    private static final String JSON_AW_METRIC_VALUE = "Value";
    private static final String JSON_AW_HUMIDITY = "RelativeHumidity";
    private static final String JSON_AW_DEW_POINT = "DewPoint";
    private static final String JSON_AW_WIND = "Wind";
    private static final String JSON_AW_DIRECTION = "Direction";
    private static final String JSON_AW_WIND_DIRECTION_ENGLISH = "English";
    private static final String JSON_AW_SPEED = "Speed";
    private static final String JSON_AW_WIND_GUST = "WindGust";
    private static final String JSON_AW_VISIBILITY = "Visibility";
    private static final String JSON_AW_CLOUD_COVER = "CloudCover";
    private static final String JSON_AW_PRESSURE = "Pressure";

    private static final String JSON_AW_ICON = "WeatherIcon";

    //constants for DarkSky
    private static final String JSON_DS_CURRENTLY = "currently";
    private static final String JSON_DS_SUMMARY = "summary";
    private static final String JSON_DS_ICON = "icon";
    private static final String JSON_DS_TEMPERATURE = "temperature";
    private static final String JSON_DS_DEW_POINT = "dewPoint";
    private static final String JSON_DS_HUMIDITY = "humidity";
    private static final String JSON_DS_PRESSURE = "pressure";
    private static final String JSON_DS_WIND_SPEED = "windSpeed";
    private static final String JSON_DS_WIND_GUST = "windGust";
    private static final String JSON_DS_WIND_DIRECTION = "windBearing";
    private static final String JSON_DS_CLOUD_COVER = "cloudCover";
    private static final String JSON_DS_VISIBILITY = "visibility";
    private static final String JSON_DS_POP = "precipProbability";
    private static final String JSON_DS_PRECIP_TYPE = "precipType";

    //pictures
    private static final String OPENWEATHER_ICON_BASE_URL = "http://openweathermap.org/img/w/";
    private static final String OPENWEATHER_ICON_EXTENSION = ".png";
    private static final String ACCUWEATHER_ICON_BASE_URL = "https://developer.accuweather.com/sites/default/files/";
    private static final String ACCUWEATHER_ICON_EXTENSION = "-s.png";

    //some constants for results
    private static final String TEMPERATURE = "Temperature: ";
    private static final String TEMPERATURE_UNIT_METRIC = " °C";

    private static final String PRESSURE = "Pressure: ";
    private static final String PRESSURE_UNIT = " hPa";

    private static final String HUMIDITY = "Humidity: ";
    private static final String UNIT_PERCENTAGE = " %";

    private static final String WIND = "Wind: ";
    private static final String WIND_GUST = "Gusting  ";
    private static final String WIND_SPEED_UNIT = " km/h";
    private static final String WIND_DIRECTION = " from: ";

    private static final String DEW_POINT = "Dew point: ";
    private static final String POP = "POP: ";
    private static final String VISIBILITY = "Visibility: ";
    private static final String UNIT_KM = " km";

    private static final String CLOUD_COVERAGE = "Cloud coverage:  ";
    private static final String CHANCE_OF = " chance of ";

    private final static String LINE_BREAK = "\n";


    /**
     * This method is for parsing the current weather from Open Weather
     * @param jsonString : raw JSON response from the server
     * @return : formatted current weather string to be represented in the UI
     * @throws JSONException
     */
    public static String parseOpenWeatherCurrent(String jsonString) throws JSONException {

        //dummy check for empty or null input
        if (jsonString == null || TextUtils.isEmpty(jsonString)) {
            return null;
        }//if

        String description, temp, pressure, humidity, windSpeed, windDirection, cloudCoverage, icon;

        //create an object from the string
        JSONObject forecastJson = new JSONObject(jsonString);

        //weather description
        JSONArray weatherArray = forecastJson.getJSONArray(JSON_WEATHER);
        JSONObject weatherObject = weatherArray.getJSONObject(0);
        description = weatherObject.optString(JSON_WEATHER_MAIN);
        icon = weatherObject.optString(JSON_ICON);
        icon = OPENWEATHER_ICON_BASE_URL + icon + OPENWEATHER_ICON_EXTENSION;

        //get a reference to the main
        JSONObject mainObject = forecastJson.getJSONObject(JSON_MAIN);

        //this return the temp value, if no such thing exists, it returns an empty string
        temp = mainObject.optString(JSON_TEMP);
        pressure = mainObject.optString(JSON_PRESSURE);
        humidity = mainObject.getString(JSON_HUMIDITY);

        //get a reference to the wind
        JSONObject windObject = forecastJson.getJSONObject(JSON_WIND);

        //get the values
        double windSpeedDouble = windObject.optDouble(JSON_WIND_SPEED);
        double windDirectionDouble = windObject.optDouble(JSON_WIND_DIRECTION);

        //convert the values
        windDirection = Conversions.degreeToDirection(windDirectionDouble);
        windSpeed = Conversions.meterToKmh(windSpeedDouble);

//        windSpeed = windObject.optString(JSON_WIND_SPEED);
//        windDirection = windObject.optString(JSON_WIND_DIRECTION);

        //cloud cover
        JSONObject cloudObject = forecastJson.getJSONObject(JSON_CLOUD);
        cloudCoverage = cloudObject.optString(JSON_CLOUDS_ALL);

        //create a response string and return that
        String results = description + LINE_BREAK +
                TEMPERATURE + temp + TEMPERATURE_UNIT_METRIC + LINE_BREAK +
                PRESSURE + pressure + PRESSURE_UNIT + LINE_BREAK +
                HUMIDITY + humidity + UNIT_PERCENTAGE + LINE_BREAK +
                WIND + windSpeed + WIND_SPEED_UNIT + WIND_DIRECTION + windDirection + LINE_BREAK +
                CLOUD_COVERAGE + cloudCoverage + UNIT_PERCENTAGE + ";" + icon;

        return results;
    }//parseOpenWeatherCurrent

    /**
     * This method is for parsing the current weather from AccuWeather
     * @param jsonString : raw JSON response from the server
     * @return : formatted current weather string to be represented in the UI
     * @throws JSONException
     */
    public static String parseAccuWeatherCurrent(String jsonString) throws JSONException {

        //dummy check for empty or null input
        if (jsonString == null || TextUtils.isEmpty(jsonString)) {
            return null;
        }//if

        String description, temp, dewPoint, pressure, humidity, windSpeed, windDirection, windGust,
                visibility, cloudCoverage, icon;

        //create the main array
        JSONArray forecastArray = new JSONArray(jsonString);

        //get the first element of the array containing all the current weather data
        JSONObject forecastObject = forecastArray.getJSONObject(0);

        //summary
        description = forecastObject.optString(JSON_AW_DESCRIPTION);

        //temp
        JSONObject tempObject  = forecastObject.getJSONObject(JSON_AW_TEMPERATURE);
        JSONObject tempMetricObject = tempObject.getJSONObject(JSON_AW_METRIC);
        double tempDouble = tempMetricObject.optDouble(JSON_AW_METRIC_VALUE);
        temp = Conversions.removeDecimal(tempDouble);

        //dew point
        JSONObject dewObject  = forecastObject.getJSONObject(JSON_AW_DEW_POINT);
        JSONObject metricDewObject = dewObject.getJSONObject(JSON_AW_METRIC);
        double dewDouble = metricDewObject.optDouble(JSON_AW_METRIC_VALUE);
        dewPoint = Conversions.removeDecimal(dewDouble);

        //pressure
        JSONObject pressObject  = forecastObject.getJSONObject(JSON_AW_PRESSURE);
        JSONObject pressMetricObject = pressObject.getJSONObject(JSON_AW_METRIC);
        double pressDouble = pressMetricObject.optDouble(JSON_AW_METRIC_VALUE);
        pressure = Conversions.removeDecimal(pressDouble);

        //humidity
        humidity = forecastObject.optString(JSON_AW_HUMIDITY);

        //wind
        JSONObject windObject = forecastObject.getJSONObject(JSON_AW_WIND);
        JSONObject windSpeedObject = windObject.getJSONObject(JSON_AW_SPEED);
        JSONObject windSpeedMetricObject = windSpeedObject.getJSONObject(JSON_AW_METRIC);
        double windDouble = windSpeedMetricObject.optDouble(JSON_AW_METRIC_VALUE);
        windSpeed = Conversions.removeDecimal(windDouble);

        JSONObject windDirectionObject = windObject.getJSONObject(JSON_AW_DIRECTION);
        windDirection = windDirectionObject.optString(JSON_AW_WIND_DIRECTION_ENGLISH);

        //windGust
        JSONObject gustObject = forecastObject.getJSONObject(JSON_AW_WIND_GUST);
        JSONObject gustSpeedObject = gustObject.getJSONObject(JSON_AW_SPEED);
        JSONObject gustSpeedMetricObject = gustSpeedObject.getJSONObject(JSON_AW_METRIC);
        double gustDouble = gustSpeedMetricObject.optDouble(JSON_AW_METRIC_VALUE);
        windGust = Conversions.removeDecimal(gustDouble);

        //visibility
        JSONObject visibilityObject = forecastObject.getJSONObject(JSON_AW_VISIBILITY);
        JSONObject visibilityMetricObject = visibilityObject.getJSONObject(JSON_AW_METRIC);
        double visibilityDouble = visibilityMetricObject.optDouble(JSON_AW_METRIC_VALUE);
        visibility = Conversions.removeDecimal(visibilityDouble);

        cloudCoverage = forecastObject.optString(JSON_AW_CLOUD_COVER);


        //icon
        int iconId = forecastObject.optInt(JSON_AW_ICON);
        if (iconId < 10) {
            icon = "0" + iconId;
        } else {
            icon = String.valueOf(iconId);
        }
        icon = ACCUWEATHER_ICON_BASE_URL + icon + ACCUWEATHER_ICON_EXTENSION;

        //final results

        String results = description + LINE_BREAK +
                TEMPERATURE + temp + TEMPERATURE_UNIT_METRIC + LINE_BREAK +
                DEW_POINT + dewPoint + TEMPERATURE_UNIT_METRIC + LINE_BREAK +
                PRESSURE + pressure + PRESSURE_UNIT + LINE_BREAK +
                HUMIDITY + humidity + UNIT_PERCENTAGE + LINE_BREAK +
                WIND + windSpeed + WIND_SPEED_UNIT + " " + windDirection + " " + WIND_GUST +
                windGust +  LINE_BREAK +
                CLOUD_COVERAGE + cloudCoverage + UNIT_PERCENTAGE + LINE_BREAK +
                VISIBILITY + visibility + UNIT_KM + ";" + icon;

        return results;
    }//parseAccuWeatherCurrent

    /**
     * This method is for parsing the current weather from DarkSky
     * @param jsonString : raw JSON response from the server
     * @return : formatted current weather string to be represented in the UI
     * @throws JSONException
     */
    public static String parseDarkSkyCurrent(String jsonString) throws JSONException {

        //dummy check for empty or null input
        if (jsonString == null || TextUtils.isEmpty(jsonString)) {
            return null;
        }//if

        String description, temp, pressure, dewPoint, humidity, windSpeed, windGust, windDirection,
                cloudCoverage, icon, pop, precipType, visibility;

        //create the main object
        JSONObject forecastObject = new JSONObject(jsonString);

        JSONObject currentObject = forecastObject.getJSONObject(JSON_DS_CURRENTLY);

        description = currentObject.optString(JSON_DS_SUMMARY);

        double popDouble = currentObject.optDouble(JSON_DS_POP);
        pop = Conversions.decimalToPercentage(popDouble);

        precipType = currentObject.optString(JSON_DS_PRECIP_TYPE);
        if (!(TextUtils.isEmpty(precipType) || precipType.length() < 2)) {
            precipType = Conversions.capitalizeFirst(precipType);
            precipType = CHANCE_OF + precipType;
        }


        double tempDouble = currentObject.optDouble(JSON_DS_TEMPERATURE);
        temp = Conversions.farToCel(tempDouble);

        double dewDouble = currentObject.optDouble(JSON_DS_DEW_POINT);
        dewPoint = Conversions.farToCel(dewDouble);

        double humidDouble = currentObject.optDouble(JSON_DS_HUMIDITY);
        humidity = Conversions.decimalToPercentage(humidDouble);

        double pressDouble = currentObject.optDouble(JSON_DS_PRESSURE);
        pressure = Conversions.removeDecimal(pressDouble);

        double windSpeedDouble = currentObject.optDouble(JSON_DS_WIND_SPEED);
        windSpeed = Conversions.mileToKm(windSpeedDouble);

        double windGustDouble = currentObject.optDouble(JSON_DS_WIND_GUST);
        windGust = Conversions.mileToKm(windGustDouble);

        double windDirDouble = currentObject.optDouble(JSON_DS_WIND_DIRECTION);
        windDirection = Conversions.degreeToDirection(windDirDouble);

        double cloudCoverDouble = currentObject.optDouble(JSON_DS_CLOUD_COVER);
        cloudCoverage = Conversions.decimalToPercentage(cloudCoverDouble);

        double visDouble = currentObject.optDouble(JSON_DS_VISIBILITY);
        visibility = Conversions.mileToKm(visDouble);

        icon = currentObject.optString(JSON_DS_ICON);

        String results = description + LINE_BREAK +
                TEMPERATURE + temp + TEMPERATURE_UNIT_METRIC + LINE_BREAK +
                DEW_POINT + dewPoint + TEMPERATURE_UNIT_METRIC + LINE_BREAK +
                PRESSURE + pressure + PRESSURE_UNIT + LINE_BREAK +
                HUMIDITY + humidity + UNIT_PERCENTAGE + LINE_BREAK +
                WIND + windSpeed + WIND_SPEED_UNIT + " " + windDirection + " " + WIND_GUST +
                windGust +  LINE_BREAK +
                CLOUD_COVERAGE + cloudCoverage + UNIT_PERCENTAGE + LINE_BREAK +
                POP + pop +UNIT_PERCENTAGE + precipType + LINE_BREAK +
                VISIBILITY + visibility + UNIT_KM;

        return results;
    }//parseDarkSkyCurrent


}//class

package tech.khash.weathercompare.utilities;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import tech.khash.weathercompare.model.Weather;

/**
 * This is responsible for receiving the JSON and decode it and return the result text to be shown on UI
 */

public class ParseJSON {

    //TODO: add rain, sunrise and sunset, icons
    //TODO: feels like attribute

    private static final String TAG = ParseJSON.class.getSimpleName();

    /*
        --------------------------- Open Weather -----------------------------------
     */
    private static final String MAIN_OW = "main";
    private static final String TEMP_OW = "temp";
    private static final String PRESSURE_OW = "pressure";
    private static final String HUMIDITY_OW = "humidity";
    private static final String WIND_OW = "wind";
    private static final String WIND_SPEED_OW = "speed";
    private static final String WIND_DIRECTION_OW = "deg";
    private static final String WEATHER_OW = "weather";
    private static final String WEATHER_MAIN_OW = "main";
    private static final String CLOUD_OW = "clouds";
    private static final String CLOUDS_ALL_OW = "all";
    private static final String ICON_OW = "icon";

    //forecast
    private static final String LIST_OW = "list";
    private static final String DATA_OW = "dt";



    //pictures
    private static final String OPENWEATHER_ICON_BASE_URL = "http://openweathermap.org/img/w/";
    private static final String OPENWEATHER_ICON_EXTENSION = ".png";

    /*
        --------------------------- Accu Weather -----------------------------------
     */
    private static final String DESCRIPTION_AW = "WeatherText";
    private static final String TEMPERATURE_AW = "Temperature";
    private static final String METRIC_AW = "Metric";
    private static final String METRIC_VALUE_AW = "Value";
    private static final String HUMIDITY_AW = "RelativeHumidity";
    private static final String DEW_POINT_AW = "DewPoint";
    private static final String WIND_AW = "Wind";
    private static final String DIRECTION_AW = "Direction";
    private static final String WIND_DIRECTION_ENGLISH_AW = "English";
    private static final String SPEED_AW = "Speed";
    private static final String WIND_GUST_AW = "WindGust";
    private static final String VISIBILITY_AW = "Visibility";
    private static final String CLOUD_COVER_AW = "CloudCover";
    private static final String PRESSURE_AW = "Pressure";
    private static final String LOCATION_KEY_AW = "Key";
    private static final String ICON_AW = "WeatherIcon";

    //forecast AW
    private static final String DAILY_FORECASTS_AW = "DailyForecasts";
    private static final String FORECAST_EPOCH_AW = "EpochDate";
    private static final String FORECAST_MIN_AW = "Minimum";
    private static final String FORECAST_MAX_AW = "Maximum";
    private static final String FORECAST_DAY_AW = "Day";
    private static final String FORECAST_NIGHT_AW = "Night";
    private static final String FORECAST_SUMMARY_AW = "ShortPhrase";
    private static final String FORECAST_POP_AW = "PrecipitationProbability";

    //pictures
    private static final String ICON_BASE_URL_AW = "https://developer.accuweather.com/sites/default/files/";
    private static final String ICON_EXTENSION_AW = "-s.png";


    /*
        --------------------------- Dark Sky -----------------------------------
     */
    private static final String CURRENTLY_DS = "currently";
    private static final String SUMMARY_DS = "summary";
    private static final String ICON_DS = "icon";
    private static final String TEMPERATURE_DS = "temperature";
    private static final String DEW_POINT_DS = "dewPoint";
    private static final String HUMIDITY_DS = "humidity";
    private static final String PRESSURE_DS = "pressure";
    private static final String WIND_SPEED_DS = "windSpeed";
    private static final String WIND_GUST_DS = "windGust";
    private static final String WIND_DIRECTION_DS = "windBearing";
    private static final String CLOUD_COVER_DS = "cloudCover";
    private static final String VISIBILITY_DS = "visibility";
    private static final String POP_DS = "precipProbability";
    private static final String PRECIP_TYPE_DS = "precipType";

    //forecast
    private static final String DAILY_DS = "daily";
    private static final String DATA_DS = "data";
    private static final String TIME_DS = "time";
    private static final String TEMP_MIN_DS = "temperatureMin";
    private static final String TEMP_MAX_DS = "temperatureMax";





    /*
        --------------------------- Some constants for results ---------------------------------
     */
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

        /*
    -------------------------------- Open Weather ----------------------------------------
     */


    /**
     * This method is for parsing the current weather from Open Weather
     * @param jsonString : raw JSON response from the server
     * @return : Weather object
     * @throws JSONException
     */
    public static Weather parseOpenWeatherCurrent(String jsonString) throws JSONException {

        //dummy check for empty or null input
        if (jsonString == null || TextUtils.isEmpty(jsonString)) {
            return null;
        }//if

        String summary, temp, pressure, humidity, windSpeed, windDirection, cloudCoverage, icon;

        //create a Weather object
        Weather weather = new Weather();

        //create an object from the string
        JSONObject forecastJson = new JSONObject(jsonString);
        //get a reference to the main
        JSONObject mainObject = forecastJson.getJSONObject(MAIN_OW);

        //weather description
        JSONArray weatherArray = forecastJson.getJSONArray(WEATHER_OW);
        JSONObject weatherObject = weatherArray.getJSONObject(0);
        summary = weatherObject.optString(WEATHER_MAIN_OW);
        weather.setSummary(summary);

        //this return the temp value, if no such thing exists, it returns an empty string
        temp = mainObject.optString(TEMP_OW);
        weather.setTemperature(temp);
        pressure = mainObject.optString(PRESSURE_OW);
        weather.setPressure(pressure);
        humidity = mainObject.getString(HUMIDITY_OW);
        weather.setHumidity(humidity);

        //get a reference to the wind
        JSONObject windObject = forecastJson.getJSONObject(WIND_OW);

        //get the values
        double windSpeedDouble = windObject.optDouble(WIND_SPEED_OW);
        double windDirectionDouble = windObject.optDouble(WIND_DIRECTION_OW);

        //convert the values
        windDirection = Conversions.degreeToDirection(windDirectionDouble);
        weather.setWindDirection(windDirection);
        windSpeed = Conversions.meterToKmh(windSpeedDouble);
        weather.setWindSpeed(windSpeed);

        //cloud cover
        JSONObject cloudObject = forecastJson.getJSONObject(CLOUD_OW);
        cloudCoverage = cloudObject.optString(CLOUDS_ALL_OW);
        weather.setCloudCoverage(cloudCoverage);

        //icon
        icon = weatherObject.optString(ICON_OW);
        icon = OPENWEATHER_ICON_BASE_URL + icon + OPENWEATHER_ICON_EXTENSION;
        weather.setIconUrl(icon);

        return weather;
    }//parseOpenWeatherCurrent

    /**
     *      This methods goes through the forecast data and create 3 Weather objects for next 3
     *      days and return the list
     * @param jsonResponse : response
     * @return : ArrayList<Weather> for the next 3 days
     * @throws JSONException
     */
    public static ArrayList<Weather> parseOpenWeatherForecast (String jsonResponse) throws JSONException {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        final long DAY_MILLI =86400000;
        ArrayList<Weather> weatherArrayList = new ArrayList<>();

        //create calender using default timezone and locale for this moment
        Calendar calendar = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        //next day
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        //start of next 4 days
        long day1StartMilli = calendar.getTimeInMillis();
        long day2StartMilli = day1StartMilli + DAY_MILLI;
        long day3StartMilli = day2StartMilli + DAY_MILLI;
        long day4StartMilli = day3StartMilli + DAY_MILLI;

        try {
            JSONObject rootObject = new JSONObject(jsonResponse);
            JSONArray listArray = rootObject.optJSONArray(LIST_OW);
            ArrayList<Weather> day1Array = getWeatherDayOW(day1StartMilli, day2StartMilli, listArray);
            ArrayList<Weather> day2Array = getWeatherDayOW(day2StartMilli, day3StartMilli, listArray);
            ArrayList<Weather> day3Array = getWeatherDayOW(day3StartMilli, day4StartMilli, listArray);

            //add to list
            if (day1Array == null || day1Array.size() <1) {
                Log.d(TAG, "OW WEATHER LIST = null/empty");
            } else {
                Weather weather1 = calculateMinMax(day1Array);
                weatherArrayList.add(weather1);
            }//if/else null/empty

            if (day2Array == null || day2Array.size() <1) {
                Log.d(TAG, "OW WEATHER LIST = null/empty");
            } else {
                Weather weather2 = calculateMinMax(day2Array);
                weatherArrayList.add(weather2);
            }//if/else null/empty

            if (day3Array == null || day3Array.size() <1) {
                Log.d(TAG, "OW WEATHER LIST = null/empty");
            } else {
                Weather weather3 = calculateMinMax(day3Array);
                weatherArrayList.add(weather3);
            }//if/else null/empty
        } catch (JSONException e) {
            Log.e(TAG, "getForecastOW - error parsing json", e);
        }
        return weatherArrayList;
    }//parseOpenWeatherForecast

    private static ArrayList<Weather> getWeatherDayOW(long start, long end, JSONArray jsonArray) {
        long epoch;
        String temp;
        ArrayList<Weather> outputList = new ArrayList<>();
        Weather weather;
        for (int i = 0; i < jsonArray.length(); i++) {
            weather = new Weather();
            try {
                JSONObject rootObject = (JSONObject) jsonArray.get(i);
                epoch = rootObject.optLong(DATA_OW);
                epoch *= 1000;
                //check for dates
                if (!(epoch > start && epoch < end))
                    continue;
                weather.setEpoch(epoch);

                JSONObject mainObject = rootObject.optJSONObject(MAIN_OW);
                temp = mainObject.optString(TEMP_OW);
                weather.setTemperature(temp);
                outputList.add(weather);
            } catch (JSONException e) {
                Log.e(TAG, "getWeatherDayOW - array error", e);
            }

        }//for
        return outputList;
    }//getWeatherDayOW

    private static Weather calculateMinMax(ArrayList<Weather> weatherArrayList) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM.dd", Locale.getDefault());
        int min, max;
        String date;
        long epoch;
        Weather weather = new Weather();

        //epoch
        epoch = (weatherArrayList.get(0)).getEpoch();
        weather.setEpoch(epoch);

        //date
        Date d = new Date(epoch);
        date = formatter.format(d);
        weather.setDate(date);

        ArrayList<Integer> tempArrayList = new ArrayList<>();
        for (Weather w : weatherArrayList) {
            int temp = Integer.valueOf(w.getTemperature());
            tempArrayList.add(temp);
        }

        min = Collections.min(tempArrayList);
        max = Collections.max(tempArrayList);

        weather.setTempMin(String.valueOf(min));
        weather.setTempMax(String.valueOf(max));

        return weather;

    }//calculateMinMax

    /*
    -------------------------------- Acce Weather ----------------------------------------
     */

    /**
     *  It extracts AccuWeather's location code from the JSON response
     * @param jsonString : JSON response from AW API
     * @return : location code
     * @throws JSONException
     */
    public static String parseAccuLocationCode (String jsonString) throws JSONException {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        String locationCode;
        JSONObject responseObject = new JSONObject(jsonString);

        //by using optString, if such string does not exists, it returns an empty string.
        locationCode = responseObject.optString(LOCATION_KEY_AW);

        Log.d(TAG, "location code: " + locationCode);

        return locationCode;
    }//parseAccuLocationCode

    /**
     * This method is for parsing the current weather from AccuWeather
     * @param jsonString : raw JSON response from the server
     * @return : Weather object
     * @throws JSONException
     */
    public static Weather parseAccuWeatherCurrent(String jsonString) throws JSONException {

        //dummy check for empty or null input
        if (jsonString == null || TextUtils.isEmpty(jsonString)) {
            return null;
        }//if

        String summary, temp, dewPoint, pressure, humidity, windSpeed, windDirection, windGust,
                visibility, cloudCoverage, icon;

        //create a Weather object
        Weather weather = new Weather();

        //create the main array
        JSONArray forecastArray = new JSONArray(jsonString);

        //get the first element of the array containing all the current weather data
        JSONObject forecastObject = forecastArray.getJSONObject(0);

        //summary
        summary = forecastObject.optString(DESCRIPTION_AW);
        weather.setSummary(summary);

        //temp
        JSONObject tempObject  = forecastObject.getJSONObject(TEMPERATURE_AW);
        JSONObject tempMetricObject = tempObject.getJSONObject(METRIC_AW);
        double tempDouble = tempMetricObject.optDouble(METRIC_VALUE_AW);
        temp = Conversions.removeDecimal(tempDouble);
        weather.setTemperature(temp);

        //dew point
        JSONObject dewObject  = forecastObject.getJSONObject(DEW_POINT_AW);
        JSONObject metricDewObject = dewObject.getJSONObject(METRIC_AW);
        double dewDouble = metricDewObject.optDouble(METRIC_VALUE_AW);
        dewPoint = Conversions.removeDecimal(dewDouble);
        weather.setDewPoint(dewPoint);

        //pressure
        JSONObject pressObject  = forecastObject.getJSONObject(PRESSURE_AW);
        JSONObject pressMetricObject = pressObject.getJSONObject(METRIC_AW);
        double pressDouble = pressMetricObject.optDouble(METRIC_VALUE_AW);
        pressure = Conversions.removeDecimal(pressDouble);
        weather.setPressure(pressure);

        //humidity
        humidity = forecastObject.optString(HUMIDITY_AW);
        weather.setHumidity(humidity);

        //wind
        JSONObject windObject = forecastObject.getJSONObject(WIND_AW);
        JSONObject windSpeedObject = windObject.getJSONObject(SPEED_AW);
        JSONObject windSpeedMetricObject = windSpeedObject.getJSONObject(METRIC_AW);
        double windDouble = windSpeedMetricObject.optDouble(METRIC_VALUE_AW);
        windSpeed = Conversions.removeDecimal(windDouble);
        weather.setWindSpeed(windSpeed);

        JSONObject windDirectionObject = windObject.getJSONObject(DIRECTION_AW);
        windDirection = windDirectionObject.optString(WIND_DIRECTION_ENGLISH_AW);
        weather.setWindDirection(windDirection);

        //windGust
        JSONObject gustObject = forecastObject.getJSONObject(WIND_GUST_AW);
        JSONObject gustSpeedObject = gustObject.getJSONObject(SPEED_AW);
        JSONObject gustSpeedMetricObject = gustSpeedObject.getJSONObject(METRIC_AW);
        double gustDouble = gustSpeedMetricObject.optDouble(METRIC_VALUE_AW);
        windGust = Conversions.removeDecimal(gustDouble);
        weather.setWindGust(windGust);

        //visibility
        JSONObject visibilityObject = forecastObject.getJSONObject(VISIBILITY_AW);
        JSONObject visibilityMetricObject = visibilityObject.getJSONObject(METRIC_AW);
        double visibilityDouble = visibilityMetricObject.optDouble(METRIC_VALUE_AW);
        visibility = Conversions.removeDecimal(visibilityDouble);
        weather.setVisibility(visibility);

        cloudCoverage = forecastObject.optString(CLOUD_COVER_AW);
        weather.setCloudCoverage(cloudCoverage);


        //icon
        int iconId = forecastObject.optInt(ICON_AW);
        if (iconId < 10) {
            icon = "0" + iconId;
        } else {
            icon = String.valueOf(iconId);
        }
        icon = ICON_BASE_URL_AW + icon + ICON_EXTENSION_AW;
        weather.setIconUrl(icon);

        return weather;
    }//parseAccuWeatherCurrent

    /**
     *      This methods goes through the forecast data and create 3 Weather objects for next 3
     *      days and return the list
     * @param jsonString : response
     * @return : ArrayList<Weather> for the next 3 days
     * @throws JSONException
     */
    public static ArrayList<Weather> parseAccuWeatherForecast (String jsonString) throws JSONException {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        String date, tempMin, tempMax, summaryDay, popDay, cloudDay, summaryNight, popNight, cloudNight;
        ArrayList<Weather> weatherArrayList = new ArrayList<>();
        Weather weather;
        //for formatting date
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM.dd", Locale.getDefault());

        JSONObject rootObject = new JSONObject(jsonString);
        JSONArray forecastArray = rootObject.getJSONArray(DAILY_FORECASTS_AW);

        /*we want data for the next three days (first object is for today, which we ignore for now
        So we get index 1, 2, and 3
         */
        for (int i = 1; i < 4; i++) {
            weather = new Weather();
            JSONObject mainObject = forecastArray.getJSONObject(i);

            //date
            long epoch = (long) mainObject.optInt(FORECAST_EPOCH_AW, -1);
            //check for -1
            if (epoch != -1) {
                //convert to milli sec
                epoch *= 1000;
                //create a date
                Date dateObject = new Date(epoch);
                date = formatter.format(dateObject);
            } else {
                date = "";
            }
            weather.setDate(date);

            //temps
            JSONObject tempObject = mainObject.optJSONObject(TEMPERATURE_AW);

            JSONObject minObject = tempObject.optJSONObject(FORECAST_MIN_AW);
            tempMin = String.valueOf(minObject.optLong(METRIC_VALUE_AW));
            weather.setTempMin(tempMin);

            JSONObject manObject = tempObject.optJSONObject(FORECAST_MAX_AW);
            tempMax = String.valueOf(manObject.optLong(METRIC_VALUE_AW));
            weather.setTempMax(tempMax);

            //day
            JSONObject dayObject = mainObject.optJSONObject(FORECAST_DAY_AW);

            summaryDay = dayObject.optString(FORECAST_SUMMARY_AW);
            weather.setSummaryDay(summaryDay);

            popDay = String.valueOf(dayObject.optInt(FORECAST_POP_AW));
            weather.setPopDay(popDay);

            cloudDay = String.valueOf(dayObject.optInt(CLOUD_COVER_AW));
            weather.setCloudDay(cloudDay);

            //night
            JSONObject nightObject = mainObject.optJSONObject(FORECAST_NIGHT_AW);

            summaryNight = nightObject.optString(FORECAST_SUMMARY_AW);
            weather.setSummaryNight(summaryNight);

            popNight = String.valueOf(nightObject.optInt(FORECAST_POP_AW));
            weather.setPopNight(popNight);

            cloudNight = String.valueOf(nightObject.optInt(CLOUD_COVER_AW));
            weather.setCloudNight(cloudNight);

            weatherArrayList.add(weather);

        }//for
        return weatherArrayList;
    }//parseAccuWeatherForecast

    /*
    -------------------------------- Dark Sky -----------------------------------------
     */
    //TODO: use the new url to get metric and then remove all this conversions

    /**
     * This method is for parsing the current weather from DarkSky
     * @param jsonString : raw JSON response from the server
     * @return : Weather object
     * @throws JSONException
     */
    public static Weather parseDarkSkyCurrent(String jsonString) throws JSONException {

        //dummy check for empty or null input
        if (jsonString == null || TextUtils.isEmpty(jsonString)) {
            return null;
        }//if

        String summary, temp, pressure, dewPoint, humidity, windSpeed, windGust, windDirection,
                cloudCoverage, icon, pop, precipType, visibility;

        Weather weather = new Weather();

        //create the main object
        JSONObject forecastObject = new JSONObject(jsonString);

        JSONObject currentObject = forecastObject.getJSONObject(CURRENTLY_DS);

        summary = currentObject.optString(SUMMARY_DS);
        weather.setSummary(summary);

        double popDouble = currentObject.optDouble(POP_DS);
        pop = Conversions.decimalToPercentage(popDouble);
        weather.setPop(pop);

        precipType = currentObject.optString(PRECIP_TYPE_DS);
        if (!(TextUtils.isEmpty(precipType) || precipType.length() < 2)) {
            precipType = Conversions.capitalizeFirst(precipType);
            weather.setPopType(precipType);
            precipType = CHANCE_OF + precipType;
        }


        double tempDouble = currentObject.optDouble(TEMPERATURE_DS);
        temp = Conversions.farToCel(tempDouble);
        weather.setTemperature(temp);

        double dewDouble = currentObject.optDouble(DEW_POINT_DS);
        dewPoint = Conversions.farToCel(dewDouble);
        weather.setDewPoint(dewPoint);

        double humidDouble = currentObject.optDouble(HUMIDITY_DS);
        humidity = Conversions.decimalToPercentage(humidDouble);
        weather.setHumidity(humidity);

        double pressDouble = currentObject.optDouble(PRESSURE_DS);
        pressure = Conversions.removeDecimal(pressDouble);
        weather.setPressure(pressure);

        double windSpeedDouble = currentObject.optDouble(WIND_SPEED_DS);
        windSpeed = Conversions.mileToKm(windSpeedDouble);
        weather.setWindSpeed(windSpeed);

        double windGustDouble = currentObject.optDouble(WIND_GUST_DS);
        windGust = Conversions.mileToKm(windGustDouble);
        weather.setWindGust(windGust);

        double windDirDouble = currentObject.optDouble(WIND_DIRECTION_DS);
        windDirection = Conversions.degreeToDirection(windDirDouble);
        weather.setWindDirection(windDirection);

        double cloudCoverDouble = currentObject.optDouble(CLOUD_COVER_DS);
        cloudCoverage = Conversions.decimalToPercentage(cloudCoverDouble);
        weather.setCloudCoverage(cloudCoverage);

        double visDouble = currentObject.optDouble(VISIBILITY_DS);
        visibility = Conversions.mileToKm(visDouble);
        weather.setVisibility(visibility);

        icon = currentObject.optString(ICON_DS);

        return weather;
    }//parseDarkSkyCurrent

    public static ArrayList<Weather> parseDarkSkyForecast (String jsonString) throws JSONException {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        String date, tempMin, tempMax, summary, pressure, dewPoint, humidity, windSpeed, windGust,
                windDirection, cloudCoverage, pop, precipType, visibility;
        ArrayList<Weather> weatherArrayList = new ArrayList<>();
        Weather weather;
        //for formatting date
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM.dd", Locale.getDefault());

        JSONObject rootObject = new JSONObject(jsonString);
        JSONObject dailyObject = rootObject.optJSONObject(DAILY_DS);
        JSONArray dataArray = dailyObject.optJSONArray(DATA_DS);

        /*we want data for the next three days (first object is for today, which we ignore for now
        So we get index 1, 2, and 3
         */


        for (int i = 1; i < 4; i++) {
            weather = new Weather();
            JSONObject mainObject = dataArray.getJSONObject(i);

            //date
            long epoch = (long) mainObject.optInt(TIME_DS, -1);
            //check for -1
            if (epoch != -1) {
                //convert to milli sec
                epoch *= 1000;
                //create a date
                Date dateObject = new Date(epoch);
                date = formatter.format(dateObject);
            } else {
                date = "";
            }
            weather.setDate(date);

            //summary
            summary = mainObject.optString(SUMMARY_DS);
            weather.setSummary(summary);

            //temps
            long tempMinLong = mainObject.optLong(TEMP_MIN_DS);
            tempMin = Conversions.farToCel(tempMinLong);
            weather.setTempMin(tempMin);

            long tempMaxLong = mainObject.optLong(TEMP_MAX_DS);
            tempMax = Conversions.farToCel(tempMaxLong);
            weather.setTempMax(tempMax);

            //dew point
            double dewDouble = mainObject.optDouble(DEW_POINT_DS);
            dewPoint = Conversions.farToCel(dewDouble);
            weather.setDewPoint(dewPoint);

            //humidity
            double humidDouble = mainObject.optDouble(HUMIDITY_DS);
            humidity = Conversions.decimalToPercentage(humidDouble);
            weather.setHumidity(humidity);

            //pressure
            double pressDouble = mainObject.optDouble(PRESSURE_DS);
            pressure = Conversions.removeDecimal(pressDouble);
            weather.setPressure(pressure);

            //wind speed
            double windSpeedDouble = mainObject.optDouble(WIND_SPEED_DS);
            windSpeed = Conversions.mileToKm(windSpeedDouble);
            weather.setWindSpeed(windSpeed);

            //wind gust
            double windGustDouble = mainObject.optDouble(WIND_GUST_DS);
            windGust = Conversions.mileToKm(windGustDouble);
            weather.setWindGust(windGust);

            //wind direction
            double windDirDouble = mainObject.optDouble(WIND_DIRECTION_DS);
            windDirection = Conversions.degreeToDirection(windDirDouble);
            weather.setWindDirection(windDirection);

            //cloud cover
            double cloudCoverDouble = mainObject.optDouble(CLOUD_COVER_DS);
            cloudCoverage = Conversions.decimalToPercentage(cloudCoverDouble);
            weather.setCloudCoverage(cloudCoverage);

            //visibility
            double visDouble = mainObject.optDouble(VISIBILITY_DS);
            visibility = Conversions.mileToKm(visDouble);
            weather.setVisibility(visibility);

            //POP
            double popDouble = mainObject.optDouble(POP_DS);
            pop = Conversions.decimalToPercentage(popDouble);
            weather.setPop(pop);

            precipType = mainObject.optString(PRECIP_TYPE_DS);
            if (!TextUtils.isEmpty(precipType)) {
                precipType = Conversions.capitalizeFirst(precipType);
                weather.setPopType(precipType);
            }

            weatherArrayList.add(weather);

        }//for
        return weatherArrayList;
    }//parseDarkSkyForecast




}//class

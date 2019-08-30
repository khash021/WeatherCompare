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
    //TODO: clean up go through it
    //TODO: double check all the results to be same unit (C, pressure, percentage, kmh, km, etc)
    //TODO: add try catch for null pointer for all array stuff

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
        --------------------------- Weather Bit -----------------------------------
     */

    private static final String DATA_WB = "data";
    private static final String TEMP_WB = "temp";
    private static final String WEATHER_WB = "weather";
    private static final String DESCRIPTION_WB = "description";
    private static final String ICON_WB = "icon";
    private static final String HUMIDITY_WB = "rh";
    private static final String PRESSURE_WB = "pres";
    private static final String CLOUD_WB = "clouds";
    private static final String VISIBILITY_WB = "vis";
    private static final String DEW_WB = "dewpt";
    private static final String WIND_SPEED_WB = "wind_spd";
    private static final String WIND_DIRECTION_WB = "wind_dir";
    private static final String EPOCH_WB = "ts";
    private static final String TEMP_MIN_WB = "min_temp";
    private static final String TEMP_MAX_WB = "max_temp";
    private static final String POP_WB = "pop";


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

    //TODO: instead of removing decimal, round it for all weathers

    /**
     * This method is for parsing the current weather from Open Weather
     *
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

        //set the provider
        weather.setProvider(Weather.PROVIDER_OW);

        //create an object from the string
        JSONObject forecastJson = new JSONObject(jsonString);
        //get a reference to the main
        JSONObject mainObject = forecastJson.getJSONObject(MAIN_OW);

        //weather description
        JSONArray weatherArray = forecastJson.getJSONArray(WEATHER_OW);
        JSONObject weatherObject = weatherArray.getJSONObject(0);
        summary = weatherObject.optString(WEATHER_MAIN_OW);
        weather.setSummary(summary);

        //temp
        temp = mainObject.optString(TEMP_OW);
        //round if it contains a decimal point
        temp = Conversions.roundDecimal(temp);

        weather.setTemperature(temp);

        //pressure
        pressure = mainObject.optString(PRESSURE_OW);

        pressure = Conversions.roundDecimal(pressure);

        weather.setPressure(pressure);

        //humidity
        humidity = mainObject.getString(HUMIDITY_OW);
        weather.setHumidity(humidity);

        //get a reference to the wind
        JSONObject windObject = forecastJson.getJSONObject(WIND_OW);

        //wind
        windSpeed = windObject.optString(WIND_SPEED_OW);
        windSpeed = Conversions.roundDecimal(windSpeed);
        weather.setWindSpeed(windSpeed);

        double windDirectionDouble = windObject.optDouble(WIND_DIRECTION_OW);
        windDirection = Conversions.degreeToDirection(windDirectionDouble);
        weather.setWindDirection(windDirection);

        //cloud cover
        JSONObject cloudObject = forecastJson.getJSONObject(CLOUD_OW);
        cloudCoverage = cloudObject.optString(CLOUDS_ALL_OW);
        cloudCoverage = Conversions.roundDecimal(cloudCoverage);
        weather.setCloudCoverage(cloudCoverage);

        //icon
        icon = weatherObject.optString(ICON_OW);
        icon = OPENWEATHER_ICON_BASE_URL + icon + OPENWEATHER_ICON_EXTENSION;
        weather.setIconUrl(icon);

        return weather;
    }//parseOpenWeatherCurrent

    /**
     * This methods goes through the forecast data and create 3 Weather objects for next 3
     * days and return the list
     *
     * @param jsonResponse : response
     * @return : ArrayList<Weather> for the next 3 days
     * @throws JSONException
     */
    public static ArrayList<Weather> parseOpenWeatherForecast(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        final long DAY_MILLI = 86400000;
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
            if (day1Array == null || day1Array.size() < 1) {
                Log.d(TAG, "OW WEATHER LIST = null/empty");
            } else {
                Weather weather1 = calculateMinMax(day1Array);
                weatherArrayList.add(weather1);
            }//if/else null/empty

            if (day2Array == null || day2Array.size() < 1) {
                Log.d(TAG, "OW WEATHER LIST = null/empty");
            } else {
                Weather weather2 = calculateMinMax(day2Array);
                weatherArrayList.add(weather2);
            }//if/else null/empty

            if (day3Array == null || day3Array.size() < 1) {
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
                //set the provider
                weather.setProvider(Weather.PROVIDER_OW);
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
        float min, max;
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

        ArrayList<Float> tempArrayList = new ArrayList<>();
        for (Weather w : weatherArrayList) {
            float temp = Float.valueOf(w.getTemperature());
            tempArrayList.add(temp);
        }

        min = Collections.min(tempArrayList);
        max = Collections.max(tempArrayList);

        //round values
        min = Math.round(min);
        max = Math.round(max);

        weather.setTempMin(Conversions.removeDemicalFloat(min));
        weather.setTempMax(Conversions.removeDemicalFloat(max));

        return weather;

    }//calculateMinMax

    /*
    -------------------------------- Accu Weather ----------------------------------------
     */

    /**
     * It extracts AccuWeather's location code from the JSON response
     *
     * @param jsonString : JSON response from AW API
     * @return : location code
     * @throws JSONException
     */
    public static String parseAccuLocationCode(String jsonString) throws JSONException {
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
     *
     * @param jsonString : raw JSON response from the server
     * @return : Weather object
     * @throws JSONException
     */
    public static Weather parseAccuWeatherCurrent(String jsonString) throws JSONException {

        //dummy check for empty or null input
        if (jsonString == null || TextUtils.isEmpty(jsonString)) {
            return null;
        }//if

        //the response is in both imperial and metric, we extract the metric

        String summary, temp, dewPoint, pressure, humidity, windSpeed, windDirection, windGust,
                visibility, cloudCoverage, icon;

        //create a Weather object
        Weather weather = new Weather();

        //create the main array
        JSONArray forecastArray = new JSONArray(jsonString);

        //get the first element of the array containing all the current weather data
        JSONObject forecastObject = forecastArray.getJSONObject(0);

        //set the provider
        weather.setProvider(Weather.PROVIDER_AC);

        //summary
        summary = forecastObject.optString(DESCRIPTION_AW);
        weather.setSummary(summary);

        //temp
        JSONObject tempObject = forecastObject.getJSONObject(TEMPERATURE_AW);
        JSONObject tempMetricObject = tempObject.getJSONObject(METRIC_AW);
        temp = tempMetricObject.optString(METRIC_VALUE_AW);
        temp = Conversions.roundDecimal(temp);
        weather.setTemperature(temp);

        //dew point
        JSONObject dewObject = forecastObject.getJSONObject(DEW_POINT_AW);
        JSONObject metricDewObject = dewObject.getJSONObject(METRIC_AW);
        dewPoint = metricDewObject.optString(METRIC_VALUE_AW);
        dewPoint = Conversions.roundDecimal(dewPoint);
        weather.setDewPoint(dewPoint);

        //pressure
        JSONObject pressObject = forecastObject.getJSONObject(PRESSURE_AW);
        JSONObject pressMetricObject = pressObject.getJSONObject(METRIC_AW);
        pressure = pressMetricObject.optString(METRIC_VALUE_AW);
        pressure = Conversions.roundDecimal(pressure);
        weather.setPressure(pressure);

        //humidity
        humidity = forecastObject.optString(HUMIDITY_AW);
        weather.setHumidity(humidity);

        //wind
        JSONObject windObject = forecastObject.getJSONObject(WIND_AW);
        JSONObject windSpeedObject = windObject.getJSONObject(SPEED_AW);
        JSONObject windSpeedMetricObject = windSpeedObject.getJSONObject(METRIC_AW);
        windSpeed = windSpeedMetricObject.optString(METRIC_VALUE_AW);
        windSpeed = Conversions.roundDecimal(windSpeed);
        weather.setWindSpeed(windSpeed);

        JSONObject windDirectionObject = windObject.getJSONObject(DIRECTION_AW);
        windDirection = windDirectionObject.optString(WIND_DIRECTION_ENGLISH_AW);
        weather.setWindDirection(windDirection);

        //windGust
        JSONObject gustObject = forecastObject.getJSONObject(WIND_GUST_AW);
        JSONObject gustSpeedObject = gustObject.getJSONObject(SPEED_AW);
        JSONObject gustSpeedMetricObject = gustSpeedObject.getJSONObject(METRIC_AW);
        windGust = gustSpeedMetricObject.optString(METRIC_VALUE_AW);
        windGust = Conversions.roundDecimal(windGust);
        weather.setWindGust(windGust);

        //visibility
        JSONObject visibilityObject = forecastObject.getJSONObject(VISIBILITY_AW);
        JSONObject visibilityMetricObject = visibilityObject.getJSONObject(METRIC_AW);
        visibility = visibilityMetricObject.optString(METRIC_VALUE_AW);
        visibility = Conversions.roundDecimal(visibility);
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
     * This methods goes through the forecast data and create 3 Weather objects for next 3
     * days and return the list
     *
     * @param jsonString : response
     * @return : ArrayList<Weather> for the next 3 days
     * @throws JSONException
     */
    public static ArrayList<Weather> parseAccuWeatherForecast(String jsonString) throws JSONException {
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

            //set the provider
            weather.setProvider(Weather.PROVIDER_AC);

            //temps
            JSONObject tempObject = mainObject.optJSONObject(TEMPERATURE_AW);

            JSONObject minObject = tempObject.optJSONObject(FORECAST_MIN_AW);
            tempMin = minObject.optString(METRIC_VALUE_AW);
            tempMin = Conversions.roundDecimal(tempMin);
            weather.setTempMin(tempMin);

            JSONObject manObject = tempObject.optJSONObject(FORECAST_MAX_AW);
            tempMax = manObject.optString(METRIC_VALUE_AW);
            tempMax = Conversions.roundDecimal(tempMax);
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
     *
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

        //set the provider
        weather.setProvider(Weather.PROVIDER_DS);

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


        temp = currentObject.optString(TEMPERATURE_DS);
        temp = Conversions.roundDecimal(temp);
        weather.setTemperature(temp);

        dewPoint = currentObject.optString(DEW_POINT_DS);
        dewPoint = Conversions.roundDecimal(dewPoint);
        weather.setDewPoint(dewPoint);

        double humidDouble = currentObject.optDouble(HUMIDITY_DS);
        humidity = Conversions.decimalToPercentage(humidDouble);
        weather.setHumidity(humidity);

        pressure = currentObject.optString(PRESSURE_DS);
        pressure = Conversions.roundDecimal(pressure);
        weather.setPressure(pressure);

        windSpeed = currentObject.optString(WIND_SPEED_DS);
        windSpeed = Conversions.roundDecimal(windSpeed);
        weather.setWindSpeed(windSpeed);

        windGust = currentObject.optString(WIND_GUST_DS);
        windGust = Conversions.roundDecimal(windGust);
        weather.setWindGust(windGust);

        double windDirDouble = currentObject.optDouble(WIND_DIRECTION_DS);
        windDirection = Conversions.degreeToDirection(windDirDouble);
        weather.setWindDirection(windDirection);

        double cloudCoverDouble = currentObject.optDouble(CLOUD_COVER_DS);
        cloudCoverage = Conversions.decimalToPercentage(cloudCoverDouble);
        weather.setCloudCoverage(cloudCoverage);

        visibility = currentObject.optString(VISIBILITY_DS);
        visibility = Conversions.roundDecimal(visibility);
        weather.setVisibility(visibility);

        icon = currentObject.optString(ICON_DS);

        return weather;
    }//parseDarkSkyCurrent

    public static ArrayList<Weather> parseDarkSkyForecast(String jsonString) throws JSONException {
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
        JSONArray dataArray = dailyObject.optJSONArray(DATA_WB);

        /*we want data for the next three days (first object is for today, which we ignore for now
        So we get index 1, 2, and 3
         */


        for (int i = 1; i < 4; i++) {
            weather = new Weather();
            //set the provider
            weather.setProvider(Weather.PROVIDER_DS);

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
            weather.setEpoch(epoch);
            weather.setDate(date);

            //summary
            summary = mainObject.optString(SUMMARY_DS);
            weather.setSummary(summary);

            //temps
            tempMin = mainObject.optString(TEMP_MIN_DS);
            tempMin = Conversions.roundDecimal(tempMin);

            tempMax = mainObject.optString(TEMP_MAX_DS);
            tempMax = Conversions.roundDecimal(tempMax);
            weather.setTempMin(tempMin);
            weather.setTempMax(tempMax);


            //dew point
            dewPoint = mainObject.optString(DEW_POINT_DS);
            dewPoint = Conversions.roundDecimal(dewPoint);
            weather.setDewPoint(dewPoint);

            //humidity
            humidity = mainObject.optString(HUMIDITY_DS);
            humidity = Conversions.roundDecimal(humidity);
            weather.setHumidity(humidity);

            //pressure
            pressure = mainObject.optString(PRESSURE_DS);
            pressure = Conversions.roundDecimal(pressure);
            weather.setPressure(pressure);

            //wind speed
            windSpeed = mainObject.optString(WIND_SPEED_DS);
            windSpeed = Conversions.roundDecimal(windSpeed);
            weather.setWindSpeed(windSpeed);

            //wind gust
            windGust = mainObject.optString(WIND_GUST_DS);
            windGust = Conversions.roundDecimal(windGust);
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
            visibility = mainObject.optString(VISIBILITY_DS);
            visibility = Conversions.roundDecimal(visibility);
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






    /*
    -------------------------------- Weather Bit -----------------------------------------
     */

    /**
     * This method is for parsing the current weather from Weather Bit
     *
     * @param jsonString : raw JSON response from the server
     * @return : Weather object
     * @throws JSONException
     */
    public static Weather parseWeatherBitCurrent(String jsonString) throws JSONException {

        //dummy check for empty or null input
        if (jsonString == null || TextUtils.isEmpty(jsonString)) {
            return null;
        }//if

        String summary, temp, pressure, humidity, windSpeed, windDirection, cloudCoverage, icon,
        dewPoint, visibility;

        //create a Weather object
        Weather weather = new Weather();

        //set the provider
        weather.setProvider(Weather.PROVIDER_WB);

        //create an object from the string
        JSONObject rootObject = new JSONObject(jsonString);
        //get a reference to the data array
        JSONArray dataArray = rootObject.getJSONArray(DATA_WB);
        //get the first element of the array for current data
        try {
            JSONObject mainObject = dataArray.getJSONObject(0);

            //summary and icon are both in weather object
            JSONObject weatherObject = mainObject.optJSONObject(WEATHER_WB);
            summary = weatherObject.optString(DESCRIPTION_WB);
            weather.setSummary(summary);
            icon = weatherObject.optString(ICON_WB);

            //temp
            temp = mainObject.optString(TEMP_WB);
            //round if it contains a decimal point
            temp = Conversions.roundDecimal(temp);
            weather.setTemperature(temp);

            //dew
            dewPoint = mainObject.optString(DEW_WB);
            dewPoint = Conversions.roundDecimal(dewPoint);
            weather.setDewPoint(dewPoint);

            //pressure
            pressure = mainObject.optString(PRESSURE_WB);
            pressure = Conversions.roundDecimal(pressure);
            weather.setPressure(pressure);

            //humidity
            humidity = mainObject.getString(HUMIDITY_WB);
            weather.setHumidity(humidity);

            //wind
            windSpeed = mainObject.optString(WIND_SPEED_WB);
            windSpeed = Conversions.roundDecimal(windSpeed);
            weather.setWindSpeed(windSpeed);


            double windDirectionDouble = mainObject.optDouble(WIND_DIRECTION_WB);
            windDirection = Conversions.degreeToDirection(windDirectionDouble);
            weather.setWindDirection(windDirection);

            //cloud cover
            cloudCoverage = mainObject.optString(CLOUD_WB);
            cloudCoverage = Conversions.roundDecimal(cloudCoverage);
            weather.setCloudCoverage(cloudCoverage);

            //visibility
            visibility = mainObject.optString(VISIBILITY_WB);
            visibility = Conversions.roundDecimal(visibility);
            weather.setVisibility(visibility);


            return weather;
        } catch (NullPointerException e) {
            //this happens when for some reason the main data array is empty
            Log.e("parseWeatherBitCurrent", "parseWeatherBitCurrent - data array is null", e);
            return null;
        }
    }//parseWeatherBitCurrent






    public static ArrayList<Weather> parseWeatherBitForecast(String jsonString) throws JSONException {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        String date, summary, tempMin, tempMax, dewPoint, pressure, humidity, windSpeed, windGust,
                windDirection, cloudCoverage, pop, visibility;
        ArrayList<Weather> weatherArrayList = new ArrayList<>();
        Weather weather;
        //for formatting date
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM.dd", Locale.getDefault());

        JSONObject rootObject = new JSONObject(jsonString);
        JSONArray dataArray = rootObject.optJSONArray(DATA_WB);

        /*we want data for the next three days (first object is for today, which we ignore for now
        So we get index 1, 2, and 3
         */


        for (int i = 1; i < 4; i++) {
            weather = new Weather();
            //set the provider
            weather.setProvider(Weather.PROVIDER_WB);

            JSONObject mainObject = dataArray.getJSONObject(i);

            //date
            long epoch = mainObject.optLong(EPOCH_WB, -1);
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
            weather.setEpoch(epoch);
            weather.setDate(date);

            //summary
            JSONObject weatherObject = mainObject.optJSONObject(WEATHER_WB);
            summary = weatherObject.optString(DESCRIPTION_WB);
            weather.setSummary(summary);

            //temps
            tempMin = mainObject.optString(TEMP_MIN_WB);
            tempMin = Conversions.roundDecimal(tempMin);

            tempMax = mainObject.optString(TEMP_MAX_WB);
            tempMax = Conversions.roundDecimal(tempMax);
            weather.setTempMin(tempMin);
            weather.setTempMax(tempMax);

            //dew point
            dewPoint = mainObject.optString(DEW_WB);
            dewPoint = Conversions.roundDecimal(dewPoint);
            weather.setDewPoint(dewPoint);

            //humidity
            humidity = mainObject.optString(HUMIDITY_WB);
            humidity = Conversions.roundDecimal(humidity);
            weather.setHumidity(humidity);

            //pressure
            pressure = mainObject.optString(PRESSURE_WB);
            pressure = Conversions.roundDecimal(pressure);
            weather.setPressure(pressure);

            //wind speed
            windSpeed = mainObject.optString(WIND_SPEED_WB);
            windSpeed = Conversions.roundDecimal(windSpeed);
            weather.setWindSpeed(windSpeed);

            //wind gust
            windGust = mainObject.optString(WIND_GUST_DS);
            windGust = Conversions.roundDecimal(windGust);
            weather.setWindGust(windGust);

            //wind direction
            double windDirDouble = mainObject.optDouble(WIND_DIRECTION_WB);
            windDirection = Conversions.degreeToDirection(windDirDouble);
            weather.setWindDirection(windDirection);

            //cloud cover
            cloudCoverage = mainObject.optString(CLOUD_WB);
            weather.setCloudCoverage(cloudCoverage);

            //visibility
            visibility = mainObject.optString(VISIBILITY_WB);
            visibility = Conversions.roundDecimal(visibility);
            weather.setVisibility(visibility);

            //POP
            pop = mainObject.optString(POP_WB);
            pop = Conversions.roundDecimal(pop);
            weather.setPop(pop);


            weatherArrayList.add(weather);

        }//for
        return weatherArrayList;
    }//parseWeatherBitForecast

}//class

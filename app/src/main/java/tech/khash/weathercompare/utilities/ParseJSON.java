package tech.khash.weathercompare.utilities;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
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
    private static final String FEEL_LIKE_AW = "RealFeelTemperature";
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
    private static final String IS_DAY_AW = "IsDayTime";


    //forecast AW
    private static final String DAILY_FORECASTS_AW = "DailyForecasts";
    private static final String FORECAST_EPOCH_AW = "EpochDate";
    private static final String FORECAST_MIN_AW = "Minimum";
    private static final String FORECAST_MAX_AW = "Maximum";
    private static final String FORECAST_DAY_AW = "Day";
    private static final String FORECAST_NIGHT_AW = "Night";
    private static final String FORECAST_SUMMARY_AW = "ShortPhrase";
    private static final String FORECAST_POP_AW = "PrecipitationProbability";
    private static final String POP_TOTAL_AW = "TotalLiquid";

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
    private static final String FEEL_LIKE_DS = "apparentTemperature";
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
    private static final String FEEL_LIKE_MIN_DS = "apparentTemperatureMin";
    private static final String TEMP_MAX_DS = "temperatureMax";
    private static final String FEEL_LIKE_MAX_DS = "apparentTemperatureMax";

    /*
        --------------------------- Weather Bit -----------------------------------
     */

    private static final String DATA_WB = "data";
    private static final String TEMP_WB = "temp";
    private static final String FEEL_LIKE_WB = "app_temp";
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
    private static final String FEEL_LIKE_MIN_WB = "app_min_temp";
    private static final String FEEL_LIKE_MAX_WB = "app_max_temp";
    private static final String POP_WB = "pop";
    private static final String PRECIPITATION_TOTAL_WB = "precip";

    /*
        --------------------------- Weather Unlocked -----------------------------------
     */

    private static final String SUMMARY_WU = "wx_desc";
    private static final String TEMP_WU = "temp_c";
    private static final String FEEL_LIKE_WU = "feelslike_c";
    private static final String DEW_WU = "dewpoint_c";
    private static final String HUMIDITY_WU = "humid_pct";
    private static final String WIND_SPEED_WU = "windspd_kmh";
    private static final String WIND_DIRECTION_WU = "winddir_deg";
    private static final String CLOUD_COVER_WU = "cloudtotal_pct";
    private static final String VISIBILITY_WU = "vis_km";

    //forecast
    private static final String DAYS_WU = "Days";
    private static final String DATE_WU = "date";
    private static final String TEMP_MIN_WU = "temp_min_c";
    private static final String TEMP_MAX_WU = "temp_max_c";
    private static final String POP_WU = "prob_precip_pct";
    private static final String PRECIP_TOTAL_WU = "precip_total_mm";
    private static final String WIND_WU = "windspd_max_kmh";
    private static final String GUST_WU = "windgst_max_kmh";
    private static final String HUMIDITY_MAX_WU = "humid_max_pct";
    private static final String HUMIDITY_MIN_WU = "humid_min_pct";



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

    private static final long DAY_MILLI = 86400000;

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

        String summary, temp, feelLike, dewPoint, pressure, humidity, windSpeed, windDirection,
                cloudCoverage, icon;

        //create a Weather object
        Weather weather = new Weather();

        //set the provider
        weather.setProvider(Weather.PROVIDER_OW);

        //create an object from the string
        JSONObject currentObject = new JSONObject(jsonString);
        //get a reference to the main
        JSONObject mainObject = currentObject.getJSONObject(MAIN_OW);

        //weather description
        JSONArray weatherArray = currentObject.getJSONArray(WEATHER_OW);
        JSONObject weatherObject = weatherArray.getJSONObject(0);
        summary = weatherObject.optString(WEATHER_MAIN_OW);
        weather.setSummary(summary);

        //temp
        temp = mainObject.optString(TEMP_OW);
        double T = mainObject.optDouble(TEMP_OW);
        //round if it contains a decimal point
        temp = Conversions.roundDecimalString(temp);

        weather.setTemperature(temp);

        //pressure
        pressure = mainObject.optString(PRESSURE_OW);

        pressure = Conversions.roundDecimalString(pressure);

        weather.setPressure(pressure);

        //humidity
        humidity = mainObject.getString(HUMIDITY_OW);
        double H = mainObject.optDouble(HUMIDITY_OW);
        weather.setHumidity(humidity);

        //get a reference to the wind
        JSONObject windObject = currentObject.getJSONObject(WIND_OW);

        //wind
        windSpeed = windObject.optString(WIND_SPEED_OW);
        double V = windObject.optDouble(WIND_SPEED_OW);
        windSpeed = Conversions.roundDecimalString(windSpeed);
        weather.setWindSpeed(windSpeed);

        double windDirectionDouble = windObject.optDouble(WIND_DIRECTION_OW);
        windDirection = Conversions.degreeToDirection(windDirectionDouble);
        weather.setWindDirection(windDirection);

        //cloud cover
        JSONObject cloudObject = currentObject.getJSONObject(CLOUD_OW);
        cloudCoverage = cloudObject.optString(CLOUDS_ALL_OW);
        cloudCoverage = Conversions.roundDecimalString(cloudCoverage);
        weather.setCloudCoverage(cloudCoverage);

        //Feel like temp and dew point
        double feel = Conversions.calculateFeelsLikeTemp(T, H, V);
        feelLike = Conversions.roundDecimalDouble(feel);
        weather.setTempFeel(feelLike);

        double dew = Conversions.calculateDewPointCel(T, H);
        dewPoint = Conversions.roundDecimalDouble(dew);
        weather.setDewPoint(dewPoint);

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
        String temp, humidity, wind;
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
                //temp
                temp = mainObject.optString(TEMP_OW);
                weather.setTemperature(temp);

                //hum
                humidity = mainObject.optString(HUMIDITY_OW);
                weather.setHumidity(humidity);

                //wind
                JSONObject windObject = rootObject.optJSONObject(WIND_OW);
                wind = windObject.optString(WIND_SPEED_OW);
                weather.setWindSpeed(wind);

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

        String summary, temp, feelLike, dewPoint, pressure, humidity, windSpeed, windDirection,
                windGust, visibility, cloudCoverage, icon;

        //create a Weather object
        Weather weather = new Weather();

        //create the main array
        JSONArray currentArray = new JSONArray(jsonString);

        //get the first element of the array containing all the current weather data
        JSONObject currentObject = currentArray.getJSONObject(0);

        //set the provider
        weather.setProvider(Weather.PROVIDER_AC);

        //summary
        summary = currentObject.optString(DESCRIPTION_AW);
        weather.setSummary(summary);

        //isDay
        boolean isDay = currentObject.optBoolean(IS_DAY_AW);
        weather.setIsDay(isDay);

        //temp
        JSONObject tempObject = currentObject.getJSONObject(TEMPERATURE_AW);
        JSONObject tempMetricObject = tempObject.getJSONObject(METRIC_AW);
        temp = tempMetricObject.optString(METRIC_VALUE_AW);
        temp = Conversions.roundDecimalString(temp);
        weather.setTemperature(temp);

        JSONObject fellLikeObject = currentObject.getJSONObject(FEEL_LIKE_AW);
        JSONObject fellLikeMetricObject = fellLikeObject.getJSONObject(METRIC_AW);
        feelLike = fellLikeMetricObject.optString(METRIC_VALUE_AW);
        feelLike = Conversions.roundDecimalString(feelLike);
        weather.setTempFeel(feelLike);

        //dew point
        JSONObject dewObject = currentObject.getJSONObject(DEW_POINT_AW);
        JSONObject metricDewObject = dewObject.getJSONObject(METRIC_AW);
        dewPoint = metricDewObject.optString(METRIC_VALUE_AW);
        dewPoint = Conversions.roundDecimalString(dewPoint);
        weather.setDewPoint(dewPoint);

        //pressure
        JSONObject pressObject = currentObject.getJSONObject(PRESSURE_AW);
        JSONObject pressMetricObject = pressObject.getJSONObject(METRIC_AW);
        pressure = pressMetricObject.optString(METRIC_VALUE_AW);
        pressure = Conversions.roundDecimalString(pressure);
        weather.setPressure(pressure);

        //humidity
        humidity = currentObject.optString(HUMIDITY_AW);
        weather.setHumidity(humidity);

        //wind
        JSONObject windObject = currentObject.getJSONObject(WIND_AW);
        JSONObject windSpeedObject = windObject.getJSONObject(SPEED_AW);
        JSONObject windSpeedMetricObject = windSpeedObject.getJSONObject(METRIC_AW);
        windSpeed = windSpeedMetricObject.optString(METRIC_VALUE_AW);
        windSpeed = Conversions.roundDecimalString(windSpeed);
        weather.setWindSpeed(windSpeed);

        JSONObject windDirectionObject = windObject.getJSONObject(DIRECTION_AW);
        windDirection = windDirectionObject.optString(WIND_DIRECTION_ENGLISH_AW);
        weather.setWindDirection(windDirection);

        //windGust
        JSONObject gustObject = currentObject.getJSONObject(WIND_GUST_AW);
        JSONObject gustSpeedObject = gustObject.getJSONObject(SPEED_AW);
        JSONObject gustSpeedMetricObject = gustSpeedObject.getJSONObject(METRIC_AW);
        windGust = gustSpeedMetricObject.optString(METRIC_VALUE_AW);
        windGust = Conversions.roundDecimalString(windGust);
        weather.setWindGust(windGust);

        //visibility
        JSONObject visibilityObject = currentObject.getJSONObject(VISIBILITY_AW);
        JSONObject visibilityMetricObject = visibilityObject.getJSONObject(METRIC_AW);
        visibility = visibilityMetricObject.optString(METRIC_VALUE_AW);
        visibility = Conversions.roundDecimalString(visibility);
        weather.setVisibility(visibility);

        cloudCoverage = currentObject.optString(CLOUD_COVER_AW);
        weather.setCloudCoverage(cloudCoverage);


        //icon
        int iconId = currentObject.optInt(ICON_AW);
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

        String date, tempMin, tempMax, feelLikeMin, feelLikeMax, summaryDay, popDay, cloudDay,
                summaryNight, popNight, cloudNight, popTotal, windSpeed, windDirection, windGust;

        /**
         * Here we just use day values for pop, cloud, wind and windgust for now
         */

        ArrayList<Weather> weatherArrayList = new ArrayList<>();
        Weather weather;
        //for formatting date
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM.dd", Locale.getDefault());

        //we create our dates for the next three days to get the data for
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

        JSONObject rootObject = new JSONObject(jsonString);
        JSONArray forecastArray = rootObject.getJSONArray(DAILY_FORECASTS_AW);

        /*we want data for the next three days (first object is for today, which we ignore for now
        So we get index 1, 2, and 3. This method has a lot of bugs, instead, we will check each
        entries against our dates and add the next three dates to the array
         */
        for (int i = 0; i < forecastArray.length(); i++) {
            //first check to see if we have three objects in our array meaning we have colledcted all data
            if (weatherArrayList.size() == 3) {
                break;
            }

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
            //check the date against our days
            if (epoch < day1StartMilli || epoch >= day4StartMilli ) {
                continue;
            }
            weather.setDate(date);

            //set the provider
            weather.setProvider(Weather.PROVIDER_AC);

            //temps
            JSONObject tempObject = mainObject.optJSONObject(TEMPERATURE_AW);

            JSONObject minObject = tempObject.optJSONObject(FORECAST_MIN_AW);
            tempMin = minObject.optString(METRIC_VALUE_AW);
            tempMin = Conversions.roundDecimalString(tempMin);
            weather.setTempMin(tempMin);

            JSONObject maxObject = tempObject.optJSONObject(FORECAST_MAX_AW);
            tempMax = maxObject.optString(METRIC_VALUE_AW);
            tempMax = Conversions.roundDecimalString(tempMax);
            weather.setTempMax(tempMax);

            JSONObject feelLikeObject = mainObject.optJSONObject(FEEL_LIKE_AW);

            JSONObject feelMinObject = feelLikeObject.optJSONObject(FORECAST_MIN_AW);
            feelLikeMin = feelMinObject.optString(METRIC_VALUE_AW);
            feelLikeMin = Conversions.roundDecimalString(feelLikeMin);
            weather.setTempFeelMin(feelLikeMin);

            JSONObject feelMaxObject = feelLikeObject.optJSONObject(FORECAST_MAX_AW);
            feelLikeMax = feelMaxObject.optString(METRIC_VALUE_AW);
            feelLikeMax = Conversions.roundDecimalString(feelLikeMax);
            weather.setTempFeelMax(feelLikeMax);

            //-------------------- day -------------------------
            JSONObject dayObject = mainObject.optJSONObject(FORECAST_DAY_AW);

            //summary
            summaryDay = dayObject.optString(FORECAST_SUMMARY_AW);
            weather.setSummaryDay(summaryDay);

            //pop
            popDay = String.valueOf(dayObject.optInt(FORECAST_POP_AW));
            weather.setPopDay(popDay);
            weather.setPop(popDay);

            JSONObject popTotalObject = dayObject.optJSONObject(POP_TOTAL_AW);
            double popTotalDouble = popTotalObject.optDouble(METRIC_VALUE_AW);
            popTotal = Conversions.roundDecimalDouble(popTotalDouble);
            weather.setPopTotal(popTotal);

            //cloud
            cloudDay = String.valueOf(dayObject.optInt(CLOUD_COVER_AW));
            weather.setCloudDay(cloudDay);
            weather.setCloudCoverage(cloudDay);

            //wind
            JSONObject windObject = dayObject.optJSONObject(WIND_AW);

            JSONObject speedObject = windObject.optJSONObject(SPEED_AW);
            double windSpeedDouble = speedObject.optDouble(METRIC_VALUE_AW);
            windSpeed = Conversions.roundDecimalDouble(windSpeedDouble);
            weather.setWindSpeed(windSpeed);

            JSONObject directionObject = windObject.optJSONObject(DIRECTION_AW);
            windDirection = directionObject.optString(WIND_DIRECTION_ENGLISH_AW);
            weather.setWindDirection(windDirection);

            JSONObject gustObject = dayObject.getJSONObject(WIND_GUST_AW);
            JSONObject gustSpeedObject = gustObject.getJSONObject(SPEED_AW);
            double gustDouble = gustSpeedObject.optDouble(METRIC_VALUE_AW);
            windGust = Conversions.roundDecimalDouble(gustDouble);
            weather.setWindGust(windGust);


            //--------------------- night   ------------------------------
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

        String summary, temp, feelLike, pressure, dewPoint, humidity, windSpeed, windGust, windDirection,
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
        temp = Conversions.roundDecimalString(temp);
        weather.setTemperature(temp);

        feelLike = currentObject.optString(FEEL_LIKE_DS);
        feelLike = Conversions.roundDecimalString(feelLike);
        weather.setTempFeel(feelLike);

        dewPoint = currentObject.optString(DEW_POINT_DS);
        dewPoint = Conversions.roundDecimalString(dewPoint);
        weather.setDewPoint(dewPoint);

        double humidDouble = currentObject.optDouble(HUMIDITY_DS);
        humidity = Conversions.decimalToPercentage(humidDouble);
        weather.setHumidity(humidity);

        pressure = currentObject.optString(PRESSURE_DS);
        pressure = Conversions.roundDecimalString(pressure);
        weather.setPressure(pressure);

        windSpeed = currentObject.optString(WIND_SPEED_DS);
        windSpeed = Conversions.roundDecimalString(windSpeed);
        weather.setWindSpeed(windSpeed);

        windGust = currentObject.optString(WIND_GUST_DS);
        windGust = Conversions.roundDecimalString(windGust);
        weather.setWindGust(windGust);

        double windDirDouble = currentObject.optDouble(WIND_DIRECTION_DS);
        windDirection = Conversions.degreeToDirection(windDirDouble);
        weather.setWindDirection(windDirection);

        double cloudCoverDouble = currentObject.optDouble(CLOUD_COVER_DS);
        cloudCoverage = Conversions.decimalToPercentage(cloudCoverDouble);
        weather.setCloudCoverage(cloudCoverage);

        visibility = currentObject.optString(VISIBILITY_DS);
        visibility = Conversions.roundDecimalString(visibility);
        weather.setVisibility(visibility);

        icon = currentObject.optString(ICON_DS);

        return weather;
    }//parseDarkSkyCurrent

    public static ArrayList<Weather> parseDarkSkyForecast(String jsonString) throws JSONException {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        String date, tempMin, tempMax, feelLikeMin, feelLikeMax, summary, pressure, dewPoint, humidity,
                windSpeed, windGust, windDirection, cloudCoverage, pop, precipType, visibility;
        ArrayList<Weather> weatherArrayList = new ArrayList<>();
        Weather weather;
        //for formatting date
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM.dd", Locale.getDefault());

        //we create our dates for the next three days to get the data for
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

        JSONObject rootObject = new JSONObject(jsonString);
        JSONObject dailyObject = rootObject.optJSONObject(DAILY_DS);
        JSONArray dataArray = dailyObject.optJSONArray(DATA_WB);

        /*we want data for the next three days (first object is for today, which we ignore for now
        So we get index 1, 2, and 3. This method has a lot of bugs, instead, we will check each
        entries against our dates and add the next three dates to the array
         */

        for (int i = 0; i < dataArray.length(); i++) {
            //first check to see if we have three objects in our array meaning we have colledcted all data
            if (weatherArrayList.size() == 3) {
                break;
            }

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

            //check the date against our days
            if (epoch < day1StartMilli || epoch >= day4StartMilli ) {
                continue;
            }

            weather.setEpoch(epoch);
            weather.setDate(date);

            //summary
            summary = mainObject.optString(SUMMARY_DS);
            weather.setSummary(summary);

            //temps
            tempMin = mainObject.optString(TEMP_MIN_DS);
            tempMin = Conversions.roundDecimalString(tempMin);

            tempMax = mainObject.optString(TEMP_MAX_DS);
            tempMax = Conversions.roundDecimalString(tempMax);
            weather.setTempMin(tempMin);
            weather.setTempMax(tempMax);

            feelLikeMin = mainObject.optString(FEEL_LIKE_MIN_DS);
            feelLikeMin = Conversions.roundDecimalString(feelLikeMin);
            weather.setTempFeelMin(feelLikeMin);

            feelLikeMax = mainObject.optString(FEEL_LIKE_MAX_DS);
            feelLikeMax = Conversions.roundDecimalString(feelLikeMax);
            weather.setTempFeelMax(feelLikeMax);


            //dew point
            dewPoint = mainObject.optString(DEW_POINT_DS);
            dewPoint = Conversions.roundDecimalString(dewPoint);
            weather.setDewPoint(dewPoint);

            //humidity
            double humidDouble = mainObject.optDouble(HUMIDITY_DS);
            humidity = Conversions.decimalToPercentage(humidDouble);
            weather.setHumidity(humidity);

            //pressure
            pressure = mainObject.optString(PRESSURE_DS);
            pressure = Conversions.roundDecimalString(pressure);
            weather.setPressure(pressure);

            //wind speed
            windSpeed = mainObject.optString(WIND_SPEED_DS);
            windSpeed = Conversions.roundDecimalString(windSpeed);
            weather.setWindSpeed(windSpeed);

            //wind gust
            windGust = mainObject.optString(WIND_GUST_DS);
            windGust = Conversions.roundDecimalString(windGust);
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
            visibility = Conversions.roundDecimalString(visibility);
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

        String summary, temp, feelLike, pressure, humidity, windSpeed, windDirection, cloudCoverage,
                icon, dewPoint, visibility;

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
            temp = Conversions.roundDecimalString(temp);
            weather.setTemperature(temp);

            feelLike = mainObject.optString(FEEL_LIKE_WB);
            feelLike = Conversions.roundDecimalString(feelLike);
            weather.setTempFeel(feelLike);

            //dew
            dewPoint = mainObject.optString(DEW_WB);
            dewPoint = Conversions.roundDecimalString(dewPoint);
            weather.setDewPoint(dewPoint);

            //pressure
            pressure = mainObject.optString(PRESSURE_WB);
            pressure = Conversions.roundDecimalString(pressure);
            weather.setPressure(pressure);

            //humidity
            humidity = mainObject.getString(HUMIDITY_WB);
            weather.setHumidity(humidity);

            //wind
            windSpeed = mainObject.optString(WIND_SPEED_WB);
            windSpeed = Conversions.roundDecimalString(windSpeed);
            weather.setWindSpeed(windSpeed);


            double windDirectionDouble = mainObject.optDouble(WIND_DIRECTION_WB);
            windDirection = Conversions.degreeToDirection(windDirectionDouble);
            weather.setWindDirection(windDirection);

            //cloud cover
            cloudCoverage = mainObject.optString(CLOUD_WB);
            cloudCoverage = Conversions.roundDecimalString(cloudCoverage);
            weather.setCloudCoverage(cloudCoverage);

            //visibility
            visibility = mainObject.optString(VISIBILITY_WB);
            visibility = Conversions.roundDecimalString(visibility);
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

        String date, summary, tempMin, tempMax, feelLikeMin, feelLikeMax, dewPoint, pressure,
                humidity, windSpeed, windGust, windDirection, cloudCoverage, pop, popTotal, visibility;
        ArrayList<Weather> weatherArrayList = new ArrayList<>();
        Weather weather;
        //for formatting date
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM.dd", Locale.getDefault());

        //we create our dates for the next three days to get the data for
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

        JSONObject rootObject = new JSONObject(jsonString);
        JSONArray dataArray = rootObject.optJSONArray(DATA_WB);

        /*we want data for the next three days (first object is for today, which we ignore for now
        So we get index 1, 2, and 3. This method has a lot of bugs, instead, we will check each
        entries against our dates and add the next three dates to the array
         */

        for (int i = 0; i < dataArray.length(); i++) {
            //first check to see if we have three objects in our array meaning we have colledcted all data
            if (weatherArrayList.size() == 3) {
                break;
            }

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

            //check the date against our days
            if (epoch < day1StartMilli || epoch >= day4StartMilli ) {
                continue;
            }

            weather.setEpoch(epoch);
            weather.setDate(date);

            //summary
            JSONObject weatherObject = mainObject.optJSONObject(WEATHER_WB);
            summary = weatherObject.optString(DESCRIPTION_WB);
            weather.setSummary(summary);

            //temps
            tempMin = mainObject.optString(TEMP_MIN_WB);
            tempMin = Conversions.roundDecimalString(tempMin);

            tempMax = mainObject.optString(TEMP_MAX_WB);
            tempMax = Conversions.roundDecimalString(tempMax);
            weather.setTempMin(tempMin);
            weather.setTempMax(tempMax);

            feelLikeMin = mainObject.optString(FEEL_LIKE_MIN_WB);
            feelLikeMin = Conversions.roundDecimalString(feelLikeMin);
            weather.setTempFeelMin(feelLikeMin);

            feelLikeMax = mainObject.optString(FEEL_LIKE_MAX_WB);
            feelLikeMax = Conversions.roundDecimalString(feelLikeMax);
            weather.setTempFeelMax(feelLikeMax);

            //dew point
            dewPoint = mainObject.optString(DEW_WB);
            dewPoint = Conversions.roundDecimalString(dewPoint);
            weather.setDewPoint(dewPoint);

            //humidity
            humidity = mainObject.optString(HUMIDITY_WB);
            humidity = Conversions.roundDecimalString(humidity);
            weather.setHumidity(humidity);

            //pressure
            pressure = mainObject.optString(PRESSURE_WB);
            pressure = Conversions.roundDecimalString(pressure);
            weather.setPressure(pressure);

            //wind speed
            windSpeed = mainObject.optString(WIND_SPEED_WB);
            windSpeed = Conversions.roundDecimalString(windSpeed);
            weather.setWindSpeed(windSpeed);

            //wind gust
            windGust = mainObject.optString(WIND_GUST_DS);
            windGust = Conversions.roundDecimalString(windGust);
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
            visibility = Conversions.roundDecimalString(visibility);
            weather.setVisibility(visibility);

            //POP
            pop = mainObject.optString(POP_WB);
            pop = Conversions.roundDecimalString(pop);
            weather.setPop(pop);

            double popTotalDouble = mainObject.optDouble(PRECIPITATION_TOTAL_WB);
            popTotal = Conversions.roundDecimalDouble(popTotalDouble);
            weather.setPopTotal(popTotal);

            weatherArrayList.add(weather);

        }//for
        return weatherArrayList;
    }//parseWeatherBitForecast



    /**
     * This method is for parsing the current weather from Weather Unlocked
     *
     * @param jsonString : raw JSON response from the server
     * @return : Weather object
     * @throws JSONException
     */
    public static Weather parseWeatherUnlockedCurrent(String jsonString) throws JSONException {

        //dummy check for empty or null input
        if (jsonString == null || TextUtils.isEmpty(jsonString)) {
            return null;
        }//if

        String summary, temp, feelLike,  humidity, windSpeed, windDirection, cloudCoverage, icon,
                dewPoint, visibility;

        //create a Weather object
        Weather weather = new Weather();

        //set the provider
        weather.setProvider(Weather.PROVIDER_WU);

        //create an object from the string
        JSONObject mainObject = new JSONObject(jsonString);

        //set summary
        summary = mainObject.optString(SUMMARY_WU);
        weather.setSummary(summary);

        //temp
        temp = mainObject.optString(TEMP_WU);
        //round if it contains a decimal point
        temp = Conversions.roundDecimalString(temp);
        weather.setTemperature(temp);

        feelLike = mainObject.optString(FEEL_LIKE_WU);
        feelLike = Conversions.roundDecimalString(feelLike);
        weather.setTempFeel(feelLike);

        //dew
        dewPoint = mainObject.optString(DEW_WU);
        dewPoint = Conversions.roundDecimalString(dewPoint);
        weather.setDewPoint(dewPoint);

        //humidity
        humidity = mainObject.getString(HUMIDITY_WU);
        humidity = Conversions.roundDecimalString(humidity);
        weather.setHumidity(humidity);

        //wind
        windSpeed = mainObject.optString(WIND_SPEED_WU);
        windSpeed = Conversions.roundDecimalString(windSpeed);
        weather.setWindSpeed(windSpeed);


        double windDirectionDouble = mainObject.optDouble(WIND_DIRECTION_WU);
        windDirection = Conversions.degreeToDirection(windDirectionDouble);
        weather.setWindDirection(windDirection);

        //cloud cover
        cloudCoverage = mainObject.optString(CLOUD_COVER_WU);
        cloudCoverage = Conversions.roundDecimalString(cloudCoverage);
        weather.setCloudCoverage(cloudCoverage);

        //visibility
        visibility = mainObject.optString(VISIBILITY_WU);
        visibility = Conversions.roundDecimalString(visibility);
        weather.setVisibility(visibility);


        return weather;
    }//parseWeatherUnlockedCurrent

    public static ArrayList<Weather> parseWeatherUnlockedForecast(String jsonString) throws JSONException {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        String date, tempMin, tempMax, feelLikeMin, feelLikeMax, humidity, dewPoint, windSpeed,
                windGust, pop, popTotal;
        ArrayList<Weather> weatherArrayList = new ArrayList<>();
        Weather weather;
        //for formatting date
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM.dd", Locale.getDefault());

        //we create our dates for the next three days to get the data for
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

        JSONObject rootObject = new JSONObject(jsonString);
        JSONArray dataArray = rootObject.optJSONArray(DAYS_WU);

        /*we want data for the next three days (first object is for today, which we ignore for now
        So we get index 1, 2, and 3. This method has a lot of bugs, instead, we will check each
        entries against our dates and add the next three dates to the array
         */

        for (int i = 0; i < dataArray.length(); i++) {
            //first check to see if we have three objects in our array meaning we have colledcted all data
            if (weatherArrayList.size() == 3) {
                break;
            }

            weather = new Weather();
            //set the provider
            weather.setProvider(Weather.PROVIDER_WU);

            JSONObject mainObject = dataArray.getJSONObject(i);

            date = mainObject.optString(DATE_WU);

            /*WU does not provide epoch, so we need to create a date (for 0100 hrs) using the string
            and then we make a formatted date to be consistent with other.
             */
            SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                Date d = f.parse(date);
                long epoch = d.getTime();

                //check the date against our days
                if (epoch < day1StartMilli || epoch >= day4StartMilli ) {
                    continue;
                }
                
                weather.setEpoch(epoch);
                date = formatter.format(epoch);
                weather.setDate(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //temps
            tempMin = mainObject.optString(TEMP_MIN_WU);
            tempMin = Conversions.roundDecimalString(tempMin);

            double tempMinDouble = mainObject.optDouble(TEMP_MIN_WU);
            double tempMaxDouble = mainObject.optDouble(TEMP_MAX_WU);

            tempMax = mainObject.optString(TEMP_MAX_WU);
            tempMax = Conversions.roundDecimalString(tempMax);
            weather.setTempMin(tempMin);
            weather.setTempMax(tempMax);

            //humidity
            double humidityMin = mainObject.optDouble(HUMIDITY_MIN_WU);
            double humidityMax = mainObject.optDouble(HUMIDITY_MAX_WU);
            double humidityDouble = (humidityMin + humidityMax)/ 2;
            humidity = Conversions.roundDecimalString(String.valueOf(humidityDouble));
            weather.setHumidity(humidity);

            //wind speed
            windSpeed = mainObject.optString(WIND_WU);
            windSpeed = Conversions.roundDecimalString(windSpeed);
            weather.setWindSpeed(windSpeed);

            double windMaxDouble = mainObject.optDouble(WIND_WU);

            //wind gust
            windGust = mainObject.optString(GUST_WU);
            windGust = Conversions.roundDecimalString(windGust);
            weather.setWindGust(windGust);

            //dew
            double dewDouble = Conversions.calculateDewPointCel(tempMaxDouble, humidityMax);
            dewPoint = Conversions.roundDecimalDouble(dewDouble);
            weather.setDewPoint(dewPoint);

            //feel
            double feelMinDouble = Conversions.calculateFeelsLikeTemp(tempMinDouble, humidityDouble,windMaxDouble );
            feelLikeMin = Conversions.roundDecimalDouble(feelMinDouble);
            weather.setTempFeelMin(feelLikeMin);

            double feelMaxDouble = Conversions.calculateFeelsLikeTemp(tempMaxDouble, humidityDouble, windMaxDouble);
            feelLikeMax = Conversions.roundDecimalDouble(feelMaxDouble);
            weather.setTempFeelMax(feelLikeMax);

            //POP
            pop = mainObject.optString(POP_WU);
            pop = Conversions.roundDecimalString(pop);
            weather.setPop(pop);

            double popTotalDouble = mainObject.optDouble(PRECIP_TOTAL_WU);
            popTotal = Conversions.roundDecimalDouble(popTotalDouble);
            weather.setPopTotal(popTotal);

            weatherArrayList.add(weather);

        }//for
        return weatherArrayList;
    }//parseWeatherUnlockedForecast

}//class

package tech.khash.weathercompare.utilities;

import android.text.TextUtils;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import tech.khash.weathercompare.model.Constant;
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

    private static final String SNOW = "snow";
    private static final String RAIN = "rain";



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
    private static final String LOCATION_NAME_AW = "EnglishName";
    private static final String ICON_AW = "WeatherIcon";
    private static final String IS_DAY_AW = "IsDayTime";
    private static final String RAIN_AW = "Rain";
    private static final String SNOW_AW = "Snow";

    //today
    private static final String DAILY_FORECAST_AW = "DailyForecasts";
    private static final String DAILY_SUN_AW = "Sun";
    private static final String DAILY_EPOCH_RISE_AW = "EpochRise";
    private static final String DAILY_EPOCH_SET_AW = "EpochSet";
    private static final String POP_TYPE_AW = "PrecipitationType";


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
    private static final String CITY_NAME_WB = "city_name";
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
    private static final String PRECIPITATION_SNOW_WB = "snow";

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

    private static final String TIMEFRAMES_WU = "Timeframes";
    private static final String CLOUD_TOTAL_WU = "cloudtotal_pct";
    private static final String PRESSURE_WU = "slp_mb";

    //forecast
    private static final String DAYS_WU = "Days";
    private static final String DATE_WU = "date";
    private static final String TEMP_MIN_WU = "temp_min_c";
    private static final String TEMP_MAX_WU = "temp_max_c";
    private static final String POP_WU = "prob_precip_pct";
    private static final String PRECIP_TOTAL_WU = "precip_total_mm";
    private static final String TOTAL_RAIN_WU = "rain_total_mm";
    private static final String TOTAL_SNOW_WU = "snow_total_mm";
    private static final String WIND_WU = "windspd_max_kmh";
    private static final String GUST_WU = "windgst_max_kmh";
    private static final String HUMIDITY_MAX_WU = "humid_max_pct";
    private static final String HUMIDITY_MIN_WU = "humid_min_pct";

    /*
        --------------------------- GEOLOCATION -----------------------------------
     */
    private static final String DATE_GEO = "date";
    private static final String SUNRISE_GEO = "sunrise";
    private static final String SUNSET_GEO = "sunset";


    private static final long DAY_MILLI = 86400000;

    /*
    -------------------------------- Accu Weather ----------------------------------------
     */

    /**
     * It extracts AccuWeather's location code from the JSON response
     *
     * @param jsonString : JSON response from AW API
     * @return : HashMap<String, String> : containing the code and name for AW
     * @throws JSONException
     */
    public static HashMap<String, String> parseAccuLocationCode(String jsonString) throws JSONException {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        String locationCode, locationName;
        JSONObject responseObject = new JSONObject(jsonString);

        //by using optString, if such string does not exists, it returns an empty string.
        locationCode = responseObject.optString(LOCATION_KEY_AW);

        //location name to be used for when using user's location
        locationName = responseObject.optString(LOCATION_NAME_AW);

        //create Hash map to return results
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constant.AW_NAME, locationName);
        hashMap.put(Constant.AW_KEY, locationCode);

        Log.d(TAG, "location code: " + locationCode);

        return hashMap;
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
                windGust, visibility, cloudCoverage;

        int icon;
        boolean isDay;

        //create a Weather object
        Weather weather = new Weather();

        //create the main array
        JSONArray currentArray = new JSONArray(jsonString);

        //get the first element of the array containing all the current weather data
        JSONObject currentObject = currentArray.getJSONObject(0);

        //set the provider
        weather.setProvider(Weather.PROVIDER_AW);

        //summary
        summary = currentObject.optString(DESCRIPTION_AW);
        weather.setSummary(summary);

        //isDay
        isDay = currentObject.optBoolean(IS_DAY_AW);
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

        return weather;
    }//parseAccuWeatherCurrent

    /**
     * Parse today's weather for AW
     *
     * @param jsonString : response json string
     * @return : Weather object representing today
     */
    public static Weather parseAccuWeatherToday(String jsonString) throws JSONException {
        //dummy check for empty or null input
        if (jsonString == null || TextUtils.isEmpty(jsonString)) {
            return null;
        }//if

        String date, tempMin, tempMax, feelLikeMin, feelLikeMax, pop, popTotal, totalRain,
                totalSnow, windSpeed, windDirection, windGust, cloud, icon;

        int popType;

        //get the main object first
        JSONObject rootObject = new JSONObject(jsonString);
        JSONArray dailyArray = rootObject.optJSONArray(DAILY_FORECAST_AW);

        if (dailyArray == null || dailyArray.length() < 1) {
            return null;
        }
        Weather weather = new Weather();
        weather.setProvider(Weather.PROVIDER_AW);
        //for formatting date
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM.dd", Locale.getDefault());

        JSONObject mainObject = dailyArray.getJSONObject(0);

        //temp
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

        //figure out whether to use day/night section
        long nowEpoch = mainObject.optLong(FORECAST_EPOCH_AW) * 1000; //to convert to millis

        Date nowDate = new Date(nowEpoch);
        date = formatter.format(nowDate);
        weather.setDate(date);

        JSONObject sunObject = mainObject.optJSONObject(DAILY_SUN_AW);
        long riseEpoch = sunObject.optLong(DAILY_EPOCH_RISE_AW) * 1000;
        long setEpoch = sunObject.optLong(DAILY_EPOCH_SET_AW) * 1000;

        JSONObject todayObject;

        if (nowEpoch >= riseEpoch || nowEpoch <= setEpoch) {
            //day
            todayObject = mainObject.optJSONObject(FORECAST_DAY_AW);
        } else {
            //night
            todayObject = mainObject.optJSONObject(FORECAST_NIGHT_AW);
        }

        //pop
        pop = todayObject.optString(FORECAST_POP_AW);
        pop = Conversions.roundDecimalString(pop);
        weather.setPop(pop);

        //popTotal
        JSONObject popTotalObject = todayObject.optJSONObject(POP_TOTAL_AW);
        double popTotalDouble = popTotalObject.optDouble(METRIC_VALUE_AW);
        popTotal = Conversions.roundDecimalDouble(popTotalDouble);
        weather.setPopTotal(popTotal);

        //popType
        String popTypeString = todayObject.optString(POP_TYPE_AW);
        popType = getPopType(popTypeString);
        weather.setPopType(popType);

        //total Rain
        JSONObject totalRainObject = todayObject.optJSONObject(RAIN_AW);
        totalRain = totalRainObject.optString(METRIC_VALUE_AW);
        weather.setTotalRain(totalRain);

        //total snow
        JSONObject totalSnowObject = todayObject.optJSONObject(SNOW_AW);
        totalSnow = totalSnowObject.optString(METRIC_VALUE_AW);
        weather.setTotalSnow(totalSnow);

        //wind
        JSONObject windObject = todayObject.optJSONObject(WIND_AW);

        JSONObject speedObject = windObject.optJSONObject(SPEED_AW);
        double windSpeedDouble = speedObject.optDouble(METRIC_VALUE_AW);
        windSpeed = Conversions.roundDecimalDouble(windSpeedDouble);
        weather.setWindSpeed(windSpeed);

        JSONObject directionObject = windObject.optJSONObject(DIRECTION_AW);
        windDirection = directionObject.optString(WIND_DIRECTION_ENGLISH_AW);
        weather.setWindDirection(windDirection);

        //gust
        JSONObject gustObject = todayObject.getJSONObject(WIND_GUST_AW);
        JSONObject gustSpeedObject = gustObject.getJSONObject(SPEED_AW);
        double gustDouble = gustSpeedObject.optDouble(METRIC_VALUE_AW);
        windGust = Conversions.roundDecimalDouble(gustDouble);
        weather.setWindGust(windGust);

        //cloud
        int cloudInt = todayObject.optInt(CLOUD_COVER_AW);
        cloud = String.valueOf(cloudInt);
        weather.setCloudCoverage(cloud);

        //icon
        icon = getIcon(popType, Integer.valueOf(pop), cloudInt);
        weather.setIcon(icon);

        return weather;
    }//parseAccuWeatherToday

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
                summaryNight, popNight, cloudNight, popTotal, totalRain, totalSnow, windSpeed,
                windDirection, windGust, icon;

        int popType;
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
            if (epoch < day1StartMilli || epoch >= day4StartMilli) {
                continue;
            }
            weather.setDate(date);

            //set the provider
            weather.setProvider(Weather.PROVIDER_AW);

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

            //popType
            String popTypeString = dayObject.optString(POP_TYPE_AW);
            popType = getPopType(popTypeString);
            weather.setPopType(popType);

            //total POP
            JSONObject popTotalObject = dayObject.optJSONObject(POP_TOTAL_AW);
            double popTotalDouble = popTotalObject.optDouble(METRIC_VALUE_AW);
            popTotal = Conversions.roundDecimalDouble(popTotalDouble);
            weather.setPopTotal(popTotal);

            //total Rain
            JSONObject totalRainObject = dayObject.optJSONObject(RAIN_AW);
            totalRain = totalRainObject.optString(METRIC_VALUE_AW);
            weather.setTotalRain(totalRain);

            //total snow
            JSONObject totalSnowObject = dayObject.optJSONObject(SNOW_AW);
            totalSnow = totalSnowObject.optString(METRIC_VALUE_AW);
            weather.setTotalSnow(totalSnow);

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

            //icon
            icon = getIcon(popType, Integer.valueOf(popDay), Integer.valueOf(cloudDay));
            weather.setIcon(icon);


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

    public static Weather parseDarkSkyToday(String jsonString) throws JSONException {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        String date, summary, tempMin, tempMax, feelLikeMin, feelLikeMax, pressure, dewPoint, humidity,
                pop, windSpeed, windDirection, windGust, cloud, visibility, icon;
        int popType;
        Weather weather = new Weather();
        weather.setProvider(Weather.PROVIDER_DS);

        //for formatting date
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM.dd", Locale.getDefault());

        //we create our dates for today and tomorrow to get the corresponding data
        Calendar calendar = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long dayStartMillis = calendar.getTimeInMillis();

        //next day
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long dayEndMillis = calendar.getTimeInMillis();

        JSONObject rootObject = new JSONObject(jsonString);
        JSONObject dailyObject = rootObject.optJSONObject(DAILY_DS);
        JSONArray dataArray = dailyObject.optJSONArray(DATA_WB);


        //we go through the array and get the data for today only
        for (int i = 0; i < dataArray.length(); i++) {

            JSONObject mainObject = dataArray.getJSONObject(i);

            //date
            long epoch = (long) mainObject.optInt(TIME_DS);
            epoch *= 1000;

            if (epoch >= dayStartMillis && epoch < dayEndMillis) {
                //date
                Date dateObject = new Date(epoch);
                date = formatter.format(dateObject);
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
                cloud = Conversions.decimalToPercentage(cloudCoverDouble);
                weather.setCloudCoverage(cloud);

                //visibility
                visibility = mainObject.optString(VISIBILITY_DS);
                visibility = Conversions.roundDecimalString(visibility);
                weather.setVisibility(visibility);

                //POP
                double popDouble = mainObject.optDouble(POP_DS);
                pop = Conversions.decimalToPercentage(popDouble);
                weather.setPop(pop);

                //popType
                String popTypeString = mainObject.optString(PRECIP_TYPE_DS);
                popType = getPopType(popTypeString);
                weather.setPopType(popType);


                icon = getIcon(popType, ((int) (popDouble * 100)), ((int) (cloudCoverDouble * 100)));
                weather.setIcon(icon);

                //break out of loop, we already got the data
                break;
            }//if - today
        }//for

        return weather;
    }//parseDarkSkyToday


    public static ArrayList<Weather> parseDarkSkyForecast(String jsonString) throws JSONException {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        String date, tempMin, tempMax, feelLikeMin, feelLikeMax, summary, pressure, dewPoint, humidity,
                windSpeed, windGust, windDirection, cloudCoverage, pop, visibility, icon;
        int popType;
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
            if (epoch < day1StartMilli || epoch >= day4StartMilli) {
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

            //popType
            String popTypeString = mainObject.optString(PRECIP_TYPE_DS);
            popType = getPopType(popTypeString);
            weather.setPopType(popType);

            icon = getIcon(popType, ((int) (popDouble * 100)), ((int) (cloudCoverDouble * 100)));
            weather.setIcon(icon);

            weatherArrayList.add(weather);

        }//for
        return weatherArrayList;
    }//parseDarkSkyForecast




    /*
    -------------------------------- Weather Bit -----------------------------------------
     */

    public static Weather parseWeatherBitToday(String jsonString) throws JSONException {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        String cityName, date, summary, tempMin, tempMax, feelLikeMin, feelLikeMax, dewPoint, pressure,
                humidity, windSpeed, windGust, windDirection, cloudCoverage, pop, totalRain, totalSnow,
                visibility, icon;
        Weather weather = new Weather();
        //set the provider
        weather.setProvider(Weather.PROVIDER_WB);

        //for formatting date
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM.dd", Locale.getDefault());

        //we create our dates for today and tomorrow to get the corresponding data
        Calendar calendar = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long dayStartMillis = calendar.getTimeInMillis();

        //next day
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long dayEndMillis = calendar.getTimeInMillis();

        JSONObject rootObject = new JSONObject(jsonString);

        //city name
        cityName = rootObject.optString(CITY_NAME_WB);
        weather.setCityName(cityName);

        JSONArray dataArray = rootObject.optJSONArray(DATA_WB);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject mainObject = dataArray.getJSONObject(i);

            long epoch = mainObject.optLong(EPOCH_WB);
            epoch *= 1000;

            if (epoch >= dayStartMillis && epoch < dayEndMillis) {
                //date
                weather.setEpoch(epoch);

                Date dateObject = new Date(epoch);
                date = formatter.format(dateObject);
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

                //total rain
                double popRainDouble = mainObject.optDouble(PRECIPITATION_TOTAL_WB);
                totalRain = Conversions.roundDecimalDouble(popRainDouble);
                weather.setTotalRain(totalRain);

                //total snow
                double popSnowDouble = mainObject.optDouble(PRECIPITATION_SNOW_WB);
                popSnowDouble = popSnowDouble / 10; //convert to cm
                totalSnow = Conversions.roundDecimalDouble(popSnowDouble);
                weather.setTotalSnow(totalSnow);

                icon = getIcon(-1, Integer.valueOf(pop), Integer.valueOf(cloudCoverage));
                weather.setIcon(icon);

                //break out of loop
                break;
            }//if
        }//for
        return weather;
    }//parseWeatherBitToday


    public static ArrayList<Weather> parseWeatherBitForecast(String jsonString) throws JSONException {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        String cityName, date, summary, tempMin, tempMax, feelLikeMin, feelLikeMax, dewPoint, pressure,
                humidity, windSpeed, windGust, windDirection, cloudCoverage, pop, totalRain,
                totalSnow, visibility, icon;
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

        //city Name
        cityName = rootObject.optString(CITY_NAME_WB);

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
            //set city name
            weather.setCityName(cityName);

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
            if (epoch < day1StartMilli || epoch >= day4StartMilli) {
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

            //total rain
            double totalRainDouble = mainObject.optDouble(PRECIPITATION_TOTAL_WB);
            totalRain = Conversions.roundDecimalDouble(totalRainDouble);
            weather.setTotalRain(totalRain);

            //total snow
            double popSnowDouble = mainObject.optDouble(PRECIPITATION_SNOW_WB);
            popSnowDouble = popSnowDouble / 10; //convert to cm
            totalSnow = Conversions.roundDecimalDouble(popSnowDouble);
            weather.setTotalSnow(totalSnow);

            icon = getIcon(-1, Integer.valueOf(pop), Integer.valueOf(cloudCoverage));
            weather.setIcon(icon);

            weatherArrayList.add(weather);

        }//for
        return weatherArrayList;
    }//parseWeatherBitForecast


    public static Weather parseWeatherUnlockedToday(String jsonString) throws JSONException {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        String date, tempMin, tempMax, feelLikeMin, feelLikeMax, humidity, dewPoint, windSpeed,
                windGust, pop, popTotal, totalRain, totalSnow, visibility, pressure, cloud, icon;
        Weather weather = new Weather();
        weather.setProvider(Weather.PROVIDER_WU);

        //for formatting date
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM.dd", Locale.getDefault());

        //we create our dates for today and tomorrow to get the corresponding data
        Calendar calendar = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long dayStartMillis = calendar.getTimeInMillis();

        //next day
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long dayEndMillis = calendar.getTimeInMillis();

        JSONObject rootObject = new JSONObject(jsonString);
        JSONArray dataArray = rootObject.optJSONArray(DAYS_WU);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject mainObject = dataArray.getJSONObject(i);

            date = mainObject.optString(DATE_WU);

            /*WU does not provide epoch, so we need to create a date (for 0100 hrs) using the string
            and then we make a formatted date to be consistent with other.
             */
            SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                Date d = f.parse(date);
                long epoch = d.getTime();

                if (epoch >= dayStartMillis && epoch < dayEndMillis) {
                    //date
                    weather.setEpoch(epoch);
                    date = formatter.format(epoch);
                    weather.setDate(date);

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
                    double humidityDouble = (humidityMin + humidityMax) / 2;
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
                    double feelMinDouble = Conversions.calculateFeelsLikeTemp(tempMinDouble, humidityDouble, windMaxDouble);
                    feelLikeMin = Conversions.roundDecimalDouble(feelMinDouble);
                    weather.setTempFeelMin(feelLikeMin);

                    double feelMaxDouble = Conversions.calculateFeelsLikeTemp(tempMaxDouble, humidityDouble, windMaxDouble);
                    feelLikeMax = Conversions.roundDecimalDouble(feelMaxDouble);
                    weather.setTempFeelMax(feelLikeMax);

                    //POP
                    pop = mainObject.optString(POP_WU);
                    pop = Conversions.roundDecimalString(pop);
                    weather.setPop(pop);

                    //popTotal
                    double popTotalDouble = mainObject.optDouble(PRECIP_TOTAL_WU);
                    popTotal = Conversions.roundDecimalDouble(popTotalDouble);
                    weather.setPopTotal(popTotal);

                    //total rain
                    double rainTotalDouble = mainObject.optDouble(TOTAL_RAIN_WU);
                    totalRain = Conversions.roundDecimalDouble(rainTotalDouble);
                    weather.setTotalRain(totalRain);

                    //total snow
                    double snowTotalDouble = mainObject.optDouble(TOTAL_SNOW_WU);
                    snowTotalDouble *= snowTotalDouble * 10;//convert to cm
                    totalSnow = Conversions.roundDecimalDouble(snowTotalDouble);
                    weather.setTotalSnow(totalSnow);

                    //time frames
                    JSONArray timeFrameArray = mainObject.optJSONArray(TIMEFRAMES_WU);
                    ArrayList<Integer> cloudList = new ArrayList<>();
                    ArrayList<Double> pressureList = new ArrayList<>();
                    ArrayList<Double> visibilityList = new ArrayList<>();
                    for (int j = 0; j < timeFrameArray.length(); j++) {
                        JSONObject timeFrameObject = timeFrameArray.optJSONObject(j);

                        //cloud
                        Integer cloudInt = timeFrameObject.optInt(CLOUD_TOTAL_WU);
                        cloudList.add(cloudInt);

                        //pressure
                        Double pressureDouble = timeFrameObject.optDouble(PRESSURE_WU);
                        pressureList.add(pressureDouble);

                        //visibility
                        Double visDouble = timeFrameObject.optDouble(VISIBILITY_WU);
                        visibilityList.add(visDouble);
                    }//for

                    //cloud
                    int cloudArraySize = cloudList.size();
                    int cloudTotal = 0;
                    for (Integer c : cloudList) {
                        cloudTotal += c;
                    }
                    double cloudDouble = cloudTotal / (double) cloudArraySize;
                    cloud = Conversions.roundDecimalDouble(cloudDouble);
                    weather.setCloudCoverage(cloud);

                    //pressure
                    int pressureSize = pressureList.size();
                    double pressureTotal = 0;
                    for (Double p: pressureList) {
                        pressureTotal += p;
                    }
                    double pressureDouble = pressureTotal / pressureSize;
                    pressure = Conversions.roundDecimalDouble(pressureDouble);
                    weather.setPressure(pressure);

                    //visibility
                    int visSize = visibilityList.size();
                    double visTotal = 0;
                    for (Double v: visibilityList) {
                        visTotal += v;
                    }
                    double visDouble = visTotal / visSize;
                    visibility = Conversions.roundDecimalDouble(visDouble);
                    weather.setVisibility(visibility);

                    //icon
                    icon = getIcon(-1, Integer.valueOf(pop), Integer.valueOf(cloud));
                    weather.setIcon(icon);
                    break;

                }//if
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }//for
        return weather;
    }//parseWeatherUnlockedToday


    public static ArrayList<Weather> parseWeatherUnlockedForecast(String jsonString) throws JSONException {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        String date, tempMin, tempMax, feelLikeMin, feelLikeMax, humidity, dewPoint, windSpeed,
                windGust, pop, popTotal, cloud, icon, pressure, visibility, totalRain, totalSnow;
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
                if (epoch < day1StartMilli || epoch >= day4StartMilli) {
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
            double humidityDouble = (humidityMin + humidityMax) / 2;
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
            double feelMinDouble = Conversions.calculateFeelsLikeTemp(tempMinDouble, humidityDouble, windMaxDouble);
            feelLikeMin = Conversions.roundDecimalDouble(feelMinDouble);
            weather.setTempFeelMin(feelLikeMin);

            double feelMaxDouble = Conversions.calculateFeelsLikeTemp(tempMaxDouble, humidityDouble, windMaxDouble);
            feelLikeMax = Conversions.roundDecimalDouble(feelMaxDouble);
            weather.setTempFeelMax(feelLikeMax);

            //POP
            pop = mainObject.optString(POP_WU);
            pop = Conversions.roundDecimalString(pop);
            weather.setPop(pop);

            //popTotal
            double popTotalDouble = mainObject.optDouble(PRECIP_TOTAL_WU);
            popTotal = Conversions.roundDecimalDouble(popTotalDouble);
            weather.setPopTotal(popTotal);

            //total rain
            double rainTotalDouble = mainObject.optDouble(TOTAL_RAIN_WU);
            totalRain = Conversions.roundDecimalDouble(rainTotalDouble);
            weather.setTotalRain(totalRain);

            //total snow
            double snowTotalDouble = mainObject.optDouble(TOTAL_SNOW_WU);
            snowTotalDouble *= snowTotalDouble * 10;//convert to cm
            totalSnow = Conversions.roundDecimalDouble(snowTotalDouble);
            weather.setTotalSnow(totalSnow);

            //time frames
            JSONArray timeFrameArray = mainObject.optJSONArray(TIMEFRAMES_WU);
            ArrayList<Integer> cloudList = new ArrayList<>();
            ArrayList<Double> pressureList = new ArrayList<>();
            ArrayList<Double> visibilityList = new ArrayList<>();
            for (int j = 0; j < timeFrameArray.length(); j++) {
                JSONObject timeFrameObject = timeFrameArray.optJSONObject(j);

                //cloud
                Integer cloudInt = timeFrameObject.optInt(CLOUD_TOTAL_WU);
                cloudList.add(cloudInt);

                //pressure
                Double pressureDouble = timeFrameObject.optDouble(PRESSURE_WU);
                pressureList.add(pressureDouble);

                //visibility
                Double visDouble = timeFrameObject.optDouble(VISIBILITY_WU);
                visibilityList.add(visDouble);
            }//for

            //cloud
            int cloudArraySize = cloudList.size();
            int cloudTotal = 0;
            for (Integer c : cloudList) {
                cloudTotal += c;
            }
            double cloudDouble = cloudTotal / (double) cloudArraySize;
            cloud = Conversions.roundDecimalDouble(cloudDouble);
            weather.setCloudCoverage(cloud);

            //pressure
            int pressureSize = pressureList.size();
            double pressureTotal = 0;
            for (Double p: pressureList) {
                pressureTotal += p;
            }
            double pressureDouble = pressureTotal / pressureSize;
            pressure = Conversions.roundDecimalDouble(pressureDouble);
            weather.setPressure(pressure);

            //visibility
            int visSize = visibilityList.size();
            double visTotal = 0;
            for (Double v: visibilityList) {
                visTotal += v;
            }
            double visDouble = visTotal / visSize;
            visibility = Conversions.roundDecimalDouble(visDouble);
            weather.setVisibility(visibility);

            //icon
            icon = getIcon(-1, Integer.valueOf(pop), Integer.valueOf(cloud));
            weather.setIcon(icon);

            weatherArrayList.add(weather);

        }//for
        return weatherArrayList;
    }//parseWeatherUnlockedForecast


    /**
     * This parse data from IP Geolocation for sunset and sunrise and determines if it is daytime
     *
     * @param jsonString : JSON response
     * @return : boolean for day. true : it is daytime ; false : it is night time ; null : error
     * @throws JSONException
     */
    public static Boolean parseSunriseSunset(String jsonString) throws JSONException {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        Boolean response = null;
        //get the now time
        DateTime nowDT = new DateTime();
        long nowEpoch = nowDT.getMillis();

        //get the data
        JSONObject mainObject = new JSONObject(jsonString);
        String dateString = mainObject.optString(DATE_GEO);
        String sunriseString = mainObject.optString(SUNRISE_GEO);
        String sunsetString = mainObject.optString(SUNSET_GEO);

        //convert to Epoch using JodaTime
        sunriseString = dateString + " at " + sunriseString;
        sunsetString = dateString + " at " + sunsetString;

        //formatter
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd 'at' HH:mm");
        //default timezone
        formatter.withZone(DateTimeZone.getDefault());

        DateTime sunriseDT = formatter.parseDateTime(sunriseString);
        long sunriseEpoch = sunriseDT.getMillis();

        DateTime sunsetDT = formatter.parseDateTime(sunsetString);
        long sunsetEpoch = sunsetDT.getMillis();

        //If the now is before sunrise and after sunset, then it is night, otherwise it is day
        response = nowEpoch > sunriseEpoch && nowEpoch < sunsetEpoch;

        return response;
    }//parseSunriseSunset


    /**
     * Calculates the weather icon based on pop and cloud
     *
     * @param popType : popType int
     * @param pop     : pop int percentage (0-100)
     * @param cloud   : cloud cover int percentage (0-100)
     * @return : string representing the icon that should be used
     */
    private static String getIcon(int popType, int pop, int cloud) {

        if (pop < 30) {
            //not much precip
            if (cloud <= 10) {
                //clear
                return "01";
            } else if (cloud <= 40) {
                //partly cloudy
                return "02";
            } else if (cloud < 80) {
                //mostly cloudy
                return "03";
            } else {
                //overcast
                return "09";
            }
        } else {
            if (pop < 70) {
                switch (popType) {
                    case Weather.POP_TYPE_RAIN:
                    case Weather.POP_TYPE_NO_INPUT:
                    case Weather.POP_TYPE_RAIN_SNOW:
                    default:
                        return "04";
                    case Weather.POP_TYPE_SNOW:
                        return "05";
                }//switch - popType
            } else {
                switch (popType) {
                    case Weather.POP_TYPE_RAIN:
                    default:
                        return "06";
                    case Weather.POP_TYPE_SNOW:
                        return "07";
                    case Weather.POP_TYPE_RAIN_SNOW:
                        return "08";
                }//switch - popType
            }
        }//if else pop30
    }//getIcon

    private static int getPopType(String popTypeString) {
        popTypeString = popTypeString.toLowerCase();
        if (popTypeString.contains(RAIN) && popTypeString.contains(SNOW)) {
            return Weather.POP_TYPE_RAIN_SNOW;
        } else if (popTypeString.contains(RAIN)) {
            return Weather.POP_TYPE_RAIN;
        } else if (popTypeString.contains(SNOW)) {
            return Weather.POP_TYPE_SNOW;
        } else {
            return Weather.POP_TYPE_NO_INPUT;
        }
    }//getPopType


}//class

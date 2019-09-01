package tech.khash.weathercompare.model;

import android.net.Uri;

/**
 * Class for weather object.
 * <p>
 * This is the main Weather object corresponding to a specific time/place.
 * <p>
 * It stores all the data for weather.
 * <p>
 * Epoch : millis Epoch corresponding to the time that the weather forecast/current is representing
 * Date : the human readable version of the epoch. it usually just stores day, and date
 * TempMin and TempMax are used for forecast only
 * //TODO: finish all the variables
 * <p>
 * The Weather object is created using the JSON response from weather providers in the ParsJSON class
 */

public class Weather {

    //TODO: make the variables match the return type from API (float, etc) and then convert on returning

    private String summary, temperature, humidity, dewPoint, pressure, windSpeed, windDirection,
            windGust, visibility, cloudCoverage, pop, popType, iconUrl;

    //for forecast (AW now)
    private String date, tempMin, tempMax, summaryDay, popDay, cloudDay, summaryNight, popNight,
            cloudNight;

    //for WU
    private String popTotal;
    private long epoch;

    private Uri iconUri;

    private int provider;
    public static final int PROVIDER_OW = 1; //Open Weather
    public static final int PROVIDER_DS = 2; //Dark Sky
    public static final int PROVIDER_WB = 3; //Weather Bit
    public static final int PROVIDER_AC = 4; //Accu Weather
    public static final int PROVIDER_WU = 5; //Weather Unlocked
    //TODO: for testing now, think of a better way for future (it just takes too much space right now)
    private static final String OPEN_WEATHER = "O-Weather";
    private static final String DARK_SKY = "DarkSky";
    private static final String WEATHER_BIT = "W-Bit";
    private static final String ACCU_WEATHER = "A-Weather";
    private static final String WEATHER_UNLOCKED = "W-Unlocked";


    //default public constructor
    public Weather() {
    }//Weather


    /*
        ------------------------ SETTER METHODS -----------------------------------------
     */

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }//setEpoch

    public void setProvider(int provider) {
        this.provider = provider;
    }//setProvider

    public void setDate(String date) {
        this.date = date;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }//setSummary

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }//setTemperature

    public void setTempMin(String tempMin) {
        this.tempMin = tempMin;
    }//setTempMin

    public void setTempMax(String tempMax) {
        this.tempMax = tempMax;
    }//setTempMax

    public void setDewPoint(String dewPoint) {
        this.dewPoint = dewPoint;
    }//setDewPoint

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }//setPressure

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }//setHumidity

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }//setWindSpeed

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }//setWindDirection

    public void setWindGust(String windGust) {
        this.windGust = windGust;
    }//setWindGust

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }//setVisibility

    public void setCloudCoverage(String cloudCoverage) {
        this.cloudCoverage = cloudCoverage;
    }//setCloudCoverage

    public void setPop(String pop) {
        this.pop = pop;
    }//setPop

    public void setPopTotal(String popTotal) {
        this.popTotal = popTotal;
    }//setPopTotal

    public void setPopType(String popType) {
        this.popType = popType;
    }//setPopType

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }//setIconUrl

    public void setPopDay(String popDay) {
        this.popDay = popDay;
    }

    public void setSummaryDay(String summaryDay) {
        this.summaryDay = summaryDay;
    }

    public void setCloudDay(String cloudDay) {
        this.cloudDay = cloudDay;
    }

    public void setSummaryNight(String summaryNight) {
        this.summaryNight = summaryNight;
    }

    public void setCloudNight(String cloudNight) {
        this.cloudNight = cloudNight;
    }

    public void setPopNight(String popNight) {
        this.popNight = popNight;
    }

    /*
        ------------------------ GETTER METHODS -----------------------------------------
     */

    public long getEpoch() {
        return epoch;
    }//getEpoch

    public int getProvider() {
        return provider;
    }//getProvider

    public String getProviderString() {
        switch (provider) {
            case PROVIDER_OW:
                return OPEN_WEATHER;
            case PROVIDER_DS:
                return DARK_SKY;
            case PROVIDER_WB:
                return WEATHER_BIT;
            case PROVIDER_AC:
                return ACCU_WEATHER;
            case PROVIDER_WU:
                return WEATHER_UNLOCKED;
            default:
                return "";
        }//switch
    }//getProviderString

    public String getDate() {
        return date;
    }//getDate

    public String getSummary() {
        if (summary == null) {
            return "";
        }
        return summary;
    }//getSummary

    public String getTemperature() {
        if (temperature == null) {
            return "";
        }
        return temperature;
    }//getTemperature

    public String getTempMax() {
        if (tempMax == null) {
            return "";
        }
        return tempMax;
    }//getTempMax

    public String getTempMin() {
        if (tempMin == null) {
            return "";
        }
        return tempMin;
    }//getTempMin

    public String getDewPoint() {
        if (dewPoint == null) {
            return "";
        }
        return dewPoint;
    }//getDewPoint

    public String getPressure() {
        if (pressure == null) {
            return "";
        }
        return pressure;
    }//getPressure

    public String getHumidity() {
        if (humidity == null) {
            return "";
        }
        return humidity;
    }//getHumidity

    public String getWindSpeed() {
        if (windSpeed == null) {
            return "";
        }
        return windSpeed;
    }//getWindSpeed

    public String getWindDirection() {
        if (windDirection == null) {
            return "";
        }
        return windDirection;
    }//getWindDirection

    public String getWindGust() {
        if (windGust == null) {
            return "";
        }
        return windGust;
    }//getWindGust

    public String getVisibility() {
        if (visibility == null) {
            return "";
        }
        return visibility;
    }//getVisibility

    public String getCloudCoverage() {
        if (cloudCoverage == null) {
            return "";
        }
        return cloudCoverage;
    }//getCloudCoverage

    public String getPop() {
        if (pop == null) {
            return "";
        }
        return pop;
    }//getPop

    public String getPopTotal() {
        if (popTotal == null) {
            return "";
        } else {
            return popTotal;
        }
    }//getPopTotal

    public String getPopType() {
        if (popType == null) {
            return "";
        }
        return popType;
    }//getPopType

    public String getSummaryDay() {
        return summaryDay;
    }

    public String getPopDay() {
        return popDay;
    }

    public String getCloudDay() {
        return cloudDay;
    }

    public String getSummaryNight() {
        return summaryNight;
    }

    public String getPopNight() {
        return popNight;
    }

    public String getCloudNight() {
        return cloudNight;
    }

    public String getIconUrl() {
        if (iconUrl == null) {
            return "";
        }
        return iconUrl;
    }//getIconUrl

    public Uri getIconUri() {
        if (hasIcon()) {
            return Uri.parse(iconUrl);
        } else {
            return null;
        }
    }//getIconUri

    /**
     * Helper method to see if there is any icon associated with the weather object
     */
    public boolean hasIcon() {
        if (iconUrl == null) {
            return false;
        } else {
            return true;
        }
    }//hasIcon
}//Weather

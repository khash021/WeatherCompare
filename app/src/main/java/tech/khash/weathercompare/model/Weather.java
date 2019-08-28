package tech.khash.weathercompare.model;

import android.net.Uri;

/**
 * Class for weather object.
 *
 * This will be used for current weather conditions
 */

public class Weather {

    //TODO: make the variables match the return type from API (float, etc) and then convert on returning

    private String summary, temperature, humidity, dewPoint, pressure, windSpeed, windDirection,
            windGust, visibility, cloudCoverage, pop, popType, iconUrl;

    //for forecast (AW now)
    private String date, tempMin, TempMax, summaryDay, popDay, cloudDay, summaryNight, popNight, cloudNight;
    private long epoch;

    //for forecast (DS)

    private Uri iconUri;

    //default public constructor
    public Weather() {
    }//Weather

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

    /**
     * Setter methods
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }//setSummary

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }//setTemperature

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

    public void setPopType(String popType) {
        this.popType = popType;
    }//setPopType

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }//setIconUrl

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }//setEpoch

    /**
     * Getter methods
     */
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
        //Open weather data is a float, here we convert to float, and then return the rounded version
        if (temperature.contains(".")) {
            float tempFloat = Float.valueOf(temperature);
            int rounded = Math.round(tempFloat);
            String output = String.valueOf(rounded);
            return output;
        }

        return temperature;
    }//getTemperature

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

    public String getPopType() {
        if (popType == null) {
            return "";
        }
        return popType;
    }//getPopType

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

    public long getEpoch() {
        return epoch;
    }//getEpoch


/*
---------------------------------------------- AW FORECAST ---------------------------------------
 */
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTempMin() {
        return tempMin;
    }

    public void setTempMin(String tempMin) {
        this.tempMin = tempMin;
    }

    public String getTempMax() {
        return TempMax;
    }

    public void setTempMax(String tempMax) {
        TempMax = tempMax;
    }

    public String getSummaryDay() {
        return summaryDay;
    }

    public void setSummaryDay(String summaryDay) {
        this.summaryDay = summaryDay;
    }

    public String getPopDay() {
        return popDay;
    }

    public void setPopDay(String popDay) {
        this.popDay = popDay;
    }

    public String getCloudDay() {
        return cloudDay;
    }

    public void setCloudDay(String cloudDay) {
        this.cloudDay = cloudDay;
    }

    public String getSummaryNight() {
        return summaryNight;
    }

    public void setSummaryNight(String summaryNight) {
        this.summaryNight = summaryNight;
    }

    public String getPopNight() {
        return popNight;
    }

    public void setPopNight(String popNight) {
        this.popNight = popNight;
    }

    public String getCloudNight() {
        return cloudNight;
    }

    public void setCloudNight(String cloudNight) {
        this.cloudNight = cloudNight;
    }
}

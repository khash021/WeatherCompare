package app.khash.weathertry.model;

import android.net.Uri;

/**
 * Class for weather object.
 *
 * This will be used for current weather conditions
 */

public class Weather {

    private String summary, temperature, dewPoint, pressure, windSpeed, windDirection, windGust,
            visibility, cloudCoverage, pop, popType, iconUrl;
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
}

package tech.khash.weathercompare.model;

import android.text.TextUtils;

import tech.khash.weathercompare.utilities.Conversions;

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

    private String summary, date, temperature, tempFeel, tempMin, tempFeelMin, tempMax, tempFeelMax,
            humidity, dewPoint, pressure, windSpeed, windDirection, windGust, visibility,
            cloudCoverage, pop, popTotal, totalRain, totalSnow, icon,
            summaryDay, popDay, cloudDay, summaryNight, popNight, cloudNight, cityName;

    private boolean isDay;

    private Integer popType;

    private long epoch;

    //providers
    private int provider;
    public static final int PROVIDER_OW = 1; //Open Weather
    public static final int PROVIDER_DS = 2; //Dark Sky
    public static final int PROVIDER_WB = 3; //Weather Bit
    public static final int PROVIDER_AW = 4; //Accu Weather
    public static final int PROVIDER_WU = 5; //Weather Unlocked
    //TODO: for testing now, think of a better way for future (it just takes too much space right now)
    private static final String OPEN_WEATHER = "O-Weather";
    private static final String DARK_SKY = "DarkSky";
    private static final String WEATHER_BIT = "W-Bit";
    private static final String ACCU_WEATHER = "A-Weather";
    private static final String WEATHER_UNLOCKED = "W-Unlocked";

    //popType
    public static final int POP_TYPE_NO_INPUT = 0;
    public static final int POP_TYPE_RAIN = 1;
    public static final int POP_TYPE_SNOW = 2;
    public static final int POP_TYPE_RAIN_SNOW = 3;


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

    public void setTempFeel(String tempFeel) {
        this.tempFeel = tempFeel;
    }//setTempFeel

    public void setTempMin(String tempMin) {
        this.tempMin = tempMin;
    }//setTempMin

    public void setTempFeelMin(String tempFeelMin) {
        this.tempFeelMin = tempFeelMin;
    }//setTempFeelMin

    public void setTempMax(String tempMax) {
        this.tempMax = tempMax;
    }//setTempMax

    public void setTempFeelMax(String tempFeelMax) {
        this.tempFeelMax = tempFeelMax;
    }//setTempFeelMax

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

    public void setTotalRain(String totalRain) {
        this.totalRain = totalRain;
    }//setTotalRain

    public void setTotalSnow(String totalSnow) {
        this.totalSnow = totalSnow;
    }//setTotalSnow

    public void setPopType(int popType) {
        this.popType = popType;
    }//setPopType

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

    public void setIsDay(boolean isDay) {
        this.isDay = isDay;
    }//setIsDay

    public void setIcon(String icon) {
        this.icon = icon;
    }//setIcon

    public void setCityName(String cityName) {
        this.cityName = cityName;
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
            case PROVIDER_AW:
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

    public String getTempFeel() {
        if (tempFeel == null) {
            return "";
        } else {
            return tempFeel;
        }
    }//getTempFeel

    public String getTempMax() {
        if (tempMax == null) {
            return "";
        }
        return tempMax;
    }//getTempMax

    public String getTempMaxImperial() {
        if (tempMax == null) {
            return "";
        }
        return Conversions.celToFarString(tempMax);
    }//getTempMax


    public String getTempMin() {
        if (tempMin == null) {
            return "";
        }
        return tempMin;
    }//getTempMin

    public String getTempMinImperial() {
        if (tempMin == null) {
            return "";
        }
        return Conversions.celToFarString(tempMin);
    }//getTempMin

    public String getTempFeelMin() {
        if (tempFeelMin == null) {
            return "";
        }
        return tempFeelMin;
    }//getTempFeelMin

    public String getTempFeelMinImperial() {
        if (tempFeelMin == null) {
            return "";
        }
        return Conversions.celToFarString(tempFeelMin);
    }//getTempFeelMin

    public String getTempFeelMax() {
        if (tempFeelMax == null) {
            return "";
        }
        return tempFeelMax;
    }//getTempFeelMax

    public String getTempFeelMaxImperial() {
        if (tempFeelMax == null) {
            return "";
        }
        return Conversions.celToFarString(tempFeelMax);
    }//getTempFeelMax

    public String getDewPoint() {
        if (dewPoint == null) {
            return "";
        }
        return dewPoint;
    }//getDewPoint

    public String getDewPointImperial() {
        if (dewPoint == null) {
            return "";
        }
        return Conversions.celToFarString(dewPoint);
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

    public String getWindSpeedImperial() {
        if (windSpeed == null) {
            return "";
        }
        return Conversions.kmhToMphString(windSpeed);
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

    public String getWindGustImperial() {
        if (windGust == null) {
            return "";
        }
        return Conversions.kmhToMphString(windGust);
    }//getWindGust

    public String getVisibility() {
        if (visibility == null) {
            return "";
        }
        return visibility;
    }//getVisibility

    public String getVisibilityImperial() {
        if (visibility == null) {
            return "";
        }
        return Conversions.kmToMileString(visibility);
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
        }
        return popTotal;
    }//getPopTotal

    public String getPopTotalImperial() {
        if (popTotal == null) {
            return "";
        }
        return Conversions.mmToInchString(popTotal);
    }//getPopTotal

    public String getTotalRain() {
        if (totalRain == null) {
            return "";
        }
        return totalRain;
    }//getTotalRain

    public String getTotalRainImperial() {
        if (totalRain == null) {
            return "";
        }
        return Conversions.mmToInchString(totalRain);
    }//getTotalRain

    public String getTotalSnow() {
        if (totalSnow == null) {
            return "";
        }
        return totalSnow;
    }//getTotalSnow

    public String getTotalSnowImperial() {
        if (totalSnow == null) {
            return "";
        }
        return Conversions.cmToInchString(totalSnow);

    }//getTotalSnow

    public int getPopType() {
        if (popType != null) {
            return popType;
        }
        return POP_TYPE_NO_INPUT;
    }//getPopType

    public String getPopTypeString() {
        if (popType == null) {
            return "";
        }
        switch (popType) {
            case POP_TYPE_RAIN:
                return "Rain";
            case POP_TYPE_SNOW:
                return "Snow";
            case POP_TYPE_RAIN_SNOW:
                return "Rain/Snow";
            case POP_TYPE_NO_INPUT:
            default:
                return "";
        }//switch
    }//getPopTypeString

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

    public String getIcon() {
        return icon;
    }//getIcon

    public boolean getIsDay() {
        return isDay;
    }//getIsDay

    public String getCityName() {
        if (TextUtils.isEmpty(cityName)) {
            return "";
        }
        return cityName;
    }//getCityName
}//Weather

package tech.khash.weathercompare.model;

/**
 * Object representing each forecast to be used for showing the list of forecasts
 */

//TODO: could delete this?

public class Forecast {

    private String day, summary, min, max, pop, popTyep, pressure, humidity, windSpeed,
            windDirection, cloudCoverage, rain, snow;

    public Forecast (){}

    public Forecast (String summary, String min, String max, String pop, String precipType, String day) {
        this.summary = summary;
        this.min = min;
        this.max = max;
        this.pop = pop;
        this.popTyep = precipType;
        this.day = day;
    }//constructor


    /*
        ------------------------ SETTER METHODS -----------------------------------------
     */

    public void setDay(String day) {
        this.day = day;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public void setPop(String pop) {
        this.pop = pop;
    }

    public void setPopTyep(String popTyep) {
        this.popTyep = popTyep;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public void setCloudCoverage(String cloudCoverage) {
        this.cloudCoverage = cloudCoverage;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }

    public void setSnow(String snow) {
        this.snow = snow;
    }
    /*
        ------------------------ GETTER METHODS -----------------------------------------
     */


    public String getDay() {
        return day;
    }

    public String getSummary() {
        return summary;
    }

    public String getMin() {
        return min;
    }

    public String getMax() {
        return max;
    }

    public String getPop() {
        return pop;
    }

    public String getPopTyep() {
        return popTyep;
    }

    public String getPressure() {
        return pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public String getCloudCoverage() {
        return cloudCoverage;
    }

    public String getRain() {
        return rain;
    }

    public String getSnow() {
        return snow;
    }
}//class

package app.khash.weathertry.model;

/**
 * Object representing each forecast to be used for showing the list of forecasts
 */

//TODO: add some sunrise, sunset, maybe even pressure, and wind stuff

public class Forecast {

    private String day, summary, min, max, pop, precipType;

    public Forecast (String summary, String min, String max, String pop, String precipType, String day) {
        this.summary = summary;
        this.min = min;
        this.max = max;
        this.pop = pop;
        this.precipType = precipType;
        this.day = day;
    }//constructor

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

    public String getPrecipType() {
        return precipType;
    }
}//class

package tech.khash.weathercompare.utilities;

import android.text.TextUtils;
import android.util.Log;


/**
 * Created by Khashayar "Khash" Mortazavi
 * <p>
 * This class is responsible for conversions, such as unit conversions, or rounding numbers,
 * capitalizing letters, etc
 */


public class Conversions {

    private final static String TAG = Conversions.class.getSimpleName();

    /**
     * This converts the input string to float, and then if there are any decimal poiints
     * it rounds it up or down, and return the result in String again
     */
    public static String roundDecimalString(String s) {
        if (!s.contains(".")) {
            return s;
        }
        float f = Float.valueOf(s);
        int rounded = Math.round(f);
        String output = String.valueOf(rounded);
        return output;
    }//roundDecimalString

    /**
     * This rounds the double with no decimal points
     * @param d : input double
     * @return : output rounded double
     */
    public static String roundDecimalDouble (double d) {
        d = Math.round(d);
        return roundDecimalString(String.valueOf(d));
    }//roundDecimalDouble

    public static String decimalToPercentage(double dec) {
        double perc = dec * 100;
        String output = String.format("%.0f", perc);
        return output;
    }//decimalToPercentage

    public static String capitalizeFirst(String s) {
        //check to makes sure it is not empty
        if (s == null || TextUtils.isEmpty(s)) {
            return "";
        }
        String output = s.substring(0, 1).toUpperCase() + s.substring(1);
        return output;
    }//capitalizeFirst

    public static String removeDemicalFloat(float f) {
        String output = String.format("%.0f", f);
        return output;
    }//removeDecimal




    /*
       -------------------------  Unit conversions ---------------------------------------
     */

    public static String celToFarString (String cel) {
        if (TextUtils.isEmpty(cel)) {
            return "";
        }
        float celF = Float.valueOf(cel);
        float farF = (celF * (9f/5f)) + 32f;
        String far = String.format("%.0f", farF);
        return far;
    }//celToFarString

    public static String mmToInchString (String mm) {
        if (TextUtils.isEmpty(mm)) {
            return "";
        }
        float mmF = Float.valueOf(mm);
        float inF = mmF * 0.0393701f;
        String inS =  String.format("%.1f", inF);
        return inS;
    }//mmToInch

    public static String cmToInchString (String cm) {
        if (TextUtils.isEmpty(cm)) {
            return "";
        }
        float cmF = Float.valueOf(cm);
        float inF = cmF * 0.393701f;
        String inS =  String.format("%.1f", inF);
        return inS;
    }//mmToInch

    public static String kmToMileString (String km) {
        if (TextUtils.isEmpty(km)) {
            return "";
        }
        float kmF = Float.valueOf(km);
        float mileF = kmF / 1.609f;
        String mile = String.format("%.0f", mileF);
        return mile;
    }//kmToMileString

    public static String kmhToMphString (String kmh) {
        if (TextUtils.isEmpty(kmh)) {
            return "";
        }
        float kmhF = Float.valueOf(kmh);
        float mphF = kmhF / 1.609f;
        String mph = String.format("%.0f", mphF);
        return mph;
    }//kmhToMphString


    public static String meterToKmhString(double meter) {
        Log.d(TAG, "m/s is: " + meter);
        double kmh = meter * 3.6;
        //TODO: use locale
        String output = String.format("%.1f", kmh);
        return output;
    }//meterToKmh

    /*
       -------------------------  Other conversion/calculations ----------------------------------
     */

    /**
     * This methods calculates dew point. All in Cel
     * @param T : given ambient temperature in Cel
     * @param H : given relative humidity in % (0-100)
     * @return : calculated dew point in Cel
     */
    public static double calculateDewPointCel (double T, double H) {
        //change humidity to decimal
        H = H/100d;

        //constants
        double c1 = 112d;
        double c2 = 0.9d;
        double c3 = 0.1d;
        double c4 = -112d;

        double dewCel = (Math.pow(H, (1/8d))) * (c1 + (c2 * T)) + (c3 * T) + c4;

        return dewCel;
    }//calculateDewPointCel

    /**
     * This method calculates the feel like temp, either by Heat Index, or Wind chill factor
     * depending on the temperature.
     *
     * For now, we calculate Heat Index for values above 20 Cel, and Wind Chill for below 10 Cel
     * @param T : ambient temp in Cel
     * @param H : relative humidity in %
     * @param V : wind speed in kmh
     * @return
     */
    public static double calculateFeelsLikeTemp(double T, double H, double V) {
        //figure out whether we need Heat index, or windchill based on temp
        if (T <= 10d) {
            //we do wind chill
            return windChillCel(T, V);
        } else if (T >= 20d) {
            //we do heat index
            return heatIndexCel(T, H);
        }
        //otherwise we just return the same since there is no point
        return T;
    }//calculateFeelsLikeTemp


    /**
     * This method calculates the Heat index in Cel
     * @param T : given ambient temperature in Cel
     * @param H : given relative humidity in % (0-100)
     * @return : calculated heat index in cel
     */
    public static double heatIndexCel(double T, double H) {

        //our constants
        double c1 = -8.78469475556d;
        double c2 = 1.61139411d;
        double c3 = 2.33854883889d;
        double c4 = -0.14611605d;
        double c5 = -0.012308094d;
        double c6 = -0.0164248277778d;
        double c7 = 0.002211732d;
        double c8 = 0.00072546d;
        double c9 = -0.000003582d;

        //calculate heat index
        double hi = c1 + (c2 * T) + (c3 * H) + (c4 * T * H) + (c5 * Math.pow(T, 2d)) +
                (c6 * Math.pow(H, 2d)) + (c7 * Math.pow(T, 2d) * H) + (c8 * T * Math.pow(H, 2d)) +
                (c9 * Math.pow(T, 2d) * Math.pow(H, 2d));

        return hi;
    }//calculateHeatIndexCel

    /**
     * This method calculates the windchill feel temperature in Cel
     * @param T : given ambient temperature in Cel
     * @param V : given wind speed (at 10 meters) in kmh
     * @return : calculated windchill feel temperature in Cel
     */
    public static double windChillCel(double T, double V) {
        double calculatedWindChillCel;

        //constants
        double c1 = 13.12d;
        double c2 = 0.6215d;
        double c3 = -11.37d;
        double c4 = 0.3965d;

        calculatedWindChillCel = c1 + (c2 * T) + (c3 * Math.pow(V, 0.16d)) +
                (c4 * T * Math.pow(V, 0.16d));

        return calculatedWindChillCel;
    }//calculateWindChillCel

    public static String degreeToDirection(double degree) {
        Log.d(TAG, "Degree is: " + degree);

        if (degree >= 348.75 || degree <= 11.25) {
            return "N";
        } else if (degree > 11.25 && degree <= 33.75) {
            return "NNE";
        } else if (degree > 33.75 && degree <= 56.25) {
            return "NE";
        } else if (degree > 56.25 && degree <= 78.75) {
            return "ENE";
        } else if (degree > 78.75 && degree <= 101.25) {
            return "E";
        } else if (degree > 101.25 && degree <= 123.75) {
            return "ESE";
        } else if (degree > 123.75 && degree <= 146.25) {
            return "SE";
        } else if (degree > 146.25 && degree <= 168.75) {
            return "SSE";
        } else if (degree > 168.75 && degree <= 191.25) {
            return "S";
        } else if (degree > 191.25 && degree <= 213.75) {
            return "SSW";
        } else if (degree > 213.75 && degree <= 236.25) {
            return "SW";
        } else if (degree > 236.25 && degree <= 258.75) {
            return "WSW";
        } else if (degree > 258.75 && degree <= 281.25) {
            return "W";
        } else if (degree > 281.25 && degree <= 303.75) {
            return "WNW";
        } else if (degree > 303.75 && degree <= 326.25) {
            return "NW";
        } else {
            return "NNW";
        }
    }//degreeToDirection

}//class

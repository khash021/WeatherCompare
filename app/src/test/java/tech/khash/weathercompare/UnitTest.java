package tech.khash.weathercompare;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;


@RunWith(JUnit4.class)
public class UnitTest {

    /**
     * Sets up the environment for testing.
     */
    @Before
    public void setup() {

    }//setup

    /**
     * TESTS
     */

//    @Test
//    public void stringFormat() {
//        String template = "My name is Khash and I am 32 years old";
//        String format = "My name is %s and I am %s years old";
//        String name = "Khash";
//        int ageInt = 32;
//        String age = "32";
//        String result = String.format(format, name, age);
//        assertEquals(template, result);
//    }//stringFormat

//    @Test
//    public void roundFloat() {
//        float f1 = 25.5f;
//        float f2 = 23.3f;
//        float f3 = 23.8f;
//
//        int i1 = Math.round(f1);
//        int i2 = Math.round(f2);
//        int i3 = Math.round(f3);
//
////        assertEquals(26, i1);
////        assertEquals(23, i2);
//        assertEquals(24, i3);
//    }

//    @Test
//    public void stringFormat() {
//        String lat = "23.6";
//        String lng = "-123.5";
//        String LOCATION_WB = "lat=%s&lon=%s";
//        String latLngString = String.format(LOCATION_WB, lat, lng);
//        assertEquals(latLngString, "lat=23.6&lon=-123.5");
//    }


//    @Test
//    public void celToFar() {
//        float far = 48.79f;
//        float cel = 9.33f;
//
//        float calculated = (cel * (9f/5f)) + 32f;
//
//        String s = String.format("%.0f", calculated);
//        String actual = "49";
//
//        assertEquals(actual, s);
//    }//celToFar

//    @Test
//    public void kmToMile() {
//        String km = "23";
//        String check = "14";
//
//        float kmF = Float.valueOf(km);
//        float mileF = kmF / 1.609f;
//        String mile = String.format("%.0f", mileF);
//
//        assertEquals(check, mile);
//    }

    @Test
    public void hashMap() {
        final String NAME = "name";
        final String CODE = "code";

        String nameCheck = "Vancouver";
        String codeCheck = "234523";

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(NAME, "Vancouver");
        hashMap.put(CODE, "234523");

        assertEquals(nameCheck, hashMap.get(NAME));
        assertEquals(codeCheck, hashMap.get(CODE));
    }

//    @Test
//    public void roundOneDecimal() {
//        double d = 34.635d;
//        double dr = 34.6d;
//        float f = (float) d;
//        String s = "34.6";
//
//        String calculated = String.format("%.1f", f);
//        assertEquals(s, calculated);
//    }

//    @Test
//    public void heatIndex() {
//        double R = 75d;
//        double T = 28d;
//
//        double check = 31.36d;
//
//        double heatIndexTempCel;
//        //our constants
//        double c1 = -8.78469475556d;
//        double c2 = 1.61139411d;
//        double c3 = 2.33854883889d;
//        double c4 = -0.14611605d;
//        double c5 = -0.012308094d;
//        double c6 = -0.0164248277778d;
//        double c7 = 0.002211732d;
//        double c8 = 0.00072546d;
//        double c9 = -0.000003582d;
//
//
//        //calculate heat index (in far)
//        double hi = c1 + (c2 * T) + (c3 * R) + (c4 * T * R) + (c5 * Math.pow(T, 2d)) +
//                (c6 * Math.pow(R, 2d)) + (c7 * Math.pow(T, 2d) * R) + (c8 * T * Math.pow(R, 2d)) +
//                (c9 * Math.pow(T, 2d) * Math.pow(R, 2d));
//
//        assertEquals(hi, check, 0.01);
//    }//heatIndex

//    @Test
//    public void windChill() {
////        double T = 3d;//cel
//        double T = 30d;//far
//
//        double V = 15d;
//
//        double checkC = 0.91d;
//        double checkF = 19.03d;
//
//        //constants - cel
////        double c1 = 13.12d;
////        double c2 = 0.6215d;
////        double c3 = -11.37d;
////        double c4 = 0.3965d;
//
//        //far
//        double c1 = 35.74d;
//        double c2 = 0.6215d;
//        double c3 = -35.75d;
//        double c4 = 0.4274d;
//
//        double calculatedWindChillCel = c1 + (c2 * T) + (c3 * Math.pow(V, 0.16d)) +
//                (c4 * T * Math.pow(V, 0.16d));
//
//        assertEquals(calculatedWindChillCel, checkF, 0.01);
//
//    }//windChill

//    @Test
//    public void dewPointCel() {
//        double T = 20.8d;
//        double H = 71d;
//
//        double check = 15.39d;
//
//        double c1 = 112d;
//        double c2 = 0.9d;
//        double c3 = 0.1d;
//        double c4 = -112d;
//
//        double h = H / 100d;
//
//        double dew = (Math.pow(h, (1/8d))) * (c1 + (c2 * T)) + (c3 * T) + c4;
//
//        assertEquals(dew, check, 0.1d);
//
//
//    }//dewPointCel



//    @Test
//    public void addition_isCorrect() {
//        assertEquals(4, 2 + 2);
//    }//addition_isCorrect
}
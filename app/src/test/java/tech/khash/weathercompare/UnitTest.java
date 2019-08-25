package tech.khash.weathercompare;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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

    @Test
    public void roundFloat() {
        float f1 = 25.5f;
        float f2 = 23.3f;
        float f3 = 23.8f;

        int i1 = Math.round(f1);
        int i2 = Math.round(f2);
        int i3 = Math.round(f3);

//        assertEquals(26, i1);
//        assertEquals(23, i2);
        assertEquals(24, i3);
    }

//    @Test
//    public void addition_isCorrect() {
//        assertEquals(4, 2 + 2);
//    }//addition_isCorrect
}
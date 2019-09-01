package tech.khash.weathercompare.fragment;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import tech.khash.weathercompare.model.Weather;

/**
 * Created by Khashayar "Khash" Mortazavi
 * <p>
 * Fragment page adapter to be used in forecast activity for showing different days in fragments
 */

public class PagerFragmentAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = PagerFragmentAdapter.class.getSimpleName();

    int mNumOfTabs;

    private ArrayList<Weather> day1WeatherArray, day2WeatherArray, day3WeatherArray;

    private Context context;

    //constructor
    public PagerFragmentAdapter(Context context, FragmentManager fm, int NumOfTabs,
                                ArrayList<Weather> day1WeatherArray,
                                ArrayList<Weather> day2WeatherArray,
                                ArrayList<Weather> day3WeatherArray) {
        super(fm);
        this.context = context;
        this.mNumOfTabs = NumOfTabs;
        this.day1WeatherArray = day1WeatherArray;
        this.day2WeatherArray = day2WeatherArray;
        this.day3WeatherArray = day3WeatherArray;
    }//PagerAdapter

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ForecastFragment1(context,day1WeatherArray);
            case 1:
                return new ForecastFragment2(context,day2WeatherArray);
            case 2:
                return new ForecastFragment3(context,day3WeatherArray);
            default:
                return null;
        }//switch
    }//getItem

    @Override
    public int getCount() {
        return mNumOfTabs;
    }//getCount
}//PagerAdapter

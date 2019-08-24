package tech.khash.weathercompare;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import tech.khash.weathercompare.model.Loc;
import tech.khash.weathercompare.utilities.SaveLoadList;

public class OpenWeatherForecast extends AppCompatActivity {

    private static final String TAG = OpenWeatherForecast.class.getSimpleName();

    private Loc currentLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_weather_forecast);

        //get the loc id from intent extra
        if (getIntent().hasExtra(CompareActivity.INTENT_EXTRA_OW_LOC)) {
            String id = getIntent().getStringExtra(CompareActivity.INTENT_EXTRA_OW_LOC);
            if (!TextUtils.isEmpty(id)) {
                //get the corresponding loc
                Loc loc = SaveLoadList.getLocFromDb(this, id);
                if (loc != null) {
                    currentLoc = loc;
//                    textCityName.setText(loc.getId());
                }//null-loc
            }//empty string
        }//has extra


    }//onCreate


}//OpenWeatherForecast

package app.khash.weathertry;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String CANMORE = "canmore";
    private TextView mResultsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultsText = findViewById(R.id.text_results);

        Button canmoreButton = findViewById(R.id.button_canmore);
        canmoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeather(CANMORE);
            }
        });

    }//onCreate

    private void getWeather(String city) {
        if (city != CANMORE) {
            return;
        }

        int cityID = 7871396;




    }//getWeather
}//main-class

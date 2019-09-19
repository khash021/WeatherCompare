package tech.khash.weathercompare;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView textView = findViewById(R.id.text_about);
        textView.setText(getResources().getString(R.string.privacy_policy_text));
    }//onCreate

    //We override this method to finish the activity instead of going back and creating the
    //host activity again
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }//onOptionsItemSelected
}

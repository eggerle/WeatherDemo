package lover.zoe.com.weatherdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import lover.zoe.com.weatherdemo.entity.Weather;
import lover.zoe.com.weatherdemo.utils.Utility;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherStr = sharedPreferences.getString("weather", null);
        if (!TextUtils.isEmpty(weatherStr) && weatherStr != null) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
        }
    }
}

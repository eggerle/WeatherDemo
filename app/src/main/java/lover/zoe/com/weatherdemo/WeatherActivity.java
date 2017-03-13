package lover.zoe.com.weatherdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lover.zoe.com.weatherdemo.entity.Forecast;
import lover.zoe.com.weatherdemo.entity.Weather;
import lover.zoe.com.weatherdemo.service.AutoUpdateService;
import lover.zoe.com.weatherdemo.utils.HttpUtils;
import lover.zoe.com.weatherdemo.utils.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private Context mContext;

    @BindView(R.id.title_city)
    TextView titleCity;
    @BindView(R.id.title_update_time)
    TextView titleUpdateTime;
    @BindView(R.id.nav_button)
    Button mButton;
    @BindView(R.id.degree_text)
    TextView degreeText;
    @BindView(R.id.weather_info_text)
    TextView weatherInfoText;
    @BindView(R.id.forecast_layout)
    LinearLayout forecastLayout;
    @BindView(R.id.aqi_text)
    TextView aqiText;
    @BindView(R.id.pm25_text)
    TextView pm25Text;
    @BindView(R.id.comfort_text)
    TextView comfortText;
    @BindView(R.id.car_wash_text)
    TextView carWashText;
    @BindView(R.id.sport_text)
    TextView sportText;
    @BindView(R.id.weather_layout)
    ScrollView scrollView;
    @BindView(R.id.swipe_refresh)
    public SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.drawer_layout)
    public DrawerLayout drawerLayout;
    @BindView(R.id.nav_layout)
    LinearLayout nav_layout;


    private String weatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        ButterKnife.bind(this);
        mContext = WeatherActivity.this;
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String weatherStr = sharedPreferences.getString("weather", null);

        if (!TextUtils.isEmpty(weatherStr) && weatherStr != null) {
            Weather weather = Utility.handleWeatherRespone(weatherStr);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            weatherId = getIntent().getStringExtra(
                    "weather_id");
            scrollView.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });
    }


    private void showWeatherInfo(Weather weather) {
        if (weather != null && "ok".equals(weather.status)) {
            titleCity.setText(weather.basic.cityName);
            titleUpdateTime.setText(weather.basic.update.updateTime);
            degreeText.setText(weather.now.tmp);
            weatherInfoText.setText(weather.now.cond.txt);
            aqiText.setText(weather.basic.cityName);

            forecastLayout.removeAllViews();
            for (Forecast forecast : weather.forecastsList) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.forecast_item, null);
                TextView dateText = (TextView) view.findViewById(R.id.date_text);
                TextView infoText = (TextView) view.findViewById(R.id.info_text);
                TextView maxText = (TextView) view.findViewById(R.id.max_text);
                TextView minText = (TextView) view.findViewById(R.id.min_text);
                dateText.setText(forecast.date);
                infoText.setText(forecast.cond.txt_d);
                maxText.setText(forecast.tmp.max);
                minText.setText(forecast.tmp.min);
                forecastLayout.addView(view);
            }
            if (weather.aqi != null) {
                aqiText.setText(weather.aqi.city.aqi);
                pm25Text.setText(weather.aqi.city.pm25);
            }

            String comfort = "舒适度：" + weather.suggestion.comf.txt;
            String carWash = "洗车指数：" + weather.suggestion.cw.txt;
            String sport = "运动指数：" + weather.suggestion.sport.txt;

            comfortText.setText(comfort);
            carWashText.setText(carWash);
            sportText.setText(sport);
            scrollView.setVisibility(View.VISIBLE);

            Intent intent = new Intent(mContext, AutoUpdateService.class);
            startService(intent);
        } else {
            Toast.makeText(mContext, "获取天气信息失败", Toast.LENGTH_SHORT).show();
        }

    }

    public void requestWeather(final String weatherId) {
        this.weatherId = weatherId;
        String requestUrl = "http://guolin.tech/api/weather?cityid=" +
                weatherId + "&key=2cf50045af8d4545b245a1522e0f39cb";
        HttpUtils.sendOkHttpRequest(requestUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String responseString = response.body().string();
                final Weather weather = Utility.handleWeatherRespone(responseString);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {

                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                            editor.putString("weather", responseString);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(mContext, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

            }
        });
    }

    @OnClick({R.id.nav_button, R.id.nav_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.nav_layout:
                drawerLayout.openDrawer(GravityCompat.START);
                Toast.makeText(mContext, "点击效果有了", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}

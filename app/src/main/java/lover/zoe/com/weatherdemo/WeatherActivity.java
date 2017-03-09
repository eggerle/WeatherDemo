package lover.zoe.com.weatherdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ScrollingView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import lover.zoe.com.weatherdemo.entity.Forecast;
import lover.zoe.com.weatherdemo.entity.Weather;
import lover.zoe.com.weatherdemo.utils.HttpUtils;
import lover.zoe.com.weatherdemo.utils.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private Context mContext;
    private ScrollView scrollView;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    private Button mButton;

    private LinearLayout forecastLayout;
    public SwipeRefreshLayout swipeRefreshLayout;
    public DrawerLayout drawerLayout;

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
        mContext = WeatherActivity.this;
        initView();
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

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });
    }

    private void initView() {

        scrollView = (ScrollView) this.findViewById(R.id.weather_layout);
        titleCity = (TextView) this.findViewById(R.id.title_city);
        titleUpdateTime = (TextView) this.findViewById(R.id.title_update_time);
        degreeText = (TextView) this.findViewById(R.id.degree_text);
        weatherInfoText = (TextView) this.findViewById(R.id.weather_info_text);
        aqiText = (TextView) this.findViewById(R.id.aqi_text);
        pm25Text = (TextView) this.findViewById(R.id.pm25_text);
        comfortText = (TextView) this.findViewById(R.id.comfort_text);
        carWashText = (TextView) this.findViewById(R.id.car_wash_text);
        sportText = (TextView) this.findViewById(R.id.sport_text);
        forecastLayout = (LinearLayout) this.findViewById(R.id.forecast_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipe_refresh);
        drawerLayout = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        mButton = (Button) this.findViewById(R.id.nav_button);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    }

    private void showWeatherInfo(Weather weather) {
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
    }

    public void requestWeather(String weather_Id) {
        String requestUrl = "http://guolin.tech/api/weather?cityid=" +
                weather_Id + "&key=2cf50045af8d4545b245a1522e0f39cb";
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
                            weatherId = weather.basic.weatherId;
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

}

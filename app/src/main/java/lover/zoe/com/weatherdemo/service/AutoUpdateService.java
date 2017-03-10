package lover.zoe.com.weatherdemo.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

import lover.zoe.com.weatherdemo.entity.Weather;
import lover.zoe.com.weatherdemo.utils.HttpUtils;
import lover.zoe.com.weatherdemo.utils.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        updateWeather();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int ahHour = 8 * 60 * 60;
        long triggerTime = SystemClock.elapsedRealtime() + ahHour;
        Intent intentservice = new Intent(this,AutoUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,intentservice,0);
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,pendingIntent);

        return super.onStartCommand(intent, flags, startId);
    }


    private void updateWeather() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this);
        String weatherStr = sharedPreferences.getString("weather", null);

        if (!TextUtils.isEmpty(weatherStr) && weatherStr != null) {
            Weather weather = Utility.handleWeatherRespone(weatherStr);
           String weatherId = weather.basic.weatherId;
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" +
                    weatherId + "&key=2cf50045af8d4545b245a1522e0f39cb";
            HttpUtils.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    final String responseString = response.body().string();
                    final Weather weather = Utility.handleWeatherRespone(responseString);
                    if (weather != null && "ok".equals(weather.status)) {

                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather", responseString);
                        editor.apply();
                    } else {
                        Toast.makeText(AutoUpdateService.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
    }
}

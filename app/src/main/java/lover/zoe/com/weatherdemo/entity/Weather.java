package lover.zoe.com.weatherdemo.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by zoe on 2017/3/8.
 */
public class Weather {

    public String status;


    public AQI aqi;
    public Basic basic;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastsList;
    public Now now;
    public Suggestion suggestion;
}


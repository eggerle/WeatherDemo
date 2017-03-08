package lover.zoe.com.weatherdemo.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zoe on 2017/3/7.
 */
public class AQI {

    public AQICity city;

    public class AQICity {
        public String aqi;
        public String co;
        public String no2;
        public String o3;
        public String pm10;
        public String pm25;
        public String qlty;
        public String so2;

    }
}

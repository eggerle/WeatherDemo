package lover.zoe.com.weatherdemo.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zoe on 2017/3/7.
 */
public class Basic {

    @SerializedName("cnty")
    public String cnty;
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    @SerializedName("lat")
    public String lat;
    @SerializedName("lon")
    public String lon;

    public Update update;

    public class Update {

        @SerializedName("loc")
        public String updateTime;
        @SerializedName("utc")
        public String utc;
    }
}

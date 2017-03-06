package lover.zoe.com.weatherdemo.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import lover.zoe.com.weatherdemo.entity.City;
import lover.zoe.com.weatherdemo.entity.County;
import lover.zoe.com.weatherdemo.entity.Province;

/**
 * Created by zoe on 2017/3/6.
 */
public class Utility {

    public static boolean handleProvinceResponse(String response) {

        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceCode(jsonObject.getInt("id"));
                    province.setProvinceName(jsonObject.getString("name"));
                    province.save();

                }
                return true;
            } catch (Exception e) {

            }
        }
        return false;
    }

    public static boolean handleCityResponse(String response, int provinceId) {

        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(jsonObject.getInt("id"));
                    city.setCityName(jsonObject.getString("name"));
                    city.setProviceId(provinceId);
                    city.save();
                }
                return true;
            } catch (Exception e) {

            }
        }
        return false;
    }

    public static boolean handleCountyResponse(String response, int cityId) {

        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    County county = new County();
                    county.setWeatherId(jsonObject.getString("weather_id"));
                    county.setCountyName(jsonObject.getString("name"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (Exception e) {

            }
        }
        return false;
    }
}

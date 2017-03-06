package lover.zoe.com.weatherdemo.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by zoe on 2017/3/6.
 */
public class City extends DataSupport {

    private  int id;
    private  String cityName;
    private String getCityCode;
    private String proviceId;

    public String getGetCityCode() {
        return getCityCode;
    }

    public void setGetCityCode(String getCityCode) {
        this.getCityCode = getCityCode;
    }

    public int getId() {

        return id;
    }

    public String getProviceId() {
        return proviceId;
    }

    public void setProviceId(String proviceId) {
        this.proviceId = proviceId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}

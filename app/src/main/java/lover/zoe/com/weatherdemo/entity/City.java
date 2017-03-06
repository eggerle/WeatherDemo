package lover.zoe.com.weatherdemo.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by zoe on 2017/3/6.
 */
public class City extends DataSupport {

    private int id;
    private String cityName;
    private int cityCode;
    private int proviceId;

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getId() {

        return id;
    }

    public int getProviceId() {
        return proviceId;
    }

    public void setProviceId(int proviceId) {
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

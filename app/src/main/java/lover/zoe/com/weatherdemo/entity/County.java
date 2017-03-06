package lover.zoe.com.weatherdemo.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by zoe on 2017/3/6.
 */
public class County extends DataSupport {

    private int id;
    private String countyName;
    private String weatherId;
    private String cityId;

    public String getCityId() {
        return cityId;
    }


    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}

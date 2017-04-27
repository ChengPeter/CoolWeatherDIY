package cs.whu.com.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by cxq on 2017/4/24.
 */
public class Country extends DataSupport {
    private  int id;
    private String  countryName;
    private  String weatherId;
    private int cityId;

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}

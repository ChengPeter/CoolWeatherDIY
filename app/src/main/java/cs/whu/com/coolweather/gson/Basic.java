package cs.whu.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.security.PrivateKey;

/**
 * Created by cxq on 2017/4/25.
 */
public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}

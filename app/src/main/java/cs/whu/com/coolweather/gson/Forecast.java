package cs.whu.com.coolweather.gson;

import android.text.TextPaint;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cxq on 2017/4/25.
 */
public class Forecast {
    public String date;
    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public MoreInfo moreInfo;

    public class Temperature{
        public String max;
        public String min;
    }
    public class MoreInfo{
        @SerializedName("txt_d")
        public String info;
    }
}

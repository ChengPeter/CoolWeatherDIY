package cs.whu.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cxq on 2017/4/25.
 */
public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public MoreInfo moreInfo;

    public class MoreInfo{
        @SerializedName("txt")
        public  String info;
    }
}

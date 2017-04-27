package cs.whu.com.coolweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cs.whu.com.coolweather.db.City;
import cs.whu.com.coolweather.db.Country;
import cs.whu.com.coolweather.db.Province;
import cs.whu.com.coolweather.gson.Weather;

/**
 * Created by cxq on 2017/4/24.
 */
public class Utility {
    /**
     * 处理省的json数据
     * @param response
     * @param provinceId
     * @return
     */
    public static boolean handleProvinceResponse(String response) {
        if(!TextUtils.isEmpty(response))
        {
            try {
                //解析json数据
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i <jsonArray.length() ; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(jsonObject.getString("name"));
                    province.setProvinceCode(jsonObject.getInt("id"));
                    province.save();//解析完成后将数据保存到数据库中
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    /**
     * 处理市的json数据
     * @param response
     * @param provinceId
     * @return
     */
    public static boolean handleCityResponse(String response,int provinceId) {
        if(!TextUtils.isEmpty(response))
        {
            try {
                //解析json数据
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i <jsonArray.length() ; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    City city = new City();
                    city.setCityName(jsonObject.getString("name"));
                    city.setCityCode(jsonObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();//解析完成后将数据保存到数据库中
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    /**
     * 处理县的json数据
     * @param response
     * @param cityId
     * @return
     */
    public static boolean handleCountryResponse(String response,int cityId) {
        if(!TextUtils.isEmpty(response))
        {
            try {
                //解析json数据
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i <jsonArray.length() ; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Country country = new Country();
                    country.setCountryName(jsonObject.getString("name"));
                    country.setWeatherId(jsonObject.getString("weather_id"));
                    country.setCityId(cityId);
                    country.save();//解析完成后将数据保存到数据库中
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }
    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContet = jsonArray.getJSONObject(0).toString();
            Log.i("hahahah",weatherContet);
            return new Gson().fromJson(weatherContet,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

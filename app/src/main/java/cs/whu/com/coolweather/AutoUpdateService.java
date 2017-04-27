package cs.whu.com.coolweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import cs.whu.com.coolweather.gson.Weather;
import cs.whu.com.coolweather.util.HttpUtil;
import cs.whu.com.coolweather.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateBingPic();
        updateWeather();
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour= 8*60*60*1000;
        long triggerTime  = SystemClock.elapsedRealtime()+anHour;
        Intent intent1 = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,intent1,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

   private  void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherStr = prefs.getString("weather",null);
        if(weatherStr != null)
        {
            Weather weather = Utility.handleWeatherResponse(weatherStr);
            String weatherId = weather.basic.weatherId;

            String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId+"&key=be5adfafe64c4634ac46b4cd58bc71f6";
            HttpUtil.sendOkHttprequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseText = response.body().string();
                    Log.i("cxq responseText", responseText);
                    final Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor =
                                        PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                    }

                }
            });
        }
    }
    private void updateBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttprequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String bing_pic = response.body().string();
                SharedPreferences.Editor editor =
                        PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("back_img", bing_pic);
                editor.apply();
            }
        });
    }
}

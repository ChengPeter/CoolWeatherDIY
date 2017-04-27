package cs.whu.com.coolweather;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import cs.whu.com.coolweather.db.City;
import cs.whu.com.coolweather.db.Country;
import cs.whu.com.coolweather.db.Province;
import cs.whu.com.coolweather.util.HttpUtil;
import cs.whu.com.coolweather.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseAreaFragment extends Fragment {

    public static final  String TAG="ChooseAreaFragment";
    public static final  int LEVEL_PROVINCE = 0;
    public static final  int LEVEL_CITY = 1;
    public static final  int LEVEL_COUNTRY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<String>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<Country> countryList;

    private Province selectedProvince;
    private Country selectedCountry;
    private City selectedCity;

    private int currentLevel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG,"onCreateView");
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView)view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG,"onActivityCreated");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currentLevel == LEVEL_PROVINCE)
                {
                   selectedProvince = provinceList.get(i);
                    queryCities();
                }else if(currentLevel == LEVEL_CITY)
                {
                    selectedCity = cityList.get(i);
                    queryCountries();
                }else if(currentLevel == LEVEL_COUNTRY){
                    String weatherId = countryList.get(i).getWeatherId();
                    if(getActivity() instanceof  MainActivity)
                    {

                        Log.i(TAG,weatherId);
                        Intent intent = new Intent(getActivity(),weatherActivity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if(getActivity() instanceof weatherActivity)
                    {
                        weatherActivity weatheractivty = (weatherActivity) getActivity();
                        weatheractivty.drawerLayout.closeDrawers();
                        weatheractivty.swipeRefresh.setRefreshing(true);
                        weatheractivty.requestWeather(weatherId);
                    }

                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel == LEVEL_CITY)
                {
                   // currentLevel = LEVEL_PROVINCE;
                    queryProvinces();
                }
                else if(currentLevel == LEVEL_COUNTRY)
                {
                    //currentLevel = LEVEL_CITY;
                    queryCities();
                }
            }
        });
        queryProvinces();
    }

    private void queryProvinces(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        //Log.i(TAG,provinceList.size()+" ");
        if(provinceList.size() > 0){
            dataList.clear();
            for (Province province:
                 provinceList) {
                Log.i(TAG,province.getProvinceName()+" "+province.getProvinceCode());
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else
        {
            String address= "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    private void queryCities(){
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?",String.valueOf(selectedProvince.getProvinceCode())).find(City.class);
        if(cityList.size() > 0){
            dataList.clear();
            for (City city:
                    cityList) {
                Log.i(TAG,city.getCityName()+" "+city.getCityCode());
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }
        else{
            String address= "http://guolin.tech/api/china/"+selectedProvince.getProvinceCode();
            queryFromServer(address,"city");
        }

    }

    private void queryCountries(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
       countryList= DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(Country.class);
        Log.i(TAG,countryList.size()+" ");
        if(countryList.size() > 0){
            dataList.clear();
            for (Country country:
                    countryList) {
                Log.i(TAG,country.getCountryName()+" "+country.getWeatherId());
                dataList.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTRY;
        }
        else{
            Log.i(TAG,selectedProvince.getProvinceCode()+" "+selectedCity.getCityCode());
            String address= "http://guolin.tech/api/china/"+selectedProvince.getProvinceCode()
                    +"/"+selectedCity.getCityCode();
            queryFromServer(address,"country");
        }
    }

    private  void queryFromServer(String address,final String type){
        HttpUtil.sendOkHttprequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
              getActivity().runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      closeProgressDialog();
                      Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                  }
              });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Looper.prepare();
                showProgressDialog();
                String responseText = response.body().string();
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }else if("city".equals(type)){
                    result = Utility.handleCityResponse(responseText,selectedProvince.getId());
                }
                else if("country".equals(type)){
                    Log.i(TAG,responseText);
                    result = Utility.handleCountryResponse(responseText,selectedCity.getId());
                }
                Log.i(TAG,String.valueOf(result));
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }
                            else if("country".equals(type)){
                               queryCountries();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showProgressDialog(){
        if(progressDialog == null)
        {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载。。。");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }


    private void closeProgressDialog(){
        if( progressDialog != null){
            progressDialog.dismiss();
        }
    }
}

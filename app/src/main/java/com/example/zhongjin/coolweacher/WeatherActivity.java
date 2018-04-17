package com.example.zhongjin.coolweacher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zhongjin.coolweacher.gson.Forecast;
import com.example.zhongjin.coolweacher.gson.Weather;
import com.example.zhongjin.coolweacher.service.AutoUpdateService;
import com.example.zhongjin.coolweacher.util.HttpUtil;
import com.example.zhongjin.coolweacher.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comforText;
    private TextView sportText;
    private TextView carWashText;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    private String mWeatherId;
    public DrawerLayout drawerLayout;
    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //融合状态栏
        if(Build.VERSION.SDK_INT >= 21)
        {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        //初始化各种控件
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comforText = (TextView) findViewById(R.id.comfort_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        //下拉刷新
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        //滑动菜单
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);

        //从缓存中获取天气信息数据
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null)
        {
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.id;
            showWeatherInfo(weather);
        } else {
            //无缓存时去服务器查询天气
            String mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

        String bingPic = prefs.getString("bing_pic", null);
        if(bingPic != null)
        {
            Glide.with(this).load(bingPic).into(bingPicImg);
        }
        else
        {
            loadBingPic();
        }

        //滑出滑动菜单
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * 根据天气id请求城市天气信息
     * @param weatherId
     */
    public void requestWeather(final String weatherId)
    {
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId+"&key=7fafc3e5604941eeb198aaa15f51c00c";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败！", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null && "ok".equals(weather.status))
                        {
                            //存储到文件缓存里面
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            mWeatherId = weather.basic.id;
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败！", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

        loadBingPic();
    }

    /**
     * 处理并展示Weather实体类中的数据
     * @param weather
     */
    private void showWeatherInfo(Weather weather)
    {
        //现在天气情况
        String cityName = weather.basic.city;
        String updateTime = weather.basic.update.loc.split(" ")[1];
        String degree = weather.now.tmp + "°C";
        String weatherInfo = weather.now.cond.txt;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        //未来几天天气情况
        forecastLayout.removeAllViews();
        for(Forecast forecast : weather.daily_forecast)
        {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView)view.findViewById(R.id.date_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            TextView minText = (TextView)view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.cond.txt_d);
            maxText.setText(forecast.tmp.max);
            minText.setText(forecast.tmp.min);
            forecastLayout.addView(view);
        }
        //空气质量
        if(weather.aqi != null)
        {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        //建议
        String comfort = "舒适度：" + weather.suggestion.comf.txt;
        String sport = "运动建议：" + weather.suggestion.sport.txt;
        String carwash = "洗车指数：" + weather.suggestion.cw.txt;
        comforText.setText(comfort);
        sportText.setText(sport);
        carWashText.setText(carwash);
        weatherLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
        startService(intent);
    }

    /**
     * 加载必应每天一图
     */
    private void loadBingPic()
    {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
}
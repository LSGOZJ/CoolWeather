package com.example.zhongjin.coolweacher.gson;

import java.util.List;

/**
 * Created by zhongjin on 18-4-14.
 */

public class Weather {
    public String status;
    public Basic basic;
    public Update update;
    public Now now;
    public Aqi aqi;
    public Suggestion suggestion;
    public List<Forecast> daily_forecast;
}

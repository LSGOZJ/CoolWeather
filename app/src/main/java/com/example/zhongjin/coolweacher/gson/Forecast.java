package com.example.zhongjin.coolweacher.gson;

/**
 * Created by zhongjin on 18-4-14.
 */

public class Forecast {
    public String date;
    public Cond cond;
    public Tmp tmp;

    public class Cond
    {
        public String txt_d;
    }

    public class Tmp
    {
        public String max;
        public String min;
    }
}

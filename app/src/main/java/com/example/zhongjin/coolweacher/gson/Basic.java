package com.example.zhongjin.coolweacher.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhongjin on 18-4-14.
 */

public class Basic {

    public String cid;

    public String location;

    public String parent_city;

    public String admin_area;

    public String cnty;

    public String lat;

    public String lon;

    public String tz;

    public String city;

    public String id;

    public Update update;

    public class Update
    {
        public String loc;
        public String utc;
    }
}

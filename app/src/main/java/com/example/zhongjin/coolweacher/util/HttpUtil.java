package com.example.zhongjin.coolweacher.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by zhongjin on 18-4-12.
 */

public class HttpUtil {

    /**
     * 发起网络请求
     */
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback)
    {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}

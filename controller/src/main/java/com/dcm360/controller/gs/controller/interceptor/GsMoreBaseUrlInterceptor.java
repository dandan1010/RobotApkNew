package com.dcm360.controller.gs.controller.interceptor;


import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 动态修改retrofit的地址
 */
public class GsMoreBaseUrlInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        //获取原始的originalRequest
        Request originalRequest = chain.request();

        List<String> urlnameList = originalRequest.headers("url_name");
        if (urlnameList != null && urlnameList.size() > 0) {
            Request.Builder builder = originalRequest.newBuilder();
            builder.removeHeader("url_name");
            String urlname = urlnameList.get(0);

            //获取老的url
            HttpUrl oldUrl = originalRequest.url();

            HttpUrl newHttpUrl;
            if ("charge".equals(urlname)) {
                //重建新的HttpUrl，需要重新设置的url部分
                newHttpUrl = oldUrl.newBuilder()
                        .port(8000)//端口
                        .build();
            } else {
                newHttpUrl = oldUrl;
            }

            //获取处理后的新newRequest
            Request newRequest = builder.url(newHttpUrl).build();
            return chain.proceed(newRequest);
        } else {
            return chain.proceed(originalRequest);
        }
    }
}

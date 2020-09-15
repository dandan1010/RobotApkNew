package com.dcm360.controller.gs.controller.interceptor;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;

import static com.dcm360.controller.gs.controller.GsController.TAG;

public class LoggerInterceptor implements Interceptor {

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (request != null && request.url() != null)
            Log.d(TAG, request.url().toString());
        return chain.proceed(chain.request());
    }

}

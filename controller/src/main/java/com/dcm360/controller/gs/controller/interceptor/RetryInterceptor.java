package com.dcm360.controller.gs.controller.interceptor;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetryInterceptor implements Interceptor {

    public int maxRetry;//最大重试次数

    public RetryInterceptor(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        int retryNum = 0;

        Request request = chain.request();
        Log.d("Retry", "num:" + retryNum);
        Response response = chain.proceed(request);
        while (!response.isSuccessful() && retryNum < maxRetry) {
            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }

            retryNum++;
            Log.d("Retry2", "num:" + retryNum);
            response = chain.proceed(request);
        }

        return response;
    }
}

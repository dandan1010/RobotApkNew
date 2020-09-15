package com.dcm360.controller.gs.controller;

import android.util.Log;

import com.dcm360.controller.robot_interface.status.RobotStatus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * @author liyan
 */
public class ResponseCallback<T> {
    public Callback call(final RobotStatus<T> status) {
        final RobotStatus<T> robotStatus = status;
        final Callback callback = new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                try {
//                    Log.d("ResponseCallback", "onResponse:" + response.message());
                    if (robotStatus != null) {
                        robotStatus.success(response.body());
                    }
                } catch (Exception e) {
                    Log.d("ResponseCallback", "Exception:" + e.getMessage());
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                try {
                    Log.d("ResponseCallback", "onFailure:" + t.getMessage());
                    if (robotStatus != null) {
                        robotStatus.error(t);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        return callback;
    }

}

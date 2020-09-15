package com.example.robotapk.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GsonUtils {

    private static final String TAG = "GsonUtils";
    private static final String TYPE = "type";
    private JSONObject jsonObject;

    private String spinnerTime = null;
    private String tvTime = null;
    private List<String> data;

    public void setData(List<String> data) {
        this.data = data;
    }

    public void setSpinnerTime(String spinnerTime) {
        this.spinnerTime = spinnerTime;
    }

    public void setTvTime(String tvTime) {
        this.tvTime = tvTime;
    }

    public String putJsonMessage(String type) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(Content.SPINNERTIME, spinnerTime);
            JSONArray jsonArray = new JSONArray(data);
            jsonObject.put(Content.DATATIME,jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String getType(String message){
        String type = null;
        if (message == null) {
            Log.d(TAG, "getType message is null");
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(message);
            type = jsonObject.getString(TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return type;
    }
}

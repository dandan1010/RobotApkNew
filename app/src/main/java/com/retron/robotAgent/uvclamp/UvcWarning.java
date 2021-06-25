package com.retron.robotAgent.uvclamp;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.retron.robotAgent.R;

public class UvcWarning {
    private static final String TAG = "UvcWarning";
    private MediaPlayer mediaPlayer;
    private Context mContext;
    private boolean isWarning = false;

    public UvcWarning(Context mContext) {
        this.mContext = mContext;
        mediaPlayer = new MediaPlayer();
    }
    /**
     * uvc开启前警告
     * */
    public void startWarning() {
        Log.v(TAG, "start warning");
        if (!isWarning) {
            mediaPlayer.reset();
            mediaPlayer = MediaPlayer.create(mContext, R.raw.uvcalarm);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            isWarning = true;
        }

    }
    /**
     * 停止警告
     * */
    public void stopWarning() {
        Log.v(TAG, "stop warning");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isWarning = false;
        }
    }
}

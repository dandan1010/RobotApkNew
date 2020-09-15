package com.example.robotapk.adapter;

import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class RecycleHolder extends RecyclerView.ViewHolder {

    /** 用于存储当前item当中的View */
    private SparseArray<View> mViews;

    public RecycleHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<View>();
    }
    public <T extends View> T findView(int ViewId) {
        View view = mViews.get(ViewId);
        //集合中没有，则从item当中获取，并存入集合当中
        if (view == null) {
            view = itemView.findViewById(ViewId);
            mViews.put(ViewId, view);
        }
        return (T) view;
    }
    public RecycleHolder setText(int viewId, String text) {
        TextView tv = findView(viewId);
        tv.setText(text);
        return this;
    }
    public RecycleHolder setText(int viewId, int text) {
        TextView tv = findView(viewId);
        tv.setText(text);
        return this;
    }
    public RecycleHolder setImageResource(int viewId, int ImageId) {
        ImageView image = findView(viewId);
        image.setImageResource(ImageId);
        return this;
    }
    public RecycleHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView imageView= findView(viewId);
        imageView.setImageBitmap(bitmap);
        return this;
    }
}

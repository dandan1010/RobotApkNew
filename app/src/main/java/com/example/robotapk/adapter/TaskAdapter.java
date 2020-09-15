package com.example.robotapk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.dcm360.controller.gs.controller.bean.map_bean.RobotMap;
import com.example.robotapk.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<RecycleHolder> {

    private Context mContext;
    private List<String> mDatas;
    private int mLayoutId;
    private LayoutInflater mInflater;

    private OnItemClickListener onItemClickListener;

    public TaskAdapter(Context mContext, int mLayoutId) {
        this.mContext = mContext;
        this.mLayoutId = mLayoutId;
        mInflater = LayoutInflater.from(mContext);
    }

    public void refeshList(List<String> mDatas) {
        this.mDatas = mDatas;
    }

    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecycleHolder(mInflater.inflate(mLayoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecycleHolder holder, int position) {
        convert(holder, mDatas.get(position), position);
        if (onItemClickListener != null) {
            //设置背景
            //holder.itemView.setBackgroundResource(R.drawable.recycler_bg);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.OnItemClickListener(holder.itemView, holder.getLayoutPosition());
                }
            });
        }

    }

    public void convert(RecycleHolder holder, String data, int position) {
        holder.setText(R.id.map_recycler_tv, data);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void OnItemClickListener(View view, int position);
    }
}

package com.retron.robot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.retron.robot.R;

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
                    onItemClickListener.OnItemClickListener(holder.itemView, holder.getLayoutPosition(), mDatas.get(position));
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
        void OnItemClickListener(View view, int position, String itemString);
    }
}

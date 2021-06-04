package com.landleaf.everyday.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.landleaf.everyday.R;
import com.landleaf.everyday.bean.TestBean;
import com.landleaf.everyday.callback.DiffCallback;

import java.util.List;

public class PageAdapter2 extends RecyclerView.Adapter<PageAdapter2.ViewHolder> {

    Context mContext;
    List<TestBean> dataList;

    public PageAdapter2(Context mContext, List<TestBean> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    public void setDataList(List<TestBean> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_scene, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("PageAdapter", "position:" + position);
        TestBean s = dataList.get(position);
        holder.tvName1.setText(s.getContent());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        Log.d("PageAdapter", "payloads:" + payloads);
        if (payloads.isEmpty()){
            onBindViewHolder(holder,position);
        }else {
            Bundle bundle = (Bundle) payloads.get(0);
            for (String key : bundle.keySet()) {
                if ("IMG".equals(key)){
                    holder.ivIcon1.setImageResource(R.drawable.img_nt_mode_zhileng_on);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0:dataList.size();
    }

    public List<TestBean> getData() {
        return dataList;
    }

    public void setData(List<TestBean> newData) {

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(newData, dataList));
        diffResult.dispatchUpdatesTo(this);
        dataList.clear();
        this.dataList.addAll(newData);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView ivIcon1;
        TextView tvName1;


        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.ivIcon1 = itemView.findViewById(R.id.ivIcon);
            this.tvName1 = itemView.findViewById(R.id.tvName);
        }
    }
}

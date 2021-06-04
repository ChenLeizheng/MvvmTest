package com.landleaf.normal.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.landleaf.normal.R;
import com.landleaf.normal.bean.NtTempId;

import java.util.List;

/**
 * Created by Lei on 2019/4/22.
 */

public class ChangePointAdapter extends BaseAdapter {

    List<NtTempId> listData;
    Context mContext;

    public ChangePointAdapter(List<NtTempId> listData, Context mContext) {
        this.listData = listData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return listData==null? 0:listData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_change_point, null);
            viewHolder.tvContent = convertView.findViewById(R.id.tvContent);
            convertView.setTag(viewHolder);
        }else {
            viewHolder  = (ViewHolder) convertView.getTag();
        }
        NtTempId ntTempId = listData.get(position);
        viewHolder.tvContent.setText(Html.fromHtml(formatHtml(ntTempId)));
        return convertView;
    }

    private class ViewHolder{
        TextView tvContent;
    }

    private String formatHtml(NtTempId ntTempId){
        return "<strong>"+ntTempId.getRoomName()+"面板:</strong>实际温度<i>"+ntTempId.getShowTempId()+"<i> -- 湿度<i>"+ntTempId.getHumidityId()+"<i>"
                +"-- 设定温度<i>"+ntTempId.getShowSetTempId()+"<i>"+"-- 操作设定温度<i>"+ntTempId.getSetTempId()+"<i>";
    }
}

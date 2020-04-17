package com.landleaf.everyday;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.landleaf.everyday.callback.DialogCallback;

public class PopUtils {

    private PopUtils(){}

    public static PopUtils getInstance(){
        return ViewHolder.instance;
    }

    int temp = 23;
    public void showSetTempDialog(Activity context, final DialogCallback callback){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_set_temp, null, false);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x22000000));
        popupWindow.showAtLocation(context.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        ImageView ivTempMinus = view.findViewById(R.id.ivTempMinus);
        ImageView ivTempPlus = view.findViewById(R.id.ivTempPlus);
        final TextView tvSetTemp = view.findViewById(R.id.tvSetTemp);
        Typeface pixFontLite = Typeface.createFromAsset(context.getAssets(), "fonts/pixFontLite.ttf");
        tvSetTemp.setTypeface(pixFontLite);
        RelativeLayout rlCancel = view.findViewById(R.id.rlCancel);
        RelativeLayout rlSure = view.findViewById(R.id.rlSure);
        RelativeLayout rlParent = view.findViewById(R.id.rlParent);


        ivTempMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp--;
                tvSetTemp.setText(temp+"℃");
            }
        });
        ivTempPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp++;
                tvSetTemp.setText(temp+"℃");
            }
        });
        rlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        rlSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSure(String.valueOf(temp));
                popupWindow.dismiss();
            }
        });
        rlParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    int windSpeedIndex = 0;
    int[] windResource = {R.drawable.img_wind_speed_0,R.drawable.img_wind_speed_1,R.drawable.img_wind_speed_2,R.drawable.img_wind_speed_3,R.drawable.img_wind_speed_4,R.drawable.img_wind_speed_5,R.drawable.img_wind_speed_6,R.drawable.img_wind_speed_7};
    public void showSetDiySceneDialog(Activity context, final DialogCallback callback){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_set_diy_scene, null, false);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x22000000));
        popupWindow.showAtLocation(context.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        final ImageView ivTempMinus = view.findViewById(R.id.ivTempMinus);
        ImageView ivTempPlus = view.findViewById(R.id.ivTempPlus);
        final TextView tvSetTemp = view.findViewById(R.id.tvSetTemp);
        final ImageView ivWindMinus = view.findViewById(R.id.ivWindMinus);
        ImageView ivWindPlus = view.findViewById(R.id.ivWindPlus);
        final ImageView ivWindSpeed = view.findViewById(R.id.ivWindSpeed);
        Typeface pixFontLite = Typeface.createFromAsset(context.getAssets(), "fonts/pixFontLite.ttf");
        tvSetTemp.setTypeface(pixFontLite);
        RelativeLayout rlCancel = view.findViewById(R.id.rlCancel);
        RelativeLayout rlSure = view.findViewById(R.id.rlSure);
        RelativeLayout rlParent = view.findViewById(R.id.rlParent);

        ivTempMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp--;
                tvSetTemp.setText(temp+"℃");
            }
        });
        ivTempPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp++;
                tvSetTemp.setText(temp+"℃");
            }
        });
        ivWindMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windSpeedIndex --;
                if (windSpeedIndex<0){
                    windSpeedIndex = 0;
                }
                ivWindSpeed.setImageResource(windResource[windSpeedIndex]);
            }
        });
        ivWindPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windSpeedIndex++;
                if (windSpeedIndex>windResource.length-1){
                    windSpeedIndex = windResource.length-1;
                }
                ivWindSpeed.setImageResource(windResource[windSpeedIndex]);
            }
        });
        rlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        rlSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSure(String.valueOf(temp));
                popupWindow.dismiss();
            }
        });
        rlParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }


    static class ViewHolder{
        private static PopUtils instance = new PopUtils();
    }
}

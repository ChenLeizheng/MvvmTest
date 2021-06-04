package com.landleaf.normal.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.landleaf.normal.R;
import com.landleaf.normal.bean.NtTempId;
import com.landleaf.normal.interfaces.PasswordDialogCallback;
import com.landleaf.normal.interfaces.RoomPointCallback;

import static com.landleaf.normal.utils.CommonUtil.AD_TYPE_HOUR;
import static com.landleaf.normal.viewmodel.NtControlViewModel.AD_TYPE_AIR_QUANTITY;
import static com.landleaf.normal.viewmodel.NtControlViewModel.AD_TYPE_AWAY_HOME;
import static com.landleaf.normal.viewmodel.NtControlViewModel.AD_TYPE_CLEAR;
import static com.landleaf.normal.viewmodel.NtControlViewModel.AD_TYPE_HUMIDIFICATION;
import static com.landleaf.normal.viewmodel.NtControlViewModel.AD_TYPE_MODE;
import static com.landleaf.normal.viewmodel.NtControlViewModel.AD_TYPE_POWER_SWITCH;

/**
 * Created by Lei on 2019/4/22.
 */

public class MaterialDialogUtil {

    private MaterialDialog addRoomMaterialDialog;
    private MaterialDialog updateMaterialDialog;
    private MaterialDialog showMultiDialog;
    private MaterialDialog projectSettingDialog;
    private MaterialDialog showSureDialog;
    private MaterialDialog inputInfoDialog;

    public static final String TITLE_PASSWORD = "请输入密码:";
    public static final String TITLE_WEATHER = "请输入WEATHER_INFO:";

    private MaterialDialogUtil() {
    }


    public static MaterialDialogUtil getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static MaterialDialogUtil instance = new MaterialDialogUtil();
    }

    /**
     * 新增房间点位
     */
    public void addRoomAddress(Context context, String title, boolean isUpdate, String deviceName, RoomPointCallback callback) {
        addRoomMaterialDialog = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_update_room_address, false)
                .title(title)
                .titleColor(Color.WHITE)
                .titleGravity(GravityEnum.CENTER)
                .backgroundColor(0xFF3c3f43)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        View customView = dialog.getCustomView();
                        EditText etTemp = customView.findViewById(R.id.etTemp);
                        EditText etHumidity = customView.findViewById(R.id.etHumidity);
                        EditText etShowSetTemp = customView.findViewById(R.id.etShowSetTemp);
                        EditText etSetTemp = customView.findViewById(R.id.etSetTemp);
                        EditText etDeviceName = customView.findViewById(R.id.etDeviceName);

                        String tempId = etTemp.getText().toString();
                        if (!isNumber(tempId)) {
                            showToast("温度点位为null或格式不正确！", context);
                            return;
                        }
                        String humidityId = etHumidity.getText().toString();
                        if (!isNumber(humidityId)) {
                            showToast("湿度点位为null或格式不正确！", context);
                            return;
                        }
                        String showSetTempId = etShowSetTemp.getText().toString();
                        if (!isNumber(showSetTempId)) {
                            showToast("设定温度点位为null或格式不正确！", context);
                            return;
                        }
                        String setTempId = etSetTemp.getText().toString();
                        if (!isNumber(setTempId)) {
                            showToast("操作温度点位为null或格式不正确！", context);
                            return;
                        }

                        String updateDeviceName = etDeviceName.getText().toString();
                        if (TextUtils.isEmpty(updateDeviceName)) {
                            showToast("面板名称为null！", context);
                            return;
                        }
                        NtTempId ntTempId = new NtTempId(Integer.valueOf(tempId), Integer.valueOf(humidityId), Integer.valueOf(showSetTempId), Integer.valueOf(setTempId), updateDeviceName);
                        callback.dataCallback(ntTempId);
                        Gson gson = new Gson();
                        String ntTempIdInfo = gson.toJson(ntTempId);
                        Prefs.with(context).write(updateDeviceName, ntTempIdInfo);
                        if (isUpdate){
                            DBUtil.getInstance().updateRoom(deviceName,updateDeviceName);
                        }else {
                            boolean insertRoom = DBUtil.getInstance().insertRoom(updateDeviceName);
                            if (!insertRoom){
                                showToast("面板名称不能重复！", context);
                            }
                        }
                    }
                })
                .positiveText("确定")
                .negativeText("取消")
                .show();

        if (isUpdate) {
            View customView = addRoomMaterialDialog.getCustomView();
            EditText etTemp = customView.findViewById(R.id.etTemp);
            EditText etHumidity = customView.findViewById(R.id.etHumidity);
            EditText etShowSetTemp = customView.findViewById(R.id.etShowSetTemp);
            EditText etSetTemp = customView.findViewById(R.id.etSetTemp);
            EditText etDeviceName = customView.findViewById(R.id.etDeviceName);

            Gson gson = new Gson();
            String json = Prefs.with(context).read(deviceName, "");
            NtTempId ntTempId = gson.fromJson(json, NtTempId.class);
            etTemp.setText(ntTempId.getShowTempId() + "");
            etHumidity.setText(ntTempId.getHumidityId() + "");
            etShowSetTemp.setText(ntTempId.getShowSetTempId() + "");
            etSetTemp.setText(ntTempId.getSetTempId() + "");
            etDeviceName.setText(ntTempId.getRoomName());
        }
    }

    /**
     * 更新主机点位
     */
    public void updateHostAddress(Context context) {
        int powerSwitch = Prefs.with(context).readInt(AD_TYPE_POWER_SWITCH, 2004);
        int modeSwitch = Prefs.with(context).readInt(AD_TYPE_MODE, 2006);
        int outHomeSwitch = Prefs.with(context).readInt(AD_TYPE_AWAY_HOME, 2008);
        int speedSwitch = Prefs.with(context).readInt(AD_TYPE_AIR_QUANTITY, 2010);
        int humiditySwitch = Prefs.with(context).readInt(AD_TYPE_HUMIDIFICATION, 2012);
        int hourSwitch = Prefs.with(context).readInt(AD_TYPE_HOUR, 1180);
        int clearSwitch = Prefs.with(context).readInt(AD_TYPE_CLEAR, 1038);
        updateMaterialDialog = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_update_host_address, false)
                .title("更新主机点位")
                .titleColor(Color.WHITE)
                .titleGravity(GravityEnum.CENTER)
                .backgroundColor(0xFF3c3f43)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        View customView = dialog.getCustomView();
                        EditText etPower = customView.findViewById(R.id.etPower);
                        EditText etMode = customView.findViewById(R.id.etMode);
                        EditText etLiJia = customView.findViewById(R.id.etLiJia);
                        EditText etWind = customView.findViewById(R.id.etWind);
                        EditText etJiaShi = customView.findViewById(R.id.etJiaShi);
                        EditText etTime = customView.findViewById(R.id.etTime);
                        EditText etTimeClear = customView.findViewById(R.id.etTimeClear);
                        boolean checkParam = false;

                        if (isNumber(etPower.getText().toString())) {
                            checkParam = true;
                            Prefs.with(context).writeInt(AD_TYPE_POWER_SWITCH,Integer.valueOf(etPower.getText().toString()));
                        }
                        if (isNumber(etMode.getText().toString())) {
                            checkParam = true;
                            Prefs.with(context).writeInt(AD_TYPE_MODE,Integer.valueOf(etMode.getText().toString()));
                        }
                        if (isNumber(etLiJia.getText().toString())) {
                            checkParam = true;
                            Prefs.with(context).writeInt(AD_TYPE_AWAY_HOME,Integer.valueOf(etLiJia.getText().toString()));
                        }
                        if (isNumber(etWind.getText().toString())) {
                            checkParam = true;
                            Prefs.with(context).writeInt(AD_TYPE_AIR_QUANTITY,Integer.valueOf(etWind.getText().toString()));
                        }
                        if (isNumber(etJiaShi.getText().toString())) {
                            checkParam = true;
                            Prefs.with(context).writeInt(AD_TYPE_HUMIDIFICATION,Integer.valueOf(etJiaShi.getText().toString()));
                        }
                        if (isNumber(etTime.getText().toString())) {
                            checkParam = true;
                            Prefs.with(context).writeInt(AD_TYPE_HOUR,Integer.valueOf(etTime.getText().toString()));
                        }
                        if (isNumber(etTimeClear.getText().toString())) {
                            checkParam = true;
                            Prefs.with(context).writeInt(AD_TYPE_CLEAR,Integer.valueOf(etTimeClear.getText().toString()));
                        }

                        if (!checkParam){
                            showToast("存在非数字参数！请检查！",context);
                        }
                    }
                })
                .positiveText("确定")
                .negativeText("取消")
                .show();

        View customView = updateMaterialDialog.getCustomView();
        EditText etPower = customView.findViewById(R.id.etPower);
        EditText etMode = customView.findViewById(R.id.etMode);
        EditText etLiJia = customView.findViewById(R.id.etLiJia);
        EditText etWind = customView.findViewById(R.id.etWind);
        EditText etJiaShi = customView.findViewById(R.id.etJiaShi);
        EditText etTime = customView.findViewById(R.id.etTime);
        EditText etTimeClear = customView.findViewById(R.id.etTimeClear);
        etPower.setText(powerSwitch + "");
        etMode.setText(modeSwitch + "");
        etLiJia.setText(outHomeSwitch + "");
        etWind.setText(speedSwitch + "");
        etJiaShi.setText(humiditySwitch + "");
        etTime.setText(hourSwitch + "");
        etTimeClear.setText(clearSwitch + "");
    }

    public void showSureDialog(Context context, PasswordDialogCallback callback){
        showSureDialog = new MaterialDialog.Builder(context)
                .title("确认该操作?")
                .iconRes(R.drawable.icon_warn)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        callback.success();
                    }
                })
                .positiveText("确定")
                .negativeText("取消")
                .show();
    }

    public void showMultiDialog(Context context) {
        showMultiDialog = new MaterialDialog.Builder(context)
                .content("勾选是否显示加湿、除湿功能")
                .items("加湿", "除湿")
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        return false;
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        Integer[] selectedIndices = dialog.getSelectedIndices();
                        //全部未选择
                        if (selectedIndices.length == 0) {
                            Prefs.with(context).write(CommonUtil.JIA_SHI_STRING, CommonUtil.HIDE_STRING);
                            Prefs.with(context).write(CommonUtil.CHU_SHI_STRING, CommonUtil.HIDE_STRING);
                        }
                        //selectedIndices[0]==0表示加湿选中
                        if (selectedIndices.length == 1) {
                            Prefs.with(context).write(CommonUtil.JIA_SHI_STRING, selectedIndices[0] == 0 ? CommonUtil.SHOW_STRING : CommonUtil.HIDE_STRING);
                            Prefs.with(context).write(CommonUtil.CHU_SHI_STRING, selectedIndices[0] == 1 ? CommonUtil.SHOW_STRING  : CommonUtil.HIDE_STRING);
                        }
                        //全部选中
                        if (selectedIndices.length == 2) {
                            Prefs.with(context).write(CommonUtil.JIA_SHI_STRING, CommonUtil.SHOW_STRING );
                            Prefs.with(context).write(CommonUtil.CHU_SHI_STRING, CommonUtil.SHOW_STRING );
                        }
                    }
                })
                .positiveText("确定")
                .negativeText("取消")
                .show();
    }

    public void setPasswordDialog(Context mContext,String title, String password, PasswordDialogCallback callback) {
        projectSettingDialog = new MaterialDialog.Builder(mContext)
                .title(title)
                .backgroundColor(0xFF3c3f43)
                .titleColor(Color.WHITE)
                .contentColor(Color.WHITE)
                .negativeColor(Color.WHITE)
                .positiveColor(Color.WHITE)
                .titleGravity(GravityEnum.CENTER)
                .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .input("password", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        String txt = input.toString();
                        if (password.equals(txt)) {
                            callback.success();
                        } else {
                            callback.fail();
                        }
                    }
                })
                .positiveText("确定")
                .negativeText("取消")
                .show();
    }

    //?表示出现0次或1次
    private boolean isNumber(String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        return number.matches("\\d+(\\.\\d+)?");
    }

    protected void showToast(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void dismissDialog() {
        if (updateMaterialDialog != null) {
            updateMaterialDialog.dismiss();
        }
        if (addRoomMaterialDialog != null) {
            addRoomMaterialDialog.dismiss();
        }
        if (showMultiDialog!=null){
            showMultiDialog.dismiss();
        }
        if (projectSettingDialog!=null){
            projectSettingDialog.dismiss();
        }
        if (showSureDialog!=null){
            showSureDialog.dismiss();
        }
    }
}

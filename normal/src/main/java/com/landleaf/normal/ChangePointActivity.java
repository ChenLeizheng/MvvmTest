package com.landleaf.normal;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.landleaf.normal.adapter.ChangePointAdapter;
import com.landleaf.normal.base.BaseActivity;
import com.landleaf.normal.bean.NtTempId;
import com.landleaf.normal.bean.Room;
import com.landleaf.normal.interfaces.PasswordDialogCallback;
import com.landleaf.normal.interfaces.RoomPointCallback;
import com.landleaf.normal.utils.DBUtil;
import com.landleaf.normal.utils.MaterialDialogUtil;
import com.landleaf.normal.utils.Prefs;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Lei on 2019/4/22.
 */

public class ChangePointActivity extends BaseActivity {

    private static final String TAG = ChangePointActivity.class.getSimpleName();
    @BindView(R.id.pointListView)
    ListView pointListView;
    private ArrayList<NtTempId> listData;
    private ChangePointAdapter changePointAdapter;

    @Override
    protected void initActivity() {
        listData = new ArrayList<>();
        List<Room> roomList = DBUtil.getInstance().getAllRoom();
        if (!roomList.isEmpty()){
            Gson gson = new Gson();
            for (Room room : roomList) {
                String json = Prefs.with(this).read(room.getRoomName(), "");
                NtTempId ntTempId = gson.fromJson(json, NtTempId.class);
                listData.add(ntTempId);
            }
        }
        changePointAdapter = new ChangePointAdapter(listData, this);
        pointListView.setAdapter(changePointAdapter);
        pointListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NtTempId ntTempId = listData.get(position);
                MaterialDialogUtil.getInstance().addRoomAddress(ChangePointActivity.this, "更新设备点位", true, ntTempId.getRoomName(), new RoomPointCallback() {
                    @Override
                    public void dataCallback(NtTempId ntTempId) {
                        listData.remove(position);
                        listData.add(ntTempId);
                        changePointAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        pointListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                MaterialDialogUtil.getInstance().showSureDialog(ChangePointActivity.this, new PasswordDialogCallback() {
                    @Override
                    public void success() {
                        listData.remove(position);
                        DBUtil.getInstance().deleteRoomByName(listData.get(position).getRoomName());
                        changePointAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void fail() {

                    }
                });
                return false;
            }
        });
    }

    @Override
    protected int getLayoutViewId() {
        return R.layout.activity_change_point;
    }

    @OnClick({R.id.ivBack,R.id.tvAdd,R.id.tvClear,R.id.tvUpdate,R.id.tvChoose})
    public void onViewClick(View view){
        switch (view.getId()){
            case R.id.ivBack:
                finish();
                break;
            case R.id.tvAdd:
                MaterialDialogUtil.getInstance().addRoomAddress(this, "添加新设备", false, "", new RoomPointCallback() {
                    @Override
                    public void dataCallback(NtTempId ntTempId) {
                        listData.add(ntTempId);
                        changePointAdapter.notifyDataSetChanged();
                    }
                });
                break;
            //是否包含加湿除湿
            case R.id.tvChoose:
                MaterialDialogUtil.getInstance().showMultiDialog(this);
                break;
            //修改主机点位
            case R.id.tvUpdate:
                MaterialDialogUtil.getInstance().updateHostAddress(this);
                break;
            case R.id.tvClear:
                MaterialDialogUtil.getInstance().showSureDialog(this, new PasswordDialogCallback() {
                    @Override
                    public void success() {
                        DBUtil.getInstance().clearAllRoom();
                        listData.clear();
                        changePointAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void fail() {

                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        hideNavigation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MaterialDialogUtil.getInstance().dismissDialog();
    }
}

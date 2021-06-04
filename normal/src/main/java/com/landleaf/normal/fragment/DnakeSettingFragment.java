package com.landleaf.normal.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.landleaf.normal.ChangePointActivity;
import com.landleaf.normal.R;
import com.landleaf.normal.base.BaseFragment;
import com.landleaf.normal.dnake.v700.dmsg;
import com.landleaf.normal.dnake.v700.dxml;
import com.landleaf.normal.utils.CommonUtil;
import com.landleaf.normal.utils.Prefs;
import com.landleaf.normal.utils.ThreadPoolManager;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static com.landleaf.normal.utils.CommonUtil.PLC_IP_STRING;
import static com.landleaf.normal.utils.CommonUtil.SERVICE_IP_STRING;


public class DnakeSettingFragment extends BaseFragment {

    @BindView(R.id.etPlcIp)
    TextInputEditText etPlcIp;
    @BindView(R.id.etDeviceIp)
    TextInputEditText etDeviceIp;
    @BindView(R.id.etMask)
    TextInputEditText etMask;
    @BindView(R.id.etGateway)
    TextInputEditText etGateway;
    @BindView(R.id.etDNS)
    TextInputEditText etDNS;
    @BindView(R.id.cbDhcp)
    CheckBox cbDhcp;
    @BindView(R.id.tvDhcpIp)
    TextView tvDhcpIp;
    private dxml p;

    @Override
    protected void initFragment() {
        init();
    }

    private void init() {
        Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(SingleEmitter<Boolean> e) throws Exception {
                dmsg req = new dmsg();
                p = new dxml();
                req.to("/settings/lan/query", null);
                p.parse(req.mBody);
                e.onSuccess(true);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            int dhcp = p.getInt("/params/dhcp", 0);
                            cbDhcp.setChecked(dhcp == 1);
                            etDeviceIp.setText(p.getText("/params/ip"));
                            etMask.setText(p.getText("/params/mask"));
                            etGateway.setText(p.getText("/params/gateway"));
                            etDNS.setText(p.getText("/params/dns"));
                            initView(cbDhcp.isChecked());
                            cbDhcp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    initView(isChecked);
                                }
                            });
                            String PLC_IP = Prefs.with(getContext()).read(PLC_IP_STRING, CommonUtil.PLC_IP);
                            etPlcIp.setText(PLC_IP);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("DnakeSettingFragment", throwable.toString());
                    }
                });
    }

    @Override
    protected int getViewID() {
        return R.layout.fragment_setting_dnake;
    }

    @OnClick({R.id.btSure, R.id.btChangePoints})
    public void onClickedView(View view) {
        switch (view.getId()) {
            case R.id.btSure:
                String PLC_IP = etPlcIp.getText().toString();
                if (CommonUtil.getInstance().ipValidate(PLC_IP)){
                    Prefs.with(getActivity()).write(PLC_IP_STRING,PLC_IP);
                    CommonUtil.PLC_IP = PLC_IP;
                }
                String ip = etDeviceIp.getText().toString();
                if (!CommonUtil.getInstance().ipValidate(ip)) {
                    showWarnToast(getActivity(), "非法ip！修改失败");
                    return;
                }
                String mask = etMask.getText().toString();
                if (!CommonUtil.getInstance().ipValidate(mask)) {
                    showWarnToast(getActivity(), "子网掩码有误！修改失败");
                    return;
                }
                String gateway = etGateway.getText().toString();
                if (!CommonUtil.getInstance().ipValidate(gateway)) {
                    showWarnToast(getActivity(), "网关有误！修改失败");
                    return;
                }
                if (!CommonUtil.getInstance().ipMatch(ip, mask, gateway)) {
                    showWarnToast(getActivity(), "ip与网关不匹配！修改失败");
                    return;
                }

                p = new dxml();
                dmsg req = new dmsg();
                p.setInt("/params/dhcp", cbDhcp.isChecked() ? 1 : 0);
                p.setText("/params/ip", ip);
                p.setText("/params/mask", mask);
                p.setText("/params/gateway", gateway);
                p.setText("/params/dns", etDNS.getText().toString());
                ThreadPoolManager.getInstance().submitJob(new Runnable() {
                    @Override
                    public void run() {
                        req.to("/settings/lan/setup", p.toString());
                    }
                }, THREAD_PRIORITY_BACKGROUND, "change_net");
                showWarnToast(getActivity(), "修改成功！");
                break;
            case R.id.btChangePoints:
                startActivity(new Intent(getActivity(), ChangePointActivity.class));
                break;
            default:
                break;
        }
    }

    private void initView(boolean isChecked) {
        etDeviceIp.setEnabled(!isChecked);
        etMask.setEnabled(!isChecked);
        etGateway.setEnabled(!isChecked);
        etDNS.setEnabled(!isChecked);
        tvDhcpIp.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
        String localIp = CommonUtil.getInstance().getLocalIp();
        if (!TextUtils.isEmpty(localIp)) {
            tvDhcpIp.setText("动态IP:" + localIp);
        }
    }
}

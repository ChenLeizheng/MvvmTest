package com.landleaf.normal.plc;

import android.util.Log;

import com.landleaf.normal.utils.CommonUtil;

import java.util.concurrent.Callable;

import static com.landleaf.normal.plc.PlcOperType.OPER_READ;
import static com.landleaf.normal.plc.PlcOperType.OPER_WRITE;
import static com.landleaf.normal.plc.PlcUnitType.VB;
import static com.landleaf.normal.plc.PlcUnitType.VD;
import static com.landleaf.normal.plc.PlcUnitType.VW;


/**
 * PLC连接线程
 */
public class PlcConnection implements Callable<PlcReturnBean> {

    private static final String TAG = PlcConnection.class.getSimpleName();

    private PlcReturnBean plcReturnBean = new PlcReturnBean();

    private String ip;

    private String operType;

    private int offset;

    private float val;

    private String unit;

    public PlcConnection(String ip, String operType, int offset, String unit) {
        this.ip = ip;
        this.operType = operType;
        this.offset = offset;
        this.unit = unit;
    }

    public PlcConnection(String ip, String operType, int offset, int val, String unit) {
        this.ip = ip;
        this.operType = operType;
        this.offset = offset;
        this.val = val;
        this.unit = unit;
    }

    private PlcConnection(Builder builder) {
        ip = builder.ip;
        operType = builder.operType;
        offset = builder.offset;
        val = builder.val;
        unit = builder.unit;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public PlcReturnBean call() {
        int res;
        int amount = 0;
        S7Client client = S7Client.getInstance();
        client.SetConnectionType(S7.OP);
        byte[] Buffer = new byte[4];
        res = client.ConnectTo(ip, PlcDefine.rack, PlcDefine.slot);
        if (res == 0) {
            Log.i(TAG, "Connected to   : " + ip + " (Rack=" + PlcDefine.rack + ", Slot=" + PlcDefine.slot + ")");
            Log.i(TAG, "PDU negotiated : " + client.PDULength() + " bytes");
            switch (operType) {
                case OPER_READ:
                    switch (unit) {
                        case VD:
                            amount = 4;
                            break;
                        case VW:
                            amount = 2;
                            break;
                        case VB:
                            amount = 1;
                            break;
                    }
                    res = client.ReadArea(S7.S7AreaDB, 1, offset, amount, Buffer);
                    Log.d(TAG, "Now is reading on " + unit + ":" + offset + ",Buffer:" + CommonUtil.getInstance().hexToString(Buffer));
                    if (res == 0) {
                        plcReturnBean.setOk(true);

                        switch (unit) {
                            case VD:
                                plcReturnBean.setRes(S7.GetFloatAt(Buffer, 0));
                                break;
                            case VW:
                                plcReturnBean.setRes(S7.GetShortAt(Buffer, 0));
                                break;
                            case VB:
                                plcReturnBean.setRes(Buffer[0]);
                                break;
                        }


                    }

                    if (res != 0)
                        Log.e(TAG, "read error " + unit + ":" + offset);
                    break;
                case OPER_WRITE:
                    switch (unit) {
                        case VD:
                            amount = 4;
                            S7.SetFloatAt(Buffer, 0, val);
                            break;
                        case VW:
                            amount = 2;
                            S7.SetShortAt(Buffer, 0, (int) val);
                            break;
                        case VB:
                            amount = 1;
                            Buffer[0] = (byte) val;
                            break;
                    }
                    Log.d(TAG, "Now is writing on " + unit + ":" + offset + ",Buffer:" + CommonUtil.getInstance().hexToString(Buffer));
                    res = client.WriteArea(S7.S7AreaDB, 1, offset, amount, Buffer);
                    if (res == 0) {
                        plcReturnBean.setOk(true);
                        plcReturnBean.setRes(val);
                    }
                    if (res != 0) {
                        Log.e(TAG, "write error " + unit + ":" + offset);
                    }
                    break;
            }
        } else {
            Log.e(TAG, "Connect error " + unit + ":" + offset);
        }
        plcReturnBean.setOffset(offset);
        plcReturnBean.setIp(ip);
        plcReturnBean.setErrorCode(res);
        plcReturnBean.setErrorInfo(S7Client.ErrorText(res));
        client.Disconnect();
        return plcReturnBean;
    }

    public static final class Builder {
        private String ip;
        private String operType;
        private int offset;
        private float val;
        private String unit;

        private Builder() {
        }

        public Builder ip(String val) {
            ip = val;
            return this;
        }

        public Builder operType(String val) {
            operType = val;
            return this;
        }

        public Builder offset(int val) {
            offset = val;
            return this;
        }

        public Builder val(float value) {
            val = value;
            return this;
        }

        public Builder unit(String val) {
            unit = val;
            return this;
        }

        public PlcConnection build() {
            return new PlcConnection(this);
        }
    }
}

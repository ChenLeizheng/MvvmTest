package com.landleaf.normal.utils;

import android.text.TextUtils;
import android.util.Log;

import com.landleaf.normal.bean.NtResult;
import com.landleaf.normal.interfaces.PlcHandler;
import com.landleaf.normal.plc.PlcExecutor;
import com.landleaf.normal.plc.PlcUnitType;

import org.greenrobot.eventbus.EventBus;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class CommonUtil {

    public static final String AD_TYPE_HOUR = "时间";//滤网使用时间
    public static final String PLC_IP_STRING = "PLC_IP"; //设置界面更改设备ip
    public static final String SERVICE_IP_STRING = "SERVICE_IP";
    public static final String JIA_SHI_STRING = "JIA_SHI";
    public static final String CHU_SHI_STRING = "CHU_SHI";
    public static final String SHOW_STRING = "1";
    public static final String HIDE_STRING = "0";
    public static String PLC_IP = "192.168.1.100";
    //0-800:舒适 800-1000：良好 1000+：较差
    public static final float CO2_LEVEL_1 = 800.0f;
    public static final float CO2_LEVEL_2 = 1000.0f;

    //40-60：舒适 其他：良好
    public static final float HUMIDITY_LEVEL_1 = 30.0F;
    public static final float HUMIDITY_LEVEL_2 = 70.0F;

    //0-50:舒适 50-100：良好 100+：较差
    public static final float PM25_LEVEL_1 = 50.0F;
    public static final float PM25_LEVEL_2 = 100.0F;
    public static final float TEMP_LEVEL_1 = 18.0F;
    public static final float TEMP_LEVEL_2 = 26.0F;

    public static final int MT_INDEX_TEMP = 8;
    public static final int MT_INDEX_HUMIDITY = 9;
    public static final int MT_INDEX_CO2 = 10;
    public static final int MT_INDEX_VOC = 11;
    public static final int MT_INDEX_PM25 = 0;

    private CommonUtil() {
    }

    public static CommonUtil getInstance() {
        return ViewHolder.instance;
    }

    private static class ViewHolder {
        private static CommonUtil instance = new CommonUtil();
    }

    public int getPm25Level(float pm25) {
        if (pm25 < PM25_LEVEL_1) {
            return 0;
        }
        if (pm25 > PM25_LEVEL_2) {
            return 2;
        }
        return 1;
    }

    public int getHumidityLevel(float humidity) {
        if (humidity >= HUMIDITY_LEVEL_1 && humidity <= HUMIDITY_LEVEL_2) {
            return 0;
        }
        return 1;
    }

    public int getTempLevel(float temp) {
        if (temp >= TEMP_LEVEL_1 && temp <= TEMP_LEVEL_2) {
            return 0;
        }
        return 1;
    }

    public int getCO2Level(float co2) {
        if (co2 > CO2_LEVEL_2) {
            return 2;
        }
        if (co2 < CO2_LEVEL_1) {
            return 0;
        }
        return 1;
    }

    public byte[] getReadSendBytes(int slaveAddress, int functionCode, int startAddress, int readLength) {
        byte[] res = new byte[6];
        res[0] = (byte) slaveAddress;
        res[1] = (byte) functionCode;
        res[2] = (byte) (startAddress >> 8);
        res[3] = (byte) (startAddress & 0xFF);
        res[4] = (byte) (readLength >> 8);
        res[5] = (byte) (readLength & 0xFF);
        return getSendBuf(res);
    }

    public byte[] getSendBuf(byte[] bb) {
        byte[] buffer = new byte[bb.length + 2];
        System.arraycopy(bb, 0, buffer, 0, bb.length);
        int ri = alex_crc16(buffer, buffer.length - 2);
        buffer[buffer.length - 2] = (byte) (0xff & ri);
        buffer[buffer.length - 1] = (byte) ((0xff00 & ri) >> 8);
        return buffer;
    }

    private int alex_crc16(byte[] buf, int len) {
        int i, j;
        int c, crc = 0xFFFF;
        for (i = 0; i < len; i++) {
            c = buf[i] & 0x00FF;
            crc ^= c;
            for (j = 0; j < 8; j++) {
                if ((crc & 0x0001) != 0) {
                    crc >>= 1;
                    crc ^= 0xA001;
                } else
                    crc >>= 1;
            }
        }
        return (crc);
    }

    /**
     * 检查数据是否CRC16校验正确
     *
     * @param bb 待校验的数据
     * @return 校验结果
     */
    public boolean checkBuf(byte[] bb) {
        if (bb == null){
            return false;
        }
        int ri = alex_crc16(bb, bb.length - 2);
        return bb[bb.length - 1] == (byte) ((0xff00 & ri) >> 8) && bb[bb.length - 2] == (byte) (ri & 0xff);
    }

    public byte[] calReadDataArray(byte[] data) {
        int dataLen = data[2] & 0xFF;
        byte[] byteArray = new byte[dataLen];
        System.arraycopy(data, 3, byteArray, 0, dataLen);
        return byteArray;
    }

    public float[] toFloatArray(byte[] bytes) {
        int byteLen = bytes.length;
        if (byteLen % 4 != 0) {
            return null;
        } else {
            int dataLen = bytes.length / 4;
            int startLoc = 0;
            float floatArray[] = new float[dataLen];
            byte[] buffer = new byte[4];
            for (int i = 0; i < dataLen; i++) {
                System.arraycopy(bytes, startLoc, buffer, 0, 4);
                floatArray[i] = Float.intBitsToFloat(GetDIntAt(buffer, 0));
                startLoc += 4;
            }
            return floatArray;
        }
    }

    private int GetDIntAt(byte[] Buffer, int Pos) {
        int Result;
        Result = Buffer[Pos];
        Result <<= 8;
        Result += (Buffer[Pos + 1] & 0x0FF);
        Result <<= 8;
        Result += (Buffer[Pos + 2] & 0x0FF);
        Result <<= 8;
        Result += (Buffer[Pos + 3] & 0x0FF);
        return Result;
    }

    private static final String HEXES = "0123456789ABCDEF";
    private static final String HEX_INDICATOR = "0x";
    private static final String SPACE = " ";

    public String hexToString(byte[] data) {
        if (data != null) {
            StringBuilder hex = new StringBuilder(2 * data.length);
            for (int i = 0; i <= data.length - 1; i++) {
                byte dataAtIndex = data[i];
                hex.append(HEX_INDICATOR);
                hex.append(HEXES.charAt((dataAtIndex & 0xF0) >> 4))
                        .append(HEXES.charAt((dataAtIndex & 0x0F)));
                hex.append(SPACE);
            }
            return hex.toString();
        } else {
            return "";
        }
    }

    public boolean isNumber(String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        return number.matches("(-)?\\d+(\\.\\d+)?");
    }

    public Boolean ipValidate(String addr) {
        final String REGX_IP = "((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)";
        if (!addr.matches(REGX_IP))
            return false;
        return true;
    }

    public Boolean ipMatch(String ip, String mask, String gateway) {
        try {
            byte[] _ip = InetAddress.getByName(ip).getAddress();
            byte[] _mask = InetAddress.getByName(mask).getAddress();
            byte[] _gateway = InetAddress.getByName(gateway).getAddress();
            for (int i = 0; i < 4; i++) {
                _ip[i] &= _mask[i];
                _gateway[i] &= _mask[i];
                if (_ip[i] != _gateway[i])
                    return false;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return true;
    }

    public String getLocalIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress())
                        return inetAddress.getHostAddress().toString();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void plcOperate(String clientId,String operateType,int addressId,float addressValue,String addressType,boolean isTemp){
        try {
            PlcExecutor.getInstance().plcOperateMethod(addressId, addressValue, CommonUtil.PLC_IP, PlcUnitType.VW, operateType, new PlcHandler() {
                @Override
                public void readRes(int offset, float val) {
                    Log.d("PlcSmartHandler", "readRes"+offset);
                    EventBus.getDefault().post(new NtResult(offset, (int) val));
                }

                @Override
                public void writeRes(int offset, float val) {
                    Log.d("PlcSmartHandler", "writeRes"+offset);
                    EventBus.getDefault().post(new NtResult(offset, (int) val));
                }

                @Override
                public void operDebug(String msg) {
                }

                @Override
                public void operRes(boolean operRes, String errorCode, int offset) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

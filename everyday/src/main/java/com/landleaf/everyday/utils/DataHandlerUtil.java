package com.landleaf.everyday.utils;

import com.landleaf.everyday.bean.HydraulicModule;

import java.util.ArrayList;
import java.util.List;

public class DataHandlerUtil {

    private DataHandlerUtil(){}

    public static DataHandlerUtil getInstance(){
        return ViewHolder.instance;
    }

    static class ViewHolder{
        private static DataHandlerUtil instance = new DataHandlerUtil();
    }


    /**
     * 解析数据获取故障下标集合  16个寄存器 ->32byte
     * @param data
     * @return
     */
    public List<Integer> checkErrorCode(byte[] data){
        List<Integer> indexError = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == 0){
                continue;
            }
            for (int j = 0; j < 7; j++) {
                index = i%2==0? ((i+1)*8+j):((i-1)*8+j);
                if ((((data[i]&0xFF)>>>j)&0x01) == 1){
                    indexError.add(index);
                }
            }
        }
        return indexError;
    }

    public HydraulicModule handlerHydraulicData(byte[] data){
        return new HydraulicModule(data[0],data[1],data[2],data[3],data[4]);
    }



    /**
     * 获取发送数据
     * @param slaveAddress
     * @param functionCode
     * @param startAddress
     * @param readLength
     * @return
     */
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
     * 获取需要解析的数据
     * @param data
     * @return
     */
    public byte[] calReadDataArray(byte[] data) {
        int dataLen = data[2] & 0xFF;
        byte[] byteArray = new byte[dataLen];
        System.arraycopy(data, 3, byteArray, 0, dataLen);
        return byteArray;
    }
}

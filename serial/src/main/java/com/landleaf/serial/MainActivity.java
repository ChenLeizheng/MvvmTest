package com.landleaf.serial;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.landleaf.serial.jni.OpenParam;
import com.landleaf.serial.jni.SerialPort;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends Activity {

    public static String TAG = "MainActivity";
    int count = 0;

    static {
        System.loadLibrary("serialport");
    }

    private TextView tvReceiveDate;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();//CPU数
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvReceiveDate = findViewById(R.id.tvReceiveDate);
        final SerialPort serialPort = new SerialPort();
        OpenParam openParam = new OpenParam("S3", 9600, 8, 1, 'n', true);
//        开启串口
        serialPort.openDev(openParam.getDevNum(), openParam.getSpeed(), openParam.getDataBit(), openParam.getStopBit(), openParam.getParity(), openParam.is485Dev(), 0, 0);

        Observable.interval(3, 10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        bytes.clear();
                        byte[] send;
                        for (int i = 0; i < 2; i++) {
                            if (i == 0) {
                                send = getReadSendBytes(1,
                                        4,
                                        0,
                                        24);
                            } else {
                                send = getReadSendBytes(3,
                                        4,
                                        0,
                                        24);
                            }
                            Log.d(TAG, "send:" + hexToString(send) + Thread.currentThread().getName());
                            //发数据
                            serialPort.sendBytes(send);
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //收数据
                            byte[] receiveBytes = serialPort.receiveBytes(53, 1);
                            if (receiveBytes != null && receiveBytes.length < 53) {
                                for (int j = 0; j < receiveBytes.length; j++) {
                                    bytes.add(receiveBytes[j]);
                                }
                            }
                            countTotal++;
                            handleData(receiveBytes, serialPort);
                            String msg = String.format(Locale.CHINA, "300us SerialPort Receive true:%d<--%d", countTrue, countTotal);
                            tvReceiveDate.setText(msg);
                            Log.d(TAG, msg);
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        //获取发送数据 地址1 功能码4 读取24个寄存器
//                        byte[] send = getReadSendBytes(1,
//                                4,
//                                0,
//                                24);
//                        Log.d(TAG, "send:" + hexToString(send)+Thread.currentThread().getName());
//                        //发数据
//                        serialPort.sendBytes(send);
//                        try {
//                            Thread.sleep(100);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                        //收数据
//                        byte[] receiveBytes = serialPort.receiveBytes(53, 1);
//                        if (receiveBytes != null && receiveBytes.length < 53) {
//                            for (int i = 0; i < receiveBytes.length; i++) {
//                                bytes.add(receiveBytes[i]);
//                            }
//                        }
//                        countTotal++;
//                        handleData(receiveBytes, serialPort);
//                        Log.d(TAG, String.format(Locale.CHINA, "200us SerialPort Receive true:%d<--%d", countTrue, countTotal));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, throwable.toString());
                    }
                });
    }

    private int countTotal = 0;
    private int countTrue = 0;
    ArrayList<Byte> bytes = new ArrayList<>();

    private void handleData(byte[] receiveBytes, SerialPort serialPort) {
        if (bytes.size() > 0 && bytes.get(0) != 3) {
            return;
        }
        if (receiveBytes != null) {
            Log.d(TAG, "receiveBytesLength:" + receiveBytes.length + ",receiveBytes:" + hexToString(receiveBytes));
            //crc校验
            if (receiveBytes.length == 53 && checkBuf(receiveBytes)) {
                countTrue++;
                //获取读到的24个寄存器  48byte的数据
                byte[] readDataArray = calReadDataArray(receiveBytes);
                //数据解析得到对应寄存器的float值
                float[] floatArray1 = toFloatArray(readDataArray);
                Log.d(TAG, "pm25:" + floatArray1[0] + ",temp" + floatArray1[8] + ",humidity:" + floatArray1[9] + ",co2:" + floatArray1[10] + ",voc:" + floatArray1[11]);
            } else {
                count++;
                Log.e(TAG, "crc校验失败" + count);
                byte[] date = serialPort.receiveBytes(53, 1);

                if (date != null) {
                    for (int i = 0; i < date.length; i++) {
                        bytes.add(date[i]);
                    }
                } else {
                    Log.d(TAG, "receive null");
                }
                handleData(date, serialPort);
            }
        } else {
            Log.d(TAG, "bytes:" + bytes.size());
            if (bytes.size() <= 53) {
                byte[] a = new byte[53];
                for (int i = 0; i < bytes.size(); i++) {
                    a[i] = bytes.get(i);
                }
                Log.d(TAG, "ping" + hexToString(a));
                handleData(a, serialPort);
            }
            Log.d(TAG, "receive data null!");
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}

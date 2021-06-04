package com.landleaf.serial;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.landleaf.serial.jni.OpenParam;
import com.landleaf.serial.jni.SerialPort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class TestActivity extends Activity {
//#define DEVICE_NAME "/dev/ioctrl"
// Fd485 = open(DEVICE_NAME, O_RDONLY);
    static {
        System.loadLibrary("serialport");
    }

    public static String TAG = "TestActivity";
    private Button btSend;
    private TextView tvReceiveDate;
    private TextView tvSend;
    private Button btSure;
    private EditText etAddress;
    private byte[] send;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    int count = 0;
    int countError = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        btSend = findViewById(R.id.btSend);
        btSure = findViewById(R.id.btSure);
        etAddress = findViewById(R.id.etAddress);
        tvReceiveDate = findViewById(R.id.tvReceiveDate);
        tvSend = findViewById(R.id.tvSend);
        final SerialPort serialPort = new SerialPort();

//        OpenParam openParam = new OpenParam("MT1", 9600, 8, 1, 'e', true);
//        //开启串口
//        serialPort.openDev(openParam.getDevNum(), openParam.getSpeed(), openParam.getDataBit(), openParam.getStopBit(), openParam.getParity(), openParam.is485Dev(), 0, 0);
        //获取发送数据 地址1 功能码4 读取24个寄存器
        send = getReadSendBytes(1,
                4,
                0,
                24);
        //Send:-->0x01 0x04 0x00 0x00 0x00 0x18 0xF0 0x00
        String sendStr = hexToString(send);
        tvSend.setText(sendStr);

        //15  S0   10  MT1
        Observable.interval(2, 10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        OpenParam openParam = new OpenParam("S0", 9600, 8, 1, 'n', true);
                        serialPort.openDev(openParam.getDevNum(), openParam.getSpeed(), openParam.getDataBit(), openParam.getStopBit(), openParam.getParity(), openParam.is485Dev(), 0, 0);
                        serialPort.sendBytes(send);
                        Log.d(TAG, "send:" + hexToString(send));
                        count ++;
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //收数据
                        byte[] receiveBytes = serialPort.receiveBytes(53, 1);
                        if (receiveBytes != null) {
                            if (!checkBuf(receiveBytes)){
                                Log.d(TAG, "crc校验失败");
                                countError ++;
                            }else {
                                //获取读到的24个寄存器  48byte的数据
                                byte[] readDataArray = calReadDataArray(receiveBytes);
                                //数据解析得到对应寄存器的float值
                                float[] floatArray1 = toFloatArray(readDataArray);
                                Log.d(TAG, "pm25:"+floatArray1[0]+",temp"+floatArray1[8]+",humidity:"+
                                        floatArray1[9]+",co2:"+floatArray1[10]+",voc:"+floatArray1[11]);
                            }
                            Log.d(TAG, "receiveBytesLength:" + receiveBytes.length + ","+countError+"/"+count+",receiveBytes:" + hexToString(receiveBytes));
                        }else {
                            countError ++;
                        }
                        serialPort.closeDev();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenParam openParam = new OpenParam("MT1", 9600, 8, 1, 'n', true);
                serialPort.openDev(openParam.getDevNum(), openParam.getSpeed(), openParam.getDataBit(), openParam.getStopBit(), openParam.getParity(), openParam.is485Dev(), 0, 0);
                //发数据
                serialPort.sendBytes(send);
                Log.d(TAG, "send:" + hexToString(send));
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //收数据
                byte[] receiveBytes = serialPort.receiveBytes(53, 1);
                if (receiveBytes != null) {
                    Log.d(TAG, "receiveBytesLength:" + receiveBytes.length + ",receiveBytes:" + hexToString(receiveBytes));
                    tvReceiveDate.setText("receiveBytesLength:" + receiveBytes.length + ",receiveBytes:" + hexToString(receiveBytes));
                    //crc校验
                    if (checkBuf(receiveBytes)){
                        //获取读到的24个寄存器  48byte的数据
                        byte[] readDataArray = calReadDataArray(receiveBytes);
                        //数据解析得到对应寄存器的float值
                        float[] floatArray1 = toFloatArray(readDataArray);
                        Log.d(TAG, "pm25:"+floatArray1[0]+",temp"+floatArray1[8]+",humidity:"+
                                floatArray1[9]+",co2:"+floatArray1[10]+",voc:"+floatArray1[11]);
                    }else {
                        Log.d(TAG, "crc校验失败");
                    }
                }else {
                    Log.d(TAG, "receive data null!");
                    tvReceiveDate.setText("receive data null!");
                }
                serialPort.closeDev();
            }
        });

        btSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String str = etAddress.getText().toString();
//                if (TextUtils.isEmpty(str)){
//                    Toast.makeText(TestActivity.this, "地址为空哦!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (!str.matches("\\d")){
//                    Toast.makeText(TestActivity.this, "非法参数!地址为整数。", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                Integer address = Integer.valueOf(str);
//                send = getReadSendBytes(address,
//                        4,
//                        0,
//                        24);
//                tvSend.setText(hexToString(send));
            }
        });
    }


    /**
     * 检查数据是否CRC16校验正确
     *
     * @param bb 待校验的数据
     * @return 校验结果
     */
    public boolean checkBuf(byte[] bb) {
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
}

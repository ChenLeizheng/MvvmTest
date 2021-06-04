package com.landleaf.serial;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.landleaf.serial.dnake.HexData;
import com.landleaf.serial.dnake.Rs485Executor;
import com.landleaf.serial.dnake.v700.vt_uart;
import com.landleaf.serial.jni.OpenParam;
import com.landleaf.serial.jni.SerialPort;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SimpleTestActivity extends Activity {
    static {
        System.loadLibrary("serialport");
    }

    int device = 0;
    int address = 1;
    boolean init = false;
    private TextView tvReceiveDate;
    private TextView tvSend;
    private TextView tvParseData;
    private byte[] receiveBytes;
    private SerialPort serialPort;

    int countTotal = 0;
    int countTrue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_test);
        Spinner spinnerAddress = findViewById(R.id.spinnerAddress);
        Spinner spinnerDevice = findViewById(R.id.spinnerDevice);
        Button btSend = findViewById(R.id.btSend);
        Button btReadStatus = findViewById(R.id.btReadStatus);
        Button btWriteStatus = findViewById(R.id.btWriteStatus);

        tvReceiveDate = findViewById(R.id.tvReceiveDate);
        tvParseData = findViewById(R.id.tvParseData);
        tvSend = findViewById(R.id.tvSend);


        btReadStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //0x01 0x03 0x00 0x0B 0x00 0x04
                Observable.interval(1, 10, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                countTotal++;
//                                byte[] readStatus = getReadSendBytes(1, 3, 11, 4);
//                                byte[] readStatus = getReadSendBytes(21, 3, 100, 5);
                                byte[] readStatus = getReadSendBytes(1, 3, 0x296, 8);
                                Rs485Executor.getInstance().send(readStatus);
                                Log.d("SimpleTestActivity", "send->"+HexData.hexToString(readStatus));
                                try {
                                    receiveBytes = Rs485Executor.getInstance().receive(readStatus, 300);
                                    Log.d("SimpleTestActivity", "-->receive:"+HexData.hexToString(receiveBytes));
                                    if (checkBuf(receiveBytes)){
                                        countTrue++;
                                        byte[] readDataArray = calReadDataArray(receiveBytes);
                                        int[] floats = toIntArray(readDataArray);
                                        OemAirBean oemAirBean = new OemAirBean(floats[0], floats[1], floats[2], floats[3], floats[4], floats[5], floats[6], floats[7]);
                                        Log.d("SimpleTestActivity", oemAirBean.toString());
//                                        LLAirBean llAirBean = new LLAirBean(floats[0], floats[1], floats[2], floats[3], floats[4]);
//                                        Log.d("SimpleTestActivity", "llAirBean:" + llAirBean);
//                                        Log.d("SimpleTestActivity", "runningMode->"+floats[0]+",windSpeed->"+floats[1]+",co2->"+floats[2]+",temp->"+floats[3]);
                                    }else {
                                        Log.e("SimpleTestActivity", "CRC校验失败！");
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Log.d("SimpleTestActivity", "countTrue/countTotal:" + countTrue+"<--"+countTotal);
                            }

                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e("SimpleTestActivity", throwable.toString());
                            }
                        });
            }
        });

        btWriteStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable.interval(1, 2, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                countTotal++;
                                byte[] setModeData = getSetModeData(1, countTotal%4);
                                if (setModeData==null){
                                    Log.d("SimpleTestActivity", "写入参数异常!");
                                    return;
                                }
                                Rs485Executor.getInstance().send(setModeData);
                                try {
                                    receiveBytes = Rs485Executor.getInstance().receive(setModeData, 300);
                                    if (receiveBytes!=null && receiveBytes.length==setModeData.length){
                                        countTrue++;
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                Log.d("SimpleTestActivity", "send->"+HexData.hexToString(setModeData));
                                Log.d("SimpleTestActivity", "receive->"+HexData.hexToString(setModeData));
                                Log.d("SimpleTestActivity", "countTrue/countTotal:" + countTrue+"<--"+countTotal);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.d("SimpleTestActivity", throwable.toString());
                            }
                        });

            }
        });


        spinnerAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] addressStr = getResources().getStringArray(R.array.address);
                address = position+1;
                Log.d("SimpleTestActivity", "device:" + address+".."+addressStr[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                device = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init = !init;
                if(init){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            vt_uart.setup(1,3);
                        }
                    }).start();

                }else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            vt_uart.setup(0,3);
                        }
                    }).start();

                }
                //获取发送数据 地址1 功能码4 读取24个寄存器
//                byte[] send = getReadSendBytes(1,
//                        4,
//                        0,
//                        24);
//                tvSend.setText(hexToString(send));
//                //冠林
//                if (device == 0){
//                    if (serialPort==null){
//                        serialPort = new SerialPort();
//                        OpenParam openParam = new OpenParam("S3", 9600, 8, 1, 'n', true);
//                        //开启串口
//                        serialPort.openDev(openParam.getDevNum(), openParam.getSpeed(), openParam.getDataBit(), openParam.getStopBit(), openParam.getParity(), openParam.is485Dev(), 0, 0);
//                    }
//                    serialPort.sendBytes(send);
//                    try {
//                        Thread.sleep(300);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    //收数据
//                    receiveBytes = serialPort.receiveBytes(53, 1);
//                }else {
//                    if (!init){
//                        Rs485Executor.getInstance().setup();
//                        init = true;
//                    }
//                    Rs485Executor.getInstance().send(send);
//                    try {
//                        receiveBytes = Rs485Executor.getInstance().receive(send, 300);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                if (receiveBytes != null) {
//                    tvReceiveDate.setText("receiveBytesLength:" + receiveBytes.length + ",receiveBytes:" + hexToString(receiveBytes));
//                    //crc校验
//                    if (checkBuf(receiveBytes)){
//                        //获取读到的24个寄存器  48byte的数据
//                        byte[] readDataArray = calReadDataArray(receiveBytes);
//                        //数据解析得到对应寄存器的float值
//                        float[] floatArray1 = toFloatArray(readDataArray);
//                        tvParseData.setText("pm25:"+floatArray1[0]+",temp"+floatArray1[8]+",humidity:"+floatArray1[9]+",co2:"+floatArray1[10]+",voc:"+floatArray1[11]);
//                    }else {
//                        tvParseData.setText("crc校验失败");
//                    }
//                }else {
//                    tvReceiveDate.setText("receive data null!");
//                    tvParseData.setText("");
//                }
            }
        });
    }

    /**
     * BCC异或校验法
     */
    public byte getBccCheck(byte[] bytes){
        byte bcc = 0;
        for (int i = 0; i < bytes.length; i++) {
            bcc = (byte) (bcc ^ bytes[i]);
        }
        return bcc;
    }


    /**
     * 检查数据是否CRC16校验正确
     *
     * @param bb 待校验的数据
     * @return 校验结果
     */
    public boolean checkBuf(byte[] bb) {
        if (bb == null) {
            return false;
        } else {
            int ri = alex_crc16(bb, bb.length - 2);
            return bb[bb.length - 1] == (byte) ((0xff00 & ri) >> 8) && bb[bb.length - 2] == (byte) (ri & 0xff);
        }
    }

    /**
     * @param deviceAddress  设备地址
     * @param writeAddress  地址 0001H：工作模式，地址 0002H：风速，地址 0003H：设置 CO2 动作浓度
     * @param writeValue    工作模式（0：关机 1：自动模式 2：手动模式 3：定时模式），风速（0：停止 1：低速 2：中速 3：高速），设置 CO2 动作浓度（数据范围： 350——600）
     */
    public byte[] getKFWindControlData(int deviceAddress,int writeAddress,int writeValue){
        return getReadSendBytes(deviceAddress, Constant.KF_WRITE_FUNCTION, writeAddress, writeValue);
    }

    public byte[] getSetModeData(int deviceAddress,int writeValue){
        if (writeValue<=3 && writeValue>=0){
            return getKFWindControlData(deviceAddress,Constant.KF_WRITE_MODE_ADDRESS,writeValue);
        }
        return null;
    }

    public byte[] getSetWindData(int deviceAddress,int writeValue){
        if (writeValue<=3 && writeValue>=0){
            return getKFWindControlData(deviceAddress,Constant.KF_WRITE_WIND_SPEED_ADDRESS,writeValue);
        }
        return null;
    }

    //设置CO2动作浓度   当到达某个值处理相应逻辑，改风量等
    public byte[] getSetCO2Data(int deviceAddress,int writeValue){
        if (writeValue<=600 && writeValue>=350){
            return getKFWindControlData(deviceAddress,Constant.KF_WRITE_CO2_ADDRESS,writeValue);
        }
        return null;
    }





    public byte[] calReadDataArray(byte[] data) {
        int dataLen = data[2] & 0xFF;
        byte[] byteArray = new byte[dataLen];
        System.arraycopy(data, 3, byteArray, 0, dataLen);
        return byteArray;
    }

    public int[] toIntArray(byte[] bytes){
        int byteLen = bytes.length;
        if (byteLen%2!=0){
            return null;
        }
        int dataLen = bytes.length/2;
        int intArray[] = new int[dataLen];

        byte[] buffer = new byte[4];
        for (int i = 0; i < dataLen; i++) {
            intArray[i] = (0x0000<<16)|(bytes[i*2]&0xFF)<<8|(bytes[i*2+1]&0xFF);
        }
        return intArray;
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

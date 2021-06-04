package com.landleaf.normal.dnake;


import com.landleaf.normal.dnake.v700.dmsg;
import com.landleaf.normal.dnake.v700.ioctl;
import com.landleaf.normal.dnake.v700.vt_uart;

/**
 * Created by thinkpad on 2017/8/10.
 * <p>
 * 狄耐克485通讯类库
 */
public class Rs485Executor {
    private static final Rs485Executor ourInstance = new Rs485Executor();

    private static final String TAG = "Rs485Executor";

    public static Rs485Executor getInstance() {
        return ourInstance;
    }

    private Rs485Executor() {
    }

    //初始化串口服务
    public void setup() {
        // 初始化进程通讯事件
        dmsg.start("/exApp");

        // RS485初始化
        vt_uart.start();
        vt_uart.setup(0, 3);

        // 12V DO关闭
        ioctl.hooter(0);
    }

    public void send(byte[] data) {
        vt_uart.tx(data);
    }

    public byte[] receive(byte[] sendBytes, int timeOut) throws InterruptedException {
        return vt_uart.rx(getReceiveLen(sendBytes), timeOut);
    }


    //获取期望收到数据的Buffer长度
    public static int getReceiveLen(byte[] send) {
        int functionCode = send[1];
        int readLen = GetShortAt(send, 4);
        int bufferLen = 0;
        switch (functionCode) {
            case 0x01:
                //读取线圈
                if (readLen % 8 != 0) {
                    bufferLen = 5 + readLen / 8 + 1;
                } else {
                    bufferLen = 5 + readLen / 8;
                }
                break;
            case 0x03:
            case 0x04:
                //读多个寄存器
                bufferLen = 5 + readLen * 2;//24*2+5=52-----
                break;
            case 0x06:
                //单点写寄存器
                bufferLen = 8;
                break;
            case 0x10:
                //多点写寄存器:从站地址1+功能码1+数据地址2+数据个数2+数据区字节数*2+校验位2
                bufferLen = 8;
                break;
            default:
                break;
        }
        return bufferLen;
    }

    public static int GetShortAt(byte[] Buffer, int Pos) {
        int hi = (Buffer[Pos]);
        int lo = (Buffer[Pos + 1] & 0x00FF);
        return ((hi << 8) + lo);
    }
}

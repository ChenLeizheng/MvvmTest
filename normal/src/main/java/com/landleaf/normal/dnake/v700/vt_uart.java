package com.landleaf.normal.dnake.v700;

import android.util.Log;


import org.apache.mina.core.buffer.IoBuffer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

public class vt_uart {

    private static final int tPort = 10060;
    private static final int rPort = 10061;

    private static final String ip = "127.0.0.1";

    private static DatagramSocket uart;

    private static final int BUFFER_SIZE = 512;

    public static Boolean start() {// 线程状态反馈
        try {
            uart = new DatagramSocket(rPort);
            byteBuffer.setAutoExpand(true);

            vt_uart_thread u = new vt_uart_thread();
            Thread thread = new Thread(u);
            thread.start();

            return true;
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static IoBuffer byteBuffer = IoBuffer.allocate(BUFFER_SIZE);

    /**
     * 数据的接收线程
     */
    private static class vt_uart_thread implements Runnable {
        @Override
        public void run() {
            Log.d("vt_uart", !Thread.interrupted()+"");
            while (!Thread.interrupted()) {
                try {
                    byte[] data = new byte[byteBuffer.limit()];
                    DatagramPacket p = new DatagramPacket(data, data.length);
                    uart.receive(p);
                    if (p.getLength() > 0) {
                        byte[] msg = new byte[p.getLength()];
                        System.arraycopy(data, 0, msg, 0, p.getLength());
                        byteBuffer.put(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void setup(int pb, int br) {// pb 0:无校验 1:偶校验 2:奇校验
        // br 0:1200 1:2400 2:4800 3:9600 4:19200 5:38400 6:57600 7:115200
        dmsg req = new dmsg();
        dxml p = new dxml();
        p.setInt("/params/parity", pb);
        p.setInt("/params/speed", br);
        req.to("/control/vt_uart/setup", p.toString());
    }

    /*
     * 发送数据
     *
     * @param data 发送的数据
     *
     * @param length 数据的长度
     */
    public static void tx(byte[] data) {
        if (uart != null) {
            try {
                DatagramPacket p = new DatagramPacket(data, data.length, InetAddress.getByName(ip), tPort);
                uart.send(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 截取缓存区的数据
     *
     * @param len     截取的数据长度
     * @param timeOut 接收的时限(依据实际情况设定时限，建议大于20)
     * @return 获取的数据
     */
    public static byte[] rx(int len, int timeOut) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(timeOut);
        byteBuffer.flip();
        byte[] read = new byte[len];
        if (byteBuffer.limit() >= len)
            byteBuffer.get(read);
        //重新分配
        byteBuffer = IoBuffer.allocate(BUFFER_SIZE);
        byteBuffer.setAutoExpand(true);
        return read;
    }
}

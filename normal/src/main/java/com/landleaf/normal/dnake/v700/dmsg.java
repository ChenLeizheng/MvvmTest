package com.landleaf.normal.dnake.v700;

import android.os.StrictMode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class dmsg {
    private static DatagramSocket ds;

    private static InetAddress rAddr = null;
    private static int rPort = 0;

    private static class __msg_port {
        String url;
        int port;
    }

    private static __msg_port[] msg_port = null;

    private static int mPort(String url) {
        setup_port();

        for (__msg_port aMsg_port : msg_port) {
            if (url.indexOf(aMsg_port.url) == 0)
                return aMsg_port.port;
        }
        return -1;
    }

    public static void setup_port() {
        if (msg_port == null) {
            msg_port = new __msg_port[10];

            msg_port[0] = new __msg_port();
            msg_port[0].url = "/talk";
            msg_port[0].port = 9800;

            msg_port[1] = new __msg_port();
            msg_port[1].url = "/ui";
            msg_port[1].port = 9801;

            msg_port[2] = new __msg_port();
            msg_port[2].url = "/monitor";
            msg_port[2].port = 9802;

            msg_port[3] = new __msg_port();
            msg_port[3].url = "/upgrade";
            msg_port[3].port = 9803;

            msg_port[4] = new __msg_port();
            msg_port[4].url = "/control";
            msg_port[4].port = 9804;

            msg_port[5] = new __msg_port();
            msg_port[5].url = "/security";
            msg_port[5].port = 9830;

            msg_port[6] = new __msg_port();
            msg_port[6].url = "/smart";
            msg_port[6].port = 9831;

            msg_port[7] = new __msg_port();
            msg_port[7].url = "/apps";
            msg_port[7].port = 9832;

            msg_port[8] = new __msg_port();
            msg_port[8].url = "/settings";
            msg_port[8].port = 9833;

            msg_port[9] = new __msg_port();
            msg_port[9].url = "/exApp";
            msg_port[9].port = 9850;

            //4.0以上版本强制开启网络发送
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public static Boolean start(String url) {
        int port = mPort(url);
        if (port < 0)
            return false;

        try {
            ds = new DatagramSocket(port, InetAddress.getByName("127.0.0.1"));
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }

        DmsgThread d = new DmsgThread();
        Thread thread = new Thread(d);
        thread.start();

        return true;
    }

    public static void ack(int result, String body) {
        if (rAddr == null)
            return;

        String s;
        if (body != null) {
            s = "MSG/1.0 " + result + " OK\r\n\r\n" + body;
        } else
            s = "MSG/1.0 " + result + " OK\r\n\r\n";
        byte[] data = s.getBytes();

        DatagramPacket p = new DatagramPacket(data, data.length, rAddr, rPort);
        try {
            ds.send(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String mBody = null;

    public int to(String url, String body) {
        mBody = null;

        int port = mPort(url);
        if (port < 0)
            return 0;

        DatagramSocket s = null;
        try {
            s = new DatagramSocket();
            String ss = null;
            if (body != null)
                ss = "POST " + url + " MSG/1.0\r\n\r\n" + body;
            else
                ss = "POST " + url + " MSG/1.0\r\n\r\n";
            byte[] data = ss.getBytes();

            DatagramPacket p = new DatagramPacket(data, data.length,
                    InetAddress.getByName("127.0.0.1"), port);
            s.setSoTimeout(500);
            s.send(p);

            data = new byte[8 * 1024];
            p = new DatagramPacket(data, data.length);
            s.receive(p);

            if (p.getLength() < 8) {
                s.close();
                return 0;
            }

            if (!is_utf8(p.getData(), p.getLength())) {
                try {
                    ss = new String(p.getData(), 0, p.getLength(), "GBK");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else
                ss = new String(p.getData(), 0, p.getLength());

            StringTokenizer tk = new StringTokenizer(ss, " ");
            tk.nextToken(); // 丢弃协议版本
            String result = tk.nextToken();

            int idx = ss.indexOf("\r\n\r\n");
            if (idx > 0) {
                idx += 4;
                mBody = ss.substring(idx);
            }
            return Integer.parseInt(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (s != null)
            s.close();
        return -1;
    }

    private static void process(DatagramPacket dp) {
        String data = null;
        if (!is_utf8(dp.getData(), dp.getLength())) {
            try {
                data = new String(dp.getData(), 0, dp.getLength(), "GBK");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else
            data = new String(dp.getData(), 0, dp.getLength());

        StringTokenizer tk = new StringTokenizer(data, " ");
        String method = tk.nextToken();
        String url = tk.nextToken();

        String body = null;
        int idx = 0;
        if (data != null) {
            idx = data.indexOf("\r\n\r\n");
        }
        if (idx > 0) {
            idx += 4;
            body = data.substring(idx);
        }

        if (url != null && method != null && method.equals("POST"))
            devent.event(url, body);
    }

    public static class DmsgThread implements Runnable {
        @Override
        public void run() {
            byte[] data = new byte[128 * 1024];
            while (true) {
                DatagramPacket dp = new DatagramPacket(data, data.length);
                try {
                    ds.receive(dp);
                    rAddr = dp.getAddress();
                    rPort = dp.getPort();
                    process(dp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                rAddr = null;
            }
        }
    }

    private static Boolean is_utf8(byte[] data, int length) {
        if (data == null)
            return true;
        Boolean asc = true;
        int utf8_n = 0, gbk_n = 0;
        for (int i = 0; i < length; i++) {
            if (data[i] > 0 && data[i] < 0x7F)
                continue;
            asc = false;
            if ((data[i] & 0xE0) == 0xC0 && i + 1 <= data.length) {
                if ((data[i + 1] & 0xC0) == 0x80) {
                    int n = data[i] & 0xFF;
                    int n2 = data[i + 1] & 0xFF;
                    if ((0x81 <= n && n <= 0xFE) && (0x40 <= n2 && n2 <= 0xFE) && (n2 != 0x7F)) {
                        System.out.println("Not do!");
                    } else {
                        utf8_n++;
                        i++;
                        continue;
                    }
                }
            } else if ((data[i] & 0xF0) == 0xE0 && i + 2 <= data.length) {
                if (((data[i + 1] & 0xC0) == 0x80)
                        && ((data[i + 2] & 0xC0) == 0x80)) {
                    utf8_n++;
                    i += 2;
                    continue;
                }
            } else if ((data[i] & 0xF8) == 0xF0 && i + 3 <= data.length) {
                if (((data[i + 1] & 0xC0) == 0x80)
                        && ((data[i + 2] & 0xC0) == 0x80)
                        && ((data[i + 3] & 0xC0) == 0x80)) {
                    utf8_n++;
                    i += 3;
                    continue;
                }
            }
            i++;
            gbk_n++;
        }
        return asc || !(gbk_n > 0 && 10 * utf8_n < gbk_n);
    }
}

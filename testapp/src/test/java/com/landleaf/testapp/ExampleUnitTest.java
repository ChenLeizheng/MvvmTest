package com.landleaf.testapp;

import com.google.gson.Gson;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws ExecutionException, InterruptedException {
        assertEquals(4, 2 + 2);
        write(0);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    for (int i = 0; i < 20; i++) {
                        write(i);
                    }
                }
            }
        });
        thread.setName("test1");


        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 20; i++) {
                    write(i);
                }
            }
        });
        thread1.setName("test2");
        thread1.start();
        thread.start();
    }

    private synchronized void write(int i){
        try {
            Thread.currentThread().sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("write"+Thread.currentThread().getName()+","+i);
    }

    private void testAAA() {
        //        Person person = new Person("荼蘼", "18888888888", "woman");
//        PersonProto.Person personProto = PersonProto.Person.newBuilder()
//                .setName("荼蘼")
//                .setPhone("18888888888")
//                .setSex("woman")
//                .build();
//        Gson gson = new Gson();
//        String s = gson.toJson(person);
//        System.out.println("proto:"+personProto.toByteArray().length);
//        System.out.println("json:"+s.getBytes().length);
//        byte[] bytes = {0x00,0x01,0x01,0x11};
//        int[] ints = toIntArray(bytes);
//        int[] ints1 = toIntArray2(bytes);
//        System.out.println(ints[0]+","+ints[1]);
//        System.out.println(ints1[0]+","+ints1[1]);

//        Person p = person;
//        p.setName("zn");
//        System.out.println(p == person);  //true
//        System.out.println(p);
//        System.out.println(person);
//
//        Person p2 = person.clone();
//        p2.setName("tu");
//        System.out.println(p2 == person); //false
//        System.out.println(p2);
//        System.out.println(person);
//        FutureTask<String> task = new FutureTask<String>(new MyThread());
//        System.out.println("===================");
//        new Thread(task).start();    //启动该线程
//        System.out.println(task.get());
//
//        System.out.println("++++++++");
//        Future<?> future = TaskExecutor.getInstance().submitSingleJob(new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                return "test";
//            }
//        }, "test", THREAD_PRIORITY_BACKGROUND, null);
//        String o = (String) future.get();
//        System.out.println(o);
//        byte b1 = 0x08;
//        byte b2 = 0x01;
//        String s = hexToString(new byte[]{(byte) (b1 << 4 | b2)});
//        System.out.println(s);
//        System.out.println(0x80 |0x02);
//        System.out.println( hexToString(new byte[]{(byte) (0x80 | 0x02)}));

//        byte[] bytes = new byte[6];
//        bytes[0] = 0x55;
//        bytes[1] = 0x11;
//        bytes[2] = 0x11;
//        bytes[3] = 0x00;
//        bytes[4] = 0x00;
//        byte bccCheck = getBccCheck(bytes);
//        bytes[5] = bccCheck;
//        System.out.println(hexToString(bytes));
    }

    private static final String HEXES = "0123456789ABCDEF";
    private static final String HEX_INDICATOR = "0x";
    private static final String SPACE = " ";
    public static String hexToString(byte[] data) {
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

    /**
     * BCC异或校验法
     */
    public byte getBccCheck(byte[] bytes){
        byte bcc = 0;
        for (int i = 0; i < bytes.length-1; i++) {
            bcc = (byte) (bcc ^ bytes[i]);
        }
        return bcc;
    }

    public int[] toIntArray(byte[] bytes) {
        int byteLen = bytes.length;
        if (byteLen % 2 != 0) {
            return null;
        }
        int dataLen = bytes.length / 2;
        int intArray[] = new int[dataLen];

        byte[] buffer = new byte[4];
        for (int i = 0; i < dataLen; i++) {
            intArray[i] = (0x0000 << 16) | (bytes[i * 2] & 0xFF) << 8 | (bytes[i * 2 + 1] & 0xFF);
        }
        return intArray;
    }

    public static int[] toIntArray2(byte[] bytes) {
        int byteLen = bytes.length;
        if (byteLen % 2 != 0) {
            return new int[bytes.length];
        } else {
            int dataLen = bytes.length / 2;
            int startLoc = 0;
            int intArray[] = new int[dataLen];
            byte[] buffer = new byte[2];
            for (int i = 0; i < dataLen; i++) {
                System.arraycopy(bytes, startLoc, buffer, 0, 2);
                intArray[i] = GetShortAt(buffer, 0);
                startLoc += 2;
            }
            return intArray;
        }
    }

    public static int GetShortAt(byte[] Buffer, int Pos) {
        int hi = (Buffer[Pos]);
        int lo = (Buffer[Pos + 1] & 0x00FF);
        return ((hi << 8) + lo);
    }
}
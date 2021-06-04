package com.landleaf.everyday;

import com.landleaf.everyday.gof.proxy.IUserController;
import com.landleaf.everyday.gof.proxy.MetricsCollectorProxy;
import com.landleaf.everyday.gof.proxy.UserController;

import org.junit.Test;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        MetricsCollectorProxy metricsCollectorProxy = new MetricsCollectorProxy();
        IUserController proxy = metricsCollectorProxy.createProxy(new UserController());
        //start===========
        //UserController login
        //end===========
        proxy.login("aa","123");
        String unit = "℃";
        System.out.println(unit.length());
//        assertEquals(4, 2 + 2);
//        int[] array = {95,94,96,99,98,96};
//        countSort(array);
        //编码
        String encode = Base64.getEncoder().encodeToString("chen".getBytes());
        System.out.println("编码encode:"+encode);
        //解码
        byte[] decode = Base64.getDecoder().decode(encode);
        System.out.println(new String(decode));
    }


    //计数排序
    private void countSort(int[] array){
        int max = array[0];
        int min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (min>array[i]){
                min = array[i];
            }
            if (max<array[i]){
                max = array[i];
            }
        }
        int[] countArray = new int[max - min +1];
        for (int i : array) {
            countArray[i-min] ++;
        }

        for (int i = 1; i < countArray.length; i++) {
            countArray[i] += countArray[i-1];
        }
        int[] sortArray = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            System.out.println(countArray[array[i]-min]-1);
            sortArray[countArray[array[i]-min]-1] = array[i];
            countArray[array[i]-min]--;
        }
        System.out.println(Arrays.toString(sortArray));
    }

    private void check485() {
        byte[] receive = new byte[]{0x01, 0x04, 0x30, 0x41, 0x41, (byte) 0x99,
                (byte) 0x9a, 0x41, 0x54, (byte) 0xcc, (byte) 0xcd, 0x41, (byte) 0xd6, 0x14, 0x7b,
                0x42, 0x6f, 0x47, (byte) 0xae, 0x44, (byte) 0x8e, (byte) 0x80, 0x00, 0x3d, 0x7d, (byte) 0xf3, (byte) 0xb6
                , 0x41, 0x3b, 0x33, 0x33, 0x41, 0x51, (byte) 0x99, (byte) 0x9a, 0x41, (byte) 0xd8, (byte) 0xb8, 0x52, 0x42
                , 0x6d, 0x47, (byte) 0xae, 0x44, (byte) 0x8a, 0x60, 0x00, 0x3d, 0x7d, (byte) 0xf3, (byte) 0xb6, (byte) 0xa1, (byte) 0xdd};

        if (checkBuf(receive)) {
            //获取读到的24个寄存器  48byte的数据
            byte[] readDataArray = calReadDataArray(receive);
            //数据解析得到对应寄存器的float值
            float[] floatArray1 = toFloatArray(readDataArray);
            System.out.println("pm25:" + floatArray1[0] + ",temp" + floatArray1[8] + ",humidity:" +
                    floatArray1[9] + ",co2:" + floatArray1[10] + ",voc:" + floatArray1[11]);
        } else {
            System.out.println("crc校验失败");
        }
        boolean flag = true;
        System.out.println(String.valueOf(12));
        System.out.println(Float.valueOf("12.5"));
    }

    public int reverse(int x) {
        String result = String.valueOf(x).replace("-", "");
        StringBuffer sb = new StringBuffer();
        sb.append(result).reverse();
        try {
            Integer integer = Integer.valueOf(sb.toString());
            return x<0? -integer:integer;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public byte[] calReadDataArray(byte[] data) {
        int dataLen = data[2] & 0xFF;
        byte[] byteArray = new byte[dataLen];
        System.arraycopy(data, 3, byteArray, 0, dataLen);
        return byteArray;
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

    public boolean checkBuf(byte[] bb) {
        int ri = alex_crc16(bb, bb.length - 2);
        return bb[bb.length - 1] == (byte) ((0xff00 & ri) >> 8) && bb[bb.length - 2] == (byte) (ri & 0xff);
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

    private float getRecordValue(float recordValue) {
        if (recordValue > 90) {
            recordValue = 30;
        }
        if (recordValue > 60 && recordValue <= 90) {
            recordValue = recordValue / 3;
        }
        if (recordValue > 30 && recordValue <= 60) {
            recordValue = recordValue / 2;
        }
        return recordValue;
    }

    public String timestampToDate(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date date = new Date(time);
        return simpleDateFormat.format(date);
    }

    private void checkErrorCode(byte[] data) {
        int index = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == 0) {
                continue;
            }
            for (int j = 0; j < 8; j++) {
                index = i % 2 == 0 ? ((i + 1) * 8 + j + 1) : ((i - 1) * 8 + j + 1);
                if ((((data[i] & 0xFF) >>> j) & 0x01) == 1) {
                    System.out.println(index);
                }
            }

        }
    }

    public int[] toSignedIntArray(byte[] bytes) {
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
                intArray[i] = getSignedShortAt(buffer);
                startLoc += 2;
            }
            return intArray;
        }
    }

    public int getSignedShortAt(byte[] buffer) {
        boolean signed = ((buffer[0] & 0x80) == 0x80);
        int high = (buffer[0] & 0x7F);
        int low = (buffer[1] & 0xFF);
        return signed ? -((high << 8) + low) : (high << 8) + low;
    }
}
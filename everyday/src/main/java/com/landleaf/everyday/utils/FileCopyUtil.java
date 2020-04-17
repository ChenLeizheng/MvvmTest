package com.landleaf.everyday.utils;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by thinkpad on 2017/6/22.
 * <p>
 * 文件拷贝工具类
 */
public class FileCopyUtil {

    private static final String TAG = "FileCopyUtil";

    //将raw中的db文件拷贝到databases文件夹中
    public static boolean copyDBFromRaw(Context context, String dbName, InputStream in) {
        //拷贝数据库
        String dbDir = Environment.getDataDirectory() + "/data/" + context.getPackageName() + "/databases/";
        String dbPath = dbDir + dbName;
        if (fileIsExists(dbPath)){
            return true;
        }
        return copyFileToApp(in, dbDir, dbPath);
    }

    //将raw中的xml文件拷贝到shared_prefs文件夹中
    public static boolean copyXmlFromRaw(Context context, InputStream in) {
        String xmlDir = Environment.getDataDirectory() + "/data/" + context.getPackageName() + "/shared_prefs/";
        String xmlPath = xmlDir + context.getPackageName() + "_preferences.xml";
        return copyFileToApp(in, xmlDir, xmlPath);
    }

    public static boolean fileIsExists(String filePath){
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                return false;
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }

    //拷贝文件到指定目录
    private static boolean copyFileToApp( InputStream in, String fileDir, String filePath) {
        boolean copyRes;
        FileOutputStream out = null;
        try {
            File dir = new File(fileDir);
            //if dir not exit,create dir
            if (!dir.exists())
                copyRes = dir.mkdir();
            //if file not exit,create file
            File file = new File(filePath);
            if (!file.exists())
                copyRes = file.createNewFile();
            out = new FileOutputStream(file);
            byte[] buffer = new byte[8192];
            int len = 0;
            while((len = in.read(buffer))!=-1) {
                out.write(buffer, 0, len);
            }
            copyRes = true;
        } catch (IOException e) {
            e.printStackTrace();
            copyRes = false;
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return copyRes;
    }
}
package com.landleaf.everyday.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.landleaf.everyday.bean.DaoMaster;
import com.landleaf.everyday.bean.DaoSession;
import com.landleaf.everyday.bean.ErrorCode;
import com.landleaf.everyday.callback.DbCopyCallback;

import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DbUtil {

    private DaoSession daoSession;
    private DaoMaster.DevOpenHelper devOpenHelper;
    private boolean initSuccess = false;

    private DbUtil(){}

    public static DbUtil getInstance(){
        return ViewHolder.instance;
    }

    public void initGreenDao(Context context,String dbName){
        devOpenHelper = new DaoMaster.DevOpenHelper(context, dbName);
        SQLiteDatabase writableDatabase = devOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(writableDatabase);
        daoSession = daoMaster.newSession();
    }

    private static class ViewHolder{
        static DbUtil instance = new DbUtil();
    }

    public void closeDb(){
        if (devOpenHelper != null){
            devOpenHelper.close();
            devOpenHelper = null;
        }
        if (daoSession!=null){
            daoSession.clear();
            daoSession = null;
        }
    }

    public String getErrorCodeById(int index){
        if (!initSuccess){
            return null;
        }
        ErrorCode load = daoSession.getErrorCodeDao().load((long) index);
        return load.getErrorCode();
    }

    public void copyDBFromRaw(final Context context, final String dbName, final InputStream inputStream){
        Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(SingleEmitter<Boolean> e) throws Exception {
                Log.d("DbUtil", "Thread.currentThread():" + Thread.currentThread().getName());
                e.onSuccess(FileCopyUtil.copyDBFromRaw(context,dbName,inputStream));
            }
        }).observeOn(Schedulers.io())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        initGreenDao(context,dbName);
                        initSuccess = aBoolean;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("DbUtil", throwable.toString());
                    }
                });

    }
}

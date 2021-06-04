package com.landleaf.everyday;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.landleaf.everyday.dao.AppDatabase;
import com.landleaf.everyday.utils.FileUtils;
import com.yanzhenjie.andserver.util.IOUtils;

import java.io.File;
import java.util.List;

public class App extends MultiDexApplication {
    private static App mInstance;

    private File mRootDir;

    @Override
    public void onCreate() {
        super.onCreate();

        if (mInstance == null) {
            mInstance = this;
            initRootPath(this);
        }

        //数据库版本 1->2 user表格新增了age列
//        Migration MIGRATION_1_2 = new Migration(1, 2) {
//            @Override
//            public void migrate(@NonNull SupportSQLiteDatabase database) {
//                database.execSQL("ALTER TABLE User ADD COLUMN age integer");
//            }
//        };
//        AppDatabase mAppDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "android_room_dev.db")
//                .allowMainThreadQueries()
//                .addMigrations(MIGRATION_1_2)
//                .build();

    }

    @NonNull
    public static App getInstance() {
        return mInstance;
    }

    @NonNull
    public File getRootDir() {
        return mRootDir;
    }

    private void initRootPath(Context context) {
        if (mRootDir != null) {
            return;
        }

        if (FileUtils.storageAvailable()) {
            mRootDir = Environment.getExternalStorageDirectory();
        } else {
            mRootDir = context.getFilesDir();
        }
        mRootDir = new File(mRootDir, "AndServer");
        IOUtils.createFolder(mRootDir);
    }
}

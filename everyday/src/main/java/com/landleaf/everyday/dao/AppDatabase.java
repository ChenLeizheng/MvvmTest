package com.landleaf.everyday.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.landleaf.everyday.bean.User;

/**
 * Author：Lei on 2020/12/10
 */
@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}

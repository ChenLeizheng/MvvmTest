package com.landleaf.kotlin

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Author：Lei on 2021/5/16
 */
@Database(version = 1, entities = [User::class])
abstract class AppDatabase : RoomDatabase() {

    companion object {
        private var instance: AppDatabase? = null
        @Synchronized
        fun getDataBase(context: Context): AppDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "db_name")
                    .build()
                    .apply { instance = this }
        }
    }

    abstract fun userDao(): UserDao
}
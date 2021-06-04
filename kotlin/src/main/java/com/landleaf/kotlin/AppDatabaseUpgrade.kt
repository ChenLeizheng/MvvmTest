package com.landleaf.kotlin

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Author：Lei on 2021/5/16
 */
@Database(version = 2, entities = [User::class, Book::class])
abstract class AppDatabaseUpgrade : RoomDatabase() {
//    object [ : 接口1,接口2,类型1, 类型2]{}    //中括号中的可省略
//    fun main(args: Array<String>) {
//        实现多个接口和类
//        val cc = object : AA, BB() {
//        }
//    }

    companion object {

        //数据库升级
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("create table Book (id integer primary key autoincrement not null,name text not null)")
            }
        }

        private var instance: AppDatabaseUpgrade? = null
        @Synchronized
        fun getDataBase(context: Context): AppDatabaseUpgrade {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext, AppDatabaseUpgrade::class.java, "db_name")
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .apply { instance = this }
        }
    }

    abstract fun userDao(): UserDao
    abstract fun bookDao(): BookDao
}
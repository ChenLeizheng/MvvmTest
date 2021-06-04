package com.landleaf.kotlin

import androidx.room.*

/**
 * Author：Lei on 2021/5/16
 */
@Dao
interface UserDao {

    @Insert
    fun insertUser(user: User): Long

    @Update
    fun updateUser(newUser: User)

    @Query("select * from User")
    fun loadAllUser():List<User>

    @Delete
    fun deleteUser(user: User)
}
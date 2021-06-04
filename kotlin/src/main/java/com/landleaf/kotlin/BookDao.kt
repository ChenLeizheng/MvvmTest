package com.landleaf.kotlin

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Author：Lei on 2021/5/16
 */
@Dao
interface BookDao {

    @Insert
    fun insertBook(book: Book) : Long

    @Query("select * from Book")
    fun loadAllBooks() : List<Book>
}
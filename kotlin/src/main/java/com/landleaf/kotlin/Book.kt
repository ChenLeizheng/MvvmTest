package com.landleaf.kotlin

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Author：Lei on 2021/5/16
 */
@Entity
data class Book(var name :String) {

    @PrimaryKey(autoGenerate = true)
    var id :Long = 0;
}
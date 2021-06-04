package com.landleaf.kotlin

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Author：Lei on 2021/5/16
 *
 * data class就是一个类中只包含一些数据字段，类似于vo,pojo,java bean。一般而言，我们在Java中定义了这个数据类之后要重写一下toString，equals等方法。要生成get,set方法。
 * equals()/hashCode() 、toString()方法
 * componentN()方法 在主构造函数中有多少个参数，就会依次生成对应的component1,component2,component3…… 给firstName赋值，其实调用的是user.component1()
 * copy()方法  var copy = user.copy(firstName = "cc")
 */
@Entity
data class User(var firstName:String,var lastName:String,var age:Int) {

    @PrimaryKey(autoGenerate = true)
    var id:Long = 0;
}
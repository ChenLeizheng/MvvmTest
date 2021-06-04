package com.landleaf.kotlin

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import kotlinx.coroutines.*
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {

    /**
     * 内联函数let、with、run、apply、also
     * 函数名  函数体内使用的对象  返回值
     * let	it	函数块的最后一行或指定return表达式
     * with	this或省略  函数块的最后一行或指定return表达式
     * run	this或省略  函数块的最后一行或指定return表达式
     * apply	this或省略	this
     * also	it	this
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var array = arrayOf(1,3,5)
        var list = ArrayList<Int>()
        list.add(1)
        var textView = TextView(this)
        textView.let {
            it.textSize = 10f
            it.text = "aa"
            it.setTextColor(Color.WHITE)
        }
        textView.apply {
            textSize = 5f
            text = "cc"
        }.apply {
            setTextColor(Color.YELLOW)
        }
        var userDao = AppDatabaseUpgrade.getDataBase(this).userDao()
        //在后台启动一个新的协程并继续,类似开启子线程,本质上,协程是轻量级的线程
        GlobalScope.launch(Dispatchers.IO){
            //使用async发起两个异步请求
            val one = async { one() }
            val two = async { two() }
            //使用await进行合并
            var result = one.await() + two.await();
            Log.d("test","result +++ $result")
            userDao.insertUser(User("z","n",28))
            userDao.insertUser(User("c","lz",27))
            userDao.insertUser(User("l","n",24))

            var loadAllUser = userDao.loadAllUser()
            for (user in loadAllUser){
                Log.d("test",user.toString())
            }
        }

        //调用了 runBlocking 的主线程会一直 阻塞 直到 runBlocking 内部的协程执行完毕。
        runBlocking {
            delay(2000)
            Log.d("test","runBlocking")
        }

        var user = User("a", "z", 17)
        var copy = user.copy(firstName = "cc")
    }

    ////使用await进行合并
    suspend fun one(): String{
        Log.d("test","one +++")
        delay(100)
        return "one"
    }

    suspend fun two(): String{
        Log.d("test","two +++")
        delay(120)
        return "two"
    }
}

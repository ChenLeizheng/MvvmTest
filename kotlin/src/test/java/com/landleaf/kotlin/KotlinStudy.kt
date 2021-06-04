package com.landleaf.kotlin

class KotlinStudy {

    /**
     * 可见性修饰符包括：public、internal、protected、private
     * public Kotlin中默认修饰符，全局可见
     * internal 模块内可见
     * protected 受保护修饰符，类及子类可见
     * private 私有修饰符，类内修饰只有本类可见
     * 函数定义使用关键字 fun，参数格式为：参数 : 类型
     * 基本数据类型 Byte、Short、Int、Long、Float、Double
     */
    //Int 参数，返回值 Int
    fun sumNum(a: Int, b: Int): Int {
        return a + b
    }

    //函数的变长参数可以用 vararg 关键字进行标识
    fun useVars(vararg nums: Int) {
        for (num in nums) {
            println(num)
        }
    }

    //lambda(匿名函数)
    val sum: (Int, Int) -> Int = { x, y -> x + y }

    /**
     * 定义常量与变量
     * 可变变量定义：var 关键字  var <标识符> : <类型> = <初始化值>
     * 不可变变量定义：val 关键字，只能赋值一次的变量(类似Java中final修饰的变量) val <标识符> : <类型> = <初始化值>
     * 字符串模板
        $ 表示一个变量名或者变量值
        $varName 表示变量值
        ${varName.fun()} 表示变量的方法返回值:
     */
    var number = 0
    fun sout(){
        println("number is $number")
        var age: String? = null
        val ages2 = age?.toInt() ?: -1
        println(ages2)
    }

    //Kotlin的空安全设计对于声明可为空的参数，在使用时要进行空判断处理，有两种处理方式，字段后加!!像Java一样抛出空异常，另一种字段后加?可不做处理返回值为 null或配合?:做空判断处理
    //类型后面加?表示可为空
    var age: String? = ""
    //抛出空指针异常
    val ages = age!!.toInt()
    //不做处理返回 null
    val ages1 = age?.toInt()
    //age为空返回-1
    val ages2 = age?.toInt() ?: -1


    //类型检测及自动类型转换
    fun getStringLength(obj:Any) : Int{
        if(obj is String){
            // 做过类型判断以后，obj会被系统自动转换为String类型
            return obj.length
        }
        return -1;
    }

    //区间表达式由具有操作符形式 .. 的 rangeTo 函数辅以 in 和 !in 形成。
    fun printRange(){
        for (i in 1..4) print(i) // 输出“1234”  等同于1<=i<=4
        for (i in 4 downTo 1) print(i) // 输出“4321”
    }

    //三个等号 === 表示比较对象地址，两个 == 表示比较两个值大小
    fun compare(){
        var a = 10
        //经过了装箱，创建了两个不同的对象
        val boxedA: Int? = a
        val anotherBoxedA: Int? = a
        println(boxedA === anotherBoxedA) //  false，值相等，对象地址不一样
        println(boxedA == anotherBoxedA) // true，值相等
    }

    //类型转换  由于不同的表示方式，较小类型并不是较大类型的子类型，较小的类型不能隐式转换为较大的类型。Char 不能直接和数字操作如char == 1类型不兼容
    fun changeType(){
        val b: Byte = 1 // OK, 字面值是静态检测的
        val i: Int = b.toInt() // OK
//        val i: Int = b // 错误
    }

    //数组
    val array1 = arrayOf(1, 2, 3)
    val array2 = Array(3, { i -> (i * 2) })  //[0,2,4]

    fun circularExpression(x :Int){
        when (x) {
            1 -> print("x == 1")
            2 -> print("x == 2")
//            0, 1 -> print("x == 0 or x == 1")
            else -> { // 注意这个块
                print("x 不是 1 ，也不是 2")
            }
        }
    }

    class Person (val name: String){
        //如果类有主构造函数，每个次构造函数都要，或直接或间接通过另一个次构造函数代理主构造函数。在同一个类中代理另一个构造函数使用 this 关键字
        constructor (name: String, age:Int) : this(name) {
            // 初始化...
        }
        var lastName: String = "zhang"
            get() = field.toUpperCase()   // 将变量赋值后转换为大写
            set

        var no: Int = 100
            get() = field                // 后端变量
            set(value) {
                if (value < 10) {       // 如果传入的值小于 10 返回该值
                    field = value
                } else {
                    field = -1         // 如果传入的值大于等于 10 返回 -1
                }
            }

        var heiht: Float = 145.4f
            private set
    }

}
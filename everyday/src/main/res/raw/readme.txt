 <vector xmlns:android="http://schemas.android.com/apk/res/android"
     android:name="triangle"//定义矢量图的名称
     android:height="64dp"//drawable的固定高度，支持所有的尺寸单位，一般使用dp
     android:width="64dp"//drawable的固定宽度，支持所有的尺寸单位，一般使用dp
     android:viewportHeight="600"//视图的高度，可以理解为画布的高度
     android:viewportWidth="600" >//视图的宽度，下面的pathData中的内容便会在600宽高的画布内操作
     <group //定义一个组，可以包含path 及子group, 同时可以定义转换信息，如旋转，伸缩，位移
         android:name="rotationGroup"//组名
         android:pivotX="300.0"//X坐标中心点，默认为0
         android:pivotY="300.0"//Y坐标中心点，默认为0
         android:rotation="45.0" >//旋转角度，顺时针
         <path
             android:name="v"//路径的名称
             android:fillColor="#000000"//填充颜色
             android:pathData="M300,70 l 0,-70 70,70 0,0 -70,70z" />//路径的数据
     </group>
 </vector>

 我们来解析一下数据：M300,70 l 0,-70 70,70 0,0 -70,70z
 先来了解一下相关的指令：
 M = moveto 相当于 android Path 里的moveTo(),用于移动起始点
 L = lineto 相当于 android Path 里的lineTo()，用于画线
 H = horizontal lineto 用于画水平线
 V = vertical lineto 用于画竖直线
 C = curveto 相当于cubicTo(),三次贝塞尔曲线
 S = smooth curveto 同样三次贝塞尔曲线，更平滑
 Q = quadratic Belzier curve quadTo()，二次贝塞尔曲线
 T = smooth quadratic Belzier curveto 同样二次贝塞尔曲线，更平滑
 A = elliptical Arc 相当于arcTo()，用于画弧
 Z = closepath 相当于closeTo(),关闭path
 坐标轴为以(0,0)为中心，X轴水平向右，Y轴水平向下
 所有指令大小写均可。大写绝对定位，参照全局坐标系；小写相对定位，参照父容器坐标系
 指令和数据间的空格可以省略
 同一指令出现多次可以只用一个
 (1). M指令
 (2). l 指令（小写的L） (l 0, -70 70,70 0,0 -70,70z 相当于 l 0,-70 l 70,70 l 0,0 l -70,70z)
 (3). z指令


 作者：MinicupSimon
 链接：https://www.jianshu.com/p/9f3221179e3c
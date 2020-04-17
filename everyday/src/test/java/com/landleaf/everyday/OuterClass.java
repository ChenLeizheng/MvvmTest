package com.landleaf.everyday;

public class OuterClass {

    innerClass a = new innerClass();

    public class innerClass{
        public void fun (){
            System.out.println(OuterClass.this.toString());
        }
    }

    public void des(){
        a = null;
    }
}

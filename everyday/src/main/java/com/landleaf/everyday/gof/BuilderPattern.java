package com.landleaf.everyday.gof;

import android.text.TextUtils;

/**
 * 建造者模式
 * 构造函数的参数列表会变得很长，代码在可读性和易用性上都会变差。在使用构造函数的时候，我们就容易搞错各参数的顺序，
 * 传递进错误的参数值，导致非常隐蔽的 bug。那就是用 set() 函数来给成员变量赋值，以替代冗长的构造函数
 * 我们可以把校验逻辑放置到 Builder 类中，先创建建造者，并且通过 set() 方法设置建造者的变量值，然后在使用 build() 方法真正创建对象之前，
 * 做集中的校验，校验通过之后才会创建对象。除此之外，我们把 BuilderPattern 的构造函数改为 private 私有权限。
 * 这样我们就只能通过建造者来创建 BuilderPattern 类对象。并且，BuilderPattern 没有提供任何 set() 方法，这样我们创建出来的对象就是不可变对象了。
 */
public class BuilderPattern {

    private String name;
    private int maxTotal;
    private int maxIdle;
    private int minIdle;

    private BuilderPattern(Builder builder) {
        this.name = builder.name;
        this.maxTotal = builder.maxTotal;
        this.maxIdle = builder.maxIdle;
        this.minIdle = builder.minIdle;
    }

    public static class Builder{
        private static final int DEFAULT_MAX_TOTAL = 8;
        private static final int DEFAULT_MAX_IDLE = 8;
        private static final int DEFAULT_MIN_IDLE = 0;

        private String name;
        private int maxTotal = DEFAULT_MAX_TOTAL;
        private int maxIdle = DEFAULT_MAX_IDLE;
        private int minIdle = DEFAULT_MIN_IDLE;

        public BuilderPattern build() {
            // 校验逻辑放到这里来做，包括必填项校验、依赖关系校验、约束条件校验等
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("...");
            }
            if (maxIdle > maxTotal) {
                throw new IllegalArgumentException("...");
            }
            if (minIdle > maxTotal || minIdle > maxIdle) {
                throw new IllegalArgumentException("...");
            }

            return new BuilderPattern(this);
        }

        public BuilderPattern.Builder setName(String name) {
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("...");
            }
            this.name = name;
            return this;
        }

        public BuilderPattern.Builder setMaxTotal(int maxTotal) {
            if (maxTotal <= 0) {
                throw new IllegalArgumentException("...");
            }
            this.maxTotal = maxTotal;
            return this;
        }

        public BuilderPattern.Builder setMaxIdle(int maxIdle) {
            if (maxIdle < 0) {
                throw new IllegalArgumentException("...");
            }
            this.maxIdle = maxIdle;
            return this;
        }

        public BuilderPattern.Builder setMinIdle(int minIdle) {
            if (minIdle < 0) {
                throw new IllegalArgumentException("...");
            }
            this.minIdle = minIdle;
            return this;
        }
    }
}



package com.landleaf.everyday.bean;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Author：Lei on 2020/12/10
 * Room 数据库示例
 * @PrimaryKey 声明该字段主键并可以声明是否自动创建。
 * @Embedded 用于嵌套，里面的字段同样会存储在数据库中,不需要多创建一个表，实现表的一对一关联关系
 */

@Entity(tableName = "user")
public class User {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Long id;
    @ColumnInfo(name = "user_account")
    public String account; // 账号
    @ColumnInfo(name = "user_pwd")
    public String pwd; //密码
    @ColumnInfo(name = "user_name")
    public String name;
    @Embedded
    public Address address; // 地址
    @Ignore
    public int state; // 状态只是临时用，所以不需要存储在数据库中
}

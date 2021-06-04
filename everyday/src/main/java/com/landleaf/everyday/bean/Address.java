package com.landleaf.everyday.bean;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

/**
 * Author：Lei on 2020/12/10
 */
@Entity
public class Address {
    public String street;
    public String state;
    public String city;

    @ColumnInfo(name = "post_code")
    public int postCode;
}

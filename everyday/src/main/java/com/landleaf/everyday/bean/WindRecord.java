package com.landleaf.everyday.bean;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class WindRecord {
    @Id(autoincrement = true)
    private Long id;
    @SerializedName("table_index")
    private int tableIndex;
    @SerializedName("data_index")
    private int dataIndex;
    @SerializedName("data_name")
    private int dataName;
    @SerializedName("float")
    private boolean isFloat;
    @Generated(hash = 915838964)
    public WindRecord(Long id, int tableIndex, int dataIndex, int dataName,
            boolean isFloat) {
        this.id = id;
        this.tableIndex = tableIndex;
        this.dataIndex = dataIndex;
        this.dataName = dataName;
        this.isFloat = isFloat;
    }
    @Generated(hash = 1913428996)
    public WindRecord() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getTableIndex() {
        return this.tableIndex;
    }
    public void setTableIndex(int tableIndex) {
        this.tableIndex = tableIndex;
    }
    public int getDataIndex() {
        return this.dataIndex;
    }
    public void setDataIndex(int dataIndex) {
        this.dataIndex = dataIndex;
    }
    public int getDataName() {
        return this.dataName;
    }
    public void setDataName(int dataName) {
        this.dataName = dataName;
    }
    public boolean getIsFloat() {
        return this.isFloat;
    }
    public void setIsFloat(boolean isFloat) {
        this.isFloat = isFloat;
    }
}

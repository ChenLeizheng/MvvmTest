package com.landleaf.everyday.bean;

/**
 * Author：Lei on 2020/12/7
 */
public class TestBean implements Cloneable {

    int id;
    String content;

    public TestBean(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public TestBean clone() {

        TestBean clone;
        try {
            clone = (TestBean) super.clone();

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e); //should not happen
        }

        return clone;
    }
}

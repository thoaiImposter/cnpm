package com.app.buchin.model;

import java.io.Serializable;

public class UnitObject implements Serializable {

    private long id;
    private String name;

    public UnitObject() {
    }

    // 1.2.4 Dialog gửi dữ liệu về lại UnitActivity (used when creating new unit).
    // 1.3.4 Dialog gửi thông tin đã chỉnh sửa về UnitActivity (used when editing unit).
    public UnitObject(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
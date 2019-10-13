package com.feri.shop.newretail.msg.model;

import lombok.Data;

@Data
public class VCode {
    private int code;
    private String phone;

    public VCode(){}
    public VCode(int code,String phone) {
        this.code = code;
        this.phone = phone;
    }
}
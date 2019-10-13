package com.fairy.shop.newretail.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class LoginDto {
    private int id;
    private String phone;
    @JsonIgnore //作用是json序列化时将java bean中的一些属性忽略掉,序列化和反序列化都受影响。
    private String pass;
    private int type; //设备类型
    private String mac; //设备的mac地址
}

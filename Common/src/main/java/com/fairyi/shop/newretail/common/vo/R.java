package com.fairyi.shop.newretail.common.vo;

import lombok.Data;

@Data
public class R {
    private int code;
    private String msg;
    private Object data;
    //仅仅练习使用
    private String name;

    public static R setOK(String msg,Object obj){
        R r=new R();
        r.setCode(200);
        r.setMsg(msg);
        r.setData(obj);
        return r;
    }

    public static R setOK(String msg){
        R r=new R();
        r.setCode(200);
        r.setMsg(msg);
        r.setData(null);
        return r;
    }

    public static R setOK(){
        R r=new R();
        r.setCode(200);
        r.setMsg("OK");
        r.setData(null);
        return r;
    }
    public static R setERROR(String msg,Object obj){
        R r=new R();
        r.setCode(400);
        r.setMsg(msg);
        r.setData(obj);
        return r;
    }
    public static R setERROR(String msg){
        R r=new R();
        r.setCode(400);
        r.setMsg(msg);
        r.setData(null);
        return r;
    }
    public static R setERROR(){
        R r=new R();
        r.setCode(400);
        r.setMsg("OK");
        r.setData(null);
        return r;
    }
    public static R setResult(boolean issuccess,String msg){
        R r=new R();
        r.setCode(issuccess?200:400);
        r.setMsg(msg);
        r.setData(null);
        return r;
    }
}

package com.feri.shop.newretail.msg.service.Impl;

import com.alibaba.fastjson.JSON;
import com.fairyi.shop.newretail.common.config.RedisKeyConfig;
import com.fairyi.shop.newretail.common.config.SmsConfig;
import com.fairyi.shop.newretail.common.util.JedisUtil;
import com.fairyi.shop.newretail.common.util.Random_Util;
import com.fairyi.shop.newretail.common.util.StrUtil;
import com.fairyi.shop.newretail.common.util.TimeUtil;
import com.fairyi.shop.newretail.common.vo.R;
import com.feri.shop.newretail.msg.config.RabbitMQConfig;
import com.feri.shop.newretail.msg.model.VCode;
import com.feri.shop.newretail.msg.service.SmsService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SmsServiceImpl implements SmsService {
    private JedisUtil jedisUtil = JedisUtil.getInstance();
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Override
    public R sendValidateCode(String phone) {
        //首先判断电话有没有发过，在规定时间是否已达到上线
        //一个消息模板，一个电话 1天只能发20次
        if (jedisUtil.exists(RedisKeyConfig.SMSKEYD1+ SmsConfig.APITEMID+":"+phone)){
            int dc=Integer.parseInt(jedisUtil.get(RedisKeyConfig.SMSKEYD1+ SmsConfig.APITEMID+":"+phone));
            if(dc>=20){
                return R.setERROR("今日已达上限");
            }
        }
        //1小时只能发4次
        if(jedisUtil.exists(RedisKeyConfig.SMSKEYH1+ SmsConfig.APITEMID+":"+phone)){
            int hc=Integer.parseInt(jedisUtil.get(RedisKeyConfig.SMSKEYH1+ SmsConfig.APITEMID+":"+phone));
            if(hc>=4){
                return R.setERROR("请稍后再来");
            }
        }
        //10分钟只能发3次
        if(jedisUtil.exists(RedisKeyConfig.SMSKEYM10+ SmsConfig.APITEMID+":"+phone)){
            int mc=Integer.parseInt(jedisUtil.get(RedisKeyConfig.SMSKEYM10+ SmsConfig.APITEMID+":"+phone));
            if(mc>=3){
                return R.setERROR("请稍后再来");
            }
        }
        //1分钟只能发1次
        if(jedisUtil.exists(RedisKeyConfig.SMSKEYM1+ SmsConfig.APITEMID+":"+phone)){
            int mc1=Integer.parseInt(jedisUtil.get(RedisKeyConfig.SMSKEYM1+ SmsConfig.APITEMID+":"+phone));
            if(mc1>=1){
                return R.setERROR("请稍后再来");
            }
        }
        int code;
        //到这里，如果没有此号码没有发过，或者并没有超过上限
        //验证验证码是否有效
        if(jedisUtil.exists(RedisKeyConfig.SMSCODE+phone)){
            code= Integer.parseInt(jedisUtil.get(RedisKeyConfig.SMSCODE+phone));
        } else {
            code= Random_Util.createNum(6);
            jedisUtil.setExpire(RedisKeyConfig.SMSCODE+phone,code+"",600);
        }
        //验证码存储到Redis   有效期 10分钟
        //      boolean issuccess= SmsUtil.sendMsg(phone,code);
        amqpTemplate.convertAndSend(RabbitMQConfig.ename,"", JSON.toJSONString(new VCode(code,phone)));
        //更新各种Key
        //1天
        setKey(RedisKeyConfig.SMSKEYD1+ SmsConfig.APITEMID+":"+phone, TimeUtil.getLastSeconds());
        //1小时
        setKey(RedisKeyConfig.SMSKEYH1+ SmsConfig.APITEMID+":"+phone,3600);
        //10分钟
        setKey(RedisKeyConfig.SMSKEYM10+ SmsConfig.APITEMID+":"+phone,600);
        //1分钟
        setKey(RedisKeyConfig.SMSKEYM1+ SmsConfig.APITEMID+":"+phone,60);
        return R.setResult(true,"OK");
    }

    @Override
    //输入的验证码和服务器产生的验证码进行比对
    public R checkCode(String phone, String code) {
        String cd=jedisUtil.get(RedisKeyConfig.SMSCODE+phone);
        if(StrUtil.checkNotEmpty(cd)){
            if(Objects.equals(code,cd)){
                jedisUtil.del(RedisKeyConfig.SMSCODE+phone);
                return R.setOK("校验验证码成功");
            }else {
                return R.setERROR("验证码不一致");
            }
        }else {
            return R.setERROR("验证码无效");
        }
    }
    private void setKey(String key,int seconds){
        if(jedisUtil.exists(key)){    //jedisUtil.ttl(key) 计算到期时间
            jedisUtil.setExpire(key,(Integer.parseInt(jedisUtil.get(key))+1)+"", (int)jedisUtil.ttl(key));
        }else {  //expire 有效期
            jedisUtil.setExpire(key,1+"",seconds);
        }
    }
}

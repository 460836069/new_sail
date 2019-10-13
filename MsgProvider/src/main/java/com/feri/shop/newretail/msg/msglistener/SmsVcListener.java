package com.feri.shop.newretail.msg.msglistener;

import com.alibaba.fastjson.JSON;
import com.feri.shop.newretail.msg.model.VCode;
import com.feri.shop.newretail.msg.util.SmsUtil;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
//@component（把普通pojo实例化到spring容器中，相当于配置文件中的<bean id="" class=""/>）
//@Component,@Service,@Controller,@Repository注解的类，并把这些类纳入进spring容器中管理。
@Component
//使用 @RabbitListener 注解标记方法，当监听到队列中有消息时则会进行接收并处理
//@RabbitListener 注解声明 Binding
//@RabbitListener 可以标注在类上面，需配合 @RabbitHandler 注解一起使用
//@RabbitListener 标注在类上面表示当有收到消息的时候，就交给 @RabbitHandler 的方法处理，
// 具体使用哪个方法处理，根据 MessageConverter 转换后的参数类型
@RabbitListener(queues = "nrsmsvc") //设置监听的队列名称
public class SmsVcListener {
    @RabbitHandler //消息获取方法
    public void receive(String json){
        VCode vCode= JSON.parseObject(json,VCode.class);
        SmsUtil.sendMsg(vCode.getPhone(),vCode.getCode());
    }
}

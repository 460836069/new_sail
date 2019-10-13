package com.feri.shop.newretail.api.api;

import com.fairyi.shop.newretail.common.vo.R;
import com.feri.shop.newretail.api.service.SmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "发送短信接口", tags="短信操作")
@RestController
public class SmsApi {
    @Autowired
    private SmsService smsService;
    //@ApiOperation(value = “接口说明”, notes = “接口发布说明”）
    @ApiOperation(value = "接口说明", notes = "发送短信验证码")
    @GetMapping("/api/sms/sendvc/{phone}") //restful风格的请求，需要用@PathVariable
    public R sendVc(@PathVariable String phone){
        return smsService.sendSms(phone);
    }
    @ApiOperation(value = "",notes = "验证短信验证码")
    @GetMapping("/api/sms/checkvc/{phone}/{code}")
    public R checkVC(@PathVariable String phone,@PathVariable String code){
        return smsService.checkVC(phone,code);
    }
}

package com.feri.shop.newretail.api.service;

import com.fairyi.shop.newretail.common.vo.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
/*
总结
•在GET请求中，不能使用@RequestBody。
•在POST请求，可以使用@RequestBody和@RequestParam，但是如果使用@RequestBody，
对于参数转化的配置必须统一。
 */
@FeignClient("MsgProvider")
public interface SmsService {
    @GetMapping("nr/provider/sms/sendvc.do")
    R sendSms(@RequestParam("phone") String phone);
    @GetMapping("nr/provider/sms/checkvc.do")
    R checkVC(@RequestParam("phone") String phone, @RequestParam("code")String code);
}

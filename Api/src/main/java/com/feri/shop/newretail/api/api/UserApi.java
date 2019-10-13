package com.feri.shop.newretail.api.api;

import com.fairy.shop.newretail.dto.UserDto;
import com.fairyi.shop.newretail.common.vo.R;
import com.feri.shop.newretail.api.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(value = "注册会员接口",tags="注册会员")
@RestController
public class UserApi {
    @Autowired
    private UserService userService;
    @ApiOperation(value = "校验手机号",notes="校验手机号")
    @GetMapping("/api/user/checkphone/{phone}")
    public R sendVc(@PathVariable String phone){
        return userService.check(phone);
    }
    @ApiOperation(value = "",notes = "新增会员")
    @PostMapping("/api/user/saveuser")
    public R save(@RequestBody UserDto userDto){
        return userService.save(userDto);
    }
}

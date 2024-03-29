package com.feri.shop.newretail.api.api;

import com.fairy.shop.newretail.dto.LoginDto;
import com.fairy.shop.newretail.dto.UserDto;
import com.fairyi.shop.newretail.common.vo.R;
import com.feri.shop.newretail.api.service.LoginService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

@Api(value = "登录接口")
public class LoginApi {
    @Autowired
    private LoginService loginService;
    //校验是否被冻结
    @GetMapping("api/login/checkfreeze/{phone}")
    public R checkP(@PathVariable String phone){
        return  loginService.checkP(phone);
    }
    //校验是否有效
    @GetMapping("api/login/checklogin")
    public R checkP(HttpServletRequest request){
        return loginService.checkToken();
    }
    //登录
    @PostMapping("api/login/login")
    public R login(@RequestBody LoginDto loginDto){
        return loginService.login(loginDto);
    }
    //密码找回
    @PostMapping("api/login/findpass")
    public R login(@RequestBody UserDto userDto){
        return loginService.findPass(userDto);
    }
    //修改密码
    @PostMapping("api/login/resetpass/{pass}")
    public R checkP(HttpServletRequest request,@PathVariable String pass){
        return  loginService.changePass(pass);
    }
    //注销
    @GetMapping("api/login/loginexit")
    public R exit(HttpServletRequest request){
        return loginService.exit();
    }
}

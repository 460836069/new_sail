package com.feri.shop.newretail.login.service;

import com.fairy.shop.newretail.dto.LoginDto;
import com.fairy.shop.newretail.dto.UserDto;
import com.fairyi.shop.newretail.common.vo.R;

public interface UserService {
    R checkFreeze(String phone);
    R checkToken(String token);
    R login(LoginDto loginDto);
    R findPass(UserDto userDto);
    R changePass(String token, String pass);
    R exit(String token);
}

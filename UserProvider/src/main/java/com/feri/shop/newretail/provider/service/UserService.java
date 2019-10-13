package com.feri.shop.newretail.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fairy.shop.newretail.dto.UserDto;
import com.fairy.shop.newretail.entity.User;
import com.fairyi.shop.newretail.common.vo.R;

public interface UserService extends IService<User> {
    R save(UserDto userDto);
    R queryPhone(String phone);
}

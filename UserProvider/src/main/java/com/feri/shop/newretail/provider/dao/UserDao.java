package com.feri.shop.newretail.provider.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fairy.shop.newretail.entity.User;

public interface UserDao extends BaseMapper<User> {
    User selectByPhone(String phone);
    int save(User user);
}

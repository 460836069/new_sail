package com.feri.shop.newretail.provider.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fairy.shop.newretail.dto.UserDto;
import com.fairy.shop.newretail.entity.User;
import com.fairyi.shop.newretail.common.config.KeyConfig;
import com.fairyi.shop.newretail.common.util.EncryptionUtil;
import com.fairyi.shop.newretail.common.util.StrUtil;
import com.fairyi.shop.newretail.common.vo.R;
import com.feri.shop.newretail.provider.dao.UserDao;
import com.feri.shop.newretail.provider.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserDao,User> implements UserService {
    @Autowired
    private UserDao userDao;
    @Override
    public R save(UserDto userDto) {
        User user = new User();
        user.setPassword(EncryptionUtil.AESEnc(KeyConfig.PASSKEY,userDto.getPass()));
        user.setPhone(userDto.getPhone());
        int r=userDao.save(user);
        return R.setResult(r>0,"新增用户");
    }

    @Override
    public R queryPhone(String phone) {
        if (StrUtil.checkNotEmpty(phone)){
            User user=userDao.selectByPhone(phone);
            if (user!=null){
                return  R.setERROR("手机号码已存在");
            } else {
                return R.setOK("可以使用该手机号码");
            }
        } else {
            return R.setERROR("请输入手机号");
        }
    }
}

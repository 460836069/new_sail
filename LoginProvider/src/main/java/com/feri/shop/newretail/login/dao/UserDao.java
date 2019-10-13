package com.feri.shop.newretail.login.dao;

import com.fairy.shop.newretail.entity.User;
import org.apache.ibatis.annotations.*;
//@Param是MyBatis所提供的(org.apache.ibatis.annotations.Param)，作为Dao层的注解，作用是用于传递参数，从而可以与SQL中的的字段名相对应，一般在2=<参数数<=5时使用最佳。
public interface UserDao {
    @Select("select * from t_user where flag=1 and phone=#{phone}")
    @ResultType(User.class)
    User selectByPhone(String phone);
    //修改密码
    @Update("update t_user set password=#{password} where phone=#{phone}")
    int updatePass(@Param("phone") String phone, @Param("password") String password);
}

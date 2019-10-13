package com.feri.shop.newretail.login.service.Impl;

import com.alibaba.fastjson.JSON;
import com.fairy.shop.newretail.dto.LoginDto;
import com.fairy.shop.newretail.dto.UserDto;
import com.fairy.shop.newretail.entity.User;
import com.fairyi.shop.newretail.common.config.KeyConfig;
import com.fairyi.shop.newretail.common.config.RedisKeyConfig;
import com.fairyi.shop.newretail.common.config.SystemConfig;
import com.fairyi.shop.newretail.common.jwt.JwtUtil;
import com.fairyi.shop.newretail.common.util.EncryptionUtil;
import com.fairyi.shop.newretail.common.util.JedisUtil;
import com.fairyi.shop.newretail.common.util.TimeUtil;
import com.fairyi.shop.newretail.common.vo.R;
import com.feri.shop.newretail.login.dao.UserDao;
import com.feri.shop.newretail.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    private JedisUtil jedisUtil = JedisUtil.getInstance();

    @Override  //校验是否被冻结
    public R checkFreeze(String phone) {
        return R.setResult(!jedisUtil.exists(RedisKeyConfig.LOGINFREEZE + phone), "账号不可用");
    }

    @Override //校验令牌  参数是令牌的信息头的key
    public R checkToken(String token) {
        //校验JWT令牌  解析令牌 是否为空 不为空 说明令牌存在（本质是通过令牌头的key查subject对象，他是令牌的主体）
        if (JwtUtil.checkJWT(token)) {
            //2.令牌不为空，说明存在令牌，需要校验令牌是否有效，查看JWT缓存是否有该令牌
            if (jedisUtil.exists(RedisKeyConfig.JWTTOKEN + token)) {
                //令牌有效
                return R.setOK("令牌有效");
            } else {
                //校验是否是被迫下线
                if (jedisUtil.hexists(RedisKeyConfig.LOGINFORCE, token)) {
                    R r = R.setERROR("令牌无效，被迫下线", jedisUtil.hget(RedisKeyConfig.LOGINFORCE, token));
                    jedisUtil.hdel(RedisKeyConfig.LOGINFORCE, token);
                    return r;
                }
            }
        }
        return R.setERROR("令牌失效，请重新登录");
    }

    @Override
    public R login(LoginDto loginDto) {
        //验证账号是否冻结
        if (jedisUtil.exists(RedisKeyConfig.LOGINFREEZE + loginDto.getPhone())) {
            //账号冻结
            return R.setERROR("账号已冻结，明天再来吧");
        } else {
            User user = userDao.selectByPhone(loginDto.getPhone());
            loginDto.setId(user.getId());
            if (user != null) {
                //校验密码
                if (Objects.equals(user.getPassword(), EncryptionUtil.AESEnc(KeyConfig.PASSKEY, loginDto.getPass()))) {
                    //密码正确，生成令牌
                    String token = JwtUtil.createJWT(JSON.toJSONString(loginDto));
                    //验证当前设备类型下有没有登陆过，如没登陆过，直接将令牌存到redis中，如有登陆过，也就是说这次登录是顶掉了正在登陆的用户，将正在登录的用户的令牌存到强制下线的列表中。
                    // key为手机号和类型 值为当前设备的令牌 30分钟
                    if (jedisUtil.exists(RedisKeyConfig.USERTOKEN + loginDto.getPhone() + ":" + loginDto.getType())) {
                        //若该设备类型下，此账号有登录，得到上次登录的令牌(因为要顶掉)
                        String t = jedisUtil.get(RedisKeyConfig.USERTOKEN + loginDto.getPhone() + ":" + loginDto.getType());
                        //记录被迫下线。 将被迫下线的令牌进行存储到 强制下线的缓存列表中 Hash类型 字段：令牌 值：设备号
                        jedisUtil.hset(RedisKeyConfig.LOGINFORCE, t, loginDto.getMac());
                        //删除无用的key   RedisKeyConfig.JWTTOKEN + token 键
                        jedisUtil.del(RedisKeyConfig.JWTTOKEN + token, JSON.toJSONString(loginDto));
                    }
                    //将令牌存储到Redis                     key为令牌 值为用户信息  30分钟
                    jedisUtil.setExpire(RedisKeyConfig.JWTTOKEN + token, JSON.toJSONString(loginDto), SystemConfig.TOKENTIMES * 60);
                    //                                    key为手机号和类型 值为当前设备的令牌 30分钟
                    jedisUtil.setExpire(RedisKeyConfig.USERTOKEN + loginDto.getPhone() + ":" + loginDto.getType(), token, SystemConfig.TOKENTIMES * 60);
                    return R.setOK("OK", token);
                }
            }
            //失败次数记录，超过次数，冻结账号                                 key为手机号：失败的次数 值为
            Set<String> errorKeys = jedisUtil.keys(RedisKeyConfig.LOGINERROR + loginDto.getPhone() + "*");//此方法是得到对应缓存列表中所有的key
            if (errorKeys != null) {
                jedisUtil.setExpire(RedisKeyConfig.LOGINERROR + loginDto.getPhone() + ":" + (errorKeys.size() + 1), "", 300);
                //失败过
                if (errorKeys.size() >= 2) {
                    jedisUtil.setExpire(RedisKeyConfig.LOGINFREEZE + loginDto.getPhone(), TimeUtil.getDate(), 24 * 60 & 60);
                    return R.setERROR("连续失败多次，账号被冻结，请24小后再来。");
                }
            } else {
                jedisUtil.setExpire(RedisKeyConfig.LOGINERROR + loginDto.getPhone() + ":1", "", 300);
            }
            return R.setERROR("用户名或密码错误，登录失败");
        }
    }

    @Override  //找回密码
    public R findPass(UserDto userDto) {
        //1.验证账号是否被冻结
        if (!jedisUtil.exists(RedisKeyConfig.LOGINFREEZE+userDto.getPhone())){
            //2.设置密码  存储到数据库，需要先将密码加密
            int r=userDao.updatePass(userDto.getPhone(),EncryptionUtil.AESEnc(KeyConfig.PASSKEY,userDto.getPass()));
            if (r>0){
                delKeys(userDto.getPhone());
            }
            return R.setResult(r>0,"密码找回");
        } else {
            return R.setERROR("账号被冻结");
        }
    }

    @Override
    public R changePass(String token, String pass) {
        //1.校验令牌是否有效
        if (jedisUtil.exists(RedisKeyConfig.JWTTOKEN+token)){
            //2.取出用户数据
            LoginDto loginDto=JSON.parseObject(jedisUtil.get(RedisKeyConfig.JWTTOKEN + token),LoginDto.class);
            int r =userDao.updatePass(loginDto.getPhone(),EncryptionUtil.AESEnc(KeyConfig.PASSKEY,pass));
            if (r>0){
                delKeys(loginDto.getPhone());
            }
            return R.setResult(r>0,"密码修改");
        } else {
            return R.setERROR("令牌无效，重新登录");
        }
    }

    @Override
    public R exit(String token) {
        if (jedisUtil.exists(RedisKeyConfig.JWTTOKEN+token)){
            //各种删除
            LoginDto loginDto=JSON.parseObject(jedisUtil.get(RedisKeyConfig.JWTTOKEN+token),LoginDto.class);
            delKeys(loginDto.getPhone());
            return R.setOK("注销成功");
        } else {
            return R.setERROR("令牌无效，重新登录");
        }
    }

    private void delKeys(String phone){
        //密码修改成功，当前所有相关信息全部失效
        Set<String> sets=jedisUtil.keys(RedisKeyConfig.USERTOKEN+phone+":*");
        for (String s:sets){
            String t=jedisUtil.get(RedisKeyConfig.JWTTOKEN+jedisUtil.get(s));
            jedisUtil.hdel(RedisKeyConfig.LOGINFORCE,t);
        }
    }
}

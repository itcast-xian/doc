package cn.itcast.service.impl;

import cn.itcast.exception.ServiceException;
import cn.itcast.mapper.UserMapper;
import cn.itcast.pojo.User;
import cn.itcast.service.UserService;
import cn.itcast.utils.JwtUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper usermapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void add(User user) {

        //判定用户名是否存在
        User u = usermapper.findByUsername(user.getUsername());
        if(u!=null){
            System.out.println("-=======> 用户名已存在");
            throw new ServiceException("用户名已存在");
        }

        //获取一个salt
        String s = RandomStringUtils.randomAlphanumeric(1, 10);
        System.out.println("----------> salt" + s);

        //加密
        String pass = DigestUtils.md5DigestAsHex((user.getPassword() + s).getBytes());
        user.setPassword(pass);

        user.setSalt(s);

        //补全属性
        user.setCreatetime(new Date());
        usermapper.add(user);
    }




    @Override
    public String login(User user) {

        //1. 校验用户名与密码是否存在
        if(Strings.isNullOrEmpty(user.getUsername()) || Strings.isNullOrEmpty(user.getPassword())){
            throw new ServiceException("用户名或密码不能为空");
        }

        //2. 校验用户名与密码的正确性
        //2.1 根据用户名查询用户信息 ;
        User u = this.usermapper.findByUsername(user.getUsername());
        if(u == null){
            throw new ServiceException("用户名不存在");
        }

        //2.2 获取用户注册加的盐, 对密码进行加盐加密
        String salt = u.getSalt();
        String pass = DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes());

        //2.3 判定密码是否匹配
        if(!pass.equals(u.getPassword())){
            throw new ServiceException("用户名或密码错误");
        }

        //生成jwt令牌
        u.setPassword("");
        u.setSalt("");
        String s = JSON.toJSONString(u);
        Map map = JSON.parseObject(s, Map.class);

        try {
            String jwt = JwtUtil.createJwt(map);

            // 将jwt令牌保存在redis中, 并设置过期时间 ;
            String key = "TOKEN_"+DigestUtils.md5DigestAsHex(jwt.getBytes());
            redisTemplate.boundValueOps(key).set(jwt); //
            redisTemplate.expire(key,7200, TimeUnit.SECONDS);

            return jwt;
        } catch (Exception e) {
            throw new ServiceException("登录出现异常");
        }

    }
}

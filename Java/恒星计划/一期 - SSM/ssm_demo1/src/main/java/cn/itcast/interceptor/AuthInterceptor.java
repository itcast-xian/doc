package cn.itcast.interceptor;

import cn.itcast.common.Result;
import cn.itcast.pojo.User;
import cn.itcast.utils.CookieUtil;
import cn.itcast.utils.JwtUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.jsonwebtoken.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AuthInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //判定请求的路径是否是白名单
        ArrayList<String> uriList = Lists.newArrayList("login.do", "register.do");
        String requestURI = request.getRequestURI();
        for (String uri : uriList) {
            if(requestURI.endsWith(uri)){
                return true;
            }
        }

        if(requestURI.startsWith("/index")){
            return true;
        }

        //从header中获取jwt令牌
        String jwt_token = request.getHeader("Authorization");
        System.out.println(jwt_token);


        //如果未登录 , 返回页面信息 , 让用户进行登录操作
        if(Strings.isNullOrEmpty(jwt_token)){
            System.out.println("----------> 未登录 ...");
            Result result = new Result(false,"NO_LOGIN");
            response.getWriter().write(JSON.toJSONString(result));
            return false;
        }


        //到redis中验证jwt令牌是否已经到期
        String key = "TOKEN_"+ DigestUtils.md5DigestAsHex(jwt_token.getBytes());
        Object value = redisTemplate.boundValueOps(key).get();
        if(value == null || !jwt_token.equals(value.toString())){
            System.out.println("----------> 前后端 jwt令牌不一致 ...");
            Result result = new Result(false,"NO_LOGIN");
            response.getWriter().write(JSON.toJSONString(result));
            return false;
        }


        // 重置过期时间
        redisTemplate.expire(key,7200, TimeUnit.SECONDS);

        //获取JWT令牌的自定义内容部分
        String claims = JwtUtil.verify(jwt_token);
        System.out.println(claims);// json 个数

        //将获取到的用户信息存储起来
        User user = JSON.parseObject(claims, User.class);
        CurrentUserHolder.setUser(user);

        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        CurrentUserHolder.removeUser();
    }

}
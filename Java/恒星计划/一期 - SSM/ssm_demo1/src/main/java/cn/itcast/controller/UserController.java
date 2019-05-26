package cn.itcast.controller;

import cn.itcast.common.Result;
import cn.itcast.interceptor.CurrentUserHolder;
import cn.itcast.pojo.User;
import cn.itcast.service.UserService;
import cn.itcast.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/register")
    public Result register(@RequestBody  User user){
        userService.add(user);
        return new Result(true,"注册成功");
    }

    @RequestMapping("/login")
    public Result login(@RequestBody  User user, HttpServletRequest request, HttpServletResponse response){

        //登录
        String jwt = userService.login(user);

        return new Result(true,jwt);
    }


    @RequestMapping("/findUserList")
    public List<User> findUserList(){

        System.out.println("-----------------> 请求已经发送至controller" );

        User user = CurrentUserHolder.getUser();

        System.out.println("------------------> 当前登录用户: " + user);

        return null;
    }

}

package cn.itcast.service;

import cn.itcast.common.PageResult;
import cn.itcast.pojo.Course;
import cn.itcast.pojo.User;

public interface UserService {

    public void add(User user);

    public String login(User user);

}

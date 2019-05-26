package cn.itcast.service;

import cn.itcast.pojo.User;

public interface UserService {

    public void add(User user);

    public String login(User user);

}

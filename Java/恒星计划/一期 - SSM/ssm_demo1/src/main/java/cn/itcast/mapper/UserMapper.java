package cn.itcast.mapper;

import cn.itcast.pojo.User;

public interface UserMapper {

    public void add(User user);

    public User findByUsername(String username);

}

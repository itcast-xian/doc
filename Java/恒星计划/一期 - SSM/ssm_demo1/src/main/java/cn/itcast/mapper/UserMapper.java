package cn.itcast.mapper;

import cn.itcast.pojo.Course;
import cn.itcast.pojo.User;

import java.util.List;

public interface UserMapper {

    public void add(User user);

    public User findByUsername(String username);

}

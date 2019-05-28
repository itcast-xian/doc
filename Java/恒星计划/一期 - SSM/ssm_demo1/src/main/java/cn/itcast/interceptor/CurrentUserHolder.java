package cn.itcast.interceptor;

import cn.itcast.pojo.User;

public class CurrentUserHolder {

    private  static  ThreadLocal<User> localuser = new ThreadLocal<User>();

    public static void setUser(User user){
        localuser.set(user);
    }

    public static User getUser(){
        return localuser.get();
    }

    public static void removeUser(){
        localuser.remove();
    }
}

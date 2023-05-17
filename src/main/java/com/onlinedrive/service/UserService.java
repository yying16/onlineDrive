package com.onlinedrive.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.onlinedrive.domain.User;
import com.onlinedrive.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    //判断用户是否已注册
    public boolean checkUserHasRegister(String account) {
        try {
            return userMapper.selectById(account) != null;
        } catch (Exception e) {
            return false;
        }
    }

    //判断该用户是否已注册(已注册则返回true)
    public boolean CheckUser(User user) {
        try {
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("account", user.getAccount());
            wrapper.eq("password", user.getPassword());
            return userMapper.selectOne(wrapper) != null;
        } catch (Exception e) {
            return false;
        }
    }

    //根据账号和密码放回对应的User
    public User getUser(String account, String password) {
        try {
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("account", account);
            wrapper.eq("password", password);
            return userMapper.selectOne(wrapper);
        } catch (Exception e) {
            return null;
        }
    }

    //判断该用户是否为管理员
    public boolean isAdmin(User user) {
        try {
            return userMapper.selectById(user.getAccount()).getStatus() == 1;
        } catch (Exception e) {
            return false;
        }
    }

    //注册用户，将用户的数据写入数据库(创建成功则返回true，失败则返回false)
    public boolean CreateUser(User user) {
        try {
            userMapper.insert(user);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //修改用户密码
    public void modifyPassword(User user, String newPassword) {
        try {
            user = userMapper.selectById(user.getAccount());
            user.setPassword(newPassword);
            userMapper.updateById(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

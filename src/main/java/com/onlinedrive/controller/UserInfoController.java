package com.onlinedrive.controller;

import com.onlinedrive.domain.User;
import com.onlinedrive.interceptor.UserInfoGetter;
import com.onlinedrive.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


//针对用户信息的控制器(用户查看自己信息、修改自己密码等)

@RestController
@RequestMapping("/infos")
public class UserInfoController {
    @Autowired
    private UserInfoGetter userInfoGetter;
    @Autowired
    private UserService userService;

    private static long onlineStatus = 0;//用户在线状态(用于解决用户登入进页面后"欢迎弹窗"重复弹出的问题)
    /*
    * onlineStatus == 0: 用户未登录
    * onlineStatus == 1: 用户成功登录(这时候要弹出欢迎框)
    * onlineStatus > 1: 用户在线(不用再显示"欢迎弹窗")
    * */

    public static void setOnlineStatus(long onlineStatus) {
        UserInfoController.onlineStatus = onlineStatus;
    }

    //当前用户获取自己的信息
    @GetMapping("/getMyInfo")
    public User getMyInfo() {
        User user = userInfoGetter.getUser();
        System.out.println(user);
        ++onlineStatus; //登录状态自增
        return user;
    }

    //返回用户登录状态
    @GetMapping("/onlineStatus")
    private long getOnlineStatus() {
        return onlineStatus;
    }


    //修改密码 change password
    @GetMapping("/cp/{p}")
    public void editPsw(@PathVariable("p") String newPsw) {
        User user = userInfoGetter.getUser();
        userService.modifyPassword(user, newPsw);
    }
}

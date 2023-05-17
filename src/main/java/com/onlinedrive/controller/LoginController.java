package com.onlinedrive.controller;

import com.onlinedrive.domain.User;
import com.onlinedrive.interceptor.UserInfoGetter;
import com.onlinedrive.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    UserService userService;
    @Autowired
    UserInfoGetter userInfoGetter;


    @GetMapping("/login")
    public String login(Model model) {//初始化登录界面
        model.addAttribute("user", new User());
        return "login";
    }


    @PostMapping("/toLogin")
    //进行登录验证
    public String toLogin(User user,Model model,HttpSession session) {//进行登录验证
        if (userService.CheckUser(user)) {//验证账号密码是否已在用户数据库内
            user = userService.getUser(user.getAccount(),user.getPassword());
            session.setAttribute("onlineUser", user);
            userInfoGetter.setUser(user);
            return "redirect:/pages/main.html";
        }
        model.addAttribute("wrong", "用户名或密码错误，请重新登录！");//设置警告信息
        return "login";//返回登录界面
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession,Model model) {//退出登录
        httpSession.invalidate();//清除
        userInfoGetter.UserLogout();//当前用户退出
        UserInfoController.setOnlineStatus(0);//重置用户在线状态
        model.addAttribute("user",new User());
        return "login";
    }
}

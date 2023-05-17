package com.onlinedrive.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/index/index")
    public String index(){
        return "forward:/pages/main.html";
    }
}

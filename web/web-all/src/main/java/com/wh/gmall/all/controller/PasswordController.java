package com.wh.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PasswordController {

    @GetMapping("/login.html")
    public String login(HttpServletRequest request){
        String originUrl = request.getParameter("originUrl");
        System.out.println(originUrl);
        request.setAttribute("originUrl", originUrl);
        return "login";
    }

}

package com.power.travel.controller;

import com.power.travel.core.Result;
import com.power.travel.model.ToEmail;
import com.power.travel.model.User;
import com.power.travel.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;
    @Autowired
    private LoginService loginService;

    @RequestMapping("/loginUI")
    public String loginUI() {
        return "login/index-login";
    }
    @RequestMapping("/forget")
    public String forgetPw() {
        return "login/forget";
    }
    @RequestMapping("/sendEmail")
    @ResponseBody
    public Result sendEmail(@RequestBody Map<String,String> toEmail, HttpServletRequest request) {
        return loginService.sendEmail(toEmail,request);
    }
    @RequestMapping("/ckCode")
    @ResponseBody
    public Result sendCode(@RequestParam("ckCode") String code,@RequestParam("email") String email) {
        return loginService.checkCode(code,email);
    }
    @RequestMapping("/resPw")
    @ResponseBody
    public Result resetPw(@RequestParam("realPw") String pw,@RequestParam("email") String email) {
        return loginService.resetPw(pw,email);
    }
    @RequestMapping("/login")
    @ResponseBody
    public Result login(Model model, User user, HttpServletResponse response) {
        return loginService.login(user, response);
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        loginService.logout(request, response);
        //SpringMVC重定向
        return "redirect:/";
    }

    @RequestMapping("/registerUI")
    public String registerUI() {
        return "login/index-register";
    }

    @RequestMapping("/register")
    @ResponseBody
    public Result register(Model model, User user) {
        return loginService.register(user);
    }
}

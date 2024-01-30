package com.dbProject.controller;

import com.dbProject.dto.ThpLoginInputDto;
import com.dbProject.model.ThpUser;
import com.dbProject.service.ThpUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/user")
public class LoginController {
    @Autowired
    private ThpUserService userService;

    @PostMapping(value = "/signup")
    @ResponseBody
    public String signUp(@RequestBody ThpUser user) {
        if (userService.signUp(user))
            return "Signup was success";
        else
            return "signup failed";
    }

    @PostMapping(value = "/login")
    @ResponseBody
    public String loginUser(@RequestBody ThpLoginInputDto inputDto) {
        return userService.loginUser(inputDto.getProvidedUserID(), inputDto.getProvidedPassword());
    }

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new ThpUser());
        return "signup";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginInputDto", new ThpLoginInputDto());
        return "login";
    }
}

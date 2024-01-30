package com.dbProject.controller;

import com.dbProject.dto.ThpLoginInputDto;
import com.dbProject.model.ThpUser;
import com.dbProject.service.ThpUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
public class LoginController {
    @Autowired
    private ThpUserService userService;

    @PostMapping(value = "/signup")
    public String signUp(@RequestBody ThpUser user){
        if (userService.signUp(user))
            return "Signup was success";
        else
            return "signup failed";
    }
    @PostMapping(value = "/login")
    public String loginUser(@RequestBody ThpLoginInputDto inputDto){
        return userService.loginUser(inputDto.getProvidedUserID(), inputDto.getProvidedPassword());
    }


}

package com.dbProject.controller;

import com.dbProject.dto.ThpCreateAccountInputDto;
import com.dbProject.dto.ThpLoginInputDto;
import com.dbProject.model.ThpUserAccount;
import com.dbProject.service.ThpAccountManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/account")
public class AccountManagerController {
    @Autowired
    private ThpAccountManagerService accountManagerService;

    @PostMapping(value = "/createNewAccount")
    @ResponseBody
    public ThpUserAccount createNewAccount(@RequestBody ThpCreateAccountInputDto inputDto){
        return accountManagerService.createNewAccount(inputDto.getUserID(), inputDto.getInitialBalance());
    }


    @GetMapping("/createNewAccount")
    public String createNewAccount(Model model) {
        model.addAttribute("createAccount", model);
        return "createAccount";
    }
}

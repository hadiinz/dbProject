package com.dbProject.controller;

import com.dbProject.dto.ThpCreateAccountInputDto;
import com.dbProject.model.ThpUserAccount;
import com.dbProject.service.ThpAccountManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/account")
public class AccountManagerController {
    @Autowired
    private ThpAccountManagerService accountManagerService;

    @PostMapping(value = "/createNewAccount")
    public ThpUserAccount createNewAccount(@RequestBody ThpCreateAccountInputDto inputDto){
        return accountManagerService.createNewAccount(inputDto.getUserID(), inputDto.getInitialBalance());
    }
}

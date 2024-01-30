package com.dbProject.service;

import com.dbProject.model.ThpUserAccount;

import java.math.BigDecimal;

public interface ThpAccountManagerService {
    public ThpUserAccount createNewAccount(String userID, BigDecimal initialBalance);


}

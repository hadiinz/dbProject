package com.dbProject.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Data
public class ThpUserAccount {
    private int accountId;
    private String userID;
    private String cardNumber;
    private String shebaNumber;
    private BigDecimal balance;
    private BigDecimal dailyLimit;
    private Date lastResetDate;
    private BigDecimal dailySatnaPayaLimit;
}

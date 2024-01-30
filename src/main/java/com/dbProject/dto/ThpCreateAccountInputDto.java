package com.dbProject.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ThpCreateAccountInputDto {
    String userID;
    BigDecimal initialBalance;
}

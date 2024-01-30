package com.dbProject.dto;

import com.dbProject.model.ThpTransactionType;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class ThpHandleTransactionInputDto {
    String sourceCard;
    String destinationCard;
    BigDecimal amount;
    ThpTransactionType transactionType;
}

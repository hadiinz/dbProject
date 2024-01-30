package com.dbProject.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ThpTransaction {
    private String sourceCardNumber;
    private String destinationCardNumber;
    private BigDecimal amount;
    private boolean isSuccess;
    private LocalDateTime dateTime;
    private String traceNumber;
    private ThpTransactionType transactionType;
}

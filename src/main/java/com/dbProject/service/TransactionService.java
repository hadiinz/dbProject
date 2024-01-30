package com.dbProject.service;

import com.dbProject.model.ThpTransaction;
import com.dbProject.model.ThpTransactionType;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    public String handleTransaction(String sourceCard, String destinationCard, BigDecimal amount, ThpTransactionType transactionType);
    public List<ThpTransaction> getUserTransactions(String userID, int number);
    public ThpTransaction traceTransaction(String traceNumber, String userID);
}

package com.dbProject.controller;

import com.dbProject.dto.ThpGetTransactionListInputDto;
import com.dbProject.dto.ThpHandleTransactionInputDto;
import com.dbProject.dto.ThpTraceTransInputDto;
import com.dbProject.model.ThpTransaction;
import com.dbProject.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/transaction")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping("/handle")
    public String handleTransaction(@RequestBody ThpHandleTransactionInputDto inputDto){
        return transactionService.handleTransaction(inputDto.getSourceCard(),inputDto.getDestinationCard(),inputDto.getAmount(), inputDto.getTransactionType());
    }
    @PostMapping("/history")
    public List<ThpTransaction> getUserTransactions(@RequestBody ThpGetTransactionListInputDto inputDto){
        return transactionService.getUserTransactions(inputDto.getUserID(), inputDto.getNumber());
    }

    @PostMapping("/trace")
    public ThpTransaction traceTransaction(@RequestBody ThpTraceTransInputDto inputDto){
        return transactionService.traceTransaction(inputDto.getTraceNumber(), inputDto.getUserID());
    }
}

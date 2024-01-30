package com.dbProject.controller;

import com.dbProject.dto.ThpGetTransactionListInputDto;
import com.dbProject.dto.ThpHandleTransactionInputDto;
import com.dbProject.dto.ThpTraceTransInputDto;
import com.dbProject.model.ThpTransaction;
import com.dbProject.model.ThpUser;
import com.dbProject.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(path = "/transaction")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping("/handle")
    @ResponseBody
    public String handleTransaction(@RequestBody ThpHandleTransactionInputDto inputDto){
        return transactionService.handleTransaction(inputDto.getSourceCard(),inputDto.getDestinationCard(),inputDto.getAmount(), inputDto.getTransactionType());
    }
    @PostMapping("/history")
    @ResponseBody
    public List<ThpTransaction> getUserTransactions(@RequestBody ThpGetTransactionListInputDto inputDto){
        return transactionService.getUserTransactions(inputDto.getUserID(), inputDto.getNumber());
    }

    @PostMapping("/trace")
    @ResponseBody
    public ThpTransaction traceTransaction(@RequestBody ThpTraceTransInputDto inputDto){
        return transactionService.traceTransaction(inputDto.getTraceNumber(), inputDto.getUserID());
    }


    @GetMapping("/handle")
    public String handleTransactionForm(Model model) {
        model.addAttribute("inputDto", new ThpHandleTransactionInputDto());
        return "handleTransaction";
    }

    @GetMapping("/history")
    public String getUserTransactionsForm(Model model) {
        model.addAttribute("inputDto", new ThpGetTransactionListInputDto());
        return "getUserTransactions";
    }

    @GetMapping("/trace")
    public String traceTransactionForm(Model model) {
        model.addAttribute("inputDto", new ThpTraceTransInputDto());
        return "traceTransaction";
    }
}

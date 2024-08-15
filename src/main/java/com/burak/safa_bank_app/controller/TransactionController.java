package com.burak.safa_bank_app.controller;

import com.burak.safa_bank_app.entity.Transaction;
import com.burak.safa_bank_app.service.impl.BankStatement;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bankStatement")
@AllArgsConstructor
public class TransactionController {

    private BankStatement bankStatement;

    @GetMapping
    public List<Transaction> generateBankStatement(@RequestParam String accountNumber,
                                                   @RequestParam String startDate,
                                                   @RequestParam String endDate){

        return bankStatement.generateStatement(accountNumber, startDate, endDate);
    }
}

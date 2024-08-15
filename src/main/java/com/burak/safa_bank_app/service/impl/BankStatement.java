package com.burak.safa_bank_app.service.impl;

import com.burak.safa_bank_app.entity.Transaction;
import com.burak.safa_bank_app.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@AllArgsConstructor
public class BankStatement {

    private final TransactionRepository transactionRepository;

    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) {
        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
            end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected format is yyyy-MM-dd.", e);
        }

        return transactionRepository.findByAccountNumberAndCreatedAtBetween(accountNumber, start, end);
    }
}

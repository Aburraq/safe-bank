package com.burak.safa_bank_app.repository;

import com.burak.safa_bank_app.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByAccountNumberAndCreatedAtBetween(String accountNumber, LocalDate start, LocalDate end);
}

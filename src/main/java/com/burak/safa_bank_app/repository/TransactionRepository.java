package com.burak.safa_bank_app.repository;

import com.burak.safa_bank_app.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}

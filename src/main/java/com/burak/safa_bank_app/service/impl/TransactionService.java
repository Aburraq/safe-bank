package com.burak.safa_bank_app.service.impl;

import com.burak.safa_bank_app.dto.TransactionRequest;
import com.burak.safa_bank_app.entity.Transaction;

public interface TransactionService {

    void saveTransaction(TransactionRequest transactionRequest);
}

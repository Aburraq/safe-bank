package com.burak.safa_bank_app.service.impl;

import com.burak.safa_bank_app.dto.BankResponse;
import com.burak.safa_bank_app.dto.UserRequest;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);
}

package com.burak.safa_bank_app.service.impl;

import com.burak.safa_bank_app.dto.BankResponse;
import com.burak.safa_bank_app.dto.EnquiryRequest;
import com.burak.safa_bank_app.dto.UserRequest;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
    String nameEnquiry(EnquiryRequest enquiryRequest);
}

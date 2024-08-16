package com.burak.safa_bank_app.service.impl;

import com.burak.safa_bank_app.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
    void sendEmailWithAttachment(EmailDetails emailDetails);
}

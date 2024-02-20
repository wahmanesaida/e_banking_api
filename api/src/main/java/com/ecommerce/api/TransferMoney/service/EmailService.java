package com.ecommerce.api.TransferMoney.service;

import com.ecommerce.api.TransferMoney.dto.EmailDetails;

public interface EmailService {
    void SendEmailAlert(EmailDetails emailDetails);
}

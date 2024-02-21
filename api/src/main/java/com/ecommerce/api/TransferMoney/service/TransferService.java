package com.ecommerce.api.TransferMoney.service;

import com.ecommerce.api.Entity.User;
import com.ecommerce.api.Entity.Beneficiary;
import com.ecommerce.api.TransferMoney.dto.BeneficiaryDto;
import com.ecommerce.api.TransferMoney.dto.Kyc;
import com.ecommerce.api.TransferMoney.dto.TransfertDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface TransferService {
    void modifyuKYC(String username, Kyc kyc);

    User showKyc(String phone);

    String getOtpFromUser(String email);

    String getEmailForClient(long client_id);

    Beneficiary transferMoney(TransfertDto transfertDto, long client_id, long bene_id, BeneficiaryDto bene);

    String generateTransferReference();

    Beneficiary selectOrAddBeneficiary(long id_user, long id_beneficiary, BeneficiaryDto beneficiary);

    String trs(TransfertDto transfertDto, long client_id, long bene_id, BeneficiaryDto bene);

    void ExpenseManagement(TransfertDto transfertDto);

    void DebitCreditAccount(TransfertDto transfertDto, User user, Beneficiary beneficiary);

    void processTransaction(User user);

    String checkAmountOfTransfert(TransfertDto transfertDto, User user, BigDecimal checkamount);

}

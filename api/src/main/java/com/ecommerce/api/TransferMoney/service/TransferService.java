package com.ecommerce.api.TransferMoney.service;

import com.ecommerce.api.Entity.User;
import com.ecommerce.api.Entity.Beneficiary;
import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.TransferMoney.Response.MessageResponse;
import com.ecommerce.api.TransferMoney.dto.BeneficiaryDto;
import com.ecommerce.api.TransferMoney.dto.Kyc;
import com.ecommerce.api.TransferMoney.dto.TransfertDto;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public interface TransferService {
    void modifyuKYC(String username, Kyc kyc);

    User showKyc(String phone);

    /* Transfert showTransfer(String transferRef); */

    String generateTransferReference();

    Beneficiary selectOrAddBeneficiary(long id_user, long id_beneficiary, BeneficiaryDto beneficiary);

    MessageResponse trs(TransfertDto transfertDto, long client_id, long bene_id, BeneficiaryDto bene);

    void ExpenseManagement(TransfertDto transfertDto);

    void DebitCreditAccount(TransfertDto transfertDto, User user, Beneficiary beneficiary);

    void processTransaction(User user);

    MessageResponse checkAmountOfTransfert(TransfertDto transfertDto, long client_id);

    String getOtpFromUser(String email);

    String getEmailForClient(long client_id);
    Beneficiary SelectBene(long id_beneficiary);
    MessageResponse AddBeneficiary(BeneficiaryDto beneficiaryDto, long id_user);
    User ShowKycByPieceIdentite(String numeroPieceIdentite);


}

package com.ecommerce.api.ReturnTheTransfer.ServiceImp;

import com.ecommerce.api.Entity.Motif;
import com.ecommerce.api.Entity.TransferStatus;
import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.Entity.Type_transfer;
import com.ecommerce.api.Repository.TransfertRepository;
import com.ecommerce.api.ReturnTheTransfer.DTO.ReturnTransferDTO;
import com.ecommerce.api.ReturnTheTransfer.Service.GabBOA;
import com.ecommerce.api.TransferMoney.Response.MessageResponse;
import com.ecommerce.api.TransferMoney.dto.MailStructure;
import com.ecommerce.api.TransferMoney.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GabBOAImp implements GabBOA {

    @Autowired
    TransfertRepository transfertRepository;

    @Autowired
    EmailService emailService;

    @Override
    public Transfert AccessTheTransactionByRef(String transferRef) {
        Optional<Transfert> transfertOptional= transfertRepository.findByTransferRef(transferRef);
        if(transfertOptional.isEmpty()){
            return null;
        }
        else{
            return transfertOptional.get();
        }

    }

    @Override
    public MessageResponse ReturnTransfer(ReturnTransferDTO returnTransferDTO) {
        Transfert transfert=AccessTheTransactionByRef(returnTransferDTO.getTransferRef());
        if(transfert == null){
            return new MessageResponse("transer not found!");

        }else{
            if(transfert.getStatus().equals(TransferStatus.A_servir) || transfert.getStatus().equals(TransferStatus.Débloqué))
            {

                if (transfert.getAgent().getId() == returnTransferDTO.getId_agent()) {
                    transfert.setMotif(returnTransferDTO.getMotif());
                    transfert.getAgent().setAccount_amount(transfert.getAgent().getAccount_amount().add(transfert.getAmount_transfer()));
                    transfert.setStatus(TransferStatus.Restitué);
                    transfertRepository.save(transfert);
                    if (returnTransferDTO.isNotification()) {
                        emailService.sendMail(
                                MailStructure.builder()
                                        .subject("Transfert restituéé")
                                        .recipient(transfert.getClient().getUsername())
                                        .message("Bonjour " + transfert.getClient().getName() + "  nous espérons que vous allez bien, votre virement associé à la référence suivante: " + transfert.getTransferRef() + "   a été restitué avec succé par l'agent: " + transfert.getAgent().getUsername() + " "
                                                + " \n" + " "
                                                + "don't share this with anyone!"
                                        )
                                        .build());
                    }

                    return new MessageResponse("transfert réstitué avec succé ");


                } else {
                    return new MessageResponse("le transfert doit etre émise par le même agent");
                }
            }else{
                return new MessageResponse("le transfert doit etre à l'état à servir ou à l'état débloqué");
            }


        }


    }
}

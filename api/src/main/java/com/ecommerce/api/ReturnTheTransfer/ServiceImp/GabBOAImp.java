package com.ecommerce.api.ReturnTheTransfer.ServiceImp;

import com.ecommerce.api.Entity.Motif;
import com.ecommerce.api.Entity.TransferStatus;
import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.Entity.User;
import com.ecommerce.api.Repository.TransfertRepository;
import com.ecommerce.api.Repository.UserRepository;
import com.ecommerce.api.ReturnTheTransfer.DTO.ReturnTransferDTO;
import com.ecommerce.api.ReturnTheTransfer.Service.GabBOA;
import com.ecommerce.api.ServingTransfer.Dto.TransferPaymentDto;
import com.ecommerce.api.TransferMoney.Response.MessageResponse;
import com.ecommerce.api.TransferMoney.dto.MailStructure;
import com.ecommerce.api.TransferMoney.service.EmailService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class GabBOAImp implements GabBOA {

    @Autowired
    TransfertRepository transfertRepository;

    @Autowired
    UserRepository userRepository;

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

                    return new MessageResponse("transfert réstitué avec succé");


                } else {
                    return new MessageResponse("le transfert doit etre émise par le même agent");
                }
            }else{
                return new MessageResponse("le transfert doit etre à l'état à servir ou à l'état débloqué");
            }


        }


    }



    @Override
    public List<Transfert> allTransfersOfClient(long id) {
        List<Transfert> transferts ;
        transferts=transfertRepository.findAllByClientId(id);
        List<Transfert> transferts1=new ArrayList<>();
        for(Transfert transfert : transferts){
            if(transfert.getStatus().equals(TransferStatus.A_servir)){
                transferts1.add(transfert);
            }
        }


        return transferts1;
    }

    @Override
    public MessageResponse ReturnTheTransferByTheClient(ReturnTransferDTO returnTransferDTO) {
        Transfert transfert = AccessTheTransactionByRef(returnTransferDTO.getTransferRef());
        if(transfert == null){
            return  new MessageResponse("il n'y a pas de transfert associé a ce client !");
        }
        else{
           if(transfert.getClient().getId()==returnTransferDTO.getClient_id()){
               if(transfert.getStatus().equals(TransferStatus.A_servir)){
                   transfert.getClient().setAccount_amount(transfert.getClient().getAccount_amount().add(transfert.getAmount_transfer()));
                   transfert.setStatus(TransferStatus.Restitué);
                   transfertRepository.save(transfert);
                   emailService.sendMail(
                           MailStructure.builder()
                                   .subject("Transert réstitué avec succé")
                                   .recipient(transfert.getClient().getUsername())
                                   .message("Bonjour " + transfert.getClient().getName() + "  nous espérons que vous allez bien, votre virement associé à la référence suivante: " + transfert.getTransferRef() + " "
                                           + " \n" + " "
                                           + "don't share this with anyone!")
                                   .build()
                   );
                   return new MessageResponse("transfert réstitué avec succé");
               }else{
                   return new MessageResponse("le transfert doit etre à l'état à servir");
               }


           }else{
               return new MessageResponse("le transfert que vous tente de réstitué doit etre émise par vous ! ");
           }
        }

    }


    @Override
    public void generateReturnReceipt(@RequestBody TransferPaymentDto transferPaymentDto, HttpServletResponse response) throws IOException, DocumentException {
        Optional<User> clientOptional = userRepository.findById(transferPaymentDto.getTransferRefDTO().getIdAgent());
        Optional<Transfert> transferOptional = transfertRepository
                .findById(transferPaymentDto.getTransferRefDTO().getId());

        User client = clientOptional.get();
        Transfert transfert = transferOptional.get();

        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=pdf_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        Rectangle demiPageSize = new Rectangle(PageSize.A4);

        Document document = new Document(demiPageSize);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        /*
         * Image logo = Image.getInstance("");
         * logo.scaleToFit(100, 100);
         * document.add(logo);
         */

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);

        Paragraph title = new Paragraph("Le reçu de l'extourne du transfert", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String currentDate = dateFormat.format(date);
        PdfContentByte canvas = writer.getDirectContent();
        ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, new Phrase("Date: " + currentDate, fontTitle),
                document.right() - 10, document.bottom() + 10, 0);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setSize(12);

        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20f);
        table.setSpacingAfter(30f);

        PdfPTable table2 = new PdfPTable(2);
        table2.setWidthPercentage(100);
        table2.setSpacingBefore(20f);
        table2.setSpacingAfter(30f);

        addTableCell(table, "Identifiant du transfert", String.valueOf(transfert.getId()), font);
        addTableCell(table, "Expéditeur",
                " " + client.getName(), font);
        addTableCell(table, "Montant du Transfert", String.valueOf(transfert.getAmount_transfer()), font);
        addTableCell(table, "Date d'émission", String.valueOf(transfert.getCreateTime()), font);
        addTableCell(table, "État", String.valueOf(transfert.getStatus()), font);

        addTableCell(table2, "Identifiant du transfert", String.valueOf(transfert.getId()), font);
        addTableCell(table2, "Bénéficiaire", transfert.getBeneficiary().getFirstName() + " " + transfert.getBeneficiary().getLastname(),
                font);
        addTableCell(table2, "Montant du Transfert", String.valueOf(transfert.getAmount_transfer()), font);
        addTableCell(table2, "Date d'émission", String.valueOf(transfert.getCreateTime()), font);
        addTableCell(table2, "État", String.valueOf(transfert.getStatus()), font);

        document.add(table);
        document.add(table2);
        document.close();

    }


    @Override
    public void addTableCell(PdfPTable table, String key, String value, Font font) {
        PdfPCell cell1 = new PdfPCell(new Phrase(key, font));
        cell1.setBorder(Rectangle.BOX); // Set cell border to be a box
        cell1.setPadding(5); // Set cell padding to 5
        PdfPCell cell2 = new PdfPCell(new Phrase(value, font));
        cell2.setBorder(Rectangle.BOX); // Set cell border to be a box
        cell2.setPadding(5); // Set cell padding to 5
        table.addCell(cell1);
        table.addCell(cell2);
    }

}

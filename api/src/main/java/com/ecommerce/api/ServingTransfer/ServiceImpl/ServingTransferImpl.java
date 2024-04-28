package com.ecommerce.api.ServingTransfer.ServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.ecommerce.api.Entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.ecommerce.api.Repository.BeneficiaryRepository;
import com.ecommerce.api.Repository.TransfertRepository;
import com.ecommerce.api.Repository.UserRepository;
import com.ecommerce.api.ServingTransfer.Dto.BeneficiaryDto;
import com.ecommerce.api.ServingTransfer.Dto.TransferPaymentDto;
import com.ecommerce.api.ServingTransfer.Dto.TransferRefDTO;
import com.ecommerce.api.ServingTransfer.Service.ServingTransfer;
import com.ecommerce.api.TransferMoney.dto.MailStructure;
import com.ecommerce.api.TransferMoney.service.EmailService;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class ServingTransferImpl implements ServingTransfer {

    @Autowired
    private TransfertRepository transfertRepository;

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Override
    public Transfert searchTransfer(@RequestBody TransferRefDTO transferRefDTO) {
        String transferRef = transferRefDTO.getTransferRef();
        Optional<Transfert> optionalTransfert = transfertRepository.findByTransferRef(transferRef);
        if (optionalTransfert.isPresent()) {
            return optionalTransfert.get();

        } else {
            throw new NoSuchElementException("Transfer not found for reference: " + transferRef);
        }
    }

    @Override
    public void enterBeneficiaryInformation(@RequestBody BeneficiaryDto beneficiaryDto) {
        Optional<Beneficiary> beneficiaryOpt = beneficiaryRepository.findById(beneficiaryDto.getId());
        if (beneficiaryOpt.isPresent()) {
            Beneficiary existBeneficiary = beneficiaryOpt.get();
            existBeneficiary.setTitle(beneficiaryDto.getTitle());
            existBeneficiary.setPieceIdentite(beneficiaryDto.getPieceIdentite());
            existBeneficiary.setPaysEmission(beneficiaryDto.getPaysEmission());
            existBeneficiary.setNumeroPieceIdentite(beneficiaryDto.getNumeroPieceIdentite());
            Date expirationPieceIdentite = Date
                    .from(beneficiaryDto.getExpirationPieceIdentite().atStartOfDay(ZoneId.systemDefault()).toInstant());
            existBeneficiary.setExpirationPieceIdentite(expirationPieceIdentite);
            Date ValiditePieceIdentite = Date
                    .from(beneficiaryDto.getExpirationPieceIdentite().atStartOfDay(ZoneId.systemDefault()).toInstant());
            existBeneficiary.setValiditePieceIdentite(ValiditePieceIdentite);
            existBeneficiary.setDatenaissance(beneficiaryDto.getDatenaissance());
            existBeneficiary.setProfession(beneficiaryDto.getProfession());
            existBeneficiary.setPayeNationale(beneficiaryDto.getPayeNationale());
            existBeneficiary.setVille(beneficiaryDto.getVille());
            beneficiaryRepository.save(existBeneficiary);

        } else {
            throw new NoSuchElementException("Beneficiary not found for ID:" + beneficiaryDto.getId());
        }

    }

    @Override
    public void validatePayment(@RequestBody TransferPaymentDto transferPaymentDto, HttpServletResponse response)
            throws DocumentException, IOException {
        String transferRef = transferPaymentDto.getTransferRefDTO().getTransferRef();
        Optional<Transfert> optionalTransfert = transfertRepository.findByTransferRef(transferRef);
        Optional<User> optionalUser = userRepository.findById(transferPaymentDto.getTransferRefDTO().getIdAgent());

        if (optionalTransfert.isPresent()) {
            Transfert transfert = optionalTransfert.get();
            if (transferPaymentDto.getTransferRefDTO().getTypeOftransfer() == TypeTransfer.SPECIES) {

                if (transfert.getStatus().equals(TransferStatus.A_servir)
                        || transfert.getStatus().equals(TransferStatus.Débloqué_a_servir)) {
                    if (isWithinDeadline(transfert)) {

                        enterBeneficiaryInformation(transferPaymentDto.getBeneficiaryDto());

                        if (optionalUser.isPresent()) {
                            User existUser = optionalUser.get();
                            BigDecimal transferAmount = transferPaymentDto.getTransferRefDTO().getAmount_transfer();
                            BigDecimal userAccountAmount = existUser.getAccount_amount();

                            if (userAccountAmount.compareTo(transferAmount) >= 0) {
                                BigDecimal newAccountAmount = userAccountAmount.subtract(transferAmount);
                                existUser.setAccount_amount(newAccountAmount);
                                userRepository.save(existUser);
                                // Add the transfer amount to the beneficiary's account
                                User beneficiary = userRepository
                                        .findById(transferPaymentDto.getBeneficiaryDto().getId())
                                        .orElseThrow(() -> new NoSuchElementException("Beneficiary not found"));
                                BigDecimal beneficiaryAccountAmount = beneficiary.getAccount_amount();
                                BigDecimal newBeneficiaryAccountAmount = beneficiaryAccountAmount.add(transferAmount);
                                beneficiary.setAccount_amount(newBeneficiaryAccountAmount);
                                userRepository.save(beneficiary);
                            } else {
                                throw new IllegalStateException("Insufficient funds for transfer");
                            }
                        } else {
                            throw new NoSuchElementException(
                                    "Agent not found for ID: " + transferPaymentDto.getTransferRefDTO().getIdAgent());
                        }

                        transfert.setStatus(TransferStatus.Payé);
                        transfertRepository.save(transfert);
                        emailService.sendMail(
                                MailStructure.builder()
                                        .subject("Successful Completion of Transfer Service")
                                        .recipient(transfert.getClient().getUsername())
                                        .message("Your transfer has been successfully served")
                                        .build());

                    } else {
                        throw new IllegalStateException("Transfer deadline has expired");
                    }

                    /*
                     * if (transfertDto.isNotification()) {
                     * emailService.sendMail(
                     * MailStructure.builder()
                     * .subject("Your account is credited")
                     * .recipient(beneficiary.getUsername())
                     * .message(beneficiary.getFirstName() + " " + "you received money from " + " "
                     * + user.getUsername() + " \n" + "your transfer reference : " + "  "
                     * + transfertDto.getGenerateRef() + " \n" + " "
                     * + "don't share this with anyone!"
                     * + " " + "your transfer amount: " + transfertDto.getAmount_transfer())
                     * .build());
                     * 
                     * }
                     */
                    // generatePaymentReceipt(transferPaymentDto,response);
                } else {
                    throw new NoSuchElementException(
                            "Transfer is already paid or blocked for transfer reference: " + transferRef);
                }
            }

        } else {
            throw new NoSuchElementException("Transfer not found for reference: " + transferRef);
        }

    }

    @Override
    public boolean isWithinDeadline(Transfert transfert) {
        // Assuming this returns the creation time of the transfer
        LocalDateTime createTime = transfert.getCreateTime(); 
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = createTime.plusWeeks(1);
        return now.isBefore(deadline);
    }

    @Override
    public void generatePaymentReceipt(@RequestBody TransferPaymentDto transferPaymentDto, HttpServletResponse response)
            throws IOException, DocumentException {
        Optional<User> clientOptional = userRepository.findById(transferPaymentDto.getTransferRefDTO().getIdAgent());
        Optional<Beneficiary> beneficiaryOptional = beneficiaryRepository
                .findById(transferPaymentDto.getBeneficiaryDto().getId());
        Optional<Transfert> transferOptional = transfertRepository
                .findById(transferPaymentDto.getTransferRefDTO().getId());

        User client = clientOptional.get();
        Beneficiary beneficiary = beneficiaryOptional.get();
        Transfert transfert = transferOptional.get();

        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=pdf_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        String imagePath = "static/images/icon_bank.png";

        // Load the image
        Image logo = Image.getInstance(getClass().getClassLoader().getResource(imagePath));
        logo.scaleToFit(100, 100); 
        logo.scaleAbsolute(100, 80);  

        // Add the image to the document
        document.add(logo);

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);

        Paragraph title = new Paragraph("Le reçu de paiement du Transfert", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        Paragraph para = new Paragraph("Cher client,",font);
        para.setAlignment(Element.ALIGN_LEFT);
        document.add(para);
        document.add(Chunk.NEWLINE);

        Paragraph paraTwo = new Paragraph("Nous vous remercions d'avoir effectué un transfert bancaire avec notre service. Veuillez trouver ci-dessous les détails de votre transaction :",font);
        paraTwo.setAlignment(Element.ALIGN_LEFT);
        document.add(paraTwo);
        // Add line at the bottom
        PdfContentByte line = writer.getDirectContent();
        line.setLineWidth(1f);
        line.moveTo(document.left(), document.bottom() + 10);
        line.lineTo(document.right(), document.bottom() + 10);
        line.stroke();

        // Add date in the right corner above the line
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT,
                new Phrase("Date: " + currentDateTime, fontTitle),
                document.right(), document.bottom() + 15, 0);
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
        addTableCell(table, "Expéditeur" ,String.valueOf(client.getName()), font);

        addTableCell(table, "Montant du Transfert", String.valueOf(transfert.getAmount_transfer()), font);
        addTableCell(table, "Date d'émission", String.valueOf(transfert.getCreateTime()), font);
        addTableCell(table, "État", String.valueOf(transfert.getStatus()), font);
        addTableCell(table, "Réference du Transfert", String.valueOf(transfert.getTransferRef()), font);

        addTableCell(table2, "Identifiant du transfert", String.valueOf(transfert.getId()), font);
        addTableCell(table2, "Bénéficiaire", beneficiary.getFirstName() + " " + beneficiary.getLastname(),font);
        addTableCell(table2, "Montant du Transfert", String.valueOf(transfert.getAmount_transfer()), font);
        addTableCell(table2, "Date d'émission", String.valueOf(transfert.getCreateTime()), font);
        addTableCell(table2, "État", String.valueOf(transfert.getStatus()), font);
        addTableCell(table2, "Réference du Transfert", String.valueOf(transfert.getTransferRef()), font);

        document.add(table);
        document.add(table2);

        Paragraph paraT = new Paragraph("Nous tenons à vous informer que votre transfert a été payé avec succès. Si vous avez des questions ou des préoccupations, n'hésitez pas à nous contacter. Votre satisfaction est notre priorité.",font);
        paraT.setAlignment(Element.ALIGN_LEFT);
        document.add(paraT);

        Paragraph paraV = new Paragraph("Cordialement,",font);
        paraV.setAlignment(Element.ALIGN_LEFT);
        document.add(paraV);

        Paragraph paraM = new Paragraph("L'équipe de BankTransfer",font);
        paraM.setAlignment(Element.ALIGN_LEFT);
        document.add(paraM);

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

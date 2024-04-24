package com.ecommerce.api.ExtouneTransfer.ServiceImpl;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.ecommerce.api.Entity.TransferStatus;
import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.Entity.User;
import com.ecommerce.api.ExtouneTransfer.Service.ExtourneService;
import com.ecommerce.api.Repository.BeneficiaryRepository;
import com.ecommerce.api.Repository.TransfertRepository;
import com.ecommerce.api.Repository.UserRepository;
import com.ecommerce.api.ServingTransfer.Dto.TransferPaymentDto;
import com.ecommerce.api.TransferMoney.service.EmailService;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
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
public class ExtourneServiceImpl implements ExtourneService {


    @Autowired
    private TransfertRepository transfertRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Override
    public void generateExtourneReceipt(@RequestBody TransferPaymentDto transferPaymentDto,HttpServletResponse response) throws IOException, DocumentException {
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

    @Override
    public void reverseTransfer(@RequestBody TransferPaymentDto transferPaymentDto, HttpServletResponse response)
            throws DocumentException, IOException {
        String transferRef = transferPaymentDto.getTransferRefDTO().getTransferRef();
        Optional<Transfert> optionalTransfert = transfertRepository.findByTransferRef(transferRef);
        Optional<User> optionalUser = userRepository.findById(transferPaymentDto.getTransferRefDTO().getIdAgent());

        if (optionalTransfert.isPresent()) {
            Transfert transfert = optionalTransfert.get();

            if (transfert.getStatus().equals(TransferStatus.A_servir)) {
                if (isSameDay(transfert.getCreateTime(), new Date())) {

                    transfert.setMotif(transferPaymentDto.getTransferRefDTO().getMotif());
                    if (optionalUser.isPresent()) {
                        User existUser = optionalUser.get();
                        BigDecimal transferAmount = transferPaymentDto.getTransferRefDTO().getAmount_transfer();
                        BigDecimal userAccountAmount = existUser.getAccount_amount();

                        BigDecimal newAccountAmount = userAccountAmount.add(transferAmount);
                        existUser.setAccount_amount(newAccountAmount);
                        userRepository.save(existUser);
                    } else {
                        throw new NoSuchElementException(
                                "Agent not found for ID: " + transferPaymentDto.getTransferRefDTO().getIdAgent());
                    }
                    
                    transfert.setStatus(TransferStatus.Extourné);

                    transfertRepository.save(transfert);

                } else {
                    throw new NoSuchElementException(
                            "The transfer is not initiated on the same day and by the same agent");
                }

            } else {
                throw new NoSuchElementException(
                        "Transfer is already paid or blocked for transfer reference: " + transferRef);
            }

        } else {
            throw new NoSuchElementException("Transfer not found for reference: " + transferRef);
        }

    }


    @Override
    public boolean isSameDay(LocalDateTime createTime, Date date2) {
        // Convert Date to LocalDateTime
        Instant instant = date2.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        // Check if 24 hours have passed since createTime
        return createTime.plusHours(24).isBefore(localDateTime);
    }


}

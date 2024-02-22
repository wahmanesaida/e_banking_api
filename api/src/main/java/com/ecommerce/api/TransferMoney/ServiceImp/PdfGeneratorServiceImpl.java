package com.ecommerce.api.TransferMoney.ServiceImp;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.api.Entity.Beneficiary;
import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.Entity.User;
import com.ecommerce.api.Repository.BeneficiaryRepository;
import com.ecommerce.api.Repository.UserRepository;
import com.ecommerce.api.TransferMoney.dto.BeneficiaryDto;
import com.ecommerce.api.TransferMoney.dto.TransfertDto;
import com.ecommerce.api.TransferMoney.service.PdfGeneratorService;

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

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    BeneficiaryRepository beneficiaryRepository;

    @Override
    public void export(HttpServletResponse response,TransfertDto transfertDto, long client_id, long bene_id, BeneficiaryDto bene) throws IOException, DocumentException {

    Optional<User> clientOptional = userRepo.findById(client_id);
    Optional<Beneficiary> beneficiaryOptional = beneficiaryRepository.findById(bene_id);
 
    if (!clientOptional.isPresent() || !beneficiaryOptional.isPresent()) {
        // Handle case when client or beneficiary is not found
        throw new EntityNotFoundException("Client or beneficiary not found");
    }

    User client = clientOptional.get();
    Beneficiary beneficiary = beneficiaryOptional.get();



        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

       /*  Image logo = Image.getInstance("");
        logo.scaleToFit(100, 100); 
        document.add(logo); */

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);

        Paragraph title = new Paragraph("Receipt of the Transfer", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String currentDate = dateFormat.format(date);
        PdfContentByte canvas = writer.getDirectContent();
        ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, new Phrase("Date: " + currentDate, fontTitle), document.right() - 10, document.bottom() + 10, 0);
        

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

        addTableCell(table, "Transfer ID", String.valueOf(transfertDto.getId()), font);
        addTableCell(table, "Sender",
                " " + client.getName(), font);
        addTableCell(table, "Amount", String.valueOf(transfertDto.getAmount_transfer()), font);
        addTableCell(table, "Issue Date", String.valueOf(transfertDto.getCreateTime()), font);
        addTableCell(table, "State", transfertDto.getStatus(), font);

        addTableCell(table2, "Transfer ID", String.valueOf(transfertDto.getId()), font);
        addTableCell(table2, "Receiver", beneficiary.getFirstName()  + " " + beneficiary.getLastname(),
                font);
        addTableCell(table2, "Amount", String.valueOf(transfertDto.getAmount_transfer()), font);
        addTableCell(table2, "Issue Date", String.valueOf(transfertDto.getCreateTime()), font);
        addTableCell(table2, "State", transfertDto.getStatus(), font);

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

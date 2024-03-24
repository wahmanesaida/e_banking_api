package com.ecommerce.api.ExtouneTransfer.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.ecommerce.api.ServingTransfer.Dto.TransferPaymentDto;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;

import jakarta.servlet.http.HttpServletResponse;

@Service
public interface ExtourneService {

    void generateExtourneReceipt(@RequestBody TransferPaymentDto transferPaymentDto,HttpServletResponse response) throws IOException, DocumentException;
    void addTableCell(PdfPTable table, String key, String value, Font font);
    void reverseTransfer(@RequestBody TransferPaymentDto transferPaymentDto, HttpServletResponse response)throws DocumentException, IOException;
    boolean isSameDay(LocalDateTime date1, Date date2) ;




}

package com.ecommerce.api.GabBoa.Service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.GabBoa.Dto.TransferRefDTO;
import com.ecommerce.api.ServingTransfer.Dto.TransferPaymentDto;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;

import jakarta.servlet.http.HttpServletResponse;

@Service
public interface ServingTransferGab {
    Transfert searchTransferGab(@RequestBody TransferRefDTO transferRefDTO) ;
    void validatePaymentGab(@RequestBody TransferPaymentDto transferPaymentDto, HttpServletResponse response) throws DocumentException, IOException;
    void generateReceiptGab(@RequestBody TransferPaymentDto transferPaymentDto,HttpServletResponse response) throws IOException, DocumentException;
    void addTableCell(PdfPTable table, String key, String value, Font font);
}

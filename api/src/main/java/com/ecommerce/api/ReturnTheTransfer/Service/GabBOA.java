package com.ecommerce.api.ReturnTheTransfer.Service;

import com.ecommerce.api.Entity.Motif;
import com.ecommerce.api.Entity.Transfert;
import com.ecommerce.api.ReturnTheTransfer.DTO.ReturnTransferDTO;
import com.ecommerce.api.ServingTransfer.Dto.TransferPaymentDto;
import com.ecommerce.api.TransferMoney.Response.MessageResponse;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.List;

@Service
public interface GabBOA {
    Transfert AccessTheTransactionByRef(String transferRef);
    MessageResponse ReturnTransfer(ReturnTransferDTO returnTransferDTO);
    List<Transfert> allTransfersOfClient(long id);
    MessageResponse ReturnTheTransferByTheClient(ReturnTransferDTO returnTransferDTO);
    void generateReturnReceipt(@RequestBody TransferPaymentDto transferPaymentDto, HttpServletResponse response) throws IOException, DocumentException;
    void addTableCell(PdfPTable table, String key, String value, Font font);
}

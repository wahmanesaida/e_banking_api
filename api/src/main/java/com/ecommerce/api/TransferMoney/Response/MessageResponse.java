package com.ecommerce.api.TransferMoney.Response;

public class MessageResponse {

    private String message;
    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

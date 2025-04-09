package com.biteup.biteup_payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponseDTO {

    private String email;
    private String receiptUrl;

}


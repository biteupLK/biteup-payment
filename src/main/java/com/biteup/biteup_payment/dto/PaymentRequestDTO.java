package com.biteup.biteup_payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentRequestDTO {

    private String foodId;
    private String foodName;
    private String currency;
    private Double amount;
    private String email;
    private String customerPhone;
    private String signedUrl;
}

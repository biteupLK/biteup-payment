package com.biteup.biteup_payment.Entity;

import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.Date;

@Data
@Document(collection = "payments")
public class Payment {
    @Id
    private String id;
    private String eventId;
    private String type;
    private Date created;
    private String email;
    private Object data;
    private Date processedAt = new Date();
}

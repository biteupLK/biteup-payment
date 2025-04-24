package com.biteup.biteup_payment.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.biteup.biteup_payment.Entity.PaymentDetails;

public interface PaymentDetailsRepository extends MongoRepository<PaymentDetails, String>{
    @Query("{ 'data.object.members.metadata.members.receipt_email.value' : ?0 }")
    List<PaymentDetails> findByReceiptEmailValue(String email);
}

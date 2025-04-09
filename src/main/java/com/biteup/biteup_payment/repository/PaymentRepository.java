package com.biteup.biteup_payment.repository;

import com.biteup.biteup_payment.Entity.Payment;


import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    boolean existsByEventId(String eventId);
}

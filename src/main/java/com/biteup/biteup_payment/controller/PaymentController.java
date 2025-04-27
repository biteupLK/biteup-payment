package com.biteup.biteup_payment.controller;

import com.biteup.biteup_payment.Entity.PaymentDetails;
import com.biteup.biteup_payment.dto.PaymentRequestDTO;
import com.biteup.biteup_payment.service.PaymentService;
import com.stripe.exception.StripeException;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@AllArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @PostMapping("/create-checkout-session")
  public ResponseEntity<Map<String, Object>> createCheckoutSession(
    @RequestBody PaymentRequestDTO req
  ) {
    try {
      Map<String, Object> response = paymentService.createCheckoutSession(req);
      return ResponseEntity.ok(response);
    } catch (StripeException e) {
      e.printStackTrace(); // Log the error
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        Map.of("error", "Failed to create checkout session: " + e.getMessage())
      );
    }
  }

  @GetMapping("/{email}/get")
  public List<PaymentDetails> getEventsByUserEmail(
    @PathVariable String email
  ) {
    return paymentService.getEventsByUserEmail(email);
  }

  @GetMapping("/{email}/get/restaurant-order")
  public List<PaymentDetails> getEventsByRestaurantEmail(
    @PathVariable String email
  ) {
    return paymentService.getEventsByRestaurantEmail(email);
  }
}

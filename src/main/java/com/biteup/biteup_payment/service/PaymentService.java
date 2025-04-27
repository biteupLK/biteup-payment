package com.biteup.biteup_payment.service;

import com.biteup.biteup_payment.Entity.PaymentDetails;
import com.biteup.biteup_payment.dto.PaymentRequestDTO;
import com.biteup.biteup_payment.repository.PaymentDetailsRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentService {

        private final PaymentDetailsRepository paymentDetailsRepository;

  public Map<String, Object> createCheckoutSession(PaymentRequestDTO req)
    throws StripeException {
    // Create customer
    CustomerCreateParams customerParams = CustomerCreateParams.builder()
      .setEmail(req.getEmail())
      .setPhone(req.getCustomerPhone())
      .build();
    Customer customer = Customer.create(customerParams);

    // Build checkout session
    SessionCreateParams params = SessionCreateParams.builder()
      .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
      .setMode(SessionCreateParams.Mode.PAYMENT)
      .setBillingAddressCollection(
        SessionCreateParams.BillingAddressCollection.REQUIRED
      )
      .setCustomer(customer.getId())
      .setSuccessUrl("http://localhost:5175/myOrders")
      .setCancelUrl("http://localhost:5175/OrderCancelled")
      .addLineItem(
        SessionCreateParams.LineItem.builder()
          .setPriceData(
            SessionCreateParams.LineItem.PriceData.builder()
              .setCurrency(req.getCurrency())
              .setProductData(
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                  .setName(req.getFoodName())
                  .putExtraParam(
                    "images",
                    Collections.singletonList(req.getSignedUrl())
                  )
                  .build()
              )
              .setUnitAmount((long) (req.getAmount() * 100))
              .build()
          )
          .setQuantity(1L)
          .build()
      )
      .putMetadata("receipt_email", req.getEmail())
      .putMetadata("phone", req.getCustomerPhone())
      .putMetadata("restaurantEmail", req.getRestaurantEmail())
      .putMetadata("cus_mobile", req.getCustomerPhone())
      .putMetadata("foodName", req.getFoodName())
      .build();

    // Create session
    Session session = Session.create(params);
    log.info("Checkout session created successfully: {}", session.getId());

    return Map.of("id", session.getId());
  }

  public List<PaymentDetails> getEventsByUserEmail(String email) {
    return paymentDetailsRepository.findByReceiptEmailValue(email);
  }

  public List<PaymentDetails> getEventsByRestaurantEmail(String email) {
    return paymentDetailsRepository.findByRestaurantEmailValue(email);
  }
}

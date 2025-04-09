package com.biteup.biteup_payment.service;

import com.biteup.biteup_payment.dto.PaymentRequestDTO;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentService {

        

        public Map<String, Object> createCheckoutSession(PaymentRequestDTO req) throws StripeException {
                CustomerCreateParams customerParams = CustomerCreateParams.builder()
                                .setEmail(req.getEmail())
                                .setPhone(req.getCustomerPhone())
                                .build();
                Customer customer = Customer.create(customerParams);

                PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO();
                paymentRequestDTO.setEmail(req.getEmail());
                SessionCreateParams params = SessionCreateParams.builder()
                                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                                .setMode(SessionCreateParams.Mode.PAYMENT)
                                .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                                .setCustomer(customer.getId())
                                .setSuccessUrl("http://localhost:8084/success")
                                .setCancelUrl("http://localhost:8084/cancel")
                                .addLineItem(
                                                SessionCreateParams.LineItem.builder()
                                                                .setPriceData(
                                                                                SessionCreateParams.LineItem.PriceData
                                                                                                .builder()
                                                                                                .setCurrency(req.getCurrency())
                                                                                                .setProductData(
                                                                                                                SessionCreateParams.LineItem.PriceData.ProductData
                                                                                                                                .builder()
                                                                                                                                .setName(req.getFoodName())
                                                                                                                                .build())
                                                                                                .setUnitAmount((long) (req
                                                                                                                .getAmount()
                                                                                                                * 100))
                                                                                                .build())
                                                                .setQuantity(1L)
                                                                .build())
                                .putMetadata("receipt_email", req.getEmail())
                                .putMetadata("phone", req.getCustomerPhone())
                                .build();

                Session session = Session.create(params);
                log.info("Checkout session created successfully: {}", session.getId());

                return Map.of("id", session.getId());

        }
            
}

package com.biteup.biteup_payment.controller;

import com.biteup.biteup_payment.Entity.Payment;
import com.biteup.biteup_payment.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
public class StripeWebhookController {

  private static final String ENDPOINT_SECRET =
    "whsec_14b9eff64c11d77cdc1c0def8ae4ac4c8c6dd74a9251afde5d0c36e746807394";
  private final PaymentRepository paymentRepository;

  @PostMapping("/webhook")
  public ResponseEntity<String> handleStripeWebhook(
    @RequestBody String payload,
    @RequestHeader("Stripe-Signature") String sigHeader
  ) {
    log.info("🔔 Webhook received");

    Event event;
    try {
      event = Webhook.constructEvent(payload, sigHeader, ENDPOINT_SECRET);
      log.info("✅ Received event type: {}", event.getType());

      log.debug("📄 Full event data: {}", event.toJson());

      switch (event.getType()) {
        case "checkout.session.completed":
          handleCheckoutSessionCompleted(event);
          // saveEventToMongoDB(event);
          break;
        case "payment_intent.succeeded":
          handlePaymentIntentSucceeded(event);
          break;
        case "charge.succeeded":
          handleChargeSucceeded(event);

          break;
        case "charge.updated":
          handleChargeUpdated(event);
          saveEventToMongoDB(event);
          break;
        default:
          log.info("🔄 Unhandled event type: {}", event.getType());
          break;
      }

      return ResponseEntity.ok("Webhook processed successfully");
    } catch (Exception e) {
      log.error("⚠️ Error handling webhook", e);
      return ResponseEntity.badRequest()
        .body("Error processing webhook: " + e.getMessage());
    }
  }

  private void saveEventToMongoDB(Event event) {
    if (paymentRepository.existsByEventId(event.getId())) {
      log.info("⏩ Event {} already processed, skipping", event.getId());
      return;
    }

    Payment document = new Payment();
    document.setEventId(event.getId());
    document.setType(event.getType());
    document.setData(event.getData());
    document.setCreated(new Date(event.getCreated() * 1000L));

    paymentRepository.save(document);
    log.info("💾 Saved event {} to MongoDB", event.getId());
  }

  private void handleCheckoutSessionCompleted(Event event)
    throws StripeException {
    log.info("💰 Handling checkout.session.completed event");

    EventDataObjectDeserializer dataObjectDeserializer =
      event.getDataObjectDeserializer();
    if (dataObjectDeserializer.getObject().isEmpty()) {
      log.warn("⚠️ No data object found in checkout.session.completed event");
      return;
    }

    StripeObject stripeObject = dataObjectDeserializer.getObject().get();

    if (stripeObject instanceof Session) {
      Session session = (Session) stripeObject;
      log.info("🛒 Checkout Session Details:");
      log.info("  - Session ID: {}", session.getId());
      log.info("  - Payment Status: {}", session.getPaymentStatus());
      log.info(
        "  - Customer Email: {}",
        session.getCustomerDetails().getEmail()
      );
      log.info(
        "  - Amount: {} {}",
        session.getAmountTotal() / 100.0,
        session.getCurrency()
      );
      log.info("  - Payment Intent ID: {}", session.getPaymentIntent());
      // Here you would typically update your database and fulfill the order
    } else {
      log.warn(
        "⚠️ Expected Session object but got: {}",
        stripeObject.getClass()
      );
    }
  }

  private void handlePaymentIntentSucceeded(Event event)
    throws StripeException {
    log.info("💳 Handling payment_intent.succeeded event");

    EventDataObjectDeserializer dataObjectDeserializer =
      event.getDataObjectDeserializer();
    if (dataObjectDeserializer.getObject().isEmpty()) {
      log.warn("⚠️ No data object found in payment_intent.succeeded event");
      return;
    }

    StripeObject stripeObject = dataObjectDeserializer.getObject().get();

    if (stripeObject instanceof PaymentIntent) {
      PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
      log.info("💵 Payment Intent Details:");
      log.info("  - ID: {}", paymentIntent.getId());
      log.info(
        "  - Amount: {} {}",
        paymentIntent.getAmount() / 100.0,
        paymentIntent.getCurrency()
      );
      log.info("  - Status: {}", paymentIntent.getStatus());
      log.info("  - Charge ID: {}", paymentIntent.getLatestCharge());

      if (paymentIntent.getLatestCharge() != null) {
        Charge charge = Charge.retrieve(paymentIntent.getLatestCharge());
        log.info("  - Charge Status: {}", charge.getStatus());
        log.info("  - Receipt URL: {}", charge.getReceiptUrl());
      }
    } else {
      log.warn(
        "⚠️ Expected PaymentIntent object but got: {}",
        stripeObject.getClass()
      );
    }
  }

  private void handleChargeSucceeded(Event event) throws StripeException {
    log.info("🔋 Handling charge.succeeded event");

    EventDataObjectDeserializer dataObjectDeserializer =
      event.getDataObjectDeserializer();
    if (dataObjectDeserializer.getObject().isEmpty()) {
      log.warn("⚠️ No data object found in charge.succeeded event");
      return;
    }

    StripeObject stripeObject = dataObjectDeserializer.getObject().get();

    if (stripeObject instanceof Charge) {
      Charge charge = (Charge) stripeObject;
      log.info("⚡ Charge Details:");
      log.info("  - ID: {}", charge.getId());
      log.info(
        "  - Amount: {} {}",
        charge.getAmount() / 100.0,
        charge.getCurrency()
      );
      log.info("  - Paid: {}", charge.getPaid());
      log.info("  - Receipt URL: {}", charge.getReceiptUrl());
      log.info("  - Payment Method: {}", charge.getPaymentMethod());
    } else {
      log.warn(
        "⚠️ Expected Charge object but got: {}",
        stripeObject.getClass()
      );
    }
  }

  private void handleChargeUpdated(Event event) throws StripeException {
    log.info("🔄 Handling charge.updated event");

    EventDataObjectDeserializer dataObjectDeserializer =
      event.getDataObjectDeserializer();
    if (dataObjectDeserializer.getObject().isEmpty()) {
      log.warn("⚠️ No data object found in charge.updated event");
      return;
    }

    StripeObject stripeObject = dataObjectDeserializer.getObject().get();

    if (stripeObject instanceof Charge) {
      Charge charge = (Charge) stripeObject;
      log.info("♻️ Updated Charge Details:");
      log.info("  - ID: {}", charge.getId());
      log.info("  - Status: {}", charge.getStatus());
      log.info("  - Captured: {}", charge.getCaptured());

      if ("succeeded".equals(charge.getStatus())) {
        log.info("✅ Payment was successfully captured");
      }
    } else {
      log.warn(
        "⚠️ Expected Charge object but got: {}",
        stripeObject.getClass()
      );
    }
  }
}

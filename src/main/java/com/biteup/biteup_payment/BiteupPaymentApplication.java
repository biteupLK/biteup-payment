package com.biteup.biteup_payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;  // Correct import

@SpringBootApplication
public class BiteupPaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(BiteupPaymentApplication.class, args);

        // Set your secret key
        Stripe.apiKey = "sk_test_51RA2a4C76phfCMf6OdrHfZZtP8vaQafaBP5c5988F8Ca1rpMk23mtIQu5ZiUr925gHYUZQnKnDJ5QGqL3sXXtgrg00vjKWy3dx";

        // The session ID you received
        String sessionId = "cs_test_a1caz0rMtGX2vkNr82kx5apy55WiLIYvtMLsf9aK57Z7o28aP1cTyaM1Gf";

        try {
            // Retrieve the session details
            Session session = Session.retrieve(sessionId);  // Correct method call

            // Access the payment details
            System.out.println("Session ID: " + session.getId());
            System.out.println("Payment Intent: " + session.getPaymentIntent());
            System.out.println("Payment Status: " + session.getPaymentStatus());
            System.out.println("Customer: " + session.getCustomer());
            System.out.println("Amount Total: " + session.getAmountTotal());
            System.out.println("Currency: " + session.getCurrency());
        } catch (StripeException e) {
            System.out.println("Error retrieving session details: " + e.getMessage());
        }
    }
}

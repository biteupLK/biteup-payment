package com.biteup.biteup_payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class BiteupPaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(BiteupPaymentApplication.class, args);


        // Set your secret key
        Stripe.apiKey = "sk_test_51RIZzqCFsCAVHXK7796CxkJ5W5ZZBUiBeRM5yN3opVphk7UxeUcmVrmhgjjHawZrjTprOqQYjQEzc0AEWGyOlUix00sWXCHSFk";

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

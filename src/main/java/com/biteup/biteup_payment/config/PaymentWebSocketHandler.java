package com.biteup.biteup_payment.config;

import com.biteup.biteup_payment.Entity.PaymentDetails;
import com.biteup.biteup_payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class PaymentWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private PaymentService paymentService;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String email = message.getPayload();  // Get email sent from frontend

        // Fetch orders based on the email
        List<PaymentDetails> orders = paymentService.getEventsByRestaurantEmail(email);

        // Send the orders as a JSON response to the frontend
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(orders);
        session.sendMessage(new TextMessage(response));
    }
}
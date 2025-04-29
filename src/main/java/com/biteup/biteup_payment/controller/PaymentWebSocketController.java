package com.biteup.biteup_payment.controller;

import com.biteup.biteup_payment.Entity.PaymentDetails;
import com.biteup.biteup_payment.service.PaymentService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.context.event.EventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Controller
public class PaymentWebSocketController {

    private final PaymentService paymentService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, String> sessionEmailMap = new ConcurrentHashMap<>();
    private final List<String> connectedEmails = new CopyOnWriteArrayList<>();

    public PaymentWebSocketController(
            PaymentService paymentService,
            SimpMessagingTemplate messagingTemplate) {
        this.paymentService = paymentService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/get-restaurant-order")
    @SendTo("/topic/restaurant-order")
    public Map<String, Object> getRestaurantOrders(
            @Payload String email,
            SimpMessageHeaderAccessor headerAccessor) {
        
        email = email.replaceAll("^\"|\"$", "");
        String sessionId = headerAccessor.getSessionId();
        
        if (sessionId != null) {
            sessionEmailMap.put(sessionId, email);
            if (!connectedEmails.contains(email)) {
                connectedEmails.add(email);
            }
        }
        
        return fetchAndTransformOrders(email);
    }

    @Scheduled(fixedRate = 5000)
    public void pushOrderUpdates() {
        for (String email : connectedEmails) {
            Map<String, Object> orders = fetchAndTransformOrders(email);
            messagingTemplate.convertAndSend("/topic/restaurant-order/" + email, orders);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        if (sessionId != null) {
            String email = sessionEmailMap.remove(sessionId);
            if (email != null) {
                connectedEmails.remove(email);
            }
        }
    }

    private Map<String, Object> fetchAndTransformOrders(String email) {
        List<PaymentDetails> payments = paymentService.getEventsByRestaurantEmail(email);
        return Map.of(
                "status", "success",
                "count", payments.size(),
                "orders", payments.stream().map(this::transformPayment).collect(Collectors.toList())
        );
    }

    private Map<String, Object> transformPayment(PaymentDetails payment) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", payment.getId());
        result.put("eventId", payment.getEventId());
        result.put("type", payment.getType());

        if (payment.getData() != null &&
                payment.getData().getObject() != null &&
                payment.getData().getObject().getMembers() != null) {
            PaymentDetails.Members members = payment
                    .getData()
                    .getObject()
                    .getMembers();
            Map<String, Object> paymentData = new HashMap<>();
            paymentData.put("paymentStatus", getValue(members.getPayment_status()));
            paymentData.put("amount", getNestedValue(members.getAmount()));
            paymentData.put("currency", getValue(members.getCurrency()));
            paymentData.put("paymentIntent", getValue(members.getPayment_intent()));
            
            if (members.getBilling_details() != null &&
                    members.getBilling_details().getMembers() != null) {
                PaymentDetails.CustomerMembers customerMembers = members
                        .getBilling_details()
                        .getMembers();

                Map<String, Object> customer = new HashMap<>();
                customer.put("name", getValue(customerMembers.getName()));
                customer.put("email", getValue(customerMembers.getEmail()));
                
                if (customerMembers.getAddress() != null &&
                        customerMembers.getAddress().getMembers() != null) {
                    PaymentDetails.AddressMembers addressMembers = customerMembers
                            .getAddress()
                            .getMembers();

                    Map<String, Object> address = new HashMap<>();
                    address.put("line1", getValue(addressMembers.getLine1()));
                    address.put("line2", getValue(addressMembers.getLine2()));
                    address.put("city", getValue(addressMembers.getCity()));
                    address.put("postalCode", getValue(addressMembers.getPostal_code()));
                    address.put("country", getValue(addressMembers.getCountry()));

                    customer.put("address", address);
                }

                paymentData.put("customer", customer);
            }
            
            if (members.getMetadata() != null &&
                    members.getMetadata().getMembers() != null) {
                PaymentDetails.MetadataMembers metadata = members
                        .getMetadata()
                        .getMembers();
                Map<String, Object> orderInfo = new HashMap<>();
                orderInfo.put("foodName", getValue(metadata.getFoodName()));
                orderInfo.put("restaurantEmail", getValue(metadata.getRestaurantEmail()));
                orderInfo.put("phone", getValue(metadata.getPhone()));

                paymentData.put("orderDetails", orderInfo);
            }

            result.put("paymentData", paymentData);
        }

        return result;
    }

    private String getValue(PaymentDetails.ValueWrapper wrapper) {
        return wrapper != null ? wrapper.getValue() : null;
    }

    private String getNestedValue(PaymentDetails.NestedValueWrapper wrapper) {
        return wrapper != null && wrapper.getValue() != null
                ? wrapper.getValue().getValue()
                : null;
    }
}
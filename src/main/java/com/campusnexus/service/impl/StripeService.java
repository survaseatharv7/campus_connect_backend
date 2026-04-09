package com.campusnexus.service.impl;

import com.campusnexus.entity.EventRegistration;
import com.campusnexus.enums.PaymentStatus;
import com.campusnexus.enums.TicketStatus;
import com.campusnexus.exception.PaymentException;
import com.campusnexus.repository.EventRegistrationRepository;
import com.campusnexus.service.NotificationService;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class StripeService {

    private static final Logger logger = LoggerFactory.getLogger(StripeService.class);

    private final StripeClient stripeClient;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final NotificationService notificationService;
    private final FCMService fcmService;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    public StripeService(StripeClient stripeClient,
                         EventRegistrationRepository eventRegistrationRepository,
                         NotificationService notificationService,
                         FCMService fcmService) {
        this.stripeClient = stripeClient;
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.notificationService = notificationService;
        this.fcmService = fcmService;
    }

    public Map<String, String> createPaymentIntent(BigDecimal amount, String currency,
                                                     UUID eventId, UUID studentId) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amount.multiply(BigDecimal.valueOf(100)).longValue())
                    .setCurrency(currency)
                    .putMetadata("eventId", eventId.toString())
                    .putMetadata("studentId", studentId.toString())
                    .build();

            PaymentIntent paymentIntent = stripeClient.v1().paymentIntents().create(params);

            Map<String, String> result = new HashMap<>();
            result.put("clientSecret", paymentIntent.getClientSecret());
            result.put("paymentIntentId", paymentIntent.getId());
            return result;
        } catch (StripeException e) {
            logger.error("Stripe PaymentIntent creation failed: {}", e.getMessage());
            throw new PaymentException("Failed to create payment: " + e.getMessage());
        }
    }

    @Transactional
    public void handleWebhookEvent(String payload, String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            if ("payment_intent.succeeded".equals(event.getType())) {
                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject()
                        .orElseThrow(() -> new PaymentException("Failed to deserialize payment intent"));

                EventRegistration registration = eventRegistrationRepository
                        .findByStripePaymentIntentId(paymentIntent.getId())
                        .orElse(null);

                if (registration != null) {
                    registration.setTicketStatus(TicketStatus.CONFIRMED);
                    registration.setPaymentStatus(PaymentStatus.SUCCESS);
                    eventRegistrationRepository.save(registration);

                    // Send confirmation email
                    String studentEmail = registration.getStudent().getEmail();
                    String eventTitle = registration.getEvent().getTitle();
                    notificationService.sendEmail(
                            studentEmail,
                            "Event Registration Confirmed - " + eventTitle,
                            "Your registration for " + eventTitle + " has been confirmed.\n" +
                            "Ticket Code: " + registration.getTicketCode() + "\n" +
                            "Thank you for registering!"
                    );

                    // Send FCM notification if token available
                    String fcmToken = registration.getStudent().getFcmToken();
                    if (fcmToken != null && !fcmToken.isEmpty()) {
                        fcmService.sendToToken(fcmToken,
                                "Registration Confirmed",
                                "Your ticket for " + eventTitle + " is confirmed!");
                    }

                    logger.info("Payment confirmed for registration: {}", registration.getId());
                }
            }
        } catch (StripeException e) {
            logger.error("Stripe webhook signature verification failed: {}", e.getMessage());
            throw new PaymentException("Invalid webhook signature");
        } catch (Exception e) {
            logger.error("Stripe webhook processing failed: {}", e.getMessage());
            throw new PaymentException("Webhook processing failed: " + e.getMessage());
        }
    }
}

package com.campusnexus.controller;

import com.campusnexus.dto.ApiResponse;
import com.campusnexus.service.impl.StripeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook")
@Tag(name = "Webhook", description = "External webhook endpoints")
public class WebhookController {

    private final StripeService stripeService;

    public WebhookController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/stripe")
    @Operation(summary = "Stripe webhook", description = "Handle Stripe payment events")
    public ResponseEntity<ApiResponse<Void>> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        stripeService.handleWebhookEvent(payload, sigHeader);
        return ResponseEntity.ok(ApiResponse.success("Webhook processed"));
    }
}

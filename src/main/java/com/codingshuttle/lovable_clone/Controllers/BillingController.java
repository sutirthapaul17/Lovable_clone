package com.codingshuttle.lovable_clone.Controllers;


import com.codingshuttle.lovable_clone.Dto.Subscription.*;
import com.codingshuttle.lovable_clone.Service.PaymentProcessor;
import com.codingshuttle.lovable_clone.Service.PlanService;
import com.codingshuttle.lovable_clone.Service.SubscriptionService;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BillingController {

    private final PlanService planService;
    private final SubscriptionService subscriptionService;
    private final PaymentProcessor paymentProcessor;

    @Value(
            "${stripe.webhook.secret}"
    )
    private String webHookSecret;



    @GetMapping("/api/plans")
    public ResponseEntity<List<PlanResponse>> getallPlans(){
        return ResponseEntity.ok(planService.getAllActivePlans());
    }
    
    
    @GetMapping("/api/me/subscription")
    public ResponseEntity<SubscriptionResponse> getMySubscription(){
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription());
    }


    @PostMapping("/api/payments/checkout")
    public ResponseEntity<CheckoutResponse> createCheckoutRespose(
            @RequestBody CheckoutRequest request
            ){
//        Long userId = 1L;
        return ResponseEntity.ok(paymentProcessor.createCheckoutSessionurl(request));
    }

    @PostMapping("/api/payments/portal")
    public ResponseEntity<PortalResponse> openCustomPortal(){
        return ResponseEntity.ok(paymentProcessor.openCustomPortal());
    };


    @PostMapping("/webhooks/payments")
    public ResponseEntity<String> handlePaymentWebhooks(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ){
        try {
            Event event = Webhook.constructEvent(payload,sigHeader,webHookSecret);
            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = null;

            if(deserializer.getObject().isPresent()){
                stripeObject = deserializer.getObject().get();
            }else{
                //Fallback : Deserialize from raw json
                try{
                    stripeObject = deserializer.deserializeUnsafe();
                    if(stripeObject == null){
                        log.warn("Failed to deserialize webhook object for event: {}",event.getType());
                        return  ResponseEntity.ok().build();
                    }
                } catch (Exception e) {
                    log.error("Unsafe deserialization failed for event  {}: {}",event.getType(),e.getMessage());
//                    throw new RuntimeException(e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deserialization failed");
                }

            }
            // Now extract metadata only if it's a Checkout Session
            Map<String, String> metadata = new HashMap<>();
            if(stripeObject instanceof Session session){
                metadata = session.getMetadata();
            }

            // Pass to your processor
            paymentProcessor.handleWebHookEvent(event.getType(),stripeObject,metadata);

            return ResponseEntity.ok().build();


        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }
    }


}

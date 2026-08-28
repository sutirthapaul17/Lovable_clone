package com.codingshuttle.lovable_clone.Service;

import com.codingshuttle.lovable_clone.Dto.Subscription.CheckoutRequest;
import com.codingshuttle.lovable_clone.Dto.Subscription.CheckoutResponse;
import com.codingshuttle.lovable_clone.Dto.Subscription.PortalResponse;
import com.stripe.model.StripeObject;

import java.util.Map;

public interface PaymentProcessor {

    CheckoutResponse createCheckoutSessionurl(CheckoutRequest request);

    PortalResponse openCustomPortal();

    void handleWebHookEvent(String type, StripeObject stripeObject, Map<String, String> metadata);
}


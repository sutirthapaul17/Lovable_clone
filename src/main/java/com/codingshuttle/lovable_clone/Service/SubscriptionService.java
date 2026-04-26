package com.codingshuttle.lovable_clone.Service;

import com.codingshuttle.lovable_clone.Dto.Subscription.CheckoutRequest;
import com.codingshuttle.lovable_clone.Dto.Subscription.CheckoutResponse;
import com.codingshuttle.lovable_clone.Dto.Subscription.PortalResponse;
import com.codingshuttle.lovable_clone.Dto.Subscription.SubscriptionResponse;
import org.jspecify.annotations.Nullable;

public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription(Long userId);

//    CheckoutResponse createCheckoutSessionurl(CheckoutRequest request);
//
//    PortalResponse openCustomPortal(Long userId);


}

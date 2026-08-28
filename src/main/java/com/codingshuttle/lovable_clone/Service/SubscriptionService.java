package com.codingshuttle.lovable_clone.Service;

import com.codingshuttle.lovable_clone.Dto.Subscription.SubscriptionResponse;
import com.codingshuttle.lovable_clone.Entity.enums.SubscriptionStatus;

import java.time.Instant;

public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription();

    void activateSubscription(long userId, long planId, String subscriptionId, String customerId);

    void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId);

    void cancelSubscription(String gatewaySubscriptionId
    );

    void renewSubscriptionPeriod(String subId, Instant periodStart, Instant periodEnd);

    void markSubscriptionPastDue(String subId);

    boolean canCreateNewProject();



//    CheckoutResponse createCheckoutSessionurl(CheckoutRequest request);
//
//    PortalResponse openCustomPortal(Long userId);

}

package com.codingshuttle.lovable_clone.Service.Impl;

import com.codingshuttle.lovable_clone.Dto.Subscription.CheckoutRequest;
import com.codingshuttle.lovable_clone.Dto.Subscription.CheckoutResponse;
import com.codingshuttle.lovable_clone.Dto.Subscription.PortalResponse;
import com.codingshuttle.lovable_clone.Dto.Subscription.SubscriptionResponse;
import com.codingshuttle.lovable_clone.Service.SubscriptionService;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    @Override
    public SubscriptionResponse getCurrentSubscription(Long userId) {
        return null;
    }

    
}

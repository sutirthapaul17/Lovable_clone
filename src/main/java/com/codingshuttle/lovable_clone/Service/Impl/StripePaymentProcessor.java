package com.codingshuttle.lovable_clone.Service.Impl;

import com.codingshuttle.lovable_clone.Dto.Subscription.CheckoutRequest;
import com.codingshuttle.lovable_clone.Dto.Subscription.CheckoutResponse;
import com.codingshuttle.lovable_clone.Dto.Subscription.PortalResponse;
import com.codingshuttle.lovable_clone.Entity.Plan;
import com.codingshuttle.lovable_clone.Entity.User;
import com.codingshuttle.lovable_clone.Repository.PlanRepository;
import com.codingshuttle.lovable_clone.Repository.UserRepository;
import com.codingshuttle.lovable_clone.Service.PaymentProcessor;
import com.codingshuttle.lovable_clone.error.ResourceNotFoundException;
import com.codingshuttle.lovable_clone.security.AuthUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.model.Price;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class StripePaymentProcessor implements PaymentProcessor{

    private final AuthUtil authUtil;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    @Value("${client.url}")
    private String frontendUrl;


    @Override
    public CheckoutResponse createCheckoutSessionurl(CheckoutRequest request){
        Plan plan = planRepository.findById(request.planId()).orElseThrow(() -> new ResourceNotFoundException("Plan", request.planId().toString()));

        Long userId = authUtil.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user",userId.toString())
        );

        var params = SessionCreateParams.builder()
                .addLineItem(
                        SessionCreateParams.LineItem.builder().setPrice(plan.getStripePriceId()).setQuantity(1L).build())
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSubscriptionData(
                        new SessionCreateParams.SubscriptionData.Builder()
                                .setBillingMode(SessionCreateParams.SubscriptionData.BillingMode.builder()
                                        .setType(SessionCreateParams.SubscriptionData.BillingMode.Type.FLEXIBLE).build())
                                .build()
                )
                .setSuccessUrl(frontendUrl + "/success.html?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/cancel.html")
                .putMetadata("user_id",userId.toString())
                .putMetadata("plan_id", String.valueOf(plan.getId()));

        try {
            String stripeCustomerId = user.getStripeCustomerId();
            if(stripeCustomerId == null || stripeCustomerId.isEmpty()){
                params.setCustomerEmail(user.getUsername());
            }else{
                params.setCustomer(user.getStripeCustomerId());
            }

            Session session = Session.create(params.build());  // making api call to the stripe backend
            return new CheckoutResponse(session.getUrl());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PortalResponse openCustomPortal(Long userId) {
        return null;
    }

    @Override
    public void handleWebHookEvent(String type, StripeObject stripeObject, Map<String, String> metadata) {
        log.info(type);
    }
}

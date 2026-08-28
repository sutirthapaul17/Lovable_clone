package com.codingshuttle.lovable_clone.Service.Impl;

import com.codingshuttle.lovable_clone.Dto.Subscription.CheckoutRequest;
import com.codingshuttle.lovable_clone.Dto.Subscription.CheckoutResponse;
import com.codingshuttle.lovable_clone.Dto.Subscription.PortalResponse;
import com.codingshuttle.lovable_clone.Entity.Plan;
//import com.codingshuttle.lovable_clone.Entity.Subscription;
import com.codingshuttle.lovable_clone.Entity.enums.SubscriptionStatus;
import com.codingshuttle.lovable_clone.Service.SubscriptionService;
import com.codingshuttle.lovable_clone.error.BadRequestException;
import com.stripe.model.*;
import com.codingshuttle.lovable_clone.Entity.User;
import com.codingshuttle.lovable_clone.Repository.PlanRepository;
import com.codingshuttle.lovable_clone.Repository.UserRepository;
import com.codingshuttle.lovable_clone.Service.PaymentProcessor;
import com.codingshuttle.lovable_clone.error.ResourceNotFoundException;
import com.codingshuttle.lovable_clone.security.AuthUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class StripePaymentProcessor implements PaymentProcessor{

    private final AuthUtil authUtil;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;

    @Value("${client.url}")
    private String frontendUrl;


    @Override
    public CheckoutResponse createCheckoutSessionurl(CheckoutRequest request){
        Plan plan = planRepository.findById(request.planId()).orElseThrow(() -> new ResourceNotFoundException("Plan", request.planId().toString()));

        Long userId = authUtil.getCurrentUserId();
        User user = getUser(userId);

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
                params.setCustomer(stripeCustomerId);
            }

            Session session = Session.create(params.build());  // making api call to the stripe backend
            return new CheckoutResponse(session.getUrl());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public PortalResponse openCustomPortal() {
        Long userId = authUtil.getCurrentUserId();
        User user = getUser(userId);
        String stripeCustomerId = user.getStripeCustomerId();

        if(stripeCustomerId == null || stripeCustomerId.isEmpty()){
            throw new BadRequestException("User does not have a Stripe customer ID. Cannot open portal. UserId: "+userId);
        }

        try {
            var portalSession = com.stripe.model.billingportal.Session.create(
                    com.stripe.param.billingportal.SessionCreateParams.builder()
                            .setCustomer(stripeCustomerId).setReturnUrl(frontendUrl).build()
            );
            return new PortalResponse(portalSession.getUrl());

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleWebHookEvent(String type, StripeObject stripeObject, Map<String, String> metadata) {
        log.info("handling stripe event type: {}",type);

        switch(type){
            case "checkout.session.completed" -> handleCheckoutSessionCompleted((Session) stripeObject,metadata); //one-time, on checkout completed
            case "customer.subscription.updated" -> handleCustomerSubscriptionUpdated((Subscription) stripeObject); //when user cancels, updates or upgrades
            case "customer.subscription.deleted" -> handleCustomerSubscriptionDeleted((Subscription) stripeObject); //when subscription ends, revoke the access
            case "invoice.paid" -> handleInvoicePaid((Invoice) stripeObject); //when invoice is paid
            case "invoice.payment_failed" -> handleInvoicePaymentFailed((Invoice) stripeObject); // when invoice is not paid. mark as PAST_DUE
            default -> log.debug("Ignoring the event: {}", type);
        }
    }

    private void handleCheckoutSessionCompleted(Session session,Map<String,String> metadata){
        if(session == null) {
            log.warn("Session is null");
            return;
        }
        long userId = Long.parseLong(metadata.get("user_id"));
        long planId = Long.parseLong(metadata.get("plan_id"));

        String subscriptionId = session.getSubscription();
        String customerId = session.getCustomer();

        User user = getUser(userId);
        if(user.getStripeCustomerId() == null){
            user.setStripeCustomerId(customerId);
            userRepository.save(user);
        }

        subscriptionService.activateSubscription(userId,planId,subscriptionId,customerId);
    }

    private void handleCustomerSubscriptionUpdated(Subscription subscription){
        if(subscription == null) {
            log.warn("Subscription object was null inside handleCustomerSubscriptionUpdated");
            return;
        }
        SubscriptionStatus status = mapStripeStatusToEnum(subscription.getStatus());
        if(status == null){
            log.error("unknown status '{}' for subscription {}",subscription.getStatus(),subscription.getId());
            return;
        }
        SubscriptionItem item = subscription.getItems().getData().get(0);
        Instant periodStart =toInstant(item.getCurrentPeriodStart());
        Instant periodEnd = toInstant(item.getCurrentPeriodEnd());

        Long planId = resolvePlanId(item.getPrice());

        subscriptionService.updateSubscription(
                subscription.getId(), status, periodStart, periodEnd,
                subscription.getCancelAtPeriodEnd(), planId
        );
    }

    private void handleCustomerSubscriptionDeleted(Subscription subscription){
        if(subscription == null){
            log.error("Subscription object was null inside handleCustomerSubscriptionDeleted");
            return;
        }
        subscriptionService.cancelSubscription(subscription.getId());
    }

    private void handleInvoicePaid(Invoice invoice){
        String subId = extractSubscriptionId(invoice);
        if(subId == null) {
            log.warn("Failed to extract subscription ID from invoice: {}", invoice.getId());
            return;
        }
        try{
            Subscription subscription = Subscription.retrieve(subId);
            var item = subscription.getItems().getData().get(0);
            Instant periodStart = toInstant(item.getCurrentPeriodStart());
            Instant periodEnd = toInstant(item.getCurrentPeriodEnd());
            subscriptionService.renewSubscriptionPeriod(
                    subId,
                    periodStart,
                    periodEnd
            );

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleInvoicePaymentFailed(Invoice invoice){
        String subId = extractSubscriptionId(invoice);
        if(subId == null) {
            log.warn("Failed to extract subscription ID from invoice: {}", invoice.getId());
            return;
        }
        subscriptionService.markSubscriptionPastDue(subId);
    }


    //utility methods
    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user", userId.toString())
        );
    }

    private SubscriptionStatus mapStripeStatusToEnum(String status) {
        return switch (status){
            case "active" -> SubscriptionStatus.ACTIVE;
            case "trailing" -> SubscriptionStatus.TRIALING;
            case "past_due","unpaid","paused","incomplete_expired" -> SubscriptionStatus.PAST_DUE;
            case "incomplete" -> SubscriptionStatus.INCOMPLETE;
            default -> {
                log.warn("Unmapped Stripe status: {}",status);
                yield null;
            }

        };
    }

    private Instant toInstant(Long epoch) {
        return epoch != null ? Instant.ofEpochSecond(epoch) : null;
    }

    private Long resolvePlanId(Price price) {
        if(price == null || price.getId() == null) return null;
        return planRepository.findByStripePriceId(price.getId())
                .map(Plan :: getId)
                .orElse(null);
    }

    private String extractSubscriptionId(Invoice invoice){
        var parent = invoice.getParent();
        if(parent == null) return null;
        var subDetails = parent.getSubscriptionDetails();
        if(subDetails == null) return null;
        return subDetails.getSubscription();
    }





}


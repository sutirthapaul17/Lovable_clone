package com.codingshuttle.lovable_clone.Service.Impl;

import com.codingshuttle.lovable_clone.Dto.Subscription.SubscriptionResponse;
import com.codingshuttle.lovable_clone.Entity.Plan;
import com.codingshuttle.lovable_clone.Entity.Subscription;
import com.codingshuttle.lovable_clone.Entity.User;
import com.codingshuttle.lovable_clone.Entity.enums.SubscriptionStatus;
import com.codingshuttle.lovable_clone.Mapper.SubscriptionMapper;
import com.codingshuttle.lovable_clone.Repository.PlanRepository;
import com.codingshuttle.lovable_clone.Repository.ProjectMemberReposirtory;
import com.codingshuttle.lovable_clone.Repository.SubscriptionRepository;
import com.codingshuttle.lovable_clone.Repository.UserRepository;
import com.codingshuttle.lovable_clone.Service.SubscriptionService;
import com.codingshuttle.lovable_clone.error.ResourceNotFoundException;
import com.codingshuttle.lovable_clone.security.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {
    private final AuthUtil authUtil;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final ProjectMemberReposirtory projectMemberReposirtory;

    private final Integer FREE_TIER_PROJECTS_ALLOWED = 1;


    @Override
    public SubscriptionResponse getCurrentSubscription() {
        Long userId = authUtil.getCurrentUserId();

        var currentSubscription =  subscriptionRepository.findByUserIdAndStatusIn(userId, Set.of(
                SubscriptionStatus.ACTIVE,SubscriptionStatus.PAST_DUE,
                SubscriptionStatus.TRIALING
        )).orElse(
                new Subscription()
        );

        return subscriptionMapper.toSubscriptionResponse(currentSubscription);
    }

    @Override
    public void activateSubscription(long userId, long planId, String subscriptionId, String customerId) {
        boolean exists = subscriptionRepository.existsByStripeSubscriptionId(subscriptionId);
        if(exists) return;
        User user = getUser(userId);
        Plan plan = getPlan(planId);
        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .status(SubscriptionStatus.INCOMPLETE)
                .stripeSubscriptionId(subscriptionId)
                .build();

        subscriptionRepository.save(subscription);


    }

    @Override
    @Transactional
    public void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        boolean subscriptionHasBeenUpdated = false;

        if(status != null && status != subscription.getStatus()){
            subscription.setStatus(status);
            subscriptionHasBeenUpdated = true;
        }
        if(periodStart != null && !periodStart.equals(subscription.getCurrentPeriodStart())){
            subscription.setCurrentPeriodStart(periodStart);
            subscriptionHasBeenUpdated = true;
        }
        if(periodEnd != null && !periodEnd.equals(subscription.getCurrentPeriodEnd())) {
            subscription.setCurrentPeriodEnd(periodEnd);
            subscriptionHasBeenUpdated = true;
        }
        if(cancelAtPeriodEnd != null && cancelAtPeriodEnd != subscription.getCancelAtPeriodEnd()){
            subscription.setCancelAtPeriodEnd(cancelAtPeriodEnd);
            subscriptionHasBeenUpdated = true;
        }

        if(planId != null && !planId.equals(subscription.getPlan().getId())){
            Plan newPlan = getPlan(planId);
            subscription.setPlan(newPlan);
            subscriptionHasBeenUpdated = true;
        }

        if(subscriptionHasBeenUpdated){
            log.debug("Subscription has been updated: {}",gatewaySubscriptionId);
            subscriptionRepository.save(subscription);
        }

    }

    @Override
    public void cancelSubscription(String gatewaySubscriptionId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscriptionRepository.save(subscription);

    }

    @Override
    public void renewSubscriptionPeriod(String gatewaySubscriptionId, Instant periodStart, Instant periodEnd) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        Instant newStart = periodStart != null ? periodStart: subscription.getCurrentPeriodEnd();
        subscription.setCurrentPeriodStart(newStart);
        subscription.setCurrentPeriodEnd(periodEnd);

        if(subscription.getStatus() == SubscriptionStatus.PAST_DUE || subscription.getStatus() == SubscriptionStatus.INCOMPLETE){
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }
        subscriptionRepository.save(subscription);

    }



    @Override
    public void markSubscriptionPastDue(String gatewaySubscriptionId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        if(subscription.getStatus() == SubscriptionStatus.PAST_DUE){
            log.debug("Subscription is already past due, gatewaySubscriptionId {}",gatewaySubscriptionId);
            return;
        }
        subscription.setStatus(SubscriptionStatus.PAST_DUE);
        subscriptionRepository.save(subscription);

    }



    @Override
    public boolean canCreateNewProject() {
        Long userId = authUtil.getCurrentUserId();
        SubscriptionResponse currentSubscription = getCurrentSubscription();
        int countOwnedProjects = projectMemberReposirtory.countProjectOwnedByUser(userId);

        if(currentSubscription.plan() == null){
            return countOwnedProjects < FREE_TIER_PROJECTS_ALLOWED;
        }

        return countOwnedProjects < currentSubscription.plan().maxProjects();
    }


    //////Utility Methods
    private User getUser(Long userId){
        return userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User", userId.toString()));
    }

    private Plan getPlan(Long planId){
        return planRepository.findById(planId).orElseThrow(
                () -> new ResourceNotFoundException("Plan", planId.toString()));
    }

    private Subscription getSubscription(String gatewaySubscriptionId) {
        return subscriptionRepository.findByStripeSubscriptionId(gatewaySubscriptionId).orElseThrow(
                () -> new ResourceNotFoundException("Subscription", gatewaySubscriptionId));
    }

}

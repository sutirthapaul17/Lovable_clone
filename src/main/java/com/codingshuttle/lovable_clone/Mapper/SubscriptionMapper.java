package com.codingshuttle.lovable_clone.Mapper;

import com.codingshuttle.lovable_clone.Dto.Subscription.PlanResponse;
import com.codingshuttle.lovable_clone.Dto.Subscription.SubscriptionResponse;
import com.codingshuttle.lovable_clone.Entity.Plan;
import com.codingshuttle.lovable_clone.Entity.Subscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    SubscriptionResponse toSubscriptionResponse(Subscription subscription);

    PlanResponse toPlanResponse(Plan plan);
}

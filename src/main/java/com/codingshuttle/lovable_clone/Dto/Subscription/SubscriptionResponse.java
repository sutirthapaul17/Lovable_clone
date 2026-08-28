package com.codingshuttle.lovable_clone.Dto.Subscription;

import java.time.Instant;

public record SubscriptionResponse(
        PlanResponse plan,
        String status,
        Instant currentPeriodEnd,
        Long tokensUsedThisCycle
) {
}

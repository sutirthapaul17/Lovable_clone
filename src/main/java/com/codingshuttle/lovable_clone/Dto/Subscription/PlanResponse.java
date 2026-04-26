package com.codingshuttle.lovable_clone.Dto.Subscription;

public record PlanResponse(
        long id,
        String name,
        Integer maxProjects,
        Integer maxTokensPerDay,
        Boolean unlimitedAi,
        String price
) {
}

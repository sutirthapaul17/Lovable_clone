package com.codingshuttle.lovable_clone.Service;

import com.codingshuttle.lovable_clone.Dto.Subscription.PlanResponse;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface PlanService {
    @Nullable List<PlanResponse> getAllActivePlans();
}

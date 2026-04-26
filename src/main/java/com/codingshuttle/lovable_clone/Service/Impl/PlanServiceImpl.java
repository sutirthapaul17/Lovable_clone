package com.codingshuttle.lovable_clone.Service.Impl;


import com.codingshuttle.lovable_clone.Dto.Subscription.PlanResponse;
import com.codingshuttle.lovable_clone.Service.PlanService;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PlanServiceImpl implements PlanService {
    @Override
    public @Nullable List<PlanResponse> getAllActivePlans() {
        return List.of();
    }
}

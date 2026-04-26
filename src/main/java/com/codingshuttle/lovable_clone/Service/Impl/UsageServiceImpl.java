package com.codingshuttle.lovable_clone.Service.Impl;

import com.codingshuttle.lovable_clone.Dto.Usage.UsageTodayResponse;
import com.codingshuttle.lovable_clone.Service.UsageService;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class UsageServiceImpl implements UsageService {
    @Override
    public @Nullable UsageTodayResponse getTodayUsageOfUser(Long userId) {
        return null;
    }

    @Override
    public @Nullable UsageTodayResponse getCurrentSubscriptionLimitsOfUser(Long userId) {
        return null;
    }
}

package com.codingshuttle.lovable_clone.Service;

import com.codingshuttle.lovable_clone.Dto.Usage.UsageTodayResponse;
import org.jspecify.annotations.Nullable;

public interface UsageService {
    @Nullable UsageTodayResponse getTodayUsageOfUser(Long userId);


    @Nullable UsageTodayResponse getCurrentSubscriptionLimitsOfUser(Long userId);

}

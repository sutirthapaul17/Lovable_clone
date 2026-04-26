package com.codingshuttle.lovable_clone.Entity;


import com.codingshuttle.lovable_clone.Entity.enums.SubscriptionStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)  //all fields are private now, we don't need to explicitly define them private
public class Subscription {

    Long id;

    User user;
    Plan plan;

    SubscriptionStatus status;

//    String stripeCustomerId;
    String StripeSubscriptionId;

    Instant currentPeriodStart;
    Instant currentPeriodEnd;
    Boolean cancelAtPeriodEnd = false;

    Instant createdAt;
    Instant updatedAt;

}

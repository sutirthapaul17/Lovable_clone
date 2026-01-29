package com.codingshuttle.lovable_clone.Entity;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsageLog {

    Long id;

    User user;
    Project project;

    String action;

    Integer tokensUsed;
    Integer duration;

    String metaData; //JSON of {model_used, prompt_used}

    Instant createdAt;
}

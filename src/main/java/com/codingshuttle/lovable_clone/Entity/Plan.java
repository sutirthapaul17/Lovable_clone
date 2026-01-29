package com.codingshuttle.lovable_clone.Entity;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)  //all fields are private now, we don't need to explicitly define them private
public class Plan {

    long id;

    String name;
    String stripePriceId;
    Integer maxProjects;
    Integer maxTokensPerDay;
    Integer maxPreviews; //max number of previews allowed per plan
    Boolean unlimitedAi; //unlimited access to llm, ignore maxtokeperday if true

    Boolean active;
}

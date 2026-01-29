package com.codingshuttle.lovable_clone.Entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;  //alternative of LocalDateTime, with some more features

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)  //all fields are private now, we don't need to explicitly define them private
public class User {

    Long id;
    String smail;
    String passwordHash;
    String name;

    String avatarUrl;
    Instant createdAt;
    Instant updatedAt;
    Instant deletedAt;  //soft delete
}

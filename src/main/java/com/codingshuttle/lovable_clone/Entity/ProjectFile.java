package com.codingshuttle.lovable_clone.Entity;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectFile {

    Long id;
    Project project;
    String path;
    String minIoObjectKey;

    Instant createdAt;
    Instant updatedAt;

    User createdBy;
    User updatedBy;

}

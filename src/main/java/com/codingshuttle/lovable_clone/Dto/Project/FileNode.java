package com.codingshuttle.lovable_clone.Dto.Project;

import java.time.Instant;

public record FileNode(
        String path
) {
    @Override
    public String toString() {
        return path;
    }
}

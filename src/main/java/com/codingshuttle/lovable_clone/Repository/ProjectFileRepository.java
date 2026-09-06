package com.codingshuttle.lovable_clone.Repository;

import com.codingshuttle.lovable_clone.Entity.ProjectFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectFileRepository extends JpaRepository<ProjectFile,Long> {
    Optional<ProjectFile> findByProjectIdAndPath(Long projectId, String cleanPath);

    List<ProjectFile> findByProjectId(long projectId);

}

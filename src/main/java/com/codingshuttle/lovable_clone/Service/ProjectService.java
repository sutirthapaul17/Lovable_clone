package com.codingshuttle.lovable_clone.Service;

import com.codingshuttle.lovable_clone.Dto.Project.ProjectRequest;
import com.codingshuttle.lovable_clone.Dto.Project.ProjectResponse;
import com.codingshuttle.lovable_clone.Dto.Project.ProjectSummeryResponse;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ProjectService {
//    List<ProjectSummeryResponse> getAllProjects(Long userId);

    List<ProjectSummeryResponse> getUserProjects();

    ProjectResponse createProject(ProjectRequest request);

    ProjectResponse getUserProjectById(Long projectId);

    ProjectResponse updateProject(Long id, ProjectRequest request);

    void softDelete(Long id);

}

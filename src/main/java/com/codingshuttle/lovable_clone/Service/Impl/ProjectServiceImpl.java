package com.codingshuttle.lovable_clone.Service.Impl;

import com.codingshuttle.lovable_clone.Dto.Project.ProjectRequest;
import com.codingshuttle.lovable_clone.Dto.Project.ProjectResponse;
import com.codingshuttle.lovable_clone.Dto.Project.ProjectSummeryResponse;
import com.codingshuttle.lovable_clone.Entity.Project;
import com.codingshuttle.lovable_clone.Entity.ProjectMember;
import com.codingshuttle.lovable_clone.Entity.ProjectMemberId;
import com.codingshuttle.lovable_clone.Entity.User;
import com.codingshuttle.lovable_clone.Entity.enums.ProjectRole;
import com.codingshuttle.lovable_clone.Mapper.ProjectMapper;
import com.codingshuttle.lovable_clone.Repository.ProjectMemberReposirtory;
import com.codingshuttle.lovable_clone.Repository.ProjectRepository;
import com.codingshuttle.lovable_clone.Repository.UserRepository;
import com.codingshuttle.lovable_clone.Service.ProjectService;
import com.codingshuttle.lovable_clone.error.ResourceNotFoundException;
import com.codingshuttle.lovable_clone.security.AuthUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@Transactional
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;
    UserRepository userRepository;
    ProjectMapper projectMapper;
    ProjectMemberReposirtory projectMemberReposirtory;
    AuthUtil authUtil;

    @Override
    public ProjectResponse createProject(ProjectRequest request) {
        Long userId = authUtil.getCurrentUserId();
//        User owner = userRepository.findById(userId).orElseThrow(
//                () -> new ResourceNotFoundException("User",userId.toString())
//        );
        User owner = userRepository.getReferenceById(userId);

        //create Project
        Project project = Project.builder()
                .name(request.name())
//                .owner(owner)
                .isPublic(false)
                .build();

        project = projectRepository.save(project);

        ProjectMemberId projectMemberId = new ProjectMemberId(project.getId(), owner.getId());

        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .projectRole(ProjectRole.OWNER)
                .user(owner)
                .acceptedAt(Instant.now())
                .invitedAt(Instant.now())
                .project(project)
                .build();
        projectMemberReposirtory.save(projectMember);

        return projectMapper.toProjectResponse(project);
    }

    @Override
    public List<ProjectSummeryResponse> getUserProjects() {
//        return projectRepository.findAllAccessibleByUser(userId)
//                .stream()
//                .map(projectMapper::toProjectSummeryResponse)
//                .toList();

        long userId = authUtil.getCurrentUserId();
        var projects = projectRepository.findAllAccessibleByUser(userId);
        return projectMapper.toListOfProjectSummeryResponse(projects);
    }

    @Override
    @PreAuthorize("@security.canViewProject(#projectId)")
    public ProjectResponse getUserProjectById(Long projectId) {
        long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId,userId);

        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public ProjectResponse updateProject(Long projectId, ProjectRequest request) {
        long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId,userId);
//        if(!project.getOwner().getId().equals(userId)){
//            throw new RuntimeException("You are not allowed to update the name");
//        }

        project.setName(request.name());
        project = projectRepository.save(project);

        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canDeleteProject(#projectId)")
    public void softDelete(Long projectId) {
        long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId,userId);
//
//        if(!project.getOwner().getId().equals(userId)){
//            throw new RuntimeException("You are not allowed to delete");
//        }

        project.setDeletedAt(Instant.now());
        projectRepository.save(project);

    }



    /// Internal functions
    public Project getAccessibleProjectById(Long projectId,Long userId){
        return projectRepository.findAllAccessibleProjectById(projectId,userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project",projectId.toString()));
    }
}

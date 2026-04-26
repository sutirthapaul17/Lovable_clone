package com.codingshuttle.lovable_clone.Service.Impl;

import com.codingshuttle.lovable_clone.Dto.Member.InviteMemberRequest;
import com.codingshuttle.lovable_clone.Dto.Member.MemberResponse;
import com.codingshuttle.lovable_clone.Dto.Member.UpdateMemberRoleRequest;
import com.codingshuttle.lovable_clone.Entity.Project;
import com.codingshuttle.lovable_clone.Entity.ProjectMember;
import com.codingshuttle.lovable_clone.Entity.ProjectMemberId;
import com.codingshuttle.lovable_clone.Entity.User;
import com.codingshuttle.lovable_clone.Mapper.ProjectMemberMapper;
import com.codingshuttle.lovable_clone.Repository.ProjectMemberReposirtory;
import com.codingshuttle.lovable_clone.Repository.ProjectRepository;
import com.codingshuttle.lovable_clone.Repository.UserRepository;
import com.codingshuttle.lovable_clone.Service.ProjectMemberService;
import com.codingshuttle.lovable_clone.security.AuthUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Service
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class ProjectMemberServiceImpl implements ProjectMemberService {

    ProjectMemberReposirtory projectMemberReposirtory;
    ProjectRepository projectRepository;
    ProjectMemberMapper projectMemberMapper;
    UserRepository userRepository;
    AuthUtil authUtil;


    @Override
    @PreAuthorize("@security.canViewMembers(#projectId)")
    public List<MemberResponse> getProjectMembers(long projectId) {
        long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId,userId);

//        List<MemberResponse> memberResponseList = new ArrayList<>();
//        memberResponseList.add(projectMemberMapper.toProjectMemberResponseFromOwner(project.getOwner()));

        return projectMemberReposirtory.findByIdProjectId(projectId)
                .stream()
                .map(projectMemberMapper :: toProjectMemberResponseFromMember)
                .toList();

    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse inviteMember(Long projectId, InviteMemberRequest request) {
        long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId,userId);
//
//        if(!project.getOwner().getId().equals(userId)){
//            throw new RuntimeException("Not allowed");
//        }

        User invitee = userRepository.findByUsername(request.userName()).orElseThrow();
        if(invitee.getId().equals(userId)){
            throw new RuntimeException("Cannot invite yourself");
        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, invitee.getId());
        if(projectMemberReposirtory.existsById(projectMemberId)){
            throw new RuntimeException("Cannot invite again");
        }
        ProjectMember member = ProjectMember.builder()
                .id(projectMemberId)
                .project(project)
                .user(invitee)
                .projectRole(request.role())
                .invitedAt(Instant.now())
                .build();
        projectMemberReposirtory.save(member);


        return projectMemberMapper.toProjectMemberResponseFromMember(member);
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request) {
        long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId,userId);
//        if(!project.getOwner().getId().equals(userId)){
//            throw new RuntimeException("Not allowed");
//        }
        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);

        ProjectMember projectMember = projectMemberReposirtory.findById(projectMemberId).orElseThrow();

        projectMember.setProjectRole(request.role());
        projectMemberReposirtory.save(projectMember);
        return projectMemberMapper.toProjectMemberResponseFromMember(projectMember);
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public void removeProjectMember(Long projectId, Long memberId) {
        long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId,userId);
//        if(!project.getOwner().getId().equals(userId)){
//            throw new RuntimeException("Not allowed");
//        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        if(!projectMemberReposirtory.existsById(projectMemberId)){
            throw new RuntimeException("member doesnot exist");
        }

        projectMemberReposirtory.deleteById(projectMemberId);

    }


    /// Internal functions
    public Project getAccessibleProjectById(Long projectId, Long userId){
        return projectRepository.findAllAccessibleProjectById(projectId,userId).orElseThrow();
    }
}

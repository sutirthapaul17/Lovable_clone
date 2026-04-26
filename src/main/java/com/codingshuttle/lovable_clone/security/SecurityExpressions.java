package com.codingshuttle.lovable_clone.security;


import com.codingshuttle.lovable_clone.Entity.enums.ProjectPermission;
import com.codingshuttle.lovable_clone.Entity.enums.ProjectRole;
import com.codingshuttle.lovable_clone.Repository.ProjectMemberReposirtory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("security")
@RequiredArgsConstructor
public class SecurityExpressions {
    private final ProjectMemberReposirtory projectMemberReposirtory;
    private final AuthUtil authUtil;

    private boolean hasPermission(Long projectId,ProjectPermission permissions){
        Long userId = authUtil.getCurrentUserId();

        return projectMemberReposirtory.findRoleByProjectIdAndUserId(projectId,userId).map(role ->
                                role.getPermissions().contains(permissions)
                )
                .orElse(false);
    }

    public boolean canViewProject(Long projectId){
//        Long userId = authUtil.getCurrentUserId();
//
//        return projectMemberReposirtory.findRoleByProjectIdAndUserId(projectId,userId).map(role ->
////                        role.equals(ProjectRole.OWNER) ||
////                        role.equals(ProjectRole.EDITOR) ||
////                        role.equals(ProjectRole.VIEWER)
//                    role.getPermissions().contains(ProjectPermission.VIEW)
//                )
//                .orElse(false);
        return hasPermission(projectId,ProjectPermission.VIEW);
    }


    public boolean canEditProject(long projectId){
//        Long userId = authUtil.getCurrentUserId();
//
//        return projectMemberReposirtory.findRoleByProjectIdAndUserId(projectId,userId).map(role ->
////                        role.equals(ProjectRole.OWNER) ||
////                        role.equals(ProjectRole.EDITOR)
//                        role.getPermissions().contains(ProjectPermission.EDIT)
//                )
//                .orElse(false);
        return hasPermission(projectId,ProjectPermission.EDIT);
    }

    public boolean canDeleteProject(long projectId){
        return hasPermission(projectId,ProjectPermission.DELETE);
    }

    public boolean canViewMembers(long projectId){
        return hasPermission(projectId,ProjectPermission.VIEW_MEMBERS);
    }

    public boolean canManageMembers(long projectId){
        return hasPermission(projectId,ProjectPermission.MANAGE_MEMBERS);
    }
}

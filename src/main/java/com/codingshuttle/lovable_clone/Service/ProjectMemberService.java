package com.codingshuttle.lovable_clone.Service;

import com.codingshuttle.lovable_clone.Dto.Member.InviteMemberRequest;
import com.codingshuttle.lovable_clone.Dto.Member.MemberResponse;
import com.codingshuttle.lovable_clone.Dto.Member.UpdateMemberRoleRequest;

import java.util.List;

public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(long projectId);

     MemberResponse inviteMember(Long projectId, InviteMemberRequest request);

     MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request);

    void removeProjectMember(Long projectId, Long memberId);

}

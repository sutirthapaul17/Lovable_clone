package com.codingshuttle.lovable_clone.Mapper;


import com.codingshuttle.lovable_clone.Dto.Member.MemberResponse;
import com.codingshuttle.lovable_clone.Entity.ProjectMember;
import com.codingshuttle.lovable_clone.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {

    @Mapping(source = "id",target="userId")
    @Mapping(target = "projectRole",constant = "OWNER")
    MemberResponse toProjectMemberResponseFromOwner(User owner);

    @Mapping(target = "userId",source = "user.id")
    @Mapping(target = "userName",source = "user.username")
    @Mapping(target = "name",source = "user.name")
    MemberResponse toProjectMemberResponseFromMember(ProjectMember projectMember);
}

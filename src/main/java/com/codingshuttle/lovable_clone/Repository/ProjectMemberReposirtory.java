package com.codingshuttle.lovable_clone.Repository;


import com.codingshuttle.lovable_clone.Entity.ProjectMember;
import com.codingshuttle.lovable_clone.Entity.ProjectMemberId;
import com.codingshuttle.lovable_clone.Entity.enums.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProjectMemberReposirtory extends JpaRepository<ProjectMember, ProjectMemberId> {

    List<ProjectMember> findByIdProjectId(Long projectId);


    @Query("""
            SELECT pm.projectRole FROM ProjectMember pm
            WHERE pm.id.projectId = :projectId AND pm.id.userId = :userId
            """
    )
    Optional<ProjectRole> findRoleByProjectIdAndUserId(@Param("projectId") Long projectId,@Param("userId") Long userId);
}

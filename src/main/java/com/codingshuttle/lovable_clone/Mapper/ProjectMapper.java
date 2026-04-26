package com.codingshuttle.lovable_clone.Mapper;


import com.codingshuttle.lovable_clone.Dto.Project.ProjectResponse;
import com.codingshuttle.lovable_clone.Dto.Project.ProjectSummeryResponse;
import com.codingshuttle.lovable_clone.Entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponse toProjectResponse(Project project);

    @Mapping(source = "name",target = "projectName")
    ProjectSummeryResponse toProjectSummeryResponse(Project project);

    List<ProjectSummeryResponse> toListOfProjectSummeryResponse(List<Project> projects);

}

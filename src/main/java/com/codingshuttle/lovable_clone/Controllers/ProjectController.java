package com.codingshuttle.lovable_clone.Controllers;


import com.codingshuttle.lovable_clone.Dto.Project.ProjectRequest;
import com.codingshuttle.lovable_clone.Dto.Project.ProjectResponse;
import com.codingshuttle.lovable_clone.Dto.Project.ProjectSummeryResponse;
import com.codingshuttle.lovable_clone.Service.ProjectService;
import com.codingshuttle.lovable_clone.security.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
//    private final AuthUtil authUtil;

    @GetMapping
    public ResponseEntity<List<ProjectSummeryResponse>> getMyProjects(){
//        Long userId = 1L;
//        Long userId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(projectService.getUserProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id){
//        long userId = 1L;
        return ResponseEntity.ok(projectService.getUserProjectById(id));

    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody @Valid ProjectRequest request){
//        Long userId = 1L;
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id,@RequestBody @Valid ProjectRequest request){
//        Long userId = 1L;
        return ResponseEntity.ok(projectService.updateProject(id,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id){
//        Long userId = 1L;
        projectService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

}

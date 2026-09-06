package com.codingshuttle.lovable_clone.Controllers;


import com.codingshuttle.lovable_clone.Dto.Project.FileContentResponse;
import com.codingshuttle.lovable_clone.Dto.Project.FileNode;
import com.codingshuttle.lovable_clone.Service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/files")
public class FileController {

    private final ProjectFileService projectFileService;

    @GetMapping
    public ResponseEntity<List<FileNode>> getFileTree(@PathVariable long projectId){
        Long userId = 1L;
        return ResponseEntity.ok(projectFileService.getFileTree(projectId));
    }

    @GetMapping("/{*path}") // e.g -> /src/hook/get-user-hhok.jsx
    public ResponseEntity<FileContentResponse> getFile(
            @PathVariable Long projectId,
            @PathVariable String path
    ){
        Long userId = 1L;
        return ResponseEntity.ok(projectFileService.getFileContent(projectId,path));

    }

}

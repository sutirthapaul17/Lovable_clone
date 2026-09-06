package com.codingshuttle.lovable_clone.Service;

import com.codingshuttle.lovable_clone.Dto.Project.FileContentResponse;
import com.codingshuttle.lovable_clone.Dto.Project.FileNode;

import java.util.List;

public interface ProjectFileService {
     List<FileNode> getFileTree(long projectId);

    FileContentResponse getFileContent(Long projectId, String path);

    void saveFile(Long projectId, String filePath, String fileContent);

}

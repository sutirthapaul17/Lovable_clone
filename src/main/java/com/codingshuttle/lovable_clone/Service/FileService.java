package com.codingshuttle.lovable_clone.Service;

import com.codingshuttle.lovable_clone.Dto.Project.FileContentResponse;
import com.codingshuttle.lovable_clone.Dto.Project.FileNode;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface FileService {
     List<FileNode> getFileTree(long projectId, Long userId);

    FileContentResponse getFileContent(Long projectId, String path, Long userId);

}

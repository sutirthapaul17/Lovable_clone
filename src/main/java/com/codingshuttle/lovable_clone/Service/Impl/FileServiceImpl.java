package com.codingshuttle.lovable_clone.Service.Impl;

import com.codingshuttle.lovable_clone.Dto.Project.FileContentResponse;
import com.codingshuttle.lovable_clone.Dto.Project.FileNode;
import com.codingshuttle.lovable_clone.Service.FileService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FileServiceImpl implements FileService {
    @Override
    public List<FileNode> getFileTree(long projectId, Long userId) {
        return List.of();
    }

    @Override
    public FileContentResponse getFileContent(Long projectId, String path, Long userId) {
        return null;
    }
}

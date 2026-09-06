package com.codingshuttle.lovable_clone.Service.Impl;

import com.codingshuttle.lovable_clone.Dto.Project.FileContentResponse;
import com.codingshuttle.lovable_clone.Dto.Project.FileNode;
import com.codingshuttle.lovable_clone.Entity.Project;
import com.codingshuttle.lovable_clone.Entity.ProjectFile;
import com.codingshuttle.lovable_clone.Mapper.ProjectFileMapper;
import com.codingshuttle.lovable_clone.Repository.ProjectFileRepository;
import com.codingshuttle.lovable_clone.Repository.ProjectRepository;
import com.codingshuttle.lovable_clone.Service.ProjectFileService;
import com.codingshuttle.lovable_clone.error.ResourceNotFoundException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectFileServiceImpl implements ProjectFileService {

    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;
    private final MinioClient minioClient;
    private final ProjectFileMapper projectFileMapper;

    @Value("${minio.project-bucket}")
    private String projectBucket;

    private static final String BUCKET_NAME = "projects";

    @Override
    public List<FileNode> getFileTree(long projectId) {

        List<ProjectFile> projectFileList = projectFileRepository.findByProjectId(projectId);

        return projectFileMapper.toListOfFileNode(projectFileList);
    }

    @Override
    public FileContentResponse getFileContent(Long projectId, String path) {
        String objectName = projectId + "/" + path;
        try (
                InputStream is = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(BUCKET_NAME)
                                .object(objectName)
                                .build())) {

            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return new FileContentResponse(path, content);
        } catch (Exception e) {
            log.error("Failed to read file: {}/{}", projectId, path, e);
            throw new RuntimeException("Failed to read file content", e);
        }
    }

    @Override
    public void saveFile(Long projectId, String path, String content) {
        log.info("Saving file: {}",path);


        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found ",projectId.toString()));
        String cleanPath = path.startsWith("/") ? path.substring(1) : path;
        String objectKey = projectId + "/" + cleanPath;

        try{
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            InputStream inputStream = new ByteArrayInputStream(contentBytes);
            //Save the content inside minio
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(projectBucket)
                            .object(objectKey)
                            .stream(inputStream, (long) contentBytes.length, (long) -1)
                            .contentType(determineContentType(path))
                            .build()
            );

            //Save the file metadata in postgress
            ProjectFile file = projectFileRepository.findByProjectIdAndPath(projectId,cleanPath).orElseGet(()->
                    ProjectFile.builder()
                            .project(project)
                            .path(cleanPath)
                            .minIoObjectKey(objectKey)
                            .createdAt(Instant.now())
                            .build()
            );
            file.setUpdatedAt(Instant.now());
            projectFileRepository.save(file);
            log.info("Saved file: {}",objectKey);
        } catch (Exception e) {
            log.error("Failed to save file {}/{}",projectId,cleanPath,e);
            throw new RuntimeException(e);
        }

    }

    private String determineContentType(String path) {
        String type= URLConnection.guessContentTypeFromName(path);
        if(type!=null){
            return type;
        }
        if (path.endsWith(".jsx") ||path.endsWith(".tsx")||path.endsWith(".ts")) {
            return "text/javascript";
        }
        if (path.endsWith(".json")) {
            return "application/json";
        }
        if (path.endsWith(".css")) {
            return "text/css";
        }
        return "text/plain";
    }
}

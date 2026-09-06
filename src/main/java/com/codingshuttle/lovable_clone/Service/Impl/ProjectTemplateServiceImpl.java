package com.codingshuttle.lovable_clone.Service.Impl;

import com.codingshuttle.lovable_clone.Entity.Project;
import com.codingshuttle.lovable_clone.Entity.ProjectFile;
import com.codingshuttle.lovable_clone.Repository.ProjectFileRepository;
import com.codingshuttle.lovable_clone.Repository.ProjectRepository;
import com.codingshuttle.lovable_clone.Service.ProjectTemplateService;
import com.codingshuttle.lovable_clone.error.ResourceNotFoundException;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectTemplateServiceImpl implements ProjectTemplateService {
    private final MinioClient minioClient;
    private final ProjectFileRepository projectFileRepository;
    private final ProjectRepository projectRepository;

    private static final String TEMPLATE_BUCKET = "starter-projects";
    private static final String TARGET_BUCKET = "projects";
    private static final String TEMPLATE_NAME = "react-vite-tailwind-daisyui-starter";

    @Override
    public void initializeProjectFromTemplate(Long projectId) {
        log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        log.info("TEMPLATE SERVICE CALLED");
        log.info("PROJECT ID = {}", projectId);
        log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ResourceNotFoundException("Project", projectId.toString()));

        try {
            log.info("Template bucket = {}", TEMPLATE_BUCKET);
            log.info("Template name = {}", TEMPLATE_NAME);
            log.info("Listing objects with prefix = {}/", TEMPLATE_NAME);
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(TEMPLATE_BUCKET)
                            .prefix(TEMPLATE_NAME + "/")
                            .recursive(true)
                            .build()
            );

            List<ProjectFile> filesToSave = new ArrayList<>(); // for metadata in postgres db
            int count = 0;
            for (Result<Item> result : results) {
                Item item = result.get();
                String sourceKey = item.objectName();
                log.info("FOUND TEMPLATE OBJECT: {}", sourceKey);

                String cleanPath = sourceKey.replaceFirst(TEMPLATE_NAME + "/", "");
                String destKey = projectId + "/" + cleanPath;
                log.info(
                        "COPYING: {} -> {}",
                        sourceKey,
                        destKey
                );

                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(TARGET_BUCKET)
                                .object(destKey)
                                .source(
                                        SourceObject.builder()
                                                .bucket(TEMPLATE_BUCKET)
                                                .object(sourceKey)
                                                .build()
                                )
                                .build()
                );

                ProjectFile pf = ProjectFile.builder()
                        .project(project)
                        .path(cleanPath)
                        .minIoObjectKey(destKey)  //.minioObjectKey(destKey)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

                filesToSave.add(pf);
                count++;
            }

            log.info("TOTAL TEMPLATE OBJECTS FOUND = {}", count);

            projectFileRepository.saveAll(filesToSave);

            log.info(
                    "TOTAL PROJECT FILE RECORDS SAVED = {}",
                    filesToSave.size()
            );

            log.info("========== TEMPLATE INITIALIZATION COMPLETE ==========");


        } catch (Exception e) {
            log.error(
                    "========== TEMPLATE INITIALIZATION FAILED ==========",
                    e
            );
            throw new RuntimeException("Failed to initialize project from template", e);
        }
    }
}

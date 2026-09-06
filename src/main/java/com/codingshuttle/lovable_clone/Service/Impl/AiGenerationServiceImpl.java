package com.codingshuttle.lovable_clone.Service.Impl;

import com.codingshuttle.lovable_clone.LLm.Advisors.FileTreeContextAdvisor;
import com.codingshuttle.lovable_clone.LLm.PromptUtils;
import com.codingshuttle.lovable_clone.LLm.Tools.CodeGenerationTools;
import com.codingshuttle.lovable_clone.Service.AiGenerationService;
import com.codingshuttle.lovable_clone.Service.ProjectFileService;
import com.codingshuttle.lovable_clone.security.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiGenerationServiceImpl implements AiGenerationService {
    private final ChatClient chatClient;
    private final AuthUtil authUtil;
    private final ProjectFileService projectFileService;
    private final FileTreeContextAdvisor fileTreeContextAdvisor;

    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file\\s+path\\s*=\\s*\"([^\"]+)\">(.*?)</file>",Pattern.DOTALL);

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<String> streamResponse(String userMessage, Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        createChatSessionIfNotExists(projectId,userId);

        Map<String, Object> advisorParams = Map.of(
                "userId",userId,
                "projectId",projectId
        );

        StringBuilder fullResponseBuffer = new StringBuilder();
        CodeGenerationTools codeGenerationTools = new CodeGenerationTools(projectFileService, projectId);
        return chatClient.prompt()
                .system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT)
                .user(userMessage)
                .tools(codeGenerationTools)
                .advisors(advisorSpec -> {
                            advisorSpec.params(advisorParams);
                            advisorSpec.advisors(fileTreeContextAdvisor);
                        }
                )
                .stream()
                .chatResponse()
                .doOnNext(response->{
                    String content = response.getResult().getOutput().getText();
                    fullResponseBuffer.append(content);
                })
                .doOnComplete(()->{
                    log.info("========== DO ON COMPLETE CALLED ==========");
                    Schedulers.boundedElastic().schedule(() ->{
                        log.info("========== PARSING FILES ==========");
                        parseAndSaveFIles(fullResponseBuffer.toString(), projectId);
                    });
                })
                .doOnError(error->{
                    log.error("Error during streaming for project: {}", error.getMessage());
                })
                .mapNotNull(response -> Objects.requireNonNull(response.getResult()).getOutput().getText());
    }

    private void parseAndSaveFIles(String fullResponse, Long projectId) {

        Matcher matcher = FILE_TAG_PATTERN.matcher(fullResponse);
        while(matcher.find()){
            String filePath = matcher.group(1);
            String fileContent = matcher.group(2).trim();

            projectFileService.saveFile(projectId,filePath,fileContent);
        }
    }

    private void createChatSessionIfNotExists(Long projectId, Long userId) {

    }
}

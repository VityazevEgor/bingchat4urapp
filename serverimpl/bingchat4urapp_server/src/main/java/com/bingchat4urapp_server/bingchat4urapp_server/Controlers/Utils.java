package com.bingchat4urapp_server.bingchat4urapp_server.Controlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.bingchat4urapp_server.bingchat4urapp_server.Models.RequestsModels;
import com.bingchat4urapp_server.bingchat4urapp_server.Models.TaskModel;

@Component
public class Utils {
    @SuppressWarnings("unused")
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(Utils.class);

    @Autowired
    private ResourceLoader resourceLoader;

    public String readFile(String filePath) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + filePath);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        }
    }

    public TaskModel createPromtTask(String promt, String timeOutForAnswer){
        Map<String, String> promtTask = Map.of("promt", promt, "timeOutForAnswer", timeOutForAnswer);

        var newTask = new TaskModel();
        newTask.type = 2;
        newTask.data = promtTask;

        return newTask;
    }

    public TaskModel createAuthTask(String provider){
        Map<String, String> authTask = Map.of("provider", provider);
        var newTask = new TaskModel();
        newTask.type = 1;
        newTask.data = authTask;

        return newTask;
    }

    public TaskModel createNewChatTask(){
        var newTask = new TaskModel();
        newTask.type = 3;
        return newTask;
    }

    public TaskModel createPromtTask(RequestsModels.PromtRequest promptRequest) {
        return createPromtTask(promptRequest.getPromt(), promptRequest.getTimeOutForAnswer().toString());
    }

    public TaskModel createAuthTask(RequestsModels.AuthRequest authRequest){
        return createAuthTask(authRequest.getProvider().toString());
    }
}

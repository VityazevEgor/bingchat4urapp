package com.bingchat4urapp_server.bingchat4urapp_server.Controlers;

import java.util.Map;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.bingchat4urapp_server.bingchat4urapp_server.Models.RequestsModels;
import com.bingchat4urapp_server.bingchat4urapp_server.Models.TaskModel;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class Utils {
    private ObjectMapper mapper = new ObjectMapper();
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(Utils.class);

    private String writeValueAsString(Map<String, String> data){
        String result = null;
        try{
            result = mapper.writeValueAsString(data);
        } catch (Exception ex){
            logger.error("Can't serialize data", ex);
        }

        return result;
    }

    public TaskModel createPromtTask(String promt, String timeOutForAnswer){
        // create String String Map that conatins promt and timeoutForAnswer
        Map<String, String> promtTask = Map.of("promt", promt, "timeOutForAnswer", timeOutForAnswer);

        var newTask = new TaskModel();
        newTask.type = 2;
        newTask.data = promtTask;

        return newTask;
    }

    public TaskModel createAuthTask(String login, String password){
        Map<String, String> authTask = Map.of("login", login, "password", password);

        String jsonString = writeValueAsString(authTask);

        var newTask = new TaskModel();
        newTask.type = 1;
        newTask.data = authTask;

        return newTask;
    }

    public TaskModel createNewChatTask(String type){
        var newTask = new TaskModel();
        newTask.type = 3;
        newTask.data = Map.of("type", type);

        return newTask;
    }

    public TaskModel createPromtTask(RequestsModels.PromtRequest promptRequest) {
        return createPromtTask(promptRequest.getPromt(), promptRequest.getTimeOutForAnswer().toString());
    }

    public TaskModel createChatTask(RequestsModels.ChatRequest chatRequest) {
        return createNewChatTask(chatRequest.getType().toString());
    }

    public TaskModel createAuthTask(RequestsModels.AuthRequest authRequest){
        return createAuthTask(authRequest.getLogin(), authRequest.getPassword());
    }
}

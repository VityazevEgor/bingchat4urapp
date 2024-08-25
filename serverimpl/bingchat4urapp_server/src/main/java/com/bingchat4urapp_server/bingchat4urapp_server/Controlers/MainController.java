package com.bingchat4urapp_server.bingchat4urapp_server.Controlers;

import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bingchat4urapp_server.bingchat4urapp_server.Context;
import com.bingchat4urapp_server.bingchat4urapp_server.Models.RequestsModels;
import com.bingchat4urapp_server.bingchat4urapp_server.Models.TaskModel;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api/")
public class MainController {
    
    @Autowired
    private Context _taskRepo;

    @Autowired
    private Utils _utils;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(MainController.class);

    @PostMapping("/auth")
    public Integer CreateAuthTask(@Valid @RequestBody RequestsModels.AuthRequest authRequest, BindingResult bindingResult){

        if (bindingResult.hasErrors()){
            logger.warn("Didn't pass validation in auth task");
            return null;
        }
        var model = _utils.createAuthTask(authRequest);
        _taskRepo.save(model);
        return model.id;
    }

    @PostMapping("/sendpromt")
    public Integer CreatPromtTask(@Valid @RequestBody RequestsModels.PromtRequest promptRequest, BindingResult bindingResult){

        if (bindingResult.hasErrors()){
            logger.warn("Didn't pass validation in prompt task");
            printValidationError(bindingResult);
            return null;
        }
        var model = _utils.createPromtTask(promptRequest);
        _taskRepo.save(model);
        return model.id;
    }

    @PostMapping("/createchat")
    public Integer CreatChat(@Valid @RequestBody RequestsModels.ChatRequest chatRequest, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            logger.warn("Didn't pass validation in chat task");
            return null;
        }
        var model = _utils.createChatTask(chatRequest);
        _taskRepo.save(model);
        return model.id;
    }

    private void printValidationError(BindingResult result){
        for (var error : result.getFieldErrors()){
            logger.warn(error.getField() + " " + error.getDefaultMessage());
        }
    }

    @RequestMapping(value = "get/{id}", method = RequestMethod.GET)
    public TaskModel GetTask(@PathVariable Integer id){
        Optional<TaskModel> found = _taskRepo.findById(id);
        if (found.isPresent()){
            return found.get();
        }
        else{
            return null;
        }
    }

    @GetMapping("/exit")
    public String exitTask() {
        var model = new TaskModel();
        model.type = 0;
        _taskRepo.save(model);
        return "Server will be down in few seconds";
    }
    
}

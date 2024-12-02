package com.bingchat4urapp_server.bingchat4urapp_server.Controlers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bingchat4urapp_server.bingchat4urapp_server.Context;
import com.bingchat4urapp_server.bingchat4urapp_server.BgTasks.CommandsExecutor;
import com.bingchat4urapp_server.bingchat4urapp_server.Models.RequestsModels;
import com.bingchat4urapp_server.bingchat4urapp_server.Models.TaskModel;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api/")
public class MainController {
    
    @Autowired
    private Context taskRepo;

    @Autowired
    private Utils utils;

    @Autowired
    private CommandsExecutor executor;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(MainController.class);

    private List<String> extractErrorMessages(List<ObjectError> errors) {
        return errors.stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());
    }

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthTask(@Valid @RequestBody RequestsModels.AuthRequest authRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.warn("Didn't pass validation in auth task");
            return ResponseEntity.badRequest()
                    .body("Validation failed: " + extractErrorMessages(bindingResult.getAllErrors()));
        }
        try {
            var model = utils.createAuthTask(authRequest);
            taskRepo.save(model);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(model.id);
        } catch (Exception e) {
            logger.error("Error while creating auth task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create auth task");
        }
    }

    @PostMapping("/useDuckDuck")
    public ResponseEntity<?> useDuckDuck(@Valid @RequestBody RequestsModels.SwitchAIRequest switchAIRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.warn("Validation failed in switch AI task");
            return ResponseEntity.badRequest()
                    .body("Validation failed: " + extractErrorMessages(bindingResult.getAllErrors()));
        }

        try {
            executor.setUseDuckDuck(switchAIRequest.getValue());
            return ResponseEntity.ok("DuckDuck switch successfully updated");
        } catch (Exception e) {
            logger.error("Error while updating DuckDuck switch", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update DuckDuck switch");
        }
    }


    @PostMapping("/sendpromt")
    public ResponseEntity<?> createPromptTask(@Valid @RequestBody RequestsModels.PromtRequest promptRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.warn("Didn't pass validation in prompt task");
            List<String> errorMessages = extractErrorMessages(bindingResult.getAllErrors());
            return ResponseEntity.badRequest()
                    .body("Validation failed: " + errorMessages);
        }
        try {
            var model = utils.createPromtTask(promptRequest);
            taskRepo.save(model);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(model.id);
        } catch (Exception e) {
            logger.error("Error while creating prompt task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create prompt task");
        }
    }

    @PostMapping("/createchat")
    public ResponseEntity<?> createChat(@Valid @RequestBody RequestsModels.ChatRequest chatRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.warn("Didn't pass validation in chat task");
            List<String> errorMessages = extractErrorMessages(bindingResult.getAllErrors());
            return ResponseEntity.badRequest()
                    .body("Validation failed: " + errorMessages);
        }
        try {
            var model = utils.createChatTask(chatRequest);
            taskRepo.save(model);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(model.id);
        } catch (Exception e) {
            logger.error("Error while creating chat task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create chat task");
        }
    }

    @RequestMapping(value = "get/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> GetTask(@PathVariable Integer id) {
        try {
            Optional<TaskModel> found = taskRepo.findById(id);
            if (found.isPresent()) {
                return ResponseEntity.ok(found.get());
            } else {
                logger.warn("Task with ID " + id + " not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Task with ID " + id + " not found.");
            }
        } catch (Exception e) {
            logger.error("Error occurred while retrieving task with ID " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving the task.");
        }
    }


    @GetMapping("/exit")
    public String exitTask() {
        var model = new TaskModel();
        model.type = 0;
        taskRepo.save(model);
        return "Server will be down in few seconds";
    }
    
}

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

import com.bingchat4urapp_server.bingchat4urapp_server.BgTasks.CommandsExecutor;
import com.bingchat4urapp_server.bingchat4urapp_server.Models.TaskRepo;
import com.bingchat4urapp_server.bingchat4urapp_server.Models.RequestsModels;
import com.bingchat4urapp_server.bingchat4urapp_server.Models.TaskModel;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api/")
public class MainController {
    
    @Autowired
    private TaskRepo taskRepo;

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

    private Optional<ResponseEntity<?>> processBindingResult(BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            logger.warn("Didn't pass validation in auth task");
            var response = ResponseEntity.badRequest().body("Validation failed: " + extractErrorMessages(bindingResult.getAllErrors()));
            return Optional.of(response);
        }
        else{
            return Optional.empty();
        }
    }

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthTask(@Valid @RequestBody RequestsModels.AuthRequest authRequest, BindingResult bindingResult) {
        return processBindingResult(bindingResult).orElseGet(() ->{
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
        });
    }
    
    @PostMapping("/sendpromt")
    public ResponseEntity<?> createPromptTask(@Valid @RequestBody RequestsModels.PromtRequest promptRequest, BindingResult bindingResult) {
        return processBindingResult(bindingResult).orElseGet(()->{
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
        });
    }

    @PostMapping("/createchat")
    public ResponseEntity<?> createChat() {
        try {
            var model = utils.createNewChatTask();
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
        return taskRepo.findById(id)
            .map(task-> ResponseEntity.ok(task))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping("/getProvidersInfo")
    public ResponseEntity<?> getAllProviders(){
        return ResponseEntity.ok(executor.getWrapper().getLlms());
    }

    @PostMapping("/setPreferedProvider")
    public ResponseEntity<?> setPreferedProvider(@Valid @RequestBody RequestsModels.SetPreferedRequest setPreferedRequest, BindingResult bindingResult) {
        return processBindingResult(bindingResult).orElseGet(()->{
            try {
                executor.getWrapper().setPreferredProvider(setPreferedRequest.getProvider());
                return ResponseEntity.ok().body("Done!");
            } catch (Exception e) {
                logger.error("Error while setting prefered provider", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to set provider. Check server console for details");
            }
        });
    }

    @GetMapping("/resetProvidersState")
    public ResponseEntity<?> resetProvidersState(){
        executor.getWrapper().resetErrorStates();
        logger.info("Reset providers state has been called");
        return ResponseEntity.ok("Providers state has been reset");
    }

    @GetMapping("/getWorkingLLM")
    public ResponseEntity<?> getWorkingLLM(){
        return ResponseEntity.ok(executor.getWrapper().getWorkingLLM());
    }

    @GetMapping("/")
    public ResponseEntity<String> base(){
        return ResponseEntity.ok("I'm working!");
    }

    @GetMapping("/exit")
    public ResponseEntity<String> exitTask() {
        var model = new TaskModel();
        model.type = 0;
        taskRepo.save(model);
        return ResponseEntity.ok("Server will be down in few seconds");
    }
    
}

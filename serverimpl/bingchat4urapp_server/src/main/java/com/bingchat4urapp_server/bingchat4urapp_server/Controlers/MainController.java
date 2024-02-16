package com.bingchat4urapp_server.bingchat4urapp_server.Controlers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.Task;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bingchat4urapp_server.bingchat4urapp_server.Context;
import com.bingchat4urapp_server.bingchat4urapp_server.Models.TaskModel;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class MainController {
    
    @Autowired
    private Context _taskRepo;

    private ObjectMapper _mapper = new ObjectMapper();

    @PostMapping("/auth")
    public Integer CreateAuthTask(@RequestBody MultiValueMap<String, String> body){

        if (!body.containsKey("login") || !body.containsKey("password") || body.size()>2){
            return null;
        }

        TaskModel task = new TaskModel();
        task.Type = 1;
        try{
            task.Data = _mapper.writeValueAsString(body.toSingleValueMap());
        }
        catch (Exception e){
            System.err.println("Can't map for some reason");
            e.printStackTrace();
        }

        if (task.Data != null){
            _taskRepo.save(task);
            return task.Id;
        }
        return null;
    }

    @PostMapping("/sendpromt")
    public Integer CreatPromtTask(@RequestBody MultiValueMap<String, String> body){
        if (!body.containsKey("promt") || body.size()>1){
            return null;
        }

        TaskModel task = new TaskModel();
        task.Type = 2;
        task.Data = body.toSingleValueMap().get("promt");
        _taskRepo.save(task);

        return task.Id;
    }

    @GetMapping("/get")
    public TaskModel GetTask(@RequestParam Integer id){
        Optional<TaskModel> found = _taskRepo.findById(id);
        if (found.isPresent()){
            return found.get();
        }
        else{
            return null;
        }
    }

    @GetMapping("/")
    public String Hello(){
        return "Yes i'm working!";
    }
}

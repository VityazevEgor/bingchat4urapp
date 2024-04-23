package com.bingchat4urapp_server.bingchat4urapp_server.Controlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.bingchat4urapp_server.bingchat4urapp_server.Context;
import com.bingchat4urapp_server.bingchat4urapp_server.Models.TaskModel;

@Controller
public class GUIController {

    @Autowired
    private Context _context;

    @Autowired
    private Utils _utils;

    @GetMapping("/")
    public ModelAndView Hello(){
        var model = new ModelAndView("main");
        model.addObject("name", "Test");
        return model;
    }

    @PostMapping("/sendgui")
    public Boolean sendgui(@RequestParam String promt){
        var newTask = _utils.createPromtTask(promt, "120");
        _context.save(newTask);
        return true;
    }
}

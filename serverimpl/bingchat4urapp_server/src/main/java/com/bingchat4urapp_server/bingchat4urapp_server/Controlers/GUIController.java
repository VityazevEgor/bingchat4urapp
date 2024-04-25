package com.bingchat4urapp_server.bingchat4urapp_server.Controlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.bingchat4urapp_server.bingchat4urapp_server.Context;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class GUIController {

    @Autowired
    private Context _context;

    @Autowired
    private Utils _utils;

    @GetMapping("/")
    public ModelAndView main(){
        var model = new ModelAndView("main");
        model.addObject("name", "Test");
        return model;
    }

    @GetMapping("/authgui")
    public ModelAndView authGui() {
        return new ModelAndView("auth");
    }

    @PostMapping("/sendgui")
    public String sendGui(@RequestParam String promt){
        var newTask = _utils.createPromtTask(promt, "120");
        _context.save(newTask);
        return "redirect:/waittask/"+newTask.id;
    }

    @PostMapping("/sendauthgui")
    public String postMethodName(@RequestParam String login, @RequestParam String password) {
        var newTask = _utils.createAuthTask(login, password);
        _context.save(newTask);
        return "redirect:/waittask/"+newTask.id;
    }
    

    @RequestMapping(value = "/waittask/{taskid}", method = RequestMethod.GET)
    public ModelAndView waitTask(@PathVariable("taskid") Integer taskId) {
        var taskModel = _context.findById(taskId).orElse(null);
        var view = new ModelAndView("taskwait");
        view.addObject("currentTask", taskModel);
        return view;
    }
        
}

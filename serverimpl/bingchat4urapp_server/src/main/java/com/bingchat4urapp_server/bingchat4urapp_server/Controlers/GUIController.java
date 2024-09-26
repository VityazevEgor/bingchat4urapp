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
import com.bingchat4urapp_server.bingchat4urapp_server.Models.TaskModel;



@Controller
public class GUIController {

    @Autowired
    private Context _context;

    @Autowired
    private Utils _utils;

    @GetMapping("/")
    public ModelAndView main(){
        var model = new ModelAndView("main");

        // TaskModel lastTask = _context.findLastFinishedTask();
        // if (lastTask != null && lastTask.result != null){
        //     if (lastTask.result.length() >60) {
        //         lastTask.result = lastTask.result.substring(0, 60);
        //     }
        //     model.addObject("lastTask", lastTask);
        // }

        var promtModels = _context.findLatestFinishedPromtTasks();
        for (TaskModel currentPromt : promtModels) {
            if (currentPromt.result != null && currentPromt.result.length() >60){
                currentPromt.result = currentPromt.result.substring(0,60);
            }
        }
        model.addObject("latestPromts", promtModels);
        return model;
    }

    @GetMapping("/authgui")
    public ModelAndView authGui() {
        return new ModelAndView("auth");
    }

    @GetMapping("/newchatgui")
    public String newChatGui(){
        var newTask = _utils.createNewChatTask("3");
        _context.save(newTask); 
        return "redirect:/task/" + newTask.id;
    }

    @PostMapping("/sendgui")
    public String sendGui(@RequestParam String promt){
        var newTask = _utils.createPromtTask(promt, "120");
        _context.save(newTask);
        return "redirect:/task/" + newTask.id;
    }

    @PostMapping("/sendauthgui")
    public String postMethodName(@RequestParam String login, @RequestParam String password) {
        var newTask = _utils.createAuthTask(login, password);
        _context.save(newTask);
        return "redirect:/task/" + newTask.id;
    }
    

    @RequestMapping(value = "/task/{taskid}", method = RequestMethod.GET)
    public ModelAndView waitTask(@PathVariable("taskid") Integer taskId) {
        var taskModel = _context.findById(taskId).orElse(null);

        // если авторизация или создание чата прошло успешна то сразу редериктим на страницу для отправки запросов
        if ((taskModel.type == 1 || taskModel.type == 3) && !taskModel.gotError && taskModel.isFinished){
            return new ModelAndView("redirect:/");
        }

        var view = new ModelAndView("taskwait");
        view.addObject("currentTask", taskModel);

        // Добовляем массив строк для того, чтобы построчно вывести ответ
        if (!taskModel.gotError && taskModel.isFinished && taskModel.result != null){
            view.addObject("answerLines", taskModel.result.split("\n"));
        }
        return view;
    }
        
}

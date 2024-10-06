package com.bingchat4urapp_server.bingchat4urapp_server.BgTasks;

import java.nio.file.Paths;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import com.bingchat4urapp.BrowserUtils;
import com.bingchat4urapp.DuckBingChat;
import com.bingchat4urapp_server.bingchat4urapp_server.Context;
import com.bingchat4urapp_server.bingchat4urapp_server.Shared;
import com.bingchat4urapp_server.bingchat4urapp_server.Models.TaskModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CommandsExecutor {
    
    private DuckBingChat _chat;
    private Boolean _DoJob = true;
    private ObjectMapper _mapper = new ObjectMapper();
    
    @Autowired
    private Context _context;

    @PostConstruct
    public void Init(){
        if (_DoJob){
            _chat = new DuckBingChat(Shared.proxy, 1920, 1080, 10431, Shared.hideBrowserWindow);
            if (Shared.examMode){
                _chat.setExamMode(true);
                print("Exam mode is ENABLED!");
            }
            if (Shared.emulateBingErros){
                print("Emulate erros mode is ENABLED");
                _chat.setEmulateErrors(true);
            }
            print("Created BingChat object with proxy = " + Shared.proxy);
            var t = LogManager.getLogger();
            t.info("test");
        }
    }

    public void setUseDuckDuck(Boolean value){
        if (_chat != null){
            _chat.setUseDuckDuck(value);
        }
    }

    public Boolean getUseDuckDuck(){
        if (_chat != null){
            return _chat.getUseDuckDuck();
        }
        else{
            return false;
        }
    }

    // 1 - авторизация
    // 2 - вопрос
    // 3 - создание чата

    @Scheduled(fixedDelay = 1500)
    public void ProcessCommands(){
        if (!_DoJob) return;

        TaskModel task = _context.findFirstUnfinishedTask();
        if (task != null) {
            if (task.type == 0){
                _chat.exit();
                return;
            }
            Map<String, String> data = convertJsonToMap(task);
            if (data != null) {
                switch (task.type) {
                    case 1:
                        processAuthTask(task, data);
                        break;
                    case 2:
                        processPromptTask(task, data);
                        break;
                    case 3:
                        processСreateChatTask(task, data);
                        break;

                    default:
                        GotError(task, "Got task with unknow type");
                        break;
                }
            }
            else{
                GotError(task, "Could not get data from task (Maybe is null or in wrong JSON format)");
            }
        }
    }

    private void processСreateChatTask(TaskModel task, Map<String, String> data){
        print("Got create chat task");
        Boolean result = _chat.createNewChat(Integer.parseInt(data.get("type")));
        task.isFinished = true;
        task.gotError = !result;
        _context.save(task);
        print("Finsihed create chat task");
    }

    private void processAuthTask(TaskModel task, Map<String, String> data) {
        print("Got auth task");
        Boolean result = _chat.auth(data.get("login"), data.get("password"));
        task.isFinished = true;
        task.gotError = !result;
        _context.save(task);
        print("Finished auth task");
    }

    private void processPromptTask(TaskModel task, Map<String, String> data) {
        print("Got promt task");
        String prompt = data.get("promt");
        Long timeOutForAnswer = Long.parseLong(data.get("timeOutForAnswer"));
        try{
            String result = _chat.askBing(prompt, timeOutForAnswer);
            task.isFinished = true;
            task.gotError = result == null;
            task.result = result;
        }catch (Exception ex){
            print("Got unexpected error in promt task");
            ex.printStackTrace();
            task.isFinished = true;
            task.gotError = true;
        }

        try{
            var imageResult = _chat._browser.takeScreenshot();
            String imageName = BrowserUtils.generateRandomFileName(15)+".png";
            ImageIO.write(imageResult, "png", Paths.get(Shared.imagesPath.toString(), imageName).toFile());
            task.imageResult = imageName;
        }
        catch (Exception e)  {
            print("Can't save image of answer!");
            e.printStackTrace();
        }

        _context.save(task);
        print("Finished promt task");
    }

    private Map<String, String> convertJsonToMap(TaskModel task) {
        try {
            return _mapper.readValue(task.data, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            GotError(task, "Can't convert json to Map");
            e.printStackTrace();
            return null;
        }
    }

    private void GotError(TaskModel task, String reason){
        task.isFinished = true;
        task.gotError = true;
        task.result = reason;
        _context.save(task);
        print(reason);
    }

    private void print(String text){
        String ANSI_YELLOW = "\u001B[33m";
        String ANSI_RESET = "\u001B[0m";
        System.out.println(ANSI_YELLOW + "[CommandsExecutor] " + text + ANSI_RESET);
    }    
}

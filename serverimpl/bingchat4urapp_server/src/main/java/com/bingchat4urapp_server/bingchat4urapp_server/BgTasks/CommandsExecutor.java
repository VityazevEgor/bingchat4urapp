package com.bingchat4urapp_server.bingchat4urapp_server.BgTasks;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.bingchat4urapp_server.bingchat4urapp_server.Context;
import com.bingchat4urapp_server.bingchat4urapp_server.Shared;
import com.bingchat4urapp_server.bingchat4urapp_server.Models.TaskModel;
import com.vityazev_egor.Wrapper;
import com.vityazev_egor.Wrapper.LLMproviders;
import com.vityazev_egor.Wrapper.WrapperMode;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;

@Component
public class CommandsExecutor {
    @Getter
    private Wrapper wrapper;
    private Boolean doJob = false;
    private final Logger logger = LoggerFactory.getLogger(CommandsExecutor.class);
    private final AtomicBoolean isTaskRunning = new AtomicBoolean(false);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    @Autowired
    private Context context;

    public CommandsExecutor(){
        try{
            wrapper = new Wrapper(Shared.proxy, LLMproviders.Copilot, Shared.examMode ? WrapperMode.ExamMode : WrapperMode.Normal);
            logger.info("Created Wrapper object with proxy = " + Shared.proxy);
            //chat.emulateError = true;
        } catch (Exception e){
            logger.error("Could not create Wrapper object", e);
            System.exit(1);
        }
    }

    public void setUseDuckDuck(Boolean value){
        if (wrapper != null){
            //chat.setUseDuckDuck(value);
            // TODO implement this
        }
    }

    public Boolean getUseDuckDuck(){
        if (wrapper != null){
            //return chat.getUseDuckDuck();
            // TODO implement this
            return false;
        }
        else{
            return false;
        }
    }

    // 1 - авторизация
    // 2 - вопрос
    // 3 - создание чата
    public void commandsProcessor(){
        if (!doJob) return;
        try{
        TaskModel task = context.findFirstUnfinishedTask();
        if (task == null) {
            logger.info("There is nothing to do");
            return;
        }
        else{
            logger.info("I got task to do with this data = " + task.data);
        }
        if (task.type == 0){
            wrapper.exit();
            System.exit(0);
            return;
        }
        if (task.data.size() > 0) {
            switch (task.type) {
                case 1:
                    processAuthTask(task);
                    break;
                case 2:
                    processPromptTask(task);
                    break;
                case 3:
                    processСreateChatTask(task);
                    break;
                default:
                    gotError(task, "Got task with unknow type");
                    break;
            }
        }
        else{
            System.out.print(task.data);
            gotError(task, "Could not get data from task (Maybe is null or in wrong JSON format)");
        }
        } catch (Exception ex){
            logger.error("Error in main loop", ex);
        }
    }

    @PostConstruct
    public void startTask(){
        isTaskRunning.set(true);
        executorService.submit(()->{
            while (isTaskRunning.get()){
                commandsProcessor();
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {}
            }
        });
    }

    @PreDestroy
    public void stopTask(){
        isTaskRunning.set(false);
    }

    private void processСreateChatTask(TaskModel task){
        logger.info("Got create chat task");
        var workingLLM = wrapper.getWorkingLLM();
        if (workingLLM.isPresent()){
            Boolean result = wrapper.createChat(workingLLM.get().getProvider());
            task.isFinished = true;
            task.gotError = !result;
            context.save(task);
        }
        else{
            gotError(task, "No working LLM");
        }
        logger.info("Finsihed create chat task");
    }

    private void processAuthTask(TaskModel task) {
        logger.info("Got auth task");
        Boolean result = wrapper.auth(LLMproviders.Copilot, task.data.get("login"), task.data.get("password"));
        task.isFinished = true;
        task.gotError = !result;
        context.save(task);
        logger.info("Finished auth task");
    }

    private void processPromptTask(TaskModel task) {
        logger.info("Got promt task");
        String prompt = task.data.get("promt");
        Integer timeOutForAnswer = Integer.parseInt(task.data.get("timeOutForAnswer"));
        try{
            // var chatAnswer = chat.askLLM(prompt, timeOutForAnswer);
            var chatAnswer = wrapper.askLLM(prompt, timeOutForAnswer);
            task.isFinished = true;
            task.gotError = !chatAnswer.getCleanAnswer().isPresent();
            task.result = chatAnswer.getCleanAnswer().isPresent() ? chatAnswer.getCleanAnswer().get() : null;
            task.htmlResult = chatAnswer.getHtmlAnswer().isPresent() ? chatAnswer.getHtmlAnswer().get() : null;

            // save answer image
            if (chatAnswer.getAnswerImage().isPresent()){
                String imageName = UUID.randomUUID().toString()+".png";
                ImageIO.write(chatAnswer.getAnswerImage().get(), "png", Paths.get(Shared.imagesPath.toString(), imageName).toFile());
                task.imageResult = imageName;
            }
        } catch (IOException ex){
            logger.error("Can't save image of answer, but i ignore it, i still will save answer from AI", ex);
        }
        catch (Exception ex){
            logger.error("Got unexpected error in promt task", ex);
            task.isFinished = true;
            task.gotError = true;
        }
        context.save(task);
        logger.info("Finished promt task");
    }

    // private Map<String, String> convertJsonToMap(TaskModel task) {
    //     try {
    //         return mapper.readValue(task.data, new TypeReference<Map<String, String>>() {});
    //     } catch (Exception e) {
    //         gotError(task, "Can't convert json to Map");
    //         e.printStackTrace();
    //         return null;
    //     }
    // }

    private void gotError(TaskModel task, String reason){
        task.isFinished = true;
        task.gotError = true;
        task.result = reason;
        context.save(task);
        logger.error(reason);
    } 
}

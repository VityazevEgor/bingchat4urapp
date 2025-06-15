package com.bingchat4urapp_server.bingchat4urapp_server.BgTasks;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bingchat4urapp_server.bingchat4urapp_server.Shared;
import com.bingchat4urapp_server.bingchat4urapp_server.Models.TaskRepo;
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
    private Boolean doJob = true;
    private final Logger logger = LoggerFactory.getLogger(CommandsExecutor.class);
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    
    @Autowired
    private TaskRepo context;

    public CommandsExecutor(){
        try{
            wrapper = new Wrapper(Shared.proxy, LLMproviders.Copilot, Shared.examMode ? WrapperMode.ExamMode : WrapperMode.Normal);
            logger.info("Created Wrapper object with proxy = {}", Shared.proxy);
            if (Shared.emulateBingErros){
                logger.warn("emulateErrors mode is enabled. Copilot will always return errors");
                Wrapper.emulateError = true;
            }
            if (Shared.examMode)
                logger.warn("examMode mode is enabled. Server will try to get answer from other LLM provider is current one failed!");
        } catch (Exception e){
            logger.error("Could not create Wrapper object", e);
            System.exit(1);
        }
    }

    // 0 - завершение работы
    // 1 - авторизация
    // 2 - вопрос
    // 3 - создание чата
    private class commandsProcessor implements Runnable {

        @Override
        public void run() {
            if (!doJob) return;
            TaskModel task = context.findFirstUnfinishedTask();
            if (task == null)
                return;
            logger.info("I got task to do with this data = {}", task.data);
            try{
                if (task.type == 0){
                    System.exit(0);
                    return;
                }
                if (task.data.isEmpty() && task.type != 3){
                    gotError(task, "There is not data for task");
                    return;
                }
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
            } catch (Exception ex){
                logger.error("Error in main loop", ex);
                gotError(task, "Internal error");
            }
        }
            
    }

    @PostConstruct
    public void startTask(){
        scheduledExecutorService.scheduleWithFixedDelay(
            new commandsProcessor(), 
            2, 
            2, 
            TimeUnit.SECONDS
        );
    }

    @PreDestroy
    public void stopTask(){
        scheduledExecutorService.shutdown();
        wrapper.exit();
    }

    private void processСreateChatTask(TaskModel task){
        logger.info("Got create chat task");
        wrapper.getWorkingLLM().ifPresentOrElse(workingLLM -> {
            Boolean result = wrapper.createChat(workingLLM.getProvider());
            task.isFinished = true;
            task.gotError = !result;
            context.save(task);
        }, ()-> gotError(task, "No working LLM"));
        logger.info("Finished create chat task");
    }

    private void processAuthTask(TaskModel task) {
        logger.info("Got auth task");
        Boolean result = false;
        try{
            LLMproviders provider = LLMproviders.valueOf(task.data.get("provider"));
            result = wrapper.auth(provider);
        }
        catch (Exception ex){
            logger.error("Got error in auth task", ex);
        }
        task.isFinished = true;
        task.gotError = !result;
        context.save(task);
        logger.info("Finished auth task");
    }

    private void processPromptTask(TaskModel task) {
        logger.info("Got prompt task");
        String prompt = task.data.get("prompt");
        Integer timeOutForAnswer = Integer.parseInt(task.data.get("timeOutForAnswer"));
        try{
            // var chatAnswer = chat.askLLM(prompt, timeOutForAnswer);
            var chatAnswer = wrapper.askLLM(prompt, timeOutForAnswer);
            task.isFinished = true;
            task.gotError = chatAnswer.getCleanAnswer().isEmpty();
            task.result = chatAnswer.getCleanAnswer().isPresent() ? chatAnswer.getCleanAnswer().get() : null;
            task.htmlResult = chatAnswer.getHtmlAnswer().isPresent() ? chatAnswer.getHtmlAnswer().get() : null;

            // save answer image
            if (chatAnswer.getAnswerImage().isPresent()){
                String imageName = UUID.randomUUID() +".png";
                ImageIO.write(chatAnswer.getAnswerImage().get(), "png", Paths.get(Shared.imagesPath.toString(), imageName).toFile());
                task.imageResult = imageName;
            }
        } catch (IOException ex){
            logger.error("Can't save image of answer, but i ignore it, i still will save answer from AI", ex);
        }
        catch (Exception ex){
            logger.error("Got unexpected error in prompt task", ex);
            task.isFinished = true;
            task.gotError = true;
        }
        context.save(task);
        logger.info("Finished prompt task");
    }

    private void gotError(TaskModel task, String reason){
        task.isFinished = true;
        task.gotError = true;
        task.result = reason;
        context.save(task);
        logger.error(reason);
    } 
}

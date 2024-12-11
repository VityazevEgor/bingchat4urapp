package com.bingchat4urapp_server.bingchat4urapp_server.BgTasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.bingchat4urapp_server.bingchat4urapp_server.*;
import com.bingchat4urapp_server.bingchat4urapp_server.Models.TaskModel;

@Component
public class ForTesting implements CommandLineRunner{

    private final Boolean isDebug = true;

    @Autowired
    private Context context;

    @Override
    public void run(String... args) throws Exception {
        if (isDebug){
            for (int i=0; i<10; i++){
                TaskModel model = new TaskModel();
                model.result = String.format("Task #%s", String.valueOf(i));
                model.gotError = false;
                model.isFinished = true;
                model.type = 2;

                context.save(model);
            }
        }
    }
    
}

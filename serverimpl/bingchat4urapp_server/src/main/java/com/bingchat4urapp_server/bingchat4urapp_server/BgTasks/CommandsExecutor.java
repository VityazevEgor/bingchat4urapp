package com.bingchat4urapp_server.bingchat4urapp_server.BgTasks;

import org.springframework.stereotype.Component;

import com.bingchat4urapp.BingChat;

import jakarta.annotation.PostConstruct;

@Component
public class CommandsExecutor {
    
    private BingChat _chat;
    private Boolean _DoJob = false;

    @PostConstruct
    public void Init(){
        if (_DoJob){
            String proxy = "127.0.0.1:1080";
            if (System.getProperty("os.name").contains("Windows")){
                proxy = "127.0.0.1:8521";
            }
            _chat = new BingChat(proxy, 1280, 720, 10431);
            print("Created BingChat object");
        }
    }

    private void print(String text){
        String ANSI_YELLOW = "\u001B[33m";
        String ANSI_RESET = "\u001B[0m";
        System.out.println(ANSI_YELLOW + "[CommandsExecutor] " + text + ANSI_RESET);
    }    
}

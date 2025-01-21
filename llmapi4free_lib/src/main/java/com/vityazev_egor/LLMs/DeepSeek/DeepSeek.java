package com.vityazev_egor.LLMs.DeepSeek;

import com.vityazev_egor.NoDriver;
import com.vityazev_egor.iChat;
import com.vityazev_egor.LLMs.DeepSeek.Modules.AskModule;
import com.vityazev_egor.LLMs.DeepSeek.Modules.AuthModule;
import com.vityazev_egor.Models.ChatAnswer;

public class DeepSeek implements iChat{
    private final NoDriver driver;

    public DeepSeek(NoDriver driver){
        this.driver = driver;
    }

    @Override
    public Boolean auth(String login, String password) {
        return new AuthModule(driver).auth();
    }

    @Override
    public ChatAnswer ask(String promt, Integer timeOutForAnswer) {
        return new AskModule(driver).ask(promt, timeOutForAnswer);
    }

    @Override
    public Boolean creatNewChat() {
        return new AuthModule(driver).auth();
    }

    @Override
    public String getName() {
        return "DeepSeek";
    }
    
}

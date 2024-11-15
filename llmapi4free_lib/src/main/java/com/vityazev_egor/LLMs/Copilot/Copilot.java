package com.vityazev_egor.LLMs.Copilot;

import com.vityazev_egor.NoDriver;
import com.vityazev_egor.iChat;
import com.vityazev_egor.Core.CustomLogger;
import com.vityazev_egor.LLMs.Copilot.Modules.Ask;
import com.vityazev_egor.LLMs.Copilot.Modules.Auth;
import com.vityazev_egor.Models.ChatAnswer;

public class Copilot implements iChat{

    private final NoDriver driver;
    private final CustomLogger logger;

    public Copilot(NoDriver driver){
        this.driver = driver;
        logger = new CustomLogger(getName());
    }

    @Override
    public Boolean auth(String login, String password) {
        return new Auth(driver).auth(login, password);
    }

    @Override
    public ChatAnswer ask(String promt, Integer timeOutForAnswer) {
        return new Ask(driver).askCopilot(promt, timeOutForAnswer);
    }

    @Override
    public Boolean creatNewChat() {
        return driver.getNavigation().loadUrlAndWait("https://copilot.microsoft.com/", 10);
    }

    @Override
    public String getName() {
        return "Copilot";
    }
    
}

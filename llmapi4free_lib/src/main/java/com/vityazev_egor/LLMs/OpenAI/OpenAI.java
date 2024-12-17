package com.vityazev_egor.LLMs.OpenAI;

import com.vityazev_egor.NoDriver;
import com.vityazev_egor.iChat;
import com.vityazev_egor.LLMs.OpenAI.Modules.Ask;
import com.vityazev_egor.LLMs.OpenAI.Modules.Auth;
import com.vityazev_egor.Models.ChatAnswer;

public class OpenAI implements iChat{

    public static final String url = "https://chatgpt.com/";

    private final NoDriver driver;
    public OpenAI(NoDriver driver) {
        this.driver = driver;
    }
    
    @Override
    public Boolean auth(String login, String password) {
        return new Auth(driver).auth();
    }

    @Override
    public ChatAnswer ask(String promt, Integer timeOutForAnswer) {
        return new Ask(driver).ask(promt, timeOutForAnswer);
    }

    @Override
    public Boolean creatNewChat() {
        return driver.getNavigation().loadUrlAndWait(OpenAI.url, 10);
    }

    @Override
    public String getName() {
        return "OpenAI";
    }
    
}

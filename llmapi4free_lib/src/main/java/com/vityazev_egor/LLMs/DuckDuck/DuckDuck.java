package com.vityazev_egor.LLMs.DuckDuck;

import com.vityazev_egor.NoDriver;
import com.vityazev_egor.iChat;
import com.vityazev_egor.Models.ChatAnswer;
import com.vityazev_egor.LLMs.DuckDuck.Modules.*;;

public class DuckDuck implements iChat{

    private final NoDriver driver;
    public static String answerDivClass = "";

    public DuckDuck(NoDriver driver){
        this.driver = driver;
    }
    @Override
    public Boolean auth(String login, String password) {
        return new Auth(driver).auth();
    }

    @Override
    public ChatAnswer ask(String promt, Integer timeOutForAnswer) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'ask'");
    }

    @Override
    public Boolean creatNewChat() {
        return new CreateChat(driver).create();
    }

    @Override
    public String getName() {
        return "DuckDuck";
    }
    
}

package com.vityazev_egor;

import com.vityazev_egor.Models.ChatAnswer;

public interface iChat {
    public Boolean auth(String login, String password);
    public ChatAnswer ask(String promt, Integer timeOutForAnswer);
    public Boolean creatNewChat();
    public String getName();
}

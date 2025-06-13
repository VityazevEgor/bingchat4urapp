package com.vityazev_egor;

import com.vityazev_egor.Models.ChatAnswer;

public interface iChat {
    public Boolean auth();
    public ChatAnswer ask(String prompt, Integer timeOutForAnswer);
    public Boolean createNewChat();
    public String getName();
}

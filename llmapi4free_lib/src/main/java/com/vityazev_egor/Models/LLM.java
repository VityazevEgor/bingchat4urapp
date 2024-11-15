package com.vityazev_egor.Models;

import com.vityazev_egor.iChat;

import lombok.Data;

@Data
public class LLM {
    private iChat chat;
    private Boolean gotError;
    private Boolean authDone;
    private Boolean authRequired;

    public LLM(iChat chat, Boolean authRequired){
        this.chat = chat;
        this.gotError = false;
        this.authDone = false;
        this.authRequired = authRequired;
    }
}

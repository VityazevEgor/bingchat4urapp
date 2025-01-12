package com.vityazev_egor.Models;

import com.vityazev_egor.iChat;
import com.vityazev_egor.Wrapper.LLMproviders;

import lombok.Data;

@Data
public class LLM {
    private LLMproviders provider;
    private iChat chat;
    private Boolean gotError;
    private Boolean authDone;
    private Boolean authRequired;
    private String lastAnswer = "";

    public LLM(iChat chat, Boolean authRequired, LLMproviders provider){
        this.chat = chat;
        this.gotError = false;
        this.authDone = false;
        this.authRequired = authRequired;
        this.provider = provider;
    }
}

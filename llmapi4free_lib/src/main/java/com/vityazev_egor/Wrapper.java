package com.vityazev_egor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.vityazev_egor.LLMs.Copilot.Copilot;
import com.vityazev_egor.Models.ChatAnswer;
import com.vityazev_egor.Models.LLM;

import lombok.Getter;

public class Wrapper {
    private final NoDriver driver;
    @Getter
    private final List<LLM> llms;

    public Wrapper(String socks5Proxy) throws IOException{
        this.driver = new NoDriver(socks5Proxy);
        this.driver.getXdo().calibrate();
        this.llms = Arrays.asList(
            new LLM(new Copilot(driver), true)
        );
    }

    public Boolean auth(String LLMname, String login, String password){
        return llms.stream().filter(l -> l.getChat().getName().equalsIgnoreCase(LLMname)).findFirst().map(l->{
            return l.getChat().auth(login, password);
        }).orElse(false);
    }

    public ChatAnswer askLLM(String LLMname, String promt, Integer timeOutForAnswer){
        return llms.stream().filter(l -> l.getChat().getName().equalsIgnoreCase(LLMname)).findFirst().map(l->{
            return l.getChat().ask(promt, timeOutForAnswer);
        }).orElse(null);
    }

    public void exit(){
        driver.exit();
    }
}

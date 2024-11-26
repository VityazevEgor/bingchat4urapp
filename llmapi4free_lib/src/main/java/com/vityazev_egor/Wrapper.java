package com.vityazev_egor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.vityazev_egor.LLMs.Copilot.Copilot;
import com.vityazev_egor.LLMs.DuckDuck.DuckDuck;
import com.vityazev_egor.Models.ChatAnswer;
import com.vityazev_egor.Models.LLM;

import lombok.Getter;

public class Wrapper {
    private final NoDriver driver;
    @Getter
    private final List<LLM> llms;

    public enum LLMproviders{
        Copilot,
        DuckDuck
    }

    public enum WrapperMode{
        ExamMode, // we will try to get answer from any AI if selected one fails
        Normal // we will just return empty answer
    }

    public Wrapper(String socks5Proxy) throws IOException{
        this.driver = new NoDriver(socks5Proxy);
        this.driver.getXdo().calibrate();
        this.llms = Arrays.asList(
            new LLM(new Copilot(driver), true, LLMproviders.Copilot),
            new LLM(new DuckDuck(driver),false, LLMproviders.DuckDuck)
        );
    }

    public Boolean auth(LLMproviders provider, String login, String password){
        return llms.stream().filter(l -> l.getProvider() == provider).findFirst().map(l->{
            Boolean result = l.getChat().auth(login, password);
            l.setAuthDone(result);
            return result;
        }).orElse(false);
    }

    public ChatAnswer askLLM(LLMproviders provider, String promt, Integer timeOutForAnswer){
        return llms.stream().filter(l -> l.getProvider() == provider).findFirst().map(l->{
            var answer = l.getChat().ask(promt, timeOutForAnswer);
            if (!answer.getCleanAnswer().isPresent()){
                l.setGotError(true);
            }
            return answer;
        }).orElse(new ChatAnswer());
    }

    public void exit(){
        driver.exit();
    }
}

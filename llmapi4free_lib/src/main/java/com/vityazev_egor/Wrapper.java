package com.vityazev_egor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.vityazev_egor.Core.CustomLogger;
import com.vityazev_egor.LLMs.Copilot.Copilot;
import com.vityazev_egor.LLMs.DuckDuck.DuckDuck;
import com.vityazev_egor.LLMs.OpenAI.OpenAI;
import com.vityazev_egor.Models.ChatAnswer;
import com.vityazev_egor.Models.LLM;

import lombok.Getter;
import lombok.Setter;

public class Wrapper {
    private final NoDriver driver;
    private final CustomLogger logger;
    @Getter
    private final List<LLM> llms;

    public enum LLMproviders{
        Copilot,
        DuckDuck,
        OpenAI
    }

    public enum WrapperMode{
        ExamMode, // we will try to get answer from any AI if selected one fails
        Normal // we will just return empty answer
    }

    // указываем какую ИИ будем использовать по умолчанию
    @Getter
    @Setter
    private LLMproviders preferredProvider;
    // указываем какой режим работы будет
    private final WrapperMode wrapperMode;

    public static Boolean emulateError = false;

    public Wrapper(String socks5Proxy, LLMproviders preferredProvider, WrapperMode wrapperMode) throws IOException{
        this.driver = new NoDriver(socks5Proxy);
        this.logger = new CustomLogger(Wrapper.class.getName());
        this.driver.getXdo().calibrate();
        this.llms = Arrays.asList(
            new LLM(new Copilot(driver), true, LLMproviders.Copilot),
            new LLM(new DuckDuck(driver),false, LLMproviders.DuckDuck),
            new LLM(new OpenAI(driver),true, LLMproviders.OpenAI)
        );
        this.preferredProvider = preferredProvider;
        this.wrapperMode = wrapperMode;
    }

    public Boolean auth(LLMproviders provider, String login, String password){
        return llms.stream().filter(l -> l.getProvider() == provider).findFirst().map(l->{
            Boolean result = l.getChat().auth(login, password);
            l.setAuthDone(result);
            return result;
        }).orElse(false);
    }

    public Boolean createChat(LLMproviders provider){
        return llms.stream().filter(l -> l.getProvider() == provider).findFirst().map(l->{
            Boolean result = l.getChat().creatNewChat();
            if (!result) l.setGotError(true);
            return result;
        }).orElse(false);
    }

    public ChatAnswer askLLM(LLMproviders provider, String promt, Integer timeOutForAnswer){
        return llms.stream().filter(l -> l.getProvider() == provider).findFirst().map(llm->{
            return askLLM(llm, promt, timeOutForAnswer);
        }).orElse(new ChatAnswer());
    }

    private ChatAnswer askLLM(LLM llm, String promt, Integer timeOutForAnswer){
        promt = promt.replaceAll("[\r\n]+", " ");
        var answer = llm.getChat().ask(promt, timeOutForAnswer);
        if (!answer.getCleanAnswer().isPresent()){
            llm.setGotError(true);
        }
        // if current answer is equals to previous answer then we can say that something went wrong
        if (answer.getCleanAnswer().isPresent() && answer.getCleanAnswer().get().equals(llm.getLastAnswer())){
            llm.setGotError(true);
            logger.error(llm.getChat().getName() + " returned the same answer as last answer", null);
            return new ChatAnswer();
        }
        answer.getCleanAnswer().ifPresent(llm::setLastAnswer);
        return answer;
    }

    public ChatAnswer askLLM(String promt, Integer timeOutForAnswer){
        switch (wrapperMode) {
            case ExamMode:
                for (int i=0; i<2; i++){
                    var workingLLM = getWorkingLLM();
                    if (!workingLLM.isPresent()) {
                        logger.error("There is no working providers avaible", null);
                        return new ChatAnswer();
                    }
                    ChatAnswer answer = askLLM(workingLLM.get(), promt, timeOutForAnswer);
                    if (!answer.getCleanAnswer().isPresent()) {
                        logger.error("LLM " + workingLLM.get().getProvider().name() + " didn't answer", null);
                        continue;
                    }
                    return answer;
                }
        
            default:
                return getWorkingLLM().map(llm ->{
                    return askLLM(llm, promt, timeOutForAnswer);
                }).orElse(new ChatAnswer());
        }
    }

    public Optional<LLM> getWorkingLLM(){
        // получаем все ИИшки, у которых требуется авторизация и она пройдена, или авторизация не требуется. И у которых не было ошибок
        var workingLLMs = llms.stream().filter(llm -> 
            ((llm.getAuthRequired() && llm.getAuthDone()) || (!llm.getAuthRequired()))
            && !llm.getGotError()
        ).toList();

        if (workingLLMs.isEmpty()){
            logger.error("There is no working LLM", null);
            return Optional.empty();
        }
        
        return workingLLMs.stream().filter(llm -> llm.getProvider() == preferredProvider).findFirst().map(llm -> {
            return Optional.ofNullable(llm);
        }).orElse(Optional.ofNullable(workingLLMs.get(0)));

    }

    public void resetErrorStates(){
        llms.forEach(llm -> llm.setGotError(false));
    }

    public void exit(){
        driver.exit();
    }
}

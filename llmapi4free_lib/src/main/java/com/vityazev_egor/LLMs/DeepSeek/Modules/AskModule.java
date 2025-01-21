package com.vityazev_egor.LLMs.DeepSeek.Modules;

import java.util.Optional;

import com.vityazev_egor.NoDriver;
import com.vityazev_egor.Core.CustomLogger;
import com.vityazev_egor.Core.WebElements.By;
import com.vityazev_egor.Core.WebElements.WebElement;
import com.vityazev_egor.LLMs.Shared;
import com.vityazev_egor.Models.ChatAnswer;

public class AskModule {
    private final NoDriver driver;
    private final CustomLogger logger = new CustomLogger(AskModule.class.getName());

    public AskModule(NoDriver driver) {
        this.driver = driver;
    }

    public ChatAnswer ask(String promt, Integer timeOutSeconds){
        Boolean isChatOppened = driver.getTitle().map(title -> title.contains("DeepSeek")).orElse(false);
        if (!isChatOppened){
            if (!new AuthModule(driver).auth()){
                return new ChatAnswer();
            }
        }

        if (sendPromt(promt) && Shared.waitForAnswer(driver, timeOutSeconds, 5000)){        
            return new ChatAnswer(
                getTextAnswer(), 
                getHtmlAnswer(), 
                driver.getMisc().captureScreenshot()
            );
        }
        else{
            logger.error("Can't get answer from DeepSeek");
            return new ChatAnswer();
        }
    }

    private Boolean sendPromt(String promt) {
        var promtInput = driver.findElement(By.id("chat-input"));
        var sendButton = driver.findElement(By.cssSelector("div[role='button'][aria-disabled='false']"));
        try{
            promtInput.waitToAppear(5, 100);
            driver.getInput().insertText(promtInput, promt);
            sendButton.waitToAppear(5, 100);
            driver.getInput().emulateClick(sendButton);
            return true;
        } catch (Exception e){
            logger.error("Can't send promt to DeepSeek", e);
            return false;
        }
    }

    private Optional<WebElement> findLastAnswerDiv(){
        var answerDivs = driver.findElements(By.cssSelector("div.ds-markdown.ds-markdown--block"));
        if (answerDivs.isEmpty()) {
            logger.error("Can't find any answer div");
            return Optional.empty();
        }
        return Optional.of(answerDivs.get(answerDivs.size() - 1));
    }

    private Optional<String> getTextAnswer(){
        return findLastAnswerDiv().map(WebElement::getText).orElse(Optional.empty());
    }
    private Optional<String> getHtmlAnswer(){
        return findLastAnswerDiv().map(WebElement::getHTMLContent).orElse(Optional.empty());
    }
}

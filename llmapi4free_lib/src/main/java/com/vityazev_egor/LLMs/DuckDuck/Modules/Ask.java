package com.vityazev_egor.LLMs.DuckDuck.Modules;

import java.util.List;
import java.util.Optional;

import com.vityazev_egor.NoDriver;
import com.vityazev_egor.Core.CustomLogger;
import com.vityazev_egor.Core.WebElements.By;
import com.vityazev_egor.Core.WebElements.WebElement;
import com.vityazev_egor.LLMs.Shared;
import com.vityazev_egor.LLMs.DuckDuck.DuckDuck;
import com.vityazev_egor.Models.ChatAnswer;

public class Ask {
    private final NoDriver driver;
    private final CustomLogger logger;

    public Ask(NoDriver driver){
        this.driver = driver;
        logger = new CustomLogger(Ask.class.getName());
    }

    public ChatAnswer ask(String promt, Integer timeOutAnswer){
        // We need to check if we are on Copilot page before asking LLM
        Boolean isChatOpened = driver.getTitle().map(titel -> titel.contains("AI Chat")).orElse(false);
        if (!isChatOpened){
            logger.warning("Not on Duck Duck page");
            if (!new Auth(driver).auth() || !new CreateChat(driver).create()){
                return new ChatAnswer();
            }
        }

        if (sendPromt(promt) && Shared.waitForAnswer(driver, timeOutAnswer, 2000)){
            return new ChatAnswer(
                getTextAnswer(), 
                getHtmlAnswer(), 
                driver.getMisc().captureScreenshot()
            );
        }
        else{
            return new ChatAnswer();
        }
    }

    private Boolean sendPromt(String promt){
        var textArea = driver.findElement(By.name("user-prompt"));
        var sendButton = driver.findElement(By.cssSelector("button[type='submit'][aria-label='Отправить'], button[type='submit'][aria-label='Send']"));

        if (!Shared.waitForElements(false, textArea, sendButton)){
            logger.error("Can't find promt text area or send button!", null);
            return false;
        }

        driver.getInput().insertText(textArea, promt);
        com.vityazev_egor.Core.Shared.sleep(1000);
        
        driver.getInput().emulateClick(sendButton);
        return true;
    }

    private List<WebElement> getAnswerDivs(){
        return driver.findElements(By.cssSelector("." + DuckDuck.answerDivClass));
    }

    private Optional<WebElement> getLastAnswerElement(){
        var answerDivs = getAnswerDivs();
        if (answerDivs.isEmpty()){
            logger.error("Could not find answers divs", null);
            return Optional.empty();
        }

        return Optional.of(answerDivs.get(answerDivs.size() - 1));
    }

    private Optional<String> getTextAnswer(){
        return getLastAnswerElement().map(element -> element.getText()).orElse(Optional.empty());
    }

    private Optional<String> getHtmlAnswer(){
        return getLastAnswerElement().map(element -> element.getHTMLContent()).orElse(Optional.empty());
    }
}

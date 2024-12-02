package com.vityazev_egor.LLMs.DuckDuck.Modules;

import java.util.List;
import java.util.Optional;

import com.vityazev_egor.NoDriver;
import com.vityazev_egor.Core.CustomLogger;
import com.vityazev_egor.Core.WaitTask;
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
        if (enterPromt(promt) && waitForAnswer(timeOutAnswer)){
            return new ChatAnswer(getTextAnswer(), getHtmlAnswer(), driver.getMisc().captureScreenshot());
        }
        else{
            return new ChatAnswer();
        }
    }

    private Boolean enterPromt(String promt){
        var textArea = driver.findElement(By.name("user-prompt"));
        var sendButton = driver.findElement(By.cssSelector("button[type='submit'][aria-label='Отправить'], button[type='submit'][aria-label='Send']"));

        if (!Shared.waitForElements(false, textArea, sendButton)){
            logger.error("Can't find promt text area or send button!", null);
            return false;
        }

        driver.getInput().enterText(textArea, promt);
        driver.getInput().emulateClick(sendButton);
        return true;
    }

    private List<WebElement> getAnswerDivs(){
        return driver.findElements(By.cssSelector("." + DuckDuck.answerDivClass));
    }

    private Optional<String> getTextAnswer(){
        var answerDivs = getAnswerDivs();
        if (answerDivs.isEmpty()){
            logger.error("Could not find answers divs", null);
            return Optional.empty();
        }

        return answerDivs.get(answerDivs.size() - 1).getText();
    }

    private Optional<String> getHtmlAnswer(){
        var answerDivs = getAnswerDivs();
        if (answerDivs.isEmpty()){
            logger.error("Could not find answers divs", null);
            return Optional.empty();
        }

        return answerDivs.get(answerDivs.size() - 1).getHTMLContent();
    }

    public Boolean waitForAnswer(Integer timeOutForAnswer){
        var waitTask = new WaitTask() {
            private String html = driver.getHtml().map(result -> {return result;}).orElse("");

            @Override
            public Boolean condition() {
                // если текущий штмл равен предыдущему то возвращаем да (копайлот перестал печатать)
                return driver.getHtml().map(currentHtml ->{
                    if (currentHtml.equals(html)){
                        return true;
                    }
                    else{
                        html = currentHtml;
                        return false;
                    }
                }).orElse(false);
            }
            
        };
        com.vityazev_egor.Core.Shared.sleep(1000);
        return waitTask.execute(timeOutForAnswer, 1 * 1000);
    }
}

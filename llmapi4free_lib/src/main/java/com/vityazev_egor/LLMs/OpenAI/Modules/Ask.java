package com.vityazev_egor.LLMs.OpenAI.Modules;

import java.util.Optional;

import com.vityazev_egor.NoDriver;
import com.vityazev_egor.Core.CustomLogger;
import com.vityazev_egor.Core.WebElements.By;
import com.vityazev_egor.Core.WebElements.WebElement;
import com.vityazev_egor.LLMs.Shared;
import com.vityazev_egor.Models.ChatAnswer;

public class Ask {
    
    private final NoDriver driver;
    private final CustomLogger logger = new CustomLogger(Ask.class.getName());

    public Ask(NoDriver driver) {
        this.driver = driver;
    }

    public ChatAnswer ask(String promt, Integer timoutAnswerSeconds){
        if (!openChatIfNotOpened()) return new ChatAnswer();
        if (sendPromt(promt) && Shared.waitForAnswer(driver, timoutAnswerSeconds, 2000)){
            return new ChatAnswer(
                getTextAnswer(), 
                getHTMLAnswer(), 
                driver.getMisc().captureScreenshot()
            );
        }
        else{
            logger.error("Can't get answer from OpenAI", null);
            return new ChatAnswer();
        }
    }

    private Boolean openChatIfNotOpened(){
        Boolean chatIsOpened = driver.getHtml().map(html->html.contains("ChatGPT")).orElse(false);
        if (chatIsOpened) return true;

        return new Auth(driver).auth();
    }

    private Boolean sendPromt(String promt){
        var input = driver.findElement(By.id("prompt-textarea"));
        if (!Shared.waitForElements(false, input)){
            logger.error("Can't find promt input", null);
            return false;
        }
        driver.getInput().emulateClick(input);
        driver.getInput().insertText(input, promt);
        com.vityazev_egor.Core.Shared.sleep(1000);

        var sendButton = driver.findElement(By.cssSelector("button[data-testid='send-button']"));
        if (sendButton.isExists()){
            driver.getInput().emulateClick(sendButton);
            return true;
        }
        else{
            logger.error("Can't find send button", null);
            return false;
        }
    }

    private Optional<WebElement> findLastAnwerBlock(){
        var elements = driver.findElements(By.cssSelector("div[data-message-author-role='assistant']"));
        if (elements.isEmpty()){
            logger.error("Can't find last answer block", null);
            return Optional.empty();
        }
        return Optional.of(elements.get(elements.size()-1));
    }

    private Optional<String> getTextAnswer(){
        return findLastAnwerBlock().map(block->block.getText()).orElse(Optional.empty());
    }

    private Optional<String> getHTMLAnswer(){
        return findLastAnwerBlock().map(block->block.getHTMLContent()).orElse(Optional.empty());
    }
}

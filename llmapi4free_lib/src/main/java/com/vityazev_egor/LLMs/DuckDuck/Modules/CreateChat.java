package com.vityazev_egor.LLMs.DuckDuck.Modules;

import com.vityazev_egor.NoDriver;
import com.vityazev_egor.Core.CustomLogger;
import com.vityazev_egor.Core.WebElements.By;
import com.vityazev_egor.LLMs.Shared;
import com.vityazev_egor.LLMs.DuckDuck.DuckDuck;

public class CreateChat {
    private final NoDriver driver;
    private final CustomLogger logger;

    public CreateChat(NoDriver driver){
        this.driver = driver;
        logger = new CustomLogger(CreateChat.class.getName());
    }

    public Boolean create(){
        if (!driver.getNavigation().loadUrlAndWait("https://duckduckgo.com/?q=DuckDuckGo&ia=chat", 10)){
            logger.error("Could not load duck duck in time", null);
            return false;
        }

        var textArea = driver.findElement(By.name("user-prompt"));
        var sendButton = driver.findElement(By.cssSelector("button[type='submit'][aria-label='Отправить'], button[type='submit'][aria-label='Send']"));

        if (!Shared.waitForElements(false, textArea, sendButton)){
            logger.error("Can't find promt text area or send button!", null);
            return false;
        }

        driver.getInput().enterText(textArea, "Напиши слово \"Lol kek\"");
        driver.getInput().emulateClick(sendButton);

        if (!new Ask(driver).waitForAnswer(20)){
            logger.error("Time out while wating for duck duck answer", null);
            return false;
        }
        return com.vityazev_egor.Core.Shared.readResource("scripts/detectDuckAnswerDiv.js").map(script ->{
            var result = driver.executeJSAndGetResult(script);
            if (result.isPresent() && !result.get().contains("not found")){
                DuckDuck.answerDivClass = result.get();
                return true;
            }
            else{
                logger.error("Can't find answer div", null);
                return false;
            }
        }).orElse(false);
    }
}

package com.vityazev_egor.LLMs.Copilot.Modules;

import com.vityazev_egor.NoDriver;
import com.vityazev_egor.Core.CustomLogger;
import com.vityazev_egor.Core.WebElements.By;
import com.vityazev_egor.LLMs.Shared;
import com.vityazev_egor.Models.ChatAnswer;

public class Ask {
    private final NoDriver driver;
    private final CustomLogger logger;

    public Ask(NoDriver driver){
        this.driver = driver;
        this.logger = new CustomLogger(Ask.class.getName());
    }

    public ChatAnswer askCopilot(String promt, Integer timeOutForAnswer){
        enterPromt(promt);
        return null;
    }

    private Boolean enterPromt(String promt){
        var userInput = driver.findElement(By.id("userInput"));
        var sendButton = driver.findElement(By.cssSelector("button[title=\"Submit message\"]"));
        var continueButton = driver.findElement(By.cssSelector("button[title=\"Continue\"]"));

        if (!Shared.waitForElements(false, userInput)){
            logger.warning("Can't find user input!");
            return false;
        }

        if (continueButton.isExists()){
            driver.getInput().emulateClick(continueButton);
        }
        driver.getInput().emulateClick(userInput);
        driver.getInput().enterText(userInput, promt);

        if (!Shared.waitForElements(false, sendButton)){
            logger.warning("Can't find send button");
            return false;
        }
        // надо немного подождать, а то просто не успевает кнопочка отобразиться
        com.vityazev_egor.Core.Shared.sleep(1000);
        driver.getInput().emulateClick(sendButton);
        return true;
    }
}

package com.vityazev_egor.LLMs.DuckDuck.Modules;

import com.vityazev_egor.NoDriver;
import com.vityazev_egor.Core.CustomLogger;
import com.vityazev_egor.Core.WebElements.By;
import com.vityazev_egor.LLMs.Shared;

// как таковой авторизации там нету, но нам надо принять все соглашения пользователя
public class Auth {
    private final NoDriver driver;
    private final CustomLogger logger;

    public Auth(NoDriver driver){
        this.driver = driver;
        logger = new CustomLogger(Auth.class.getName());
    }

    public Boolean auth(){
        if (!driver.getNavigation().loadUrlAndWait("https://duckduckgo.com/?q=DuckDuckGo&ia=chat", 10)) {
            logger.error("Could not load duck duck in time", null);
            return false;
        }

        if (Shared.waitForElements(false, driver.findElement(By.name("user-prompt")))){
            logger.info("There is not need to accept anything");
            return true;
        }

        var getStartedButton = driver.findElement(By.cssSelector("button[type='button'][tabindex='1']"));
        if (!getStartedButton.isExists()){
            logger.error("Can't find 'Get Started' button", null);
            return false;
        }

        driver.executeJS("Array.from(document.querySelectorAll('button')).find(el => el.textContent === 'Далее' || el.textContent === 'Next').click();");
        driver.executeJS("Array.from(document.querySelectorAll('button')).find(el => el.textContent === 'Принимаю условия' || el.textContent === 'I Agree').click();");

        if (Shared.waitForElements(false, driver.findElement(By.name("user-prompt")))){
            logger.info("Created duck duck chat");
            return true;
        }
        else{
            logger.error("Could not create duck duck chat", null);
            return false;
        }
    }
}

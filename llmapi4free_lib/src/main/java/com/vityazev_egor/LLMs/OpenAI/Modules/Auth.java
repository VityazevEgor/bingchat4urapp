package com.vityazev_egor.LLMs.OpenAI.Modules;

import com.vityazev_egor.NoDriver;
import com.vityazev_egor.Core.CustomLogger;
import com.vityazev_egor.Core.WebElements.By;
import com.vityazev_egor.LLMs.Shared;

public class Auth {
    private final NoDriver driver;
    private final CustomLogger logger = new CustomLogger(Auth.class.getName());

    public Auth(NoDriver driver) {
        this.driver = driver;
    }

    // you have to be logged in Google to use this method
    public Boolean auth(){
        if (!new CreateChat(driver).loadSite()){
            logger.error("Could not load site", null);
            return false;
        }
        if (isLoggedIn()) return true;

        var welcomeLoginButton = driver.findElement(By.cssSelector("button[data-testid='welcome-login-button']"));
        var loginButton = driver.findElement(By.cssSelector("button[data-testid='login-button']"));
        if (welcomeLoginButton.isExists()) {
            driver.getInput().emulateClick(welcomeLoginButton);
        }
        else if (loginButton.isExists()) {
            driver.getInput().emulateClick(loginButton);
        }
        else {
            logger.error("Could not find login button", null);
            return false;
        }
        
        // Sometimes there is cf challenge
        com.vityazev_egor.Core.Shared.sleep(2000); 
        if (!driver.getNavigation().waitFullLoad(10) || !driver.getNavigation().loadUrlAndBypassCFXDO(null, null, 20)){
            logger.error("Could not bypass CF chalenge", null);
            return false;
        }

        // wait for "google", "apple" auth buttons
        var socialButton = driver.findElement(By.className("social-btn"));
        if (!Shared.waitForElements(false, socialButton)){
            logger.error("Could not find social button", null);
            return false;
        }

        var socialButtons = driver.findElements(By.className("social-btn"));
        if (socialButtons.isEmpty()){
            logger.error("Could not find social buttons", null);
            return false;
        }
        driver.getInput().emulateClick(socialButtons.get(0));

        return isLoggedIn();
    }

    private Boolean isLoggedIn(){
        var profileButton = driver.findElement(By.cssSelector("button[data-testid='profile-button']"));
        if (!Shared.waitForElements(false, 10, profileButton)){
            logger.error("Could not find profile button", null);
            return false;
        }
        return true;
    }
}

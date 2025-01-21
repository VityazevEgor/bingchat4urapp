package com.vityazev_egor.LLMs.DeepSeek.Modules;

import com.vityazev_egor.NoDriver;
import com.vityazev_egor.Core.CustomLogger;
import com.vityazev_egor.Core.Shared;
import com.vityazev_egor.Core.WebElements.By;
import com.vityazev_egor.LLMs.Shared.ProviderException;

public class AuthModule {
    private final NoDriver driver;
    private final CustomLogger logger = new CustomLogger(AuthModule.class.getName());

    public AuthModule(NoDriver driver) {
        this.driver = driver;
    }

    public Boolean auth(){
        try{
            // open site and fing google login button
            driver.getNavigation().loadUrlAndBypassCFXDO("https://chat.deepseek.com/", 5, 20);
            if (isAuthDone()){
                return true;
            }

            var signInWithGoogleButton = driver.findElement(By.cssSelector("div.ds-button__icon"));
            signInWithGoogleButton.waitToAppear(5, 100);
            driver.getInput().emulateClick(signInWithGoogleButton);

            // bypass cf and login in by using google account
            driver.getNavigation().waitFullLoad(5);
            if (!driver.getNavigation().loadUrlAndBypassCFXDO(null, null, 20)){
                throw new ProviderException("Can't bypass cloudflare before google auth");
            }
            var selectGoogleAccountButton = driver.findElement(By.cssSelector("[data-email]"));
            selectGoogleAccountButton.waitToAppear(5, 100);
            driver.getInput().emulateClick(selectGoogleAccountButton);
            Shared.sleep(1000);
            driver.getNavigation().waitFullLoad(5);


            var declineAcceptButtons = driver.findElements(By.cssSelector("button[data-idom-class]"));
            if (declineAcceptButtons.size() == 0){
                throw new ProviderException("Can't find decline and accept buttons");
            }
            driver.getInput().emulateClick(declineAcceptButtons.get(1));
            Shared.sleep(1000);
            driver.getNavigation().waitFullLoad(5);

            return isAuthDone();
        }
        catch (ProviderException e){
            logger.error("", e);
            return false;
        }
        catch (Exception ex){
            logger.error("Unexpected error!", ex);
            return false;
        }
    }

    private Boolean isAuthDone(){
        var promtInput = driver.findElement(By.id("chat-input"));
        return com.vityazev_egor.LLMs.Shared.waitForElements(false, promtInput);
    }
}

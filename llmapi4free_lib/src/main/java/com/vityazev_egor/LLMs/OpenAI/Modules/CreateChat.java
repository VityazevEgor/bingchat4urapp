package com.vityazev_egor.LLMs.OpenAI.Modules;

import com.vityazev_egor.NoDriver;
import com.vityazev_egor.Core.CustomLogger;
import com.vityazev_egor.Core.WaitTask;
import com.vityazev_egor.Core.WebElements.By;
import com.vityazev_egor.LLMs.OpenAI.OpenAI;

public class CreateChat {
    private final NoDriver driver;
    private final CustomLogger logger = new CustomLogger(CreateChat.class.getName());

    public CreateChat(NoDriver driver){
        this.driver = driver;
    }

    // Try to bypass custom OpenAI CF page
    public Boolean loadSite(){
        driver.getNavigation().loadUrlAndWait(OpenAI.url, 10);
        var cfPleaseWait = driver.findElement(By.id("cf-please-wait"));

        if (!cfPleaseWait.isExists()){
            logger.info("There is not need to bypass cf challenge");
            return true;
        }

        var bypassCf = new WaitTask() {

            @Override
            public Boolean condition() {
                if (!cfPleaseWait.isExists()) return true;
                var spacer = driver.findElement(By.className("spacer"));
                var size = spacer.getSize();
                var position = spacer.getPosition();
                if (!size.isPresent() || !position.isPresent()){
                    logger.error("Could not get captcha position and size", null);
                    return false;
                }
                Double yClick = position.get().getY();
                Double xClick = position.get().getX() - size.get().getWidth()/2 + 20;
                driver.getXdo().click(xClick, yClick);
                return false;      
            }
            
        };

        return bypassCf.execute(20, 500);
    }
    
}

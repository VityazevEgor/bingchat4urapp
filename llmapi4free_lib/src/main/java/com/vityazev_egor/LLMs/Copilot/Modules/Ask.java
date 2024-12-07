package com.vityazev_egor.LLMs.Copilot.Modules;

import java.util.List;
import java.util.Optional;

import com.vityazev_egor.NoDriver;
import com.vityazev_egor.Core.CustomLogger;
import com.vityazev_egor.Core.WaitTask;
import com.vityazev_egor.Core.WebElements.By;
import com.vityazev_egor.Core.WebElements.WebElement;
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
        // We need to check if we are on Copilot page before asking LLM
        var pageTitel = driver.getTitle();
        if (pageTitel.isPresent() && !pageTitel.get().contains("Copilot")){
            logger.warning("Not on Copilot page");
            if (!driver.getNavigation().loadUrlAndWait("https://copilot.microsoft.com/", 10)){
                return new ChatAnswer();
            }
        }

        if (enterPromt(promt) && waitForAnswer(timeOutForAnswer)){
            return new ChatAnswer(
                getLastAnswerText(), 
                getLastAnswerHtml(),
                driver.getMisc().captureScreenshot()
            );
        } else{
            return new ChatAnswer();
        }
    }

    // TODO переделать расчёт координат для клика используя процентные отношения
    private Boolean bypassCaptcha(){
        var captchaDiv = driver.findElement(By.id("cf-turnstile"));
        if (!captchaDiv.isExists()) return true;
        logger.warning("Found CloudFlare capctha");
        
        var size = captchaDiv.getSize();
        var position = captchaDiv.getPosition();
        if (!size.isPresent() || !position.isPresent()){
            logger.error("Could not get captcha position and size", null);
            return false;
        }
        Double yClick = position.get().getY();
        Double xClick = position.get().getX() - size.get().getWidth()/2 + 20;
        driver.getXdo().click(xClick, yClick);
        return false;
    }

    private Boolean enterPromt(String promt){
        var userInput = driver.findElement(By.id("userInput"));
        var sendButton = driver.findElement(By.cssSelector("button[title='Submit message']"));
        var continueButton = driver.findElement(By.cssSelector("button[title='Continue']"));

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

    private Boolean waitForAnswer(Integer timeOutForAnswer){
        var waitTask = new WaitTask() {
            private String html = driver.getHtml().map(result -> {return result;}).orElse("");

            @Override
            public Boolean condition() {
                // если текущий штмл равен предыдущему то возвращаем да (копайлот перестал печатать)
                return driver.getHtml().map(currentHtml ->{
                    // каждый раз проверяем наличие капчи и пробуем обойти её
                    bypassCaptcha();
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
        return waitTask.execute(timeOutForAnswer, 2 * 1000);
    }

    private Optional<WebElement> getLastAnswerElement(){
        List<WebElement> elements = driver.findElements(By.cssSelector("div[data-content='ai-message']"));
        if (elements.isEmpty()) {
            logger.error("Can't find last answer elements", null);
            return Optional.empty();
        }
        return Optional.of(elements.get(elements.size()-1));
    }

    private Optional<String> getLastAnswerHtml(){
        var answerElement = getLastAnswerElement();
        return answerElement.map(element ->{
            return element.getHTMLContent();
        }).orElse(Optional.empty());
    }

    private Optional<String> getLastAnswerText(){
        return getLastAnswerElement().map(element ->{
            return element.getText();
        }).orElse(Optional.empty());
    }
}

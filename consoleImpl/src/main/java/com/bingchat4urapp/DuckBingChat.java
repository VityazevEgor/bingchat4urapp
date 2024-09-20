package com.bingchat4urapp;

import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

// this class is used for situations when is realy important to get answer from AI. If it fails with bing it will try to use DuckDuckAI instead
public class DuckBingChat extends BingChat{

    private Boolean examMode = false;
    private Boolean bingHaveErrors = false;
    private final Logger logger = LogManager.getLogger(com.bingchat4urapp.DuckBingChat.class);
    private String answerDivClassName = null;
    private WebElement textArea = null, sendButton = null;

    public DuckBingChat(String proxy, int width, int height, int DebugPort, Boolean hideWindow) {
        super(proxy, width, height, DebugPort, hideWindow);
    }

    // устанавливаем режим экзамена в котором нам надо обязательно получить ответ от ИИ
    public void setExamMode(boolean isEnabled){
        examMode = isEnabled;
    }

    @Override
    public String askBing(String prompt, long timeOutForAnswer) {
        if (examMode && bingHaveErrors) {
            logger.warn("We had problems with bing and exam mode is enabled. Trying to get answers from DuckDuck.");
            return tryDuckDuck(prompt, timeOutForAnswer);
        }

        String result = super.askBing(prompt, timeOutForAnswer);

        if (result == null) {
            bingHaveErrors = true;
            if (examMode) {
                logger.warn("Exam mode is enabled; Got error from Bing. Trying to get answer from DuckDuck instead.");
                return tryDuckDuck(prompt, timeOutForAnswer);
            }
        }

        return result;
    }

    private String tryDuckDuck(String prompt, long timeOutForAnswer) {
        if (!_browser._driver.getCurrentUrl().contains("duck")){
            acceptAllDuck();
            if (!createNewDuckChat()) {
                return null;
            }
        }

        var response = askDuckAI(prompt, timeOutForAnswer);
        return response.orElse(null);
    }


    @Override
    public Boolean createNewChat(int ModeType){
        if (examMode && bingHaveErrors){
            logger.warn("We had problems with bing. Going to create new chat with duck duck");
            acceptAllDuck();
            return createNewDuckChat();
        }
        else{
            var result = super.createNewChat(ModeType);
            if (!result) {
                bingHaveErrors = true;
            }
            return result;
        }
    }

    public Boolean acceptAllDuck(){
        if (!_browser.loadAndWaitForComplete("https://duckduckgo.com/?q=DuckDuckGo&ia=chat" , java.time.Duration.ofSeconds(5),0)) return false;
        
        if (_browser.waitForElement(timeOutTime, By.name("user-prompt"))){
            logger.info("There is no need to accept anything");
            return true;
        }

        if (!_browser.waitForElement(timeOutTime, By.xpath("//button[@type='button' and @tabindex='1']"))){
            logger.error("Can't find 'Get started' button");
            return false;
        }
        logger.info("Found button");
        _browser._driver.findElement(By.xpath("//button[@type='button' and @tabindex='1']")).click();

        // if (!_browser.waitForElement(timeOutTime, By.xpath("//button[@type='button' and @tabindex='-1']"))){
        //     logger.error("Can't find 'Next' button");
        //     return false;
        // }
        // logger.info("Found 'Next' button");
        // _browser._driver.findElement(By.xpath("//button[@type='button' and @tabindex='-1']")).click();

        // if (!_browser.waitForElement(timeOutTime, By.xpath("//button[@type='button' and @tabindex='-1']"))){
        //     logger.error("Can't find 'Next' button");
        //     return false;
        // }

        JavascriptExecutor js = (JavascriptExecutor) _browser._driver;
        try{
            js.executeScript(
                "Array.from(document.querySelectorAll('button')).find(el => el.textContent === 'Далее' || el.textContent === 'Next').click();"
            );
            js.executeScript(
                "Array.from(document.querySelectorAll('button')).find(el => el.textContent === 'Принимаю условия' || el.textContent === 'I Agree').click();"
            );
        }
        catch (Exception ex){
            logger.error("Can't execute JS", ex);
            return false;
        }

        return true;
    }

    public Boolean createNewDuckChat() {
        if (!_browser.loadAndWaitForComplete("https://duckduckgo.com/?q=DuckDuckGo&ia=chat" , java.time.Duration.ofSeconds(5),0)) return false;
        try {
            textArea = _browser._driver.findElement(By.name("user-prompt"));
            sendButton = _browser._driver.findElement(By.xpath("//button[@type='submit' and (@aria-label='Отправить' or @aria-label='Send')]"));
    
            textArea.sendKeys("Напиши слово \"Lol kek\"");
            sendButton.click();
    
            if (!waitDuckResponse(sendButton, 5)) {
                logger.error("Could not get answer in time");
                return false;
            }
    
            String result = executeJsAndGetResult();
            if (!result.contains("не найден")) {
                logger.info("Found answer class: " + result);
                answerDivClassName = result;
                return true;
            }
    
            logger.warn("Could not find answer class div");
        } catch (NoSuchElementException e) {
            logger.warn("Can't find elements");
        }
        return false;
    }

    public Optional<String> askDuckAI(String promt, long timeOutForAnswer){
        promt = promt.replace("\n", " ").replace("\r", " ");
        new Actions(_browser._driver).moveToElement(textArea, 5, 5).click().sendKeys(promt).build().perform();
        sendButton.click();
        if (!waitDuckResponse(sendButton, timeOutForAnswer)){
            logger.warn("Could not get answer from DuckDuck in time");
        }

        List<WebElement> elements =  _browser._driver.findElements(By.cssSelector("."+answerDivClassName));
        if (elements.isEmpty()){
            logger.warn("Could not find answer div's");
            return Optional.empty();
        }

        return Optional.ofNullable(elements.get(elements.size()-1).getText());
    }
    
    private boolean waitDuckResponse(WebElement sendButton, long timeoutInSeconds) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}
        Instant initTime = Instant.now();
        while (isButtonInStopPhase(sendButton)) {
            if (Duration.between(initTime, Instant.now()).getSeconds() >= timeoutInSeconds) {
                return false;
            }
        }
        logger.info("Answer is printed");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}
        return true;
    }    
    
    private boolean isButtonInStopPhase(WebElement sendButton) {
        String ariaLabel = sendButton.getAttribute("aria-label");
        return ariaLabel.equalsIgnoreCase("Stop") || ariaLabel.equalsIgnoreCase("Остановить");
    }
    
    private String executeJsAndGetResult() {
        String script = null;
        try{
            InputStream scriptStream = getClass().getClassLoader().getResourceAsStream("scripts/detectDuckAnswerDiv.js");
            BufferedReader reader = new BufferedReader(new InputStreamReader(scriptStream, StandardCharsets.UTF_8));
            script = reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception ex){
            logger.error("Can't get script from resourses", ex);
            return "не найден";
        }
        JavascriptExecutor js = (JavascriptExecutor) _browser._driver;
        return (String) js.executeScript(script);
    }
    
    
}

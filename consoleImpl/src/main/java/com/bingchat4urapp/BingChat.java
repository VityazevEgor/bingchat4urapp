package com.bingchat4urapp;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import java.awt.image.BufferedImage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class BingChat {
    public EdgeBrowser _browser;
    private final Duration timeOutTime = java.time.Duration.ofSeconds(10);

    private final Logger logger = LogManager.getLogger(com.bingchat4urapp.BingChat.class);

    public BingChat(String proxy, int width, int height, int DebugPort){
        _browser = new EdgeBrowser(proxy, width, height, DebugPort);
    }


    // I need to fix it cuz there is sometime different types of auth
    public Boolean auth(String login, String password){
        if (!_browser.loadAndWaitForComplete("https://bing.com", timeOutTime, 0)) return false;
        
        // TODO сделать возможность делать чистую авторизацию (удалять информацию о продедылущей)
        // _browser.CleanCookies();
        // logger.info("I deleted all cocokies for the bing.com. Going to load site again");
        // _browser._driver.get("https://google.com"); // got damn that thing is not good
        
        // if (!_browser.LoadAndWaitForComplete("https://bing.com", timeOutTime, 0)) return false;
        // logger.info("Loaded bing");

        if (!_browser.waitForElement(timeOutTime, By.id("id_s"))){
            // если нету кнопки "Войти" то чекаем не авторизированы ли мы уже
            if (_browser.waitForElement(timeOutTime, By.id("id_n"))){
                logger.info("You already logged in as - " + _browser._driver.findElement(By.id("id_n")).getText());
                return true;
            }
            else{
                return false;
            }
        }
        //_browser._driver.findElement(By.id("id_s")).click();
        WebElement ariaSwitcherButton = _browser._driver.findElement(By.id("id_l"));
        Instant initTime =  Instant.now();

        while (!"true".equalsIgnoreCase(ariaSwitcherButton.getAttribute("aria-expanded"))) {
            if (Duration.between(initTime, Instant.now()).toSeconds()>=timeOutTime.getSeconds()){
                logger.error("Could not open area in time");
                return false;
            }

            new Actions(_browser._driver).moveToElement(_browser._driver.findElement(By.id("id_s"))).click().perform();
            logger.info("Clicked on login button");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }   
        }
        logger.info("Expanded area");

        if (_browser.waitForElement(timeOutTime, By.cssSelector(".id_accountItem"))){
            logger.info("Detected second type of auth");
            _browser._driver.findElement(By.cssSelector(".id_accountItem")).click();
        }
        
        if (!_browser.waitForComplete(timeOutTime, 4000)) return false;
        logger.info("Loaded login page");
        if (!_browser.waitForElement(timeOutTime, By.name("loginfmt"))) return false;
        _browser._driver.findElement(By.name("loginfmt")).sendKeys(login);
        _browser._driver.findElement(By.id("idSIButton9")).click();
        logger.info("Entered login and clicked next button");

        if (!_browser.waitForElement(timeOutTime, By.name("passwd"))) return false;
        _browser._driver.findElement(By.name("passwd")).sendKeys(password);
        _browser._driver.findElement(By.id("idSIButton9")).click();
        logger.info("Entered password");

        if (!_browser.waitForComplete(timeOutTime, 0) || !_browser.waitForElement(timeOutTime, By.id("acceptButton"))) return false;
        logger.info("Loadded 'Stay signed' page");
        _browser._driver.findElement(By.id("acceptButton")).click();

        if (!_browser.waitForComplete(timeOutTime, 0) || !_browser.waitForElement(timeOutTime, By.id("bnp_btn_accept"))) return false;
        _browser._driver.findElement(By.id("bnp_btn_accept")).click();
        logger.info("Finished auth!");

        return true;
    }

    // method that opens chat with bing and select specific conversation mode
    public Boolean createNewChat(int ModeType){
        if (!_browser.loadAndWaitForComplete("https://www.bing.com/search?q=Bing+AI&showconv=1&FORM=hpcodx", java.time.Duration.ofSeconds(5),0)) return false;
        logger.info("Loaded chat");

        if (!_browser.waitForElement(timeOutTime, By.cssSelector(".cib-serp-main"))){
            logger.error("Can't find main");
            return false;
        }
        logger.info("Found main block");

        SearchContext main = _browser._driver.findElement(By.cssSelector(".cib-serp-main")).getShadowRoot();
        if (!_browser.waitForElement(timeOutTime, By.cssSelector("#cib-conversation-main"), main)){
            logger.error("Can't find conversation main");
            return false;
        }
        logger.info("Found conversation block");

        SearchContext ConversationMain = main.findElement(By.cssSelector("#cib-conversation-main")).getShadowRoot();
        if (!_browser.waitForElement(timeOutTime, By.cssSelector("cib-welcome-container"), ConversationMain)){
            logger.error("Can't find welcome container");
            return false;
        }
        logger.info("Found welcome container");

        SearchContext WelcomeContainer = ConversationMain.findElement(By.cssSelector("cib-welcome-container")).getShadowRoot();
        if (!_browser.waitForElement(timeOutTime, By.cssSelector("cib-tone-selector"), WelcomeContainer)){
            logger.error("Can't find tone selector");
            return false;
        }
        logger.info("Found tone selector block");

        SearchContext ToneSelector = WelcomeContainer.findElement(By.cssSelector("cib-tone-selector")).getShadowRoot();
        if (!_browser.waitForElement(timeOutTime, By.cssSelector(".tone-precise"), ToneSelector)){
            logger.error("Can't find tone-precise option");
            return false;
        }
        logger.info("Found options for conversation type");
        
        WebElement MorePrecise = ToneSelector.findElement(By.cssSelector(".tone-precise"));
        WebElement Balanced = ToneSelector.findElement(By.cssSelector(".tone-balanced"));
        WebElement Creative = ToneSelector.findElement(By.cssSelector(".tone-creative"));

        Instant start = Instant.now();
        Boolean ElemtsOnTheCorrectPositions = false;
        
        // scroll web page down to botoom
        new Actions(_browser._driver).scrollByAmount(0, 1000);

        while (Duration.between(Instant.now(), start).getSeconds() <= timeOutTime.getSeconds()) {
            if (checkElemntPosition(Creative) && checkElemntPosition(Balanced) && checkElemntPosition(MorePrecise)){
                ElemtsOnTheCorrectPositions = true;
                break;
            }
            try{
                Thread.sleep(500);
            }
            catch (InterruptedException e){}
        }

        if (!ElemtsOnTheCorrectPositions){
            logger.error("Could not load elements to select chat mode");
            return false;
        }

        switch (ModeType) {
            case 1:
                new Actions(_browser._driver).moveToElement(Creative).click().build().perform();  
                break;
            
            case 2:
                new Actions(_browser._driver).moveToElement(Balanced).click().build().perform();
                break;
                    
            default:
                new Actions(_browser._driver).moveToElement(MorePrecise).click().build().perform();
                break;
        }

        logger.info("Clicked on option");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.error("For some reason i can not stop thread", e);
        }
        //_browser.TakeScreenshot("SelectedMode.png");
        return true;
    }

    // method that checks if element position can be accesed by new Actions
    private Boolean checkElemntPosition(WebElement element){
        Point pos = element.getLocation();
        java.awt.Dimension BrowserSize = _browser.getBrowserSize();

        if (pos.getX()>=0 && pos.getX()<=BrowserSize.getWidth() && pos.getX()>=0 && pos.getX()<=BrowserSize.getHeight()){
            return true;
        }
        else{
            logger.warn("Elemet out of bounds");
            return false;
        }
    }

    // TimeOutForAnswer in seconds. This method must be called only after CreateNewChat
    public String askBing(String promt, long timeOutForAnswer){

        SearchContext actionBarContext = _browser._driver.findElement(By.cssSelector(".cib-serp-main")).getShadowRoot()
            .findElement(By.cssSelector("#cib-action-bar-main")).getShadowRoot();

        if (!_browser.waitForElement(timeOutTime, By.cssSelector("cib-text-input"), actionBarContext)){
            logger.error("Can't find action bar");
            return null;
        }

        WebElement textInput = actionBarContext.findElement(By.cssSelector("cib-text-input")).getShadowRoot().findElement(By.cssSelector("#searchbox"));
        promt = promt.replace("\n", "").replace("\r", "");

        new Actions(_browser._driver).moveToElement(textInput).click().sendKeys(promt+"\n").perform();
        logger.info("I sent promt");

        // time when we started waiting for answer from bing
        Instant startTime = Instant.now();

        // Experiment code
        WebElement stopTypingButton = _browser._driver.findElement(By.cssSelector(".cib-serp-main")).getShadowRoot()
            .findElement(By.cssSelector("#cib-action-bar-main")).getShadowRoot()
            .findElement(By.cssSelector("cib-typing-indicator")).getShadowRoot()
            .findElement(By.cssSelector("#stop-responding-button"));

        while (!"true".equalsIgnoreCase(stopTypingButton.getAttribute("disabled"))) {
            if (Duration.between(startTime, Instant.now()).toSeconds()>=timeOutForAnswer){
                logger.error("Could not get answer in time");
                _browser.takeScreenshot("cantGetAnswer.png");
                return null;
            }

            try{
                Thread.sleep(500);
            }
            catch (Exception e){
                logger.error("Can't stop thread for some reason", e);
            }
        }

        return extractBingAnswer();
    }

    // method that change zoom, takescreen, reset zoom ans scroold page to the end
    public BufferedImage takeScreenOfAsnwer(String path){
        setZoom(70);
        BufferedImage result = null;
        if (path != null){
            result = _browser.takeScreenshot(path);
        }
        else{
            result = _browser.takeScreenshot();
        }
        setZoom(100);
        new Actions(_browser._driver).keyDown(Keys.CONTROL).sendKeys(Keys.END).keyUp(Keys.CONTROL).perform();
        return result;
    }

    private void setZoom(Integer percentage){
        JavascriptExecutor jsexec =  (JavascriptExecutor)_browser._driver;
        jsexec.executeScript("document.body.style.zoom = '"+percentage+"%'");
    }

    // да кода больше, но мне так проще потом вспоминать что я делал :)
    public String extractBingAnswer(){
        try {
            logger.info("Start extracting Bing answer");
            
            // Получение главного контейнера
            WebElement cibSerpMain = _browser._driver.findElement(By.cssSelector(".cib-serp-main"));
            if (cibSerpMain == null) {
                logger.error("Main container '.cib-serp-main' not found");
                return null;
            }
            logger.info("Found main container '.cib-serp-main'");
            
            // Переход к дочерним элементам с использованием Shadow DOM
            WebElement cibConversationMain = cibSerpMain.getShadowRoot().findElement(By.cssSelector("#cib-conversation-main"));
            if (cibConversationMain == null) {
                logger.error("Conversation main '#cib-conversation-main' not found");
                return null;
            }
            logger.info("Found conversation main '#cib-conversation-main'");
            
            // Получение списка всех chat-turn
            List<WebElement> actionBarContext = cibConversationMain.getShadowRoot().findElements(By.cssSelector("cib-chat-turn"));
            if (actionBarContext == null || actionBarContext.isEmpty()) {
                logger.error("Chat turns 'cib-chat-turn' not found");
                return null;
            }
            logger.info("Found chat turns 'cib-chat-turn', total: " + actionBarContext.size());
            
            // Получение последнего chat-turn
            WebElement lastChatTurn = actionBarContext.get(actionBarContext.size() - 1);
            if (lastChatTurn == null) {
                logger.error("Last chat turn not found");
                return null;
            }
            logger.info("Found last chat turn");
            
            // Переход к элементам внутри последнего chat-turn
            WebElement cibMessage = lastChatTurn.getShadowRoot().findElement(By.cssSelector(".response-message-group"));
            if (cibMessage == null) {
                logger.error(".response-message-group not found in last chat turn");
                return null;
            }
            logger.info("Found .response-message-group in last chat turn");
            
            WebElement cibMessageShadow = cibMessage.getShadowRoot().findElement(By.cssSelector("cib-message"));
            if (cibMessageShadow == null) {
                logger.error("cib-message not found in .response-message-group");
                return null;
            }
            logger.info("Found cib-message in .response-message-group");
            
            WebElement responseBlock = cibMessageShadow.getShadowRoot().findElement(By.cssSelector("cib-shared"));
            if (responseBlock == null) {
                logger.error("cib-shared not found in cib-message");
                return null;
            }
            logger.info("Found cib-shared in cib-message");
            
            WebElement responseContent = responseBlock.findElement(By.cssSelector(".content.user-select-text"));
            if (responseContent == null) {
                logger.error(".content.user-select-text not found in cib-shared");
                return null;
            }
            logger.info("Found .content.user-select-text in cib-shared");
            
            // Получение атрибута aria-label
            String ariaLabel = responseContent.getAttribute("aria-label");
            if (ariaLabel != null && ariaLabel.contains("Copilot")) {
                logger.info("Found answer: " + ariaLabel);
                return ariaLabel;
            } else {
                logger.error("Aria label is null or does not contain 'Copilot'. Can't get answer");
                return null;
            }
        } catch (Exception e) {
            logger.error("An error occurred: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public void exit(){
        _browser.exit();
    } 
}

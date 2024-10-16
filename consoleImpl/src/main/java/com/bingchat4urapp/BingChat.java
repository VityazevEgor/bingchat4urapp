package com.bingchat4urapp;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import java.awt.image.BufferedImage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class BingChat extends EdgeBrowser{
    public final Duration timeOutTime = java.time.Duration.ofSeconds(10);
    private Boolean emulateErrors = false;

    private final Logger logger = LogManager.getLogger(com.bingchat4urapp.BingChat.class);

    public BingChat(String proxy, int width, int height, int DebugPort, Boolean hideWindow){
        super(proxy, width, height, DebugPort, hideWindow);
    }

    public void setEmulateErrors(boolean isEnabled){
        emulateErrors = isEnabled;
    }

    // Update of Bing design 8.10.2024
    public Boolean auth(String login, String password) {
        if (!loadPageAndVerify("https://copilot.microsoft.com/", timeOutTime)) {
            logger.warn("Can't load copilot web page");
            return false;
        }
        
        Boolean isRussianLanguage = driver.getTitle().contains("ваш ИИ");
        handleConfidentialityAgreement(isRussianLanguage);
        
        if (isLoggedIn()) {
            logger.info("You are already logged in");
            return true;
        }
        
        if (!expandMenu(isRussianLanguage)) return false;
        
        if (!clickSignInButton(isRussianLanguage)) return false;
    
        if (!enterLogin(login)) return false;

        if (driver.getCurrentUrl().startsWith("https://copilot.")) return true;
        
        if (!enterPassword(password)) return false;
    
        if (!clickStaySignedIn()) return false;
    
        if (!waitForComplete(timeOutTime, 0)) {
            logger.warn("Could not load chat page");
            return false;
        }
    
        logger.info("Auth is done!");
        return true;
    }
    
    private boolean loadPageAndVerify(String url, Duration timeOutTime) {
        if (!loadAndWaitForComplete(url, timeOutTime, 0)) {
            return false;
        }
        logger.info(driver.getCurrentUrl());
        return true;
    }
    
    private void handleConfidentialityAgreement(Boolean isRussianLanguage) {
        String acceptXPath = isRussianLanguage ? "//button[@title='Принять']" : "//button[@title='Accept']";
        if (waitForElement(Duration.ofSeconds(1), By.xpath(acceptXPath))) {
            driver.findElement(By.xpath(acceptXPath)).click();
            logger.info("Clicked 'Accept' button");
        }
    }
    
    private boolean isLoggedIn() {
        if (waitForElement(Duration.ofSeconds(1), By.id(":r8:"))) {
            logger.info("You already logged in account");
            return true;
        }
        return false;
    }
    
    private boolean expandMenu(Boolean isRussianLanguage) {
        String signInXPath = isRussianLanguage ? "//button[@title='Войти']" : "//button[@title='Sign in']";
        if (!waitForElement(timeOutTime, By.xpath(signInXPath))) {
            logger.warn("Can't find 'Sign in' button");
            return false;
        }
        driver.findElement(By.xpath(signInXPath)).click();
        BrowserUtils.sleep(1);
        if (!waitForComplete(timeOutTime, 0)) {
            logger.warn("Could not load login page in time");
            return false;
        }
        logger.info("Expanded menu");
        return true;
    }

    private boolean clickSignInButton(Boolean isRussianLanguage) {
        String signInXPath = isRussianLanguage ? "//button[@title='Войти']" : "//button[@title='Sign in']";
        try{
            List<WebElement> buttons = driver.findElements(By.xpath(signInXPath));
            if (buttons.isEmpty()){
                logger.warn("Could not find sign in buttons");
                return false;
            }
            buttons.get(buttons.size()-1).click();
        }
        catch(Exception ex){
            logger.error("Can't click on sign in button", ex);
            return false;
        }
        BrowserUtils.sleep(1);
        if (!waitForComplete(timeOutTime, 0)) {
            logger.warn("Could not load login page in time");
            return false;
        }
        logger.info("Loaded login page");
        return true;
    }

    private boolean enterLogin(String login) {
        if (!waitForElement(timeOutTime, By.name("loginfmt")) || !waitForElement(timeOutTime, By.id("idSIButton9"))) {
            logger.warn("Could not find login field or login button");
            return false;
        }
        driver.findElement(By.name("loginfmt")).sendKeys(login);
        driver.findElement(By.id("idSIButton9")).click();
        BrowserUtils.sleep(1);
        logger.info("Entered login and clicked next button");
        if (!waitForComplete(timeOutTime, 0)) {
            logger.warn("Could not load password page in time");
            return false;
        }
        return true;
    }
    
    private boolean enterPassword(String password) {
        if (!waitForElement(timeOutTime, By.id("i0118")) || !waitForElement(timeOutTime, By.id("idSIButton9"))) {
            logger.warn("Could not load password page in time");
            return false;
        }
        driver.findElement(By.id("i0118")).sendKeys(password);
        driver.findElement(By.id("idSIButton9")).click();
        BrowserUtils.sleep(1);
        logger.info("Entered password and clicked on login button");
        return waitForComplete(timeOutTime, 0);
    }
    
    private boolean clickStaySignedIn() {
        if (!waitForElement(timeOutTime, By.xpath("//button[@aria-labelledby='kmsiTitle']"))) {
            logger.warn("Could not load 'Yes' button");
            return false;
        }
        driver.findElement(By.xpath("//button[@aria-labelledby='kmsiTitle']")).click();
        BrowserUtils.sleep(1);
        return true;
    }
    

    // legacy code
    public Boolean createNewChat(int modeType){
        if (!loadAndWaitForComplete("https://copilot.microsoft.com/", timeOutTime, 0)){
            logger.warn("Could not load chat in time");
            return false;
        }

        return true;
    }

    private Boolean enterPromt(String promt){
        By userInput = By.id("userInput");
        if (!waitForElement(timeOutTime, userInput)){
            logger.warn("Can't find user input");
            return false;
        }
        driver.findElement(userInput).sendKeys(promt);
        logger.info("Entered promt");

        By continueButton = By.xpath("//button[@title='Continue']");
        if (waitForElement(Duration.ofSeconds(1), continueButton)){
            driver.findElement(continueButton).click();
            logger.info("Found 'Continue button'");
        }
        else{
            logger.info("There is not need to click on 'Continue button'");
        }

        By sendButton = By.xpath("//button[@title='Submit message']");
        if (!waitForElement(timeOutTime, sendButton)){
            logger.warn("Can't find 'send button'");
            return false;
        }
        driver.findElement(sendButton).click();
        return true;
    }

    private Boolean waitForAnswer(long answerTimeOutSeconds) {
        Instant startTime = Instant.now();
        Instant lastChangeTime = startTime;
        String previousHtml = getHtml();
        long checkIntervalSeconds = 5; // Интервал для проверки изменений
        Integer sleepDurationSeconds = 1; // Время ожидания между проверками
        while (Duration.between(startTime, Instant.now()).getSeconds() < answerTimeOutSeconds) {
            String currentHtml = getHtml();
    
            // Если HTML изменился, обновляем время последнего изменения
            if (!currentHtml.equals(previousHtml)) {
                lastChangeTime = Instant.now();
                previousHtml = currentHtml;
            }
    
            // Проверяем, не прошло ли 5 секунд без изменений
            if (Duration.between(lastChangeTime, Instant.now()).getSeconds() >= checkIntervalSeconds) {
                logger.info("HTML has not changed for 5 seconds. Answer is probably printed.");
                return true;
            }
    
            // Ожидаем перед следующей проверкой
            BrowserUtils.sleep(sleepDurationSeconds);
        }
    
        // Таймаут ожидания ответа
        logger.warn("Timeout for answer!");
        return false;
    }

    private Optional<String> getLastAnswerText(){
        try {
            List<WebElement> aiMessages = driver.findElements(By.xpath("//div[@data-content='ai-message']"));
            if (aiMessages.size() == 0){
                logger.warn("Can't find ai answer blocks");
                return Optional.empty();
            }

            return Optional.ofNullable(aiMessages.get(aiMessages.size()-1).getText());
        } catch (Exception e) {
            logger.error("Can't find AI answer", e);
            return Optional.empty();
        }
    }
    

    public String askBing(String promt, long answerTimeOutSeconds){
        if (emulateErrors){
            return null;
        }
        promt = promt.replace("\n", " ").replace("\r", " ");
        if (!enterPromt(promt)){
            return null;
        }
        
        if (!waitForAnswer(answerTimeOutSeconds)){
            return null;
        }

        var answer = getLastAnswerText();
        if (answer.isPresent()){
            return answer.get();
        }
        else{
            return null;
        }
    }

    // method that checks if element position can be accesed by new Actions
    private Boolean checkElemntPosition(WebElement element){
        Point pos = element.getLocation();
        java.awt.Dimension BrowserSize = getBrowserSize();

        if (pos.getX()>=0 && pos.getX()<=BrowserSize.getWidth() && pos.getX()>=0 && pos.getX()<=BrowserSize.getHeight()){
            return true;
        }
        else{
            logger.warn("Elemet out of bounds");
            return false;
        }
    }

    // method that change zoom, takescreen, reset zoom ans scroold page to the end
    public BufferedImage takeScreenOfAsnwer(String path){
        setZoom(70);
        BufferedImage result = null;
        if (path != null){
            result = takeScreenshot(path);
        }
        else{
            result = takeScreenshot();
        }
        setZoom(100);
        new Actions(driver).keyDown(Keys.CONTROL).sendKeys(Keys.END).keyUp(Keys.CONTROL).perform();
        return result;
    }

    private void setZoom(Integer percentage){
        JavascriptExecutor jsexec =  (JavascriptExecutor)driver;
        jsexec.executeScript("document.body.style.zoom = '"+percentage+"%'");
    }
}

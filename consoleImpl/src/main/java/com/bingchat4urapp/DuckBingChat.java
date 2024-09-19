package com.bingchat4urapp;

import java.time.Duration;
import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

// this class is used for situations when is realy important to get answer from AI. If it fails with bing it will try to use DuckDuckAI instead
public class DuckBingChat extends BingChat{

    public Boolean examMode = false;
    private Boolean gotError = false;
    private final Logger logger = LogManager.getLogger(com.bingchat4urapp.DuckBingChat.class);
    private final String detectLolKekScript = "const divs = document.querySelectorAll('div');\n" +
        "let foundDiv = null;\n" +
        "divs.forEach(div => {\n" +
        "  const p = div.querySelector('p');\n" +
        "  if (p && p.textContent.includes('Lol kek')) {\n" +
        "    foundDiv = div;\n" +
        "  }\n" +
        "});\n" +
        "if (foundDiv) {\n" +
        "  return foundDiv.className;\n" +
        "} else {\n" +
        "  return 'Div не найден';\n" +
    "}";

    private String answerClassName = null;

    public DuckBingChat(String proxy, int width, int height, int DebugPort, Boolean hideWindow) {
        super(proxy, width, height, DebugPort, hideWindow);
    }

    // TODO сделать так, чтобы если с бингом что-то идёт не так, то производилась попытка использовать DuckDuckAI. Если будет включён флаг examMode и до этого была ошибка с бингом то этот метод будет по умолчанию использовать DuckDuck
    @Override
    public String askBing(String promt, long timeOutForAnswer){
        String result = super.askBing(promt, timeOutForAnswer);
        return result;
    }

    public Boolean createDuckChat(){
        if (!_browser.loadAndWaitForComplete("https://duckduckgo.com/?q=DuckDuckGo&ia=chat" , java.time.Duration.ofSeconds(5),0)) return false;
        // driver.findElement(By.xpath("//button[@type='button' and @tabindex='1']"));
        if (!_browser.waitForElement(timeOutTime, By.xpath("//button[@type='button' and @tabindex='1']"))){
            logger.error("Can't find 'Get started' button");
            return false;
        }
        logger.info("Found button");
        _browser._driver.findElement(By.xpath("//button[@type='button' and @tabindex='1']")).click();

        if (!_browser.waitForElement(timeOutTime, By.xpath("//button[@type='button' and @tabindex='-1']"))){
            logger.error("Can't find 'Next' button");
            return false;
        }
        logger.info("Found 'Next' button");
        _browser._driver.findElement(By.xpath("//button[@type='button' and @tabindex='-1']")).click();

        if (!_browser.waitForElement(timeOutTime, By.xpath("//button[@type='button' and @tabindex='-1']"))){
            logger.error("Can't find 'Next' button");
            return false;
        }
        JavascriptExecutor js = (JavascriptExecutor) _browser._driver;
        js.executeScript(
            "Array.from(document.querySelectorAll('button')).find(el => el.textContent === 'Принимаю условия' || el.textContent === 'I agree').click();"
        );

        return true;
    }

    public Boolean detectDuckAnswerClassName() {
        try {
            WebElement textArea = _browser._driver.findElement(By.name("user-prompt"));
            WebElement sendButton = _browser._driver.findElement(By.xpath("//button[@type='submit' and (@aria-label='Отправить' or @aria-label='Send')]"));
    
            textArea.sendKeys("Напиши слово \"Lol kek\"");
            sendButton.click();
    
            if (!waitDuckResponse(sendButton, 5)) {
                logger.error("Could not get answer in time");
                return false;
            }
    
            String result = executeJsAndGetResult();
            if (!result.equalsIgnoreCase("не найден")) {
                logger.info("Found answer class: " + result);
                answerClassName = result;
                return true;
            }
    
            logger.warn("Could not find answer class div");
        } catch (NoSuchElementException e) {
            logger.warn("Can't find elements");
        }
        return false;
    }
    
    private boolean waitDuckResponse(WebElement sendButton, Integer timeoutInSeconds) {
        Instant initTime = Instant.now();
        while (isButtonInStopPhase(sendButton)) {
            if (Duration.between(initTime, Instant.now()).getSeconds() >= timeoutInSeconds) {
                return false;
            }
        }
        logger.info("Answer is printed");
        return true;
    }    
    
    private boolean isButtonInStopPhase(WebElement sendButton) {
        String ariaLabel = sendButton.getAttribute("aria-label");
        return ariaLabel.equalsIgnoreCase("Stop") || ariaLabel.equalsIgnoreCase("Остановить");
    }
    
    private String executeJsAndGetResult() {
        JavascriptExecutor js = (JavascriptExecutor) _browser._driver;
        return (String) js.executeScript(detectLolKekScript);
    }
    
    
}

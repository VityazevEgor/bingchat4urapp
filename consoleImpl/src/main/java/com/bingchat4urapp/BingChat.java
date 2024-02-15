package com.bingchat4urapp;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.bingchat4urapp.BrowserUtils.ImageData;
import com.jogamp.nativewindow.util.Rectangle;

public class BingChat {
    public EdgeBrowser _browser;
    private HashMap<String, ImageData> _images = new HashMap<>();

    // fields for AskBing method
    private boolean TextIsNotChanging = false;
    private Rectangle PositionOfChat = new Rectangle(31, 243, 845, 307);

    public BingChat(String proxy, int width, int height, int DebugPort){
        // load all images and their positions in HashMap
        try{
            _images.put("meAnything", new ImageData(
                ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/meAnything.png")), 
                new Rectangle(136, 625, 109, 24)
            ));
            _images.put("smallArrow", new ImageData(
                ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/smallArrow.png")), 
                new Rectangle(832, 665, 33, 28)
            ));
        }
        catch (IOException e){
            print("I can't load images");
        }

        _browser = new EdgeBrowser(proxy, width, height, DebugPort);
    }

    // I need to fix it cuz there is sometime different types of auth
    public Boolean Auth(String login, String password){
        Duration timeOutTime = java.time.Duration.ofSeconds(5);
        if (!_browser.LoadAndWaitForComplete("https://bing.com", timeOutTime, 0)) return false;
        print("Loaded bing");

        if (!_browser.WaitForElement(timeOutTime, By.id("id_s"))) return false;
        if (_browser.WaitForElement(java.time.Duration.ofSeconds(2), By.id("id_accountItem"))){
            print("Detected second type of auth");
            _browser._driver.findElement(By.id("id_s")).click();
            _browser._driver.findElement(By.id("id_accountItem")).click();
        }
        else{
            print("Detected default auth");
            _browser._driver.findElement(By.id("id_s")).click();
        }
        
        if (!_browser.WaitForComplete(timeOutTime, 0)) return false;
        print("Loaded login page");
        if (!_browser.WaitForElement(timeOutTime, By.name("loginfmt"))) return false;
        _browser._driver.findElement(By.name("loginfmt")).sendKeys(login);
        _browser._driver.findElement(By.id("idSIButton9")).click();
        print("Entered login and clicked next button");

        if (!_browser.WaitForElement(timeOutTime, By.name("passwd"))) return false;
        _browser._driver.findElement(By.name("passwd")).sendKeys(password);
        _browser._driver.findElement(By.id("idSIButton9")).click();
        print("Entered password");

        if (!_browser.WaitForComplete(timeOutTime, 0) || !_browser.WaitForElement(timeOutTime, By.id("acceptButton"))) return false;
        print("Loadded 'Stay signed' page");
        _browser._driver.findElement(By.id("acceptButton")).click();

        if (!_browser.WaitForComplete(timeOutTime, 0) || !_browser.WaitForElement(timeOutTime, By.id("bnp_btn_accept"))) return false;
        _browser._driver.findElement(By.id("bnp_btn_accept")).click();
        print("Finished auth!");

        _browser.GetHtml("bing.html");
        _browser.TakeScreenshot("logintest.png");

        return true;
    }

    public Boolean CreateNewChat(){
        Duration timeOutTime = java.time.Duration.ofSeconds(5);
        if (!_browser.LoadAndWaitForComplete("https://www.bing.com/search?q=Bing+AI&showconv=1&FORM=hpcodx", java.time.Duration.ofSeconds(5),0)) return false;

        if (!_browser.WaitForElement(timeOutTime, By.cssSelector(".cib-serp-main"))){
            print("Can't find main");
            return false;
        }

        SearchContext main = _browser._driver.findElement(By.cssSelector(".cib-serp-main")).getShadowRoot();
        if (!_browser.WaitForElement(timeOutTime, By.cssSelector("#cib-conversation-main"), main)){
            print("Can't find conversation main");
            return false;
        }

        SearchContext ConversationMain = main.findElement(By.cssSelector("#cib-conversation-main")).getShadowRoot();
        if (!_browser.WaitForElement(timeOutTime, By.cssSelector("cib-welcome-container"), ConversationMain)){
            print("Can't find welcome container");
            return false;
        }

        SearchContext WelcomeContainer = ConversationMain.findElement(By.cssSelector("cib-welcome-container")).getShadowRoot();
        if (!_browser.WaitForElement(timeOutTime, By.cssSelector("cib-tone-selector"), WelcomeContainer)){
            print("Can't find tone selector");
            return false;
        }

        SearchContext ToneSelector = WelcomeContainer.findElement(By.cssSelector("cib-tone-selector"));
        if (!_browser.WaitForElement(timeOutTime, By.cssSelector(".tone-precise"), ToneSelector)){
            print("Can't find tone tone-precise option");
            return false;
        }
        
        WebElement MorePrecise = ToneSelector.findElement(By.cssSelector(".tone-precise"));
        new Actions(_browser._driver).moveToElement(MorePrecise).click().build().perform();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        _browser.TakeScreenshot("SelectedMode.png");
        return true;
    }

    // TimeOutForAnswer in seconds
    public String AskBing(String promt, long TimeOutForAnswer, long TimeForAnwerShouldChange) throws InterruptedException{
        if (!_browser.LoadAndWaitForComplete("https://www.bing.com/search?q=Bing+AI&showconv=1&FORM=hpcodx", java.time.Duration.ofSeconds(5),0)) return "";
        if (!_browser.WaitForImage(java.time.Duration.ofSeconds(10), _images.get("smallArrow"))){
            print("Can't find image");
            return "";
        }
        promt = promt.replace("\n", "");
        print("Loaded chat");
        _browser.TakeScreenshot("loadedchat.png");

        new Actions(_browser._driver).moveToLocation(135, 639).click().sendKeys(promt+"\n").perform();

        // time when we started waiting for answer from bing
        Instant TimeStart = Instant.now();

        // Experiment code
        WebElement StopTypingButton = _browser._driver.findElement(By.cssSelector(".cib-serp-main")).getShadowRoot()
            .findElement(By.cssSelector("#cib-action-bar-main")).getShadowRoot()
            .findElement(By.cssSelector("cib-typing-indicator")).getShadowRoot()
            .findElement(By.cssSelector("#stop-responding-button"));

        // while (!ExtractBingAnswers(_browser.GetHtml()).contains("Received message.")) {

            // if (Duration.between(TimeStart, Instant.now()).toSeconds()>=TimeOutForAnswer){
            //     print("Could not get answer in time");
            //     break;
            // }
        //     Thread.sleep(500);

        // }

        while (!"true".equalsIgnoreCase(StopTypingButton.getAttribute("disabled"))) {
            if (Duration.between(TimeStart, Instant.now()).toSeconds()>=TimeOutForAnswer){
                print("Could not get answer in time");
                break;
            }
            Thread.sleep(500);
        }
        _browser.TakeScreenshot("gotAnswer.png");
        return ExtractBingAnswers(_browser.GetHtml()).replace("Received message.", "");
    }

    // method that gets raw text of bing answer
    private String ExtractBingAnswers(String html) {
        List<String> results = new ArrayList<>();
        if (html == null) return "";

        Document doc = Jsoup.parse(html);
        Elements contentNodes = doc.select("#CIBLiveRegion");
        for (Element node : contentNodes) {
            results.add(node.html().replace("<br>", "\n"));
        }

        if (results.size()>0){
            return results.get(results.size()-1);
        }
        else{
            return "";
        }
    }


    public void Exit(){
        _browser.Exit();
    }
    private void print(String text){
        System.out.println("[BingChat] "+text);
    }
}

package com.bingchat4urapp;

import java.time.Duration;
import java.util.HashMap;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import com.bingchat4urapp.BrowserUtils.ImageData;
import com.jogamp.nativewindow.util.Rectangle;

public class BingChat {
    public EdgeBrowser _browser;
    private HashMap<String, ImageData> _images = new HashMap<>();

    

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

    public Boolean Auth(String login, String password){
        Duration timeOutTime = java.time.Duration.ofSeconds(5);
        if (!_browser.LoadAndWaitForComplete("https://bing.com", timeOutTime, 0)) return false;
        print("Loaded bing");

        if (!_browser.WaitForElement(timeOutTime, By.id("id_s"))){
            // sometimes there is another button for login
            if (_browser.WaitForElement(java.time.Duration.ofSeconds(2), By.className("id_text_signin"))){
                print("Found another way to login");
                _browser._driver.findElement(By.className("id_text_signin")).click();
            }
            else{
                print("I can't even find second button");
                return false;
            }
        }
        else{
            // if we found a first way to log in
            _browser._driver.findElement(By.id("id_s")).click();
        }
        print("Found login button");
        
        
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

    public String AskBing(String promt) throws InterruptedException{
        if (!_browser.LoadAndWaitForComplete("https://www.bing.com/search?q=Bing+AI&showconv=1&FORM=hpcodx", java.time.Duration.ofSeconds(5),0)) return "";
        //new Actions(_browser._driver).moveToLocation(267, 642).click().sendKeys("How are you?\n").perform();
        //print("I sent promt");
        //Thread.sleep(5000);
        if (!_browser.WaitForImage(java.time.Duration.ofSeconds(10), _images.get("smallArrow"))){
            print("Can't find image");
        }
        print("Loaded chat");
        _browser.TakeScreenshot("bingchat.png");
        return "";
    }

    public void Exit(){
        _browser.Exit();
    }
    private void print(String text){
        System.out.println("[BingChat] "+text);
    }
}

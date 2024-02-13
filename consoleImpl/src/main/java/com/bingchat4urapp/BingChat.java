package com.bingchat4urapp;

import java.time.Duration;
import java.util.HashMap;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import com.jogamp.nativewindow.util.Rectangle;

public class BingChat {
    public EdgeBrowser _browser;
    private HashMap<String, ImageData> _images = new HashMap<>();

    public class ImageData{
        public BufferedImage image;
        public Rectangle position;
        
        public ImageData (BufferedImage _image, Rectangle _position){
            image = _image;
            position = _position;
        }
    }

    public BingChat(String proxy, int width, int height, int DebugPort){
        _browser = new EdgeBrowser(proxy, width, height, DebugPort);

        // load all images and their positions in HashMap
        try{
            _images.put("meAnything", new ImageData(
                ImageIO.read(new File(getClass().getClassLoader().getResource("images/meAnything.png").getFile())), 
                new Rectangle(229, 625, 111, 25)
            ));
        }
        catch (IOException e){
            print("I can't load images");
        }
    }

    public Boolean Auth(String login, String password){
        Duration timeOutTime = java.time.Duration.ofSeconds(5);
        if (!_browser.LoadAndWaitForComplete("https://bing.com", timeOutTime, 0)) return false;
        print("Loaded bing");
        if (!_browser.WaitForElement(timeOutTime, By.id("id_s"))) return false;
        print("Found login button");
        
        _browser._driver.findElement(By.id("id_s")).click();
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
        if (!_browser.LoadAndWaitForComplete("https://www.bing.com/search?q=Bing+AI&showconv=1&FORM=hpcodx", java.time.Duration.ofSeconds(5), 7000)) return "";
        new Actions(_browser._driver).moveToLocation(267, 642).click().sendKeys("How are you?\n").perform();
        print("I sent promt");
        Thread.sleep(5000);
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

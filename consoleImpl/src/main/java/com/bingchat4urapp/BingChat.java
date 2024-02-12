package com.bingchat4urapp;

import java.time.Duration;

import org.openqa.selenium.By;

public class BingChat {
    private EdgeBrowser _browser;

    public BingChat(String proxy, int width, int height, int DebugPort){
        _browser = new EdgeBrowser(proxy, width, height, DebugPort);
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

    public void Exit(){
        _browser.Exit();
    }
    private void print(String text){
        System.out.println("[BingChat] "+text);
    }
}

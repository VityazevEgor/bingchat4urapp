package com.bingchat4urapp;

import java.io.File;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


public class App 
{
    public static void main( String[] args ) throws InterruptedException
    {
        String proxy = "127.0.0.1:1080";
        if (System.getProperty("os.name").contains("Windows")){
            proxy = "127.0.0.1:8521";
        }
        EdgeBrowser browser =  new EdgeBrowser(proxy, 1280, 900, 10431);
        browser.LoadAndWait("https://bing.com", java.time.Duration.ofSeconds(5), 2000);
        browser.TakeScreenshot("tets.png");
        browser.Exit();
        System.exit(0);
    }
}

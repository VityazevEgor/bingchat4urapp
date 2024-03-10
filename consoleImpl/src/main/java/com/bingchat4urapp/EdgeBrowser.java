package com.bingchat4urapp;


import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefApp.CefAppState;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefMessageRouter;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.bingchat4urapp.BrowserUtils.ImageData;

import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;

// Fake edge browser that use JCEF
public class EdgeBrowser extends JFrame
{
    // fields for browser
    private CefBrowser _browser;
    private CefClient _client;
    private CefApp _app;
    private Component _browserUI;
    private JLayeredPane _panel;
    private boolean isWindows = false;

    // field for selenium
    public WebDriver _driver;


    // proxy - SOCKS5 proxy like 127.0.0.1:1800. empty string if no proxy
    public EdgeBrowser(String proxy, int width, int height, int DebugPort){
        isWindows = System.getProperty("os.name").contains("Windows");
        Boolean UseOSR = true;
        // JCEF init
        CefAppBuilder builder = new CefAppBuilder();
        builder.getCefSettings().user_agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0";
        // in undecarated mode browser won't render anything without it
        builder.getCefSettings().windowless_rendering_enabled = UseOSR;
        builder.setAppHandler(new MavenCefAppHandlerAdapter() {
            @Override
            public void stateHasChanged(org.cef.CefApp.CefAppState state) {
                if (state == CefAppState.TERMINATED) System.exit(0);
            }
        });
        // Enable debug port to control JCEF via Selenium
        builder.getCefSettings().remote_debugging_port = DebugPort;
        builder.getCefSettings().command_line_args_disabled = false;

        if (proxy != null && !proxy.trim().isEmpty()){
            builder.addJcefArgs("--remote-allow-origins=*", "--proxy-server=socks5://"+proxy);
        }
        else{
            builder.addJcefArgs("--remote-allow-origins=*");
        }

        try{
            _app = builder.build();
        }
        catch (Exception e){
            System.out.println("Can't build cef app. Terminating app");
            System.exit(1);
        }

        _client = _app.createClient();
        CefMessageRouter msgRouter = CefMessageRouter.create();
        _client.addMessageRouter(msgRouter);

        // Now i need to export html file with loader page
        InputStream loaderHtmlStream = getClass().getClassLoader().getResourceAsStream("html/loader.html");
        Path pathToLoaderHtml = Paths.get(System.getProperty("user.home"), "Documents", "loader.html");
        if (!Files.exists(pathToLoaderHtml)) {
            try {
                Files.copy(loaderHtmlStream, pathToLoaderHtml);
            } catch (IOException e) {
                print("Can't export loader html page");
                System.exit(1);
            }
        }
        else{
            print("Loader html is already exported");
        }

        _browser = _client.createBrowser("file:///"+pathToLoaderHtml.toString(), UseOSR, false);
        _browserUI = _browser.getUIComponent();

        _panel = new JLayeredPane();
        _panel.setPreferredSize(new Dimension(width, height));

        _browserUI.setBounds(0,0,width, height);
        _panel.add(_browserUI, JLayeredPane.DEFAULT_LAYER);

        setContentPane(_panel);

        pack();
        setVisible(true);
        System.out.println("Craeted window with witdh = " + getWidth() + " height = " + getHeight());
        System.out.println("Browser size: " +" Width = "+_browserUI.getWidth() + " Height = "+_browserUI.getHeight());
        // if windows was closed then we close JCEF
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Exit();
            }
        });

        // wait some time to give cef init
        try{
            Thread.sleep(2000);
        }
        catch (Exception e){
            System.out.println("Filed to stop thread");
        }
        InitSelenium(DebugPort);
        setVisible(false);
    }

    private void InitSelenium(int DebugPort){
        if (!BrowserUtils.DownloadChromeDriver()){
            print("Can't download chromedriver so /kill");
            System.exit(1);
        }

        if (isWindows){
            String driverPath = Paths.get(System.getProperty("user.home"), "Documents", "chromedriver-win64", "chromedriver.exe").toString();
            System.setProperty("webdriver.chrome.driver", driverPath);
            print("I selected driver for Windows");
        }
        else{
            String driverPath = Paths.get(System.getProperty("user.home"), "Documents", "chromedriver-linux64", "chromedriver").toString();
            System.setProperty("webdriver.chrome.driver", driverPath);
            print("I selected driver for linux");
        }

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:"+DebugPort);
        _driver = new ChromeDriver(options);
    }


    public Dimension GetBrowserSize(){
        return _browserUI.getSize();
    }

    public void CleanCookies(){
        _driver.manage().deleteAllCookies();
    }

    // method that trying to load site and waits for complete document ready state
    // it return false if it could not load site
    // true if everything is ok
    public boolean LoadAndWaitForComplete(String url, java.time.Duration TimeOut, int AdditionalWait){
        _driver.get(url);
        return WaitForComplete(TimeOut, AdditionalWait);
    }

    public boolean WaitForComplete(java.time.Duration TimeOut, int AdditionalWait){
        WebDriverWait wait = new WebDriverWait(_driver, TimeOut);
        try{
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            if (AdditionalWait>0){
                Thread.sleep(AdditionalWait);
            }
            return true;
        }
        catch (org.openqa.selenium.TimeoutException e){
            print("Failed to load site");
            return false;
        }
        catch (InterruptedException e){
            print("Failed to sleep");
            return false;
        }
    }

    public boolean WaitForElement(java.time.Duration TimeOut, By Element){
        WebDriverWait wait = new WebDriverWait(_driver, TimeOut);
        try{
            wait.until(ExpectedConditions.elementToBeClickable(Element));
            return true;
        }
        catch (org.openqa.selenium.TimeoutException e){
            print("Can't find element");
            GetHtml("cantfind.html");
            TakeScreenshot("cantfind.png");
            return false;
        }
    }

    
    public boolean WaitForElement(java.time.Duration TimeOut, By Element, SearchContext context){
        WebDriverWait wait = new WebDriverWait(_driver, TimeOut);
        try{
            wait.until(new ExpectedCondition<Boolean>() {
               @Override
               public Boolean apply(WebDriver driver){
                    try{
                        return context.findElement(Element).isDisplayed();
                    }
                    catch (Exception e){
                        return false;
                    }
               } 
            });
            return true;
        }
        catch (org.openqa.selenium.TimeoutException e){
            print("Can't find element");
            GetHtml("cantfind.html");
            TakeScreenshot("cantfind.png");
            return false;
        }
    }

    // method that waits for image to appear in specific place
    public boolean WaitForImage(java.time.Duration TimeOut, ImageData ImData){
        WebDriverWait wait = new WebDriverWait(_driver, TimeOut);
        try{
            wait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver arg0) {
                    return BrowserUtils.CompareImages(TakeScreenshot(), ImData);
                }
            });
            return true;
        }
        catch (org.openqa.selenium.TimeoutException e){
            print("Can't find element");
            return false;
        }
    }

    // method that get html code of page by JS
    public String GetHtml(){
        JavascriptExecutor js = (JavascriptExecutor)_driver;
        String pageContent = (String) js.executeScript("return document.documentElement.outerHTML;");
        return pageContent;
    }

    public void GetHtml(String FilePath){
        String html = GetHtml();
        try {
            Files.write(Paths.get(FilePath), html.getBytes());
        } catch (IOException e) {
            print("Can't write html to the file");
            e.printStackTrace();
        }
    }

    // method that takes screenshot of browser using selenium
    public BufferedImage TakeScreenshot(){
        BufferedImage result = null;
        File screen = ((TakesScreenshot)_driver).getScreenshotAs(OutputType.FILE);
        try{
            result = ImageIO.read(screen);
        }
        catch (IOException e){
            print("Failed to copy screen to BufferedImage in {TakeScreenshot}");
        }
        return result;
    }

    // method that takes screenshot and save it to file
    public BufferedImage TakeScreenshot(String FilePath){
        BufferedImage image = TakeScreenshot();
        if (image !=null){
            try{
                ImageIO.write(image, "png", new File(FilePath));
            }
            catch (IOException e){
                print("I can't save screenshot in {TakeScreenshot}");
                e.printStackTrace();
            }
        }
        return image;
    }

    // method that makes screenshot using tools provided by JCEF Library
    public void MadeNativeScreenshot(){
        try{
            BufferedImage screen = _browser.createScreenshot(true).get();
            File outpu = new File("screen.png");
            ImageIO.write(screen, "png", outpu);
        }
        catch (Exception e){
            System.out.println("Could not make screenshot");
        }
    }

    public void Exit(){
        try {
            Thread.sleep(1000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        _driver.quit();
        print("Finished driver");
        // we need to give some time for driver
        try {
            Thread.sleep(3000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CefApp.getInstance().dispose();
        print("Dispose cef");
        // for some reason previous line of code do not stop CEF when it use OSR mode 
        if (isWindows){
            try{
                Runtime.getRuntime().exec("taskkill /F /IM jcef_helper.exe");
            }
            catch(IOException e){
                print("I can't stop process of CEF Helper");
            }
        }
        dispose();
        System.exit(0);
    }

    private void print(String text){
        System.out.println("[EdgeBrowser] "+text);
    }
}

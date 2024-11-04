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
import com.bingchat4urapp.Logger.CustomLogger;

import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.nio.file.Files;
import java.nio.file.Path;

// Fake edge browser that use JCEF
public class EdgeBrowser extends JFrame
{
    // fields for browser
    private CefBrowser browser;
    private CefClient cefClient;
    private CefApp cefApp;
    private Component browserUI;
    private JLayeredPane panel;
    private boolean isWindows = false;

    // field for selenium
    public WebDriver driver;

    private final java.util.logging.Logger logger = CustomLogger.getLogger(com.bingchat4urapp.EdgeBrowser.class);
    private final String chachePach = Paths.get(System.getProperty("user.home"),  "Documents",  "cefChache").toAbsolutePath().toString();
    private final Boolean userOSR = true;


    // proxy - SOCKS5 proxy like 127.0.0.1:1800. empty string if no proxy
    public EdgeBrowser(String proxy, int width, int height, int DebugPort, Boolean hideWindow){
        isWindows = System.getProperty("os.name").contains("Windows");
        // JCEF init
        CefAppBuilder builder = new CefAppBuilder();
        builder.getCefSettings().user_agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0";
        // in undecarated mode browser won't render anything without it
        builder.getCefSettings().windowless_rendering_enabled = userOSR;
        builder.setAppHandler(new MavenCefAppHandlerAdapter() {
            @Override
            public void stateHasChanged(org.cef.CefApp.CefAppState state) {
                if (state == CefAppState.TERMINATED) System.exit(0);
            }
        });
        // Enable debug port to control JCEF via Selenium
        builder.getCefSettings().remote_debugging_port = DebugPort;
        builder.getCefSettings().command_line_args_disabled = false;
        builder.getCefSettings().cache_path = chachePach;

        if (proxy != null && !proxy.trim().isEmpty()){
            builder.addJcefArgs("--remote-allow-origins=*", "--proxy-server=socks5://"+proxy);
        }
        else{
            builder.addJcefArgs("--remote-allow-origins=*");
        }

        try{
            cefApp = builder.build();
        }
        catch (Exception e){
            logger.log(Level.SEVERE ,"Error while initializing JCEF", e);
            System.exit(1);
        }

        cefClient = cefApp.createClient();
        CefMessageRouter msgRouter = CefMessageRouter.create();
        cefClient.addMessageRouter(msgRouter);

        // Now i need to export html file with loader page
        InputStream loaderHtmlStream = getClass().getClassLoader().getResourceAsStream("html/loader.html");
        Path pathToLoaderHtml = Paths.get(System.getProperty("user.home"), "Documents", "loader.html");
        if (!Files.exists(pathToLoaderHtml)) {
            try {
                Files.copy(loaderHtmlStream, pathToLoaderHtml);
            } catch (IOException e) {
                logger.log(Level.SEVERE , "Error while copying loader.html", e);
                System.exit(1);
            }
        }
        else{
            logger.info("loader.html already exists");
        }

        browser = cefClient.createBrowser("file:///"+pathToLoaderHtml.toString(), userOSR, false);
        browserUI = browser.getUIComponent();

        panel = new JLayeredPane();
        panel.setPreferredSize(new Dimension(width, height));

        browserUI.setBounds(0,0,width, height);
        panel.add(browserUI, JLayeredPane.DEFAULT_LAYER);

        setContentPane(panel);

        pack();
        setTitle("BingChat4UrApp by Egor Viatyzev");

        setVisible(true);
        logger.info("Browser size: " +" Width = "+browserUI.getWidth() + " Height = "+browserUI.getHeight());
        logger.info("Craeted window with witdh = " + getWidth() + " height = " + getHeight());

        // if windows was closed then we close JCEF
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });

        // wait some time to give cef init
        BrowserUtils.sleep(2);
        initSelenium(DebugPort);
        if (hideWindow) setVisible(false);
    }

    private void initSelenium(int DebugPort){
        if (!BrowserUtils.downloadChromeDriver()){
            logger.log(Level.SEVERE, "Can't download chromedriver so /kill");
            System.exit(1);
        }

        if (isWindows){
            String driverPath = Paths.get(System.getProperty("user.home"), "Documents", "chromedriver-win64", "chromedriver.exe").toString();
            System.setProperty("webdriver.chrome.driver", driverPath);
            logger.info("I selected driver for Windows");
        }
        else{
            String driverPath = Paths.get(System.getProperty("user.home"), "Documents", "chromedriver-linux64", "chromedriver").toString();
            System.setProperty("webdriver.chrome.driver", driverPath);
            logger.info("I selected driver for Linux");
        }

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:" + DebugPort);
        driver = new ChromeDriver(options);
    }

    public Dimension getBrowserSize(){
        return browserUI.getSize();
    }

    public void cleanCookies(){
        driver.manage().deleteAllCookies();
    }

    private void generateErrorReport(){
        String htmlPageName = BrowserUtils.generateRandomFileName(15)+".html";
        String screenShotName = BrowserUtils.generateRandomFileName(15)+".png";
        BrowserUtils.checkLogsDir();

        getHtml(htmlPageName);
        takeScreenshot(screenShotName);
        logger.warning("Saved screenshot to "+BrowserUtils.logsDir.toString()+"/"+screenShotName);
        logger.warning("Saved html page to "+BrowserUtils.logsDir.toString()+"/"+htmlPageName);
    }

    // method that trying to load site and waits for complete document ready state
    // it return false if it could not load site
    // true if everything is ok
    public boolean loadAndWaitForComplete(String url, java.time.Duration TimeOut, int AdditionalWait){
        driver.get(url);
        BrowserUtils.sleep(1);
        return waitForComplete(TimeOut, AdditionalWait);
    }

    public boolean waitForComplete(java.time.Duration TimeOut, int AdditionalWait){
        WebDriverWait wait = new WebDriverWait(driver, TimeOut);
        try{
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            if (AdditionalWait>0){
                Thread.sleep(AdditionalWait);
            }
            return true;
        }
        catch (Exception e){
            logger.log(Level.SEVERE ,"Can't load site", e);
            return false;
        }
    }

    public boolean waitForElement(java.time.Duration TimeOut, By Element){
        WebDriverWait wait = new WebDriverWait(driver, TimeOut);
        try{
            wait.until(ExpectedConditions.elementToBeClickable(Element));
            return true;
        }
        catch (org.openqa.selenium.TimeoutException e){
            logger.severe("Can't find element");
            generateErrorReport();
            return false;
        }
    }

    public boolean waitForElement(java.time.Duration TimeOut, By Element, SearchContext context){
        WebDriverWait wait = new WebDriverWait(driver, TimeOut);
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
            logger.severe("Can't find element");
            generateErrorReport();
            return false;
        }
    }

    // method that waits for image to appear in specific place
    public boolean waitForImage(java.time.Duration TimeOut, ImageData ImData){
        WebDriverWait wait = new WebDriverWait(driver, TimeOut);
        try{
            wait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver arg0) {
                    return BrowserUtils.compareImages(takeScreenshot(), ImData);
                }
            });
            return true;
        }
        catch (org.openqa.selenium.TimeoutException e){
            logger.log(Level.SEVERE, "Can't find element", e);
            generateErrorReport();
            return false;
        }
    }

    // method that get html code of page by JS
    public String getHtml(){
        JavascriptExecutor js = (JavascriptExecutor)driver;
        String pageContent = (String) js.executeScript("return document.documentElement.outerHTML;");
        return pageContent;
    }

    public void getHtml(String FilePath){
        BrowserUtils.checkLogsDir();
        String html = getHtml();
        try {
            Files.write(Paths.get(BrowserUtils.logsDir.toString(), FilePath), html.getBytes());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Can't save page to file", e);
        }
    }

    // method that takes screenshot of browser using selenium
    public BufferedImage takeScreenshot(){
        BufferedImage result = null;
        File screen = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        try{
            result = ImageIO.read(screen);
        }
        catch (IOException e){
            logger.log(Level.SEVERE, "Failed to copy screen to BufferedImage", e);
        }
        return result;
    }

    // method that takes screenshot and save it to file
    public BufferedImage takeScreenshot(String FilePath){
        BrowserUtils.checkLogsDir();
        BufferedImage image = takeScreenshot();
        if (image !=null){
            try{
                ImageIO.write(image, "png", new File(Paths.get(BrowserUtils.logsDir.toString(), FilePath).toString()));
            }
            catch (IOException e){
                logger.log(Level.SEVERE, "Failed to save screen to file", e);
            }
        }
        return image;
    }

    public void exit(){
        BrowserUtils.sleep(1);
        driver.quit();
        logger.info("Finished driver");
        // we need to give some time for driver
        BrowserUtils.sleep(3);
        CefApp.getInstance().dispose();
        logger.info("Dispose cef");
        // for some reason previous line of code do not stop CEF when it use OSR mode 
        if (isWindows){
            try{
                Runtime.getRuntime().exec("taskkill /F /IM jcef_helper.exe");
            }
            catch(IOException e){
                logger.log(Level.SEVERE, "Failed to kill jcef_helper.exe", e);
            }
        }
        System.exit(0);
    }
}

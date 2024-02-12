package com.bingchat4urapp;


import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefApp.CefAppState;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefMessageRouter;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import java.io.*;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.nio.file.Files;

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
        setUndecorated(isWindows);
        Boolean UseOSR = isWindows;
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

        if (!proxy.trim().isEmpty()){
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

        _browser = _client.createBrowser("https://google.com", UseOSR, false);
        _browserUI = _browser.getUIComponent();

        _panel = new JLayeredPane();
        _panel.setPreferredSize(new Dimension(width, height));

        // if we are on linux then we going to create label that hide browser ui cuz setOpacity is not wokring on linux...
        if (!isWindows){
            JLabel label = new JLabel();
            label.setText("[BingChat4urApp] You can hide this window");
            label.setBackground(Color.BLACK);
            label.setForeground(Color.white);
            // we are not closing the whole area of form cuz it can stop browserUI from rendering...
            label.setBounds(0,0, width-2, height-2);
            label.setOpaque(true);
            _panel.add(label, JLayeredPane.PALETTE_LAYER);

            // also we going to prevent user from opening browser window on linux
            addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                    if ((e.getNewState() & JFrame.NORMAL) == JFrame.NORMAL) {
                        System.out.println("Окно развернуто");
                        //setExtendedState(JFrame.ICONIFIED);
                    }
                }
            });
        }
        _browserUI.setBounds(0,0,width, height);
        _panel.add(_browserUI, JLayeredPane.DEFAULT_LAYER);

        setContentPane(_panel);

        // on windows we can create "fake headless" browser by setting windows opacity to 0 and removing it from taskbar
        if (isWindows){
            setOpacity(0.0f);
            setType(Window.Type.UTILITY);
        }
        pack();
        setVisible(true);
        System.out.println("Craeted window with witdh = " + getWidth() + " height = " + getHeight());
        // if windows was closed then we close JCEF
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Exit();
            }
        });
        setExtendedState(JFrame.ICONIFIED);

        // wait some time to give cef init
        try{
            Thread.sleep(2000);
        }
        catch (Exception e){
            System.out.println("Filed to stop thread");
        }
        InitSelenium(DebugPort);
    }

    private void InitSelenium(int DebugPort){
        String desktopPath = Paths.get(System.getProperty("user.home"), "Desktop").toString();
        if (isWindows){
            String driverPath = Paths.get(desktopPath, "chromedriver.exe").toString();
            System.setProperty("webdriver.chrome.driver", driverPath);
            print("I selected driver for Windows");
        }
        else{
            String driverPath = Paths.get(desktopPath, "chromedriver").toString();
            System.setProperty("webdriver.chrome.driver", driverPath);
            print("I selecred driver for linux");
        }
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:"+DebugPort);
        _driver = new ChromeDriver(options);
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
        _driver.quit();
        try {
            Thread.sleep(3000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CefApp.getInstance().dispose();
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
    }

    private void print(String text){
        System.out.println("[EdgeBrowser] "+text);
    }
}

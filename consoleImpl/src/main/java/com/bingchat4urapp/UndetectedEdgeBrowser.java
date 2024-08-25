package com.bingchat4urapp;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.CefApp.CefAppState;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefMessageRouter;
import org.cef.handler.CefDisplayHandlerAdapter;

import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;

// trying to implement everything without Selenium

// it can bypass cloudflare only if ur ip is not from data center (with home ip it should work)
public class UndetectedEdgeBrowser extends JFrame{
    // fields for browser
    private CefBrowser _browser;
    private CefClient _client;
    private CefApp _app;
    private Component _browserUI;
    private JLayeredPane _panel;
    private boolean isWindows = false;

    private final Logger logger = LogManager.getLogger(com.bingchat4urapp.UndetectedEdgeBrowser.class);
    private final String chachePach = Paths.get(System.getProperty("user.home"),  "Documents",  "cefChache").toAbsolutePath().toString();

    public UndetectedEdgeBrowser(String startUrl, String proxy, int width, int height, Boolean hideWindow, Boolean cleanChache){
        isWindows = System.getProperty("os.name").contains("Windows");
        Boolean UseOSR = true;
        if (hideWindow != null && hideWindow) setUndecorated(true);

        // JCEF init
        CefAppBuilder builder = new CefAppBuilder();
        builder.getCefSettings().user_agent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36 Edg/127.0.0.0";
        builder.getCefSettings().windowless_rendering_enabled = UseOSR;
        builder.setAppHandler(new MavenCefAppHandlerAdapter() {
            @Override
            public void stateHasChanged(org.cef.CefApp.CefAppState state) {
                if (state == CefAppState.TERMINATED) System.exit(0);
            }
        });
        builder.getCefSettings().command_line_args_disabled = false;
        builder.getCefSettings().cache_path = chachePach;

        // if proxy present then we add it to args
        if (proxy != null && !proxy.trim().isEmpty()){
            builder.addJcefArgs("--proxy-server=socks5://"+proxy);
        }
        // idk if it even working :)
        builder.addJcefArgs("--enable-chrome-runtime", "--no-sandbox");

        try{
            _app = builder.build();
        }
        catch (Exception e){
            logger.error("Can't init CEF browser", e);
            System.exit(1);
        }

        _client = _app.createClient();
        // Create and configure CefDisplayHandler
        _client.addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level, String message, String source, int line) {
                // TODO make something like stack of messages to give methods ability to get answers from JS
                //logger.info("Got message from console: \n" + message);
                System.out.println(message);
                return true;
            }
        });

        
        CefMessageRouter messageRouter = CefMessageRouter.create();
        _client.addMessageRouter(messageRouter);

        // Now i need to export html file with loader page
        InputStream loaderHtmlStream = getClass().getClassLoader().getResourceAsStream("html/loader.html");
        Path pathToLoaderHtml = Paths.get(System.getProperty("user.home"), "Documents", "loader.html");
        if (!Files.exists(pathToLoaderHtml)) {
            try {
                Files.copy(loaderHtmlStream, pathToLoaderHtml);
            } catch (IOException e) {
                logger.error("Error while copying loader.html", e);
                System.exit(1);
            }
        }
        else{
            logger.info("loader.html already exists");
        }

        if (startUrl == null){
            _browser = _client.createBrowser("file:///home/egor/Desktop/testB.html", UseOSR, false);
        }
        else{
            _browser = _client.createBrowser(startUrl, UseOSR, false);
        }
        _browserUI = _browser.getUIComponent();

        _panel = new JLayeredPane();
        _panel.setPreferredSize(new Dimension(width, height));

        _browserUI.setBounds(0,0,width, height);
        _panel.add(_browserUI, JLayeredPane.DEFAULT_LAYER);

        setContentPane(_panel);

        pack();
        setTitle("Test UD JCEF browser");

        setVisible(true);
        if (hideWindow != null && hideWindow ) setState(JFrame.ICONIFIED);
        logger.info("Browser size: " +" Width = "+_browserUI.getWidth() + " Height = "+_browserUI.getHeight());
        logger.info("Craeted window with witdh = " + getWidth() + " height = " + getHeight());

        // if windows was closed then we close JCEF
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });

        // wait some time to give cef init
        try{
            Thread.sleep(2000);
        }
        catch (Exception e){}
        if ( hideWindow == null || hideWindow){
            //setVisible(false); we can't hide window cuz without window we can not send key events to component
            setOpacity(0f);
        }
    }

    public void getHTML() {
        String script = "console.log(document.documentElement.innerHTML);";
        _browser.executeJavaScript(script, _browser.getURL(), 0);
    }

    @SuppressWarnings("deprecation")
    public void emulateLeftClikc(int x, int y){
        java.util.List<MouseEvent> moves = new ArrayList<>();

        moves.add(new MouseEvent(_browserUI, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, x, y, 0, false));
        moves.add(new MouseEvent(_browserUI, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis() + 10, MouseEvent.BUTTON1_MASK, x, y, 1, false));
        moves.add(new MouseEvent(_browserUI, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis() + 20, MouseEvent.BUTTON1_MASK, x, y, 1, false));

        for (int i=0; i<moves.size(); i++){
            _browserUI.dispatchEvent(moves.get(i));
        }
    }

    // TODO implemet screenshot method

    private void emulateKeyPress(int keyCode){
        char keyChar = UndetectedBrowserUtils.getCharFromKeyCode(keyCode);
        emulateKeyPress(keyCode, keyChar);
    }

    // this method works only if JFram is visible
    public void emulateKeyPress(int keyCode, char keyChar){
        var keyEvents = new ArrayList<KeyEvent>();
        
        keyEvents.add(new KeyEvent(
            _browserUI,
            KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), 
            0,
            keyCode,
            keyChar
        ));

        keyEvents.add(new KeyEvent(
            _browserUI,
            KeyEvent.KEY_RELEASED,    
            System.currentTimeMillis()+10,
            0,
            keyCode,
            keyChar
        ));

        for (KeyEvent event : keyEvents) {
            _browserUI.dispatchEvent(event);
        }
    }

    // TODO implement method which can do the same thing but using JS
    public void enterText(String text) throws Exception{
        
        // this code support only english alphabet
        for (char c : text.toCharArray()){
            Integer keyCode = UndetectedBrowserUtils.getKeyCodeFromChar(c);
            if (keyCode == null){
                throw new Exception("There is not keycode for this character - " + c);
            }
            //keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
            emulateKeyPress(keyCode);
        }
    }

    public void exit(){
        try {
            Thread.sleep(3000); 
        } catch (InterruptedException e) {}
        CefApp.getInstance().dispose();
        logger.info("Dispose cef");
        // for some reason previous line of code do not stop CEF when it use OSR mode 
        if (isWindows){
            try{
                Runtime.getRuntime().exec("taskkill /F /IM jcef_helper.exe");
            }
            catch(IOException e){
                logger.error("Failed to kill jcef_helper.exe", e);
            }
        }
        System.exit(0);
    }
}

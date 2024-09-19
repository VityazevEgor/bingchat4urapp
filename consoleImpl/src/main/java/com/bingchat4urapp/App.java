package com.bingchat4urapp;

import java.util.List;

import com.jogamp.common.util.InterruptSource.Thread;

import java.io.IOException;
import java.nio.file.*;
import java.awt.event.*;

public class App 
{
    public static void main( String[] args ) throws InterruptedException, IOException
    {
        testDuckBingChat();
    }

    private static void testDuckBingChat() throws InterruptedException, IOException{
        Path pwdPath = Paths.get(System.getProperty("user.home"), "Desktop", "bingp.txt");
        List<String> data = Files.readAllLines(pwdPath);

        DuckBingChat chat = new DuckBingChat("127.0.0.1:2080", 1280, 1000, 10431, false);
        System.out.println(chat.createDuckChat());
        System.out.println(chat.detectDuckAnswerClassName());
        Thread.sleep(2000);
        chat.exit();
    }

    @SuppressWarnings("unused")
    private static void testKeyEmulation(){
        UndetectedEdgeBrowser udBrowser = new UndetectedEdgeBrowser("https://bot.sannysoft.com/", null, 1280, 1000, false, true);
        UndetectedBrowserUtils.sleep(30000);
        System.out.println("Enetring text");
        try {
            udBrowser.enterText("lol kek. 1-2 = 3");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Clicking on button");
        udBrowser.emulateLeftClikc(100, 100);
        //udBrowser.emulateKeyPress(65, 'Ф');
        udBrowser.exit();
    }

    @SuppressWarnings("unused")
    private static void testUDEdge() throws InterruptedException{
        UndetectedEdgeBrowser udBrowser = new UndetectedEdgeBrowser("https://chat.openai.com", null, 1280, 1000, false, true);
        Thread.sleep(5000);
        udBrowser.getHTML();
        for (int i=0; i<2; i++){
            udBrowser.emulateLeftClikc(214, 285);
            Thread.sleep(5000);
            System.out.println("Clicked!");
        }
        udBrowser.getHTML();
        udBrowser.exit();
    }
    @SuppressWarnings("unused")
    private static void testBingChat() throws InterruptedException, IOException{
        Path pwdPath = Paths.get(System.getProperty("user.home"), "Desktop", "bingp.txt");
        List<String> data = Files.readAllLines(pwdPath);
        
        BingChat chat = new BingChat("127.0.0.1:2080", 1280, 1000, 10431, false);
        Boolean result = false;
        try{
            result = chat.auth(data.get(0), data.get(1));
        }
        catch (Exception e){
            
        }
        if (result == false){
            chat._browser.takeScreenshot("error.png");
        }
        if (result){
            //sc.nextLine();
            //chat.extractBingAnswerRecode();
            System.out.println(chat.createNewChat(3));
            //System.out.println(chat.askBing("Как у тебя дела?", 120));
            System.out.println(chat.askBing("Найди в итнернете когда родился путин?", 120));
            //chat.TakeScreenOfAsnwer("first.png");
            //System.out.println(chat.AskBing("А теперь покажи формулы которые нужно знать для решения задач на горизонтальные броски тела", 100));
            //chat.TakeScreenOfAsnwer("second.png");
        }
        System.out.println(result);
        Thread.sleep(1000);
        chat.exit();
        System.out.println("Called exit");
    }
}

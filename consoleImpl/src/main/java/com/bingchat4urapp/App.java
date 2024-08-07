package com.bingchat4urapp;

import java.util.List;
import java.io.IOException;
import java.nio.file.*;

public class App 
{
    public static void main( String[] args ) throws InterruptedException, IOException
    {
        testUDEdge();
    }

    private static void testUDEdge() throws InterruptedException{
        UndetectedEdgeBrowser udBrowser = new UndetectedEdgeBrowser("https://chatgpt.com/", "127.0.0.1:2080", 1280, 1000, false, true);
        Thread.sleep(10000);
        udBrowser.getHTML();
        for (int i=0; i<2; i++){
            udBrowser.emulateLeftClikc(214, 285);
            Thread.sleep(5000);
            System.out.println("Clicked!");
        }
        udBrowser.getHTML();
        udBrowser.exit();
    }
    private static void testBingChat() throws InterruptedException, IOException{
        Path pwdPath = Paths.get(System.getProperty("user.home"), "Desktop", "bingp.txt");
        List<String> data = Files.readAllLines(pwdPath);
        
        BingChat chat = new BingChat(null, 1280, 1000, 10431);
        Boolean result = false;
        //chat.Exit();
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
            System.out.println(chat.askBing("Как у тебя дела?", 120));
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

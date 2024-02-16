package com.bingchat4urapp;

import java.nio.file.Paths;
import java.util.List;
import java.io.IOException;
import java.nio.file.*;

public class App 
{
    public static void main( String[] args ) throws InterruptedException, IOException
    {
        String proxy = "127.0.0.1:1080";
        if (System.getProperty("os.name").contains("Windows")){
            proxy = "127.0.0.1:8521";
        }

        Path pwdPath = Paths.get(System.getProperty("user.home"), "Desktop", "bingp.txt");
        List<String> data = Files.readAllLines(pwdPath);

        BingChat chat = new BingChat(proxy, 1280, 720, 10431);
        Boolean result = false;
        try{
            result = chat.Auth(data.get(0), data.get(1));
        }
        catch (Exception e){
            
        }
        if (result == false){
            chat._browser.TakeScreenshot("error.png");
        }
        if (result){
            //String answer = chat.AskBing("kek", 120, 0);
            //System.out.println("Bing said = "+answer);
            System.out.println(chat.CreateNewChat(1));
            System.out.println(chat.AskBing("How are you?", 80));
            System.out.println(chat.AskBing("What i said to u in previuos message?", 80));
        }
        System.out.println(result);
        Thread.sleep(1000);
        chat.Exit();
        System.out.println("Called exit");
        System.exit(0);
    }
}

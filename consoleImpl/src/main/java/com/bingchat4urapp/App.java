package com.bingchat4urapp;

import java.util.List;

import com.jogamp.common.util.InterruptSource.Thread;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class App 
{
    public static void main( String[] args ) throws InterruptedException, IOException
    {
        testBingChat();
    }

    private static void emulateErrorSituatin() throws IOException{
        var chat = new DuckBingChat("127.0.0.1:2080", 1920, 1080, 10431, false);
        chat.setExamMode(true);
        chat.setEmulateErrors(true);
        Path pwdPath = Paths.get(System.getProperty("user.home"), "Desktop", "bingp.txt");
        List<String> data = Files.readAllLines(pwdPath);
        var authRes = chat.auth(data.get(0), data.get(1));
        if (authRes && chat.createNewChat(3)){
            var promtResult = chat.askBing("Как у тебя дела?", 120);
            System.out.println("AI response: " + promtResult);
            promtResult = chat.askBing("Напиши формулу поиска угловой скорости для физики", 120);
            System.out.println("AI response: " + promtResult);
        }
        waitForInput();
        chat.exit();
    }

    @SuppressWarnings("unused")
    private static void testDuckBingChat() throws InterruptedException, IOException{
        DuckBingChat chat = new DuckBingChat(null, 1280, 1000, 10431, false);
        System.out.println(chat.acceptAllDuck());
        var detectResult = chat.createNewDuckChat();
        if (detectResult){
            System.out.println(chat.askDuckAI("Напиши hello world на java", 120));
            //System.out.println(chat.askDuckAI("Напиши основные формулы в физике, которые используется в механике", 120));
            //Thread.sleep(2000);
        }
        waitForInput();
        chat.exit();
    }

    private static void waitForInput(){
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();
    }

    @SuppressWarnings("unused")
    private static void testBingChat() throws InterruptedException, IOException{
        Path pwdPath = Paths.get(System.getProperty("user.home"), "Desktop", "bingp.txt");
        List<String> data = Files.readAllLines(pwdPath);
        
        BingChat chat = new BingChat("127.0.0.1:2080", 1280, 1000, 10431, false);
        Boolean result = chat.auth(data.get(0),data.get(1));
        if (result){
            chat.createNewChat(0);
            System.out.println(chat.askBing("Напиши hello world на Java", 60));
            System.out.println(chat.askBing("Напиши формулы для решеня задача на броски тела под углом к горизонту", 120));
        }
        System.out.println(result);
        Thread.sleep(1000);
        chat.exit();
        System.out.println("Called exit");
    }
}

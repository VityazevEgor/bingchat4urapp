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
            //System.out.println(chat.CreateNewChat(3));
            //System.out.println(chat.AskBing("У меня есть текст в котором пользователь описывает что ему нужно сделать: Я хочу поиграть в майнкрафт в понедельник, а потом во вторник сходит вечером с мамой за попкупками\". Я хочу чтобы ты перевёл строку пользователя в удобный формат JSON у которого будут поля:name - название заметки time - время в формате DateTime C# (если не указана точная дата, то считай время от текущего) desription - описание", 120));
            //chat.TakeScreenOfAsnwer("first.png");
            //System.out.println(chat.AskBing("А теперь покажи формулы которые нужно знать для решения задач на горизонтальные броски тела", 100));
            //chat.TakeScreenOfAsnwer("second.png");
        }
        System.out.println(result);
        Thread.sleep(1000);
        chat.Exit();
        System.out.println("Called exit");
    }
}

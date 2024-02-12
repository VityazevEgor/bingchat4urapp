package com.bingchat4urapp;


public class App 
{
    public static void main( String[] args ) throws InterruptedException
    {
        String proxy = "127.0.0.1:1080";
        if (System.getProperty("os.name").contains("Windows")){
            proxy = "127.0.0.1:8521";
        }
        BingChat chat =  new BingChat(proxy, 1280, 900, 10431);
        Boolean result = chat.Auth("a", "a");
        System.out.println(result);
        chat.Exit();
        System.out.println("Called exit");
        System.exit(0);
    }
}

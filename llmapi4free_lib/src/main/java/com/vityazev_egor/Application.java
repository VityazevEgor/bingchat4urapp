package com.vityazev_egor;

import java.io.IOException;
import java.util.Scanner;

public class Application {

    public static void main(String[] args) throws IOException, InterruptedException {
        var wrapper = new Wrapper("127.0.0.1:2080");
        System.out.println("Auth result = " + wrapper.auth("Copilot", "login", "password"));
        wrapper.askLLM("Copilot", "How\nare you", 120);
        waitEnter();
        wrapper.exit();
    }

    public static void waitEnter(){
        System.out.println("Waiting for input");
        var sc = new Scanner(System.in);
        sc.nextLine();
        sc.close();
    }
}

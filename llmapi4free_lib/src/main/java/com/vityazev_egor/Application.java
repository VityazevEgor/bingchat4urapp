package com.vityazev_egor;

import java.io.IOException;
import java.util.Scanner;

public class Application {

    public static void main(String[] args) throws IOException, InterruptedException {
        var driver = new NoDriver("127.0.0.1:2080");
        waitEnter();
        driver.exit();
    }

    @SuppressWarnings("unused")
    private static void waitEnter(){
        System.out.println("Waiting for input");
        var sc = new Scanner(System.in);
        sc.nextLine();
        sc.close();
    }
}

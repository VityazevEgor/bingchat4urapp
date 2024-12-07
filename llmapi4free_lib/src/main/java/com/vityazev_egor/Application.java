package com.vityazev_egor;

import java.io.IOException;
import java.util.Scanner;

public class Application {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Ooops...");
    }

    @SuppressWarnings("unused")
    private static void waitEnter(){
        System.out.println("Waiting for input");
        var sc = new Scanner(System.in);
        sc.nextLine();
        sc.close();
    }
}

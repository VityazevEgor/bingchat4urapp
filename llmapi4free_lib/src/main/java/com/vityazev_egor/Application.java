package com.vityazev_egor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import com.vityazev_egor.Wrapper.LLMproviders;
import com.vityazev_egor.Wrapper.WrapperMode;

public class Application {

    public static void main(String[] args) throws IOException, InterruptedException {
        testDuckDuck();
    }

    @SuppressWarnings("unused")
    private static void testCopilot() throws IOException{
        Path pwdPath = Paths.get(System.getProperty("user.home"), "Desktop", "bingp.txt");
        List<String> data = Files.readAllLines(pwdPath);
        String loging = data.get(0);
        String password = data.get(1);
        var wrapper = new Wrapper("127.0.0.1:2080", LLMproviders.Copilot, WrapperMode.ExamMode);
        Boolean result = wrapper.auth(LLMproviders.Copilot, loging, password);
        if (result){
            var answer = wrapper.askLLM(LLMproviders.Copilot, "How are you?", 20);
            System.out.println(answer.getCleanAnswer());
        }
        wrapper.exit();
    }

    private static void testDuckDuck() throws IOException {
        var wrapper = new Wrapper("127.0.0.1:2080", LLMproviders.Copilot, WrapperMode.ExamMode);
        Boolean result = wrapper.auth(LLMproviders.DuckDuck, null, null);
        if (result) result = wrapper.createChat(LLMproviders.DuckDuck);
        if (result) {
            var answer = wrapper.askLLM(LLMproviders.DuckDuck, "Can you write hello world in java", 100);
            System.out.println(answer.getCleanAnswer());
        }
        waitEnter();
        wrapper.exit();
    }

    private static void waitEnter(){
        System.out.println("Waiting for input");
        var sc = new Scanner(System.in);
        sc.nextLine();
        sc.close();
    }
}

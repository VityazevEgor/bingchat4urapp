package com.vityazev_egor;

import org.junit.jupiter.api.Test;

import com.vityazev_egor.Core.Shared;
import com.vityazev_egor.Wrapper.LLMproviders;
import com.vityazev_egor.Wrapper.WrapperMode;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class ApplicationTest {

    @Test
    void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    void duckduckChatIsNotOpened() throws IOException{
        var wrapper = new Wrapper("127.0.0.1:2080",LLMproviders.DuckDuck, WrapperMode.Normal);
        var answer = wrapper.askLLM("How are you today?",40);
        assertTrue(answer.getCleanAnswer().isPresent());
        answer = wrapper.askLLM("Write hello world in java", 40);
        assertTrue(answer.getCleanAnswer().isPresent());
        wrapper.exit();
    }

    @Test
    void testDuckDuckScreenShot() throws IOException{
        var wrapper = new Wrapper("127.0.0.1:2080",LLMproviders.DuckDuck, WrapperMode.ExamMode);
        var answer = wrapper.askLLM("напиши формулу равноускоренного движения в физике",40);
        wrapper.exit();
        assertTrue(answer.getAnswerImage().isPresent());
    }

    @Test
    void copilotAuth() throws IOException{
        var wrapper = new Wrapper("127.0.0.1:2080",LLMproviders.Copilot, WrapperMode.ExamMode);
        Path pwdPath = Paths.get(System.getProperty("user.home"), "Desktop", "bingp.txt");
        List<String> data = Files.readAllLines(pwdPath);
        String loging = data.get(0);
        String password = data.get(1);
        var result = wrapper.auth(LLMproviders.Copilot, loging, password);
        wrapper.exit();
        assertTrue(result);
    }

    @Test
    void copilotAnswer() throws IOException{
        var wrapper = new Wrapper("127.0.0.1:2080",LLMproviders.Copilot, WrapperMode.Normal);
        Path pwdPath = Paths.get(System.getProperty("user.home"), "Desktop", "bingp.txt");
        List<String> data = Files.readAllLines(pwdPath);
        String loging = data.get(0);
        String password = data.get(1);
        var result = wrapper.auth(LLMproviders.Copilot, loging, password);
        assertTrue(result);
        var answer = wrapper.askLLM("Напиши формулу равноусоркенного движения в физике",60);
        wrapper.exit();
        assertTrue(answer.getCleanAnswer().isPresent());
        System.out.println(answer.getCleanAnswer());
        assertTrue(answer.getAnswerImage().isPresent());
        // var firstAnswer = answer.getCleanAnswer();
        // assertTrue(firstAnswer.isPresent());
        // System.out.println(firstAnswer);
        // answer = wrapper.askLLM("Can you write hello world in java?",60);
        // assertTrue(answer.getCleanAnswer().isPresent() && !answer.getCleanAnswer().get().equalsIgnoreCase(firstAnswer.get()));
        // answer = wrapper.askLLM("Can you show me how to use for loop in java?",60);
        // assertTrue(answer.getCleanAnswer().isPresent() && !answer.getCleanAnswer().get().equalsIgnoreCase(firstAnswer.get()));
        // System.out.println(answer.getCleanAnswer());
    }

    @Test
    void testOpenAIAuth() throws IOException{
        var wrapper = new Wrapper("127.0.0.1:2080", LLMproviders.OpenAI, WrapperMode.ExamMode);
        Boolean result = wrapper.createChat(LLMproviders.OpenAI);
        if (result) result = wrapper.auth(LLMproviders.OpenAI, "", "");
        Shared.sleep(5000);
        wrapper.exit();
        assertTrue(result);
    }

    @Test
    void testOpenAIChat() throws IOException{
        var wrapper = new Wrapper("127.0.0.1:2080", LLMproviders.OpenAI, WrapperMode.Normal);
        wrapper.auth(LLMproviders.OpenAI, "null", "null");
        var answer = wrapper.askLLM("Can you write hello world in java?",100);
        var answer2 = wrapper.askLLM("Can you write quick sort in java?", 100);
        wrapper.exit();
        System.out.println(answer.getCleanAnswer());
        System.out.println(answer2.getCleanAnswer());
        assertTrue(answer.getCleanAnswer().isPresent());
        assertTrue(answer2.getCleanAnswer().isPresent());
    }

    @Test
    void testRotatingSystem() throws IOException{
        var wrapper = new Wrapper("127.0.0.1:2080",LLMproviders.Copilot, WrapperMode.ExamMode);
        assertTrue(wrapper.getWorkingLLM().isPresent() && wrapper.getWorkingLLM().get().getProvider() == LLMproviders.DuckDuck);

        Path pwdPath = Paths.get(System.getProperty("user.home"), "Desktop", "bingp.txt");
        List<String> data = Files.readAllLines(pwdPath);
        String loging = data.get(0);
        String password = data.get(1);

        var result = wrapper.auth(LLMproviders.Copilot, loging, password);
        assertTrue(result);
        assertTrue(wrapper.getWorkingLLM().isPresent() && wrapper.getWorkingLLM().get().getProvider() == LLMproviders.Copilot);
        wrapper.exit();
    }

    @Test
    void testDeepSeek() throws IOException, InterruptedException{
        var wrapper = new Wrapper("127.0.0.1:2080", LLMproviders.DeepSeek, WrapperMode.Normal);
        var answer = wrapper.askLLM("Напиши hello world \n на Java",60);
        var answer2 = wrapper.askLLM("Write quick sort in java", 60);
        wrapper.exit();
        System.out.println(answer.getCleanAnswer());
        System.out.println(answer2.getCleanAnswer());
        assertTrue(answer2.getCleanAnswer().isPresent());
        assertTrue(answer.getCleanAnswer().isPresent());
    }

    @Test
    void deepSeekAuth() throws IOException{
        var wrapper = new Wrapper("127.0.0.1:2080", LLMproviders.DeepSeek, WrapperMode.Normal);
        var result = wrapper.auth(LLMproviders.DeepSeek, "login", "password");
        wrapper.exit();
        assertTrue(result);
    }
}

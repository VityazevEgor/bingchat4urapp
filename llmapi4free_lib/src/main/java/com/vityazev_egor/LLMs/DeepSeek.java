package com.vityazev_egor.LLMs;

import com.vityazev_egor.Core.CustomLogger;
import com.vityazev_egor.Core.Shared;
import com.vityazev_egor.Core.WebElements.By;
import com.vityazev_egor.NoDriver;
import com.vityazev_egor.iChat;
import com.vityazev_egor.Models.ChatAnswer;

public class DeepSeek implements iChat{
    private final NoDriver driver;
    private final String url = "https://chat.deepseek.com/";
    private final CustomLogger logger = new CustomLogger(DeepSeek.class.getName());

    public DeepSeek(NoDriver driver){
        this.driver = driver;
    }

    /**
     * Checks if the authentication process is complete by verifying the presence of a prompt input field.
     *
     * @return {@code true} if the authentication is done, otherwise {@code false}.
     */
    private Boolean isAuthDone(){
        var promptInput = driver.findElement(By.id("chat-input"));
        try{
            promptInput.waitToAppear(5, 100);
            return true;
        }
        catch (Exception ex){
            return false;
        }
    }

    /**
     * Authenticates the user by navigating through Google sign-in process.
     *
     * @param login The user's login credentials (not used in this method).
     * @param password The user's password credentials (not used in this method).
     * @return {@code true} if the authentication is successful, otherwise {@code false}.
     */
    @Override
    public Boolean auth(String login, String password) {
        try{
            if (!driver.getNavigation().loadUrlAndBypassCFXDO(url, 5, 20))
                throw new Exception("Could not load URL or bypass CF challenge.");
            if (isAuthDone())
                return true;
            var signInWithGoogleButton = driver.findElement(By.cssSelector("div.ds-button__icon"));
            signInWithGoogleButton.waitToAppear(5, 100);
            driver.getInput().emulateClick(signInWithGoogleButton);
            driver.getNavigation().waitFullLoad(5);
            if (!driver.getNavigation().loadUrlAndBypassCFXDO(null, null, 20))
                throw new Exception("Could not load URL or bypass CF challenge.");

            var selectGoogleAccountButton = driver.findElement(By.cssSelector("[data-email]"));
            selectGoogleAccountButton.waitToAppear(5, 100);
            driver.getInput().emulateClick(selectGoogleAccountButton);
            Shared.sleep(1000);
            driver.getNavigation().waitFullLoad(5);

            var declineAcceptButtons = driver.findElements(By.cssSelector("button[data-idom-class]"));
            if (declineAcceptButtons.isEmpty())
                throw new com.vityazev_egor.LLMs.Shared.ProviderException("Can't find decline and accept buttons");
            driver.getInput().emulateClick(declineAcceptButtons.get(1));
            Shared.sleep(1000);
            driver.getNavigation().waitFullLoad(5);

            return isAuthDone();
        } catch (Exception ex) {
            logger.error("Error occurred while authenticating: " + ex.getMessage(), ex);
            return false;
        }
    }

    /**
     * Sends a prompt to the chat and waits for an answer.
     *
     * @param prompt The user's input prompt.
     * @param timeOutForAnswer The maximum time (in milliseconds) to wait for an answer.
     * @return A {@link ChatAnswer} object containing the response text, HTML content, and screenshot of the answer.
     */
    @Override
    public ChatAnswer ask(String prompt, Integer timeOutForAnswer) {
        try {
            // MAKE SURE THAT CHAT IS OPENED
            Boolean isChatOpened = driver.getTitle().map(title -> title.contains("DeepSeek")).orElse(false);
            if (!isChatOpened)
                if (!auth(null, null))
                    throw new Exception("Could not open chat or authenticate.");

            // SEND PROMPT
            var chatInput = driver.findElement(By.id("chat-input"));
            var sendButton = driver.findElement(By.cssSelector("div[role='button'][aria-disabled='false']"));
            chatInput.waitToAppear(5, 100);
            driver.getInput().insertText(chatInput, prompt);
            sendButton.waitToAppear(5, 100);
            driver.getInput().emulateClick(sendButton);

            // GET ANSWER
            if (!com.vityazev_egor.LLMs.Shared.waitForAnswer(driver, timeOutForAnswer, 5000))
                throw new RuntimeException("Could not receive answer within the specified time limit.");
            var answerDivs = driver.findElements(By.cssSelector("div.ds-markdown.ds-markdown--block"));
            if (answerDivs.isEmpty())
                throw new RuntimeException("No answer received.");
            var lastestAnswer = answerDivs.get(answerDivs.size() - 1);
            return new ChatAnswer(
                lastestAnswer.getText(),
                lastestAnswer.getHTMLContent(),
                driver.getMisc().captureScreenshot()
            );
        }
        catch (Exception ex){
            logger.error("Error occurred while opening chat: " + ex.getMessage(), ex);
            return new ChatAnswer();
        }
    }

    @Override
    public Boolean creatNewChat() {
        return auth(null, null);
    }

    @Override
    public String getName() {
        return "DeepSeek";
    }
    
}

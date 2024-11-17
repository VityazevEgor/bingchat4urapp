package com.vityazev_egor.LLMs.Copilot.Modules;

import com.vityazev_egor.NoDriver;
import com.vityazev_egor.Core.CustomLogger;
import com.vityazev_egor.Core.WebElements.By;
import com.vityazev_egor.LLMs.Shared;

public class Auth {
    private final NoDriver driver;
    private final CustomLogger logger;

    public Auth(NoDriver driver){
        this.driver = driver;
        this.logger = new CustomLogger(Auth.class.getName());
    }

    public Boolean auth(String login, String password){
        driver.getNavigation().loadUrlAndWait("https://copilot.microsoft.com/", 10);
        acceptTerms();

        if (isLoggedIn()) {
            logger.info("User is already logged in");
            return true;
        }

        if (openLoginPage() && enterLogin(login) && enterPassword(password) && staySigned()){
            return true;
        }
        else{
            return false;
        }
    }

    private void acceptTerms(){
        var acceptButton = driver.findElement(By.cssSelector("button[title='Accept']"));
        if (Shared.waitForElements(false, acceptButton)){
            driver.getInput().emulateClick(acceptButton);
        }
    }

    private Boolean isLoggedIn(){
        var profileButton = driver.findElement(By.id(":r2:"));
        return Shared.waitForElements(false,profileButton);
    }

    private Boolean openLoginPage(){
        // Ожидаем и нажимаем на первую кнопку "Sign in"
        var signInButton = driver.findElement(By.cssSelector("button[title='Sign in']"));
        if (!Shared.waitForElements(false,signInButton)) {
            logger.warning("Can't find sign in button");
            return false;
        }
        driver.getInput().emulateClick(signInButton);

        // Проверяем наличие второй кнопки "Sign in" после раскрытия меню
        var signInButtons = driver.findElements(By.cssSelector("button[title='Sign in']"));
        if (signInButtons.size() < 2) {
            logger.warning("There are less than 2 'Sign in' buttons - " + signInButtons.size());
            if (!Shared.waitForElements(false, driver.findElement(By.name("loginfmt")))){
                return false;
            }
            else{
                logger.info("Instead of expanding menu it oppened login page. Sometimes this happens");
                return true;
            }
        }
        driver.getInput().emulateClick(signInButtons.get(1));

        return true;
    }

    private Boolean enterLogin(String login){
        // Ожидаем появления поля для ввода логина
        var loginInput = driver.findElement(By.name("loginfmt"));
        var loginButton = driver.findElement(By.id("idSIButton9"));
        if (!Shared.waitForElements(true, loginButton, loginButton)) {
            logger.warning("Can't find login input");
            return false;
        }

        // Вводим email и нажимаем кнопку "Далее"
        driver.getInput().enterText(loginInput, login);
        driver.getInput().emulateClick(loginButton);

        return true;
    }

    private Boolean enterPassword(String password){
        var passwordInput = driver.findElement(By.id("i0118"));
        var passwordButton = driver.findElement(By.id("idSIButton9"));
        if (!Shared.waitForElements(true, passwordInput, passwordButton)){
            logger.warning("Can't find password input or password button");
            return false;
        }
        driver.getInput().enterText(passwordInput, password);
        driver.getInput().emulateClick(passwordButton);

        return true;
    }

    private Boolean staySigned(){
        var yesButton = driver.findElement(By.cssSelector("button[aria-labelledby='kmsiTitle']"));
        if (!Shared.waitForElements(true, yesButton)){
            logger.warning("Can't find 'yes' button");
            return false;
        }
        driver.getInput().emulateClick(yesButton);
        return true;
    }
}

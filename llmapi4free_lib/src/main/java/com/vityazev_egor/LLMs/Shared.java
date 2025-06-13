package com.vityazev_egor.LLMs;

import com.vityazev_egor.NoDriver;
import com.vityazev_egor.Core.WaitTask;
import com.vityazev_egor.Core.WebElements.WebElement;

public class Shared {
    public static Boolean waitForElements(Boolean checkForClicable, WebElement... elements){
        return waitForElements(checkForClicable, 8, elements);
    }

    public static Boolean waitForElements(Boolean checkForClickable, Integer timeOutSeconds, WebElement... elements){
        var waitTask = new WaitTask() {

            @Override
            public Boolean condition() {
                for (WebElement element : elements){
                    if (!element.isExists()) return false;
                }
                if (checkForClickable){
                    if (!elements[elements.length-1].isClickable()) return false;
                }
                return true;
            }
            
        };

        return waitTask.execute(timeOutSeconds, 400);
    }

    public static Boolean waitForAnswer(NoDriver driver, Integer timeOutForAnswer, Integer delayMiliseconds){
        var waitTask = new WaitTask() {
            private String html = driver.getHtml().map(result -> result).orElse("");

            @Override
            public Boolean condition() {
                // если текущий штмл равен предыдущему то возвращаем да (копайлот перестал печатать)
                return driver.getHtml().map(currentHtml ->{
                    if (currentHtml.equals(html)){
                        return true;
                    }
                    else{
                        html = currentHtml;
                        return false;
                    }
                }).orElse(false);
            }
            
        };
        com.vityazev_egor.Core.Shared.sleep(delayMiliseconds);
        return waitTask.execute(timeOutForAnswer, delayMiliseconds);
    }

    public static class ProviderException extends Exception {
        public ProviderException(String message) {
            super(message);
        }    
    }


}

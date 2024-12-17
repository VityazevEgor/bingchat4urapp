package com.vityazev_egor.LLMs;

import com.vityazev_egor.Core.WaitTask;
import com.vityazev_egor.Core.WebElements.WebElement;

public class Shared {
    public static Boolean waitForElements(Boolean checkForClicable, WebElement... elements){
        return waitForElements(checkForClicable, 8, elements);
    }

    public static Boolean waitForElements(Boolean checkForClicable, Integer timeOutSeconds, WebElement... elements){
        var waitTask = new WaitTask() {

            @Override
            public Boolean condition() {
                for (WebElement element : elements){
                    if (!element.isExists()) return false;
                }
                if (checkForClicable){
                    if (!elements[elements.length-1].isClickable()) return false;
                }
                return true;
            }
            
        };

        return waitTask.execute(timeOutSeconds, 400);
    }
}

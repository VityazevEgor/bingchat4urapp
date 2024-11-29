package com.vityazev_egor.LLMs.DuckDuck.Modules;

import com.vityazev_egor.NoDriver;
import com.vityazev_egor.Core.CustomLogger;

public class Ask {
    private final NoDriver driver;
    private final CustomLogger logger;

    public Ask(NoDriver driver){
        this.driver = driver;
        logger = new CustomLogger(Ask.class.getName());
    }
}

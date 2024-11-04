package com.bingchat4urapp.Logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomLogger {
    public static Logger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz.getName());
        
        // Remove existing handlers from this specific logger
        if (logger.getHandlers().length == 0) {
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new ColorFormatter());
            logger.addHandler(consoleHandler);
            logger.setLevel(Level.ALL);
            consoleHandler.setLevel(Level.ALL);
        }
        
        return logger;
    }
}

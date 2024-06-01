package com.bingchat4urapp_server.bingchat4urapp_server;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Shared {
    public static String proxy = null;
    public static final Path imagesPath = Paths.get(System.getProperty("user.dir"),"images").toAbsolutePath();
}

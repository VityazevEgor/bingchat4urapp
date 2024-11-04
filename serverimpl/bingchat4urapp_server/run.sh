#!/bin/bash
cd target
java --add-exports java.base/java.lang=ALL-UNNAMED --add-exports java.desktop/sun.awt=ALL-UNNAMED --add-exports java.desktop/sun.java2d=ALL-UNNAMED -jar bingchat4urapp_server-0.0.5-SNAPSHOT.jar --proxy 127.0.0.1:2080 --examMode true --hideBrowser false

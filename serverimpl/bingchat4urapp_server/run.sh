#!/bin/bash
bash build.sh
cd target
java -jar bingchat4urapp_server-0.0.5-SNAPSHOT.jar --proxy 127.0.0.1:2080 --examMode false

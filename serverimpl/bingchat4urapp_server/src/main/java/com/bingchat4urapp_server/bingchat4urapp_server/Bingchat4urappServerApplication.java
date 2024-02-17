package com.bingchat4urapp_server.bingchat4urapp_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.regex.Pattern;

@EnableScheduling
@SpringBootApplication
public class Bingchat4urappServerApplication {

	public static void main(String[] args) {
		if (args.length>0){
			String proxy = args[0];
			if (isValidProxy(proxy)){
				System.out.print("It is valid proxy i'm going to use it");
				Shared.proxy = proxy;
			}
		}
		SpringApplication app = new SpringApplication(Bingchat4urappServerApplication.class);
		app.setHeadless(false);
		app.run();
	}

	private static boolean isValidProxy(String proxy) {
        String IP_PORT_PATTERN = 
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5]):([0-9]{1,5})$";
        return Pattern.compile(IP_PORT_PATTERN).matcher(proxy).matches();
    }

}

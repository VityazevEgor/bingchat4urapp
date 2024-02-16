package com.bingchat4urapp_server.bingchat4urapp_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Bingchat4urappServerApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Bingchat4urappServerApplication.class);
		app.setHeadless(false);
		app.run(args);
	}

}

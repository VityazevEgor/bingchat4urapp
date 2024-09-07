package com.bingchat4urapp_server.bingchat4urapp_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.nio.file.Files;
import java.util.regex.Pattern;

@EnableScheduling
@SpringBootApplication
public class Bingchat4urappServerApplication {

	public static void main(String[] args) {
		String proxy = null;
		Boolean hideBrowser = true; 

		// Обработка аргументов командной строки
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
				case "--proxy":
					if (i + 1 < args.length) {
						proxy = args[i + 1];
						i++;
					} else {
						System.out.println("Error: no value is specified for --proxy");
						System.exit(1);
					}
					break;
				case "--hideBrowser":
					if (i + 1 < args.length) {
						hideBrowser = Boolean.parseBoolean(args[i + 1]);
						i++;
					} else {
						System.out.println("Error: no value is specified for --hideBrowser");
						System.exit(1);
					}
					break;
				default:
					System.out.println("Unknown arg: " + args[i]);
					System.exit(1);
			}
		}

		// Если указан proxy и он валидный
		if (proxy != null && isValidProxy(proxy)) {
			System.out.println("Using proxy: " + proxy);
			Shared.proxy = proxy;
		} else {
			Shared.proxy = null;
			System.out.println("Proxy is not valid. Running browser without proxy (null).");
		}
		// remove before package
		Shared.hideBrowserWindow = hideBrowser;
		SpringApplication app = new SpringApplication(Bingchat4urappServerApplication.class);
		app.setHeadless(false);
		app.run();
	}

	private static boolean isValidProxy(String proxy) {
        String IP_PORT_PATTERN = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5]):([0-9]{1,5})$";
        return Pattern.compile(IP_PORT_PATTERN).matcher(proxy).matches();
    }

}

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
				case "--examMode":
					if (i + 1 < args.length) {
						Shared.examMode = Boolean.parseBoolean(args[i + 1]);
						i++;
					} else {
						System.out.println("Error: no value is specified for --examMode");
						System.exit(1);
					}
					break;
				case "--emulateErrors":
					if (i + 1 < args.length) {
						Shared.emulateBingErros = Boolean.parseBoolean(args[i + 1]);
						i++;
					} else {
						System.out.println("Error: no value is specified for --emulateErrors");
						System.exit(1);
					}
					break;
				default:
					System.out.println("Unknown arg: " + args[i]);
					System.exit(1);
			}
		}
		//proxy = "127.0.0.1:2080";
		// Если указан proxy и он валидный
		if (proxy != null && isValidProxy(proxy)) {
			System.out.println("Using proxy: " + proxy);
			Shared.proxy = proxy;
		} else {
			Shared.proxy = null;
			System.out.println("Proxy is not valid. Running browser without proxy (null).");
		}

		// Создание папки для изображений, если она не существует
		if (!Files.exists(Shared.imagesPath)) {
			try {
				Files.createDirectories(Shared.imagesPath);
			} catch (Exception e) {
				System.out.println("Can't create folder for images! \n\n");
				e.printStackTrace();
				System.exit(1);
			}
		}

		SpringApplication app = new SpringApplication(Bingchat4urappServerApplication.class);
		app.setHeadless(false);
		app.run();
	}

	private static boolean isValidProxy(String proxy) {
        String IP_PORT_PATTERN = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5]):([0-9]{1,5})$";
        return Pattern.compile(IP_PORT_PATTERN).matcher(proxy).matches();
    }

}

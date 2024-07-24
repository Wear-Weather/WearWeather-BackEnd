package com.WearWeather.wear;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WearApplication {

	public static void main(String[] args) {
		System.setProperty("server.servlet.context-path", "/api/v1");
		SpringApplication.run(WearApplication.class, args);
	}

}

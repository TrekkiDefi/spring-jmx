package com.github.fnpac.jmx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ServerJmxApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerJmxApplication.class, args);
	}
}

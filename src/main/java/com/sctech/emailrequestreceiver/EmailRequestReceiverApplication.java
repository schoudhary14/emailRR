package com.sctech.emailrequestreceiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EmailRequestReceiverApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailRequestReceiverApplication.class, args);
	}

}

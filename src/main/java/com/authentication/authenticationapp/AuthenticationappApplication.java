package com.authentication.authenticationapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class AuthenticationappApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationappApplication.class, args);
	}
}

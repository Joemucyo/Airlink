package com.Airlink.AirticketingSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AirticketingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirticketingSystemApplication.class, args);
	}

}

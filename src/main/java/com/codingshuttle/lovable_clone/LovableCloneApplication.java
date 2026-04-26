package com.codingshuttle.lovable_clone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LovableCloneApplication {

	public static void main(String[] args) {
		System.out.println("user.timezone property: " + System.getProperty("user.timezone"));
		System.out.println("JVM Timezone: " + java.util.TimeZone.getDefault());
		SpringApplication.run(LovableCloneApplication.class, args);

	}

}

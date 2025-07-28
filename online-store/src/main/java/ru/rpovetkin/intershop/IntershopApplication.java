package ru.rpovetkin.intershop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;

@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
public class IntershopApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntershopApplication.class, args);
		System.out.println("это модуль online-store");
	}
}

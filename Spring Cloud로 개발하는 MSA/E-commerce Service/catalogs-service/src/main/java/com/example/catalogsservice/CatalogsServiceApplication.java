package com.example.catalogsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CatalogsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatalogsServiceApplication.class, args);
	}

}

package br.edu.atitus.api_discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ApiDiscoveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiDiscoveryApplication.class, args);
	}

}

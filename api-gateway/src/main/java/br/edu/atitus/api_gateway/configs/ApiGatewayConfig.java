package br.edu.atitus.api_gateway.configs;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfig {
	
	@Bean
	RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p
						.path("/get")
						.filters(f -> f
								.addRequestHeader("Hello", "World")
								.addRequestParameter("Hello", "World"))
						.uri("http://httpbin.org:80"))
				.route(p -> p
						.path("/products/**")
						.uri("lb://product-service"))
				.route(p -> p
						.path("/ws/products/**")
						.uri("lb://product-service"))
				.route(p -> p
						.path("/product-service/**")
						.uri("lb://product-service"))
				
				.route(p -> p
						.path("/currency-service/**")
						.uri("lb://currency-service"))
				.route(p -> p
						.path("/currency/**")
						.uri("lb://currency-service"))
				
				.route(p -> p
						.path("/auth-service/**")
						.uri("lb://auth-service"))
				.route(p -> p
						.path("/auth/**")
						.uri("lb://auth-service"))
	
				.build();
	}

}

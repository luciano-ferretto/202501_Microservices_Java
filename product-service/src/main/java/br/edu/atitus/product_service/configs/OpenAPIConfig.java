package br.edu.atitus.product_service.configs;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@SecurityScheme(
	    name = "bearerAuth",
	    type = SecuritySchemeType.HTTP,
	    scheme = "bearer",
	    bearerFormat = "JWT" // Indica que o token é JWT
	)
public class OpenAPIConfig {
	

//	@Bean
//	OpenAPI customOpenAPI(@Value("${openapi.service.title}") String serviceTitle,
//		      @Value("${openapi.service.version}") String serviceVersion,
//		      @Value("${openapi.service.url}") String url) {
//		return new OpenAPI()
//				.servers(List.of(new Server().url(url)))
//				.info(new Info().title(serviceTitle).description("Descrição")
//				.contact(new Contact().name("Desenvolvedor Senior").email("developer@atitus.edu.br"))
//				.version(serviceVersion));
//	}
	

	@Bean
	OpenAPI customOpenAPI(@Value("${openapi.service.url}") String url) {
		return new OpenAPI()
				.servers(List.of(new Server().url(url), new Server().url("/")));
	}

}

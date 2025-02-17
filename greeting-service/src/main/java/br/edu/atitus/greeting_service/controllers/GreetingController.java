package br.edu.atitus.greeting_service.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class GreetingController {
	
	@Value("${greeting-service.greeting}")
	private String greeting;
	
	@Value("${greeting-service.default-name}")
	private String defaultName;

	@GetMapping({"", "/", "/{namePath}"})
	public ResponseEntity<String> getGreeting(
			@RequestParam(required = false) String nameParam,
			@PathVariable(required = false) String namePath) {
		
		String name = nameParam != null ? nameParam : namePath != null ? namePath : defaultName;
		
		String out = String.format("%s %s!!!", greeting, name);
		
		return ResponseEntity.ok(out);
	}
	
}

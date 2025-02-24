package br.edu.atitus.currency_service.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.atitus.currency_service.entities.CurrencyEntity;
import br.edu.atitus.currency_service.repositories.CurrencyRepository;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@ApiResponses(value = {
		@ApiResponse(responseCode = "400", description = "Erro de validação ou requisição inválida", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class), examples = @ExampleObject(value = "Dados inválidos!!!"))),
		@ApiResponse(responseCode = "403", description = "Acesso não permitido", content = @Content(mediaType = "none"))
})
@RestController
@RequestMapping("currency")
public class CurrencyController {
	
	private final CurrencyRepository repository;

	public CurrencyController(CurrencyRepository repository) {
		super();
		this.repository = repository;
	}
	
	@Value("${server.port}")
	private int portServer;
	
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200")})
	@GetMapping("/{value}/{source}/{target}")
	public ResponseEntity<CurrencyEntity> get(
			@PathVariable double value,
			@PathVariable String source,
			@PathVariable String target) throws Exception {
		CurrencyEntity currency = repository.findBySourceAndTarget(source, target)
				.orElseThrow(() -> new Exception("Currency Unsupported"));
		currency.setConvertedValue(currency.getConversionRate() * value);
		currency.setEnviroment("Currency-service running on port: " + portServer);
		return ResponseEntity.ok(currency);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception e) {
		String cleanMessage = e.getMessage().replaceAll("[\\r\\n]", " ");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cleanMessage);
	}
	

}

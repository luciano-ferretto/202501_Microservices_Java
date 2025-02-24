package br.edu.atitus.product_service.controllers;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.atitus.product_service.clients.CurrencyClient;
import br.edu.atitus.product_service.clients.CurrencyResponse;
import br.edu.atitus.product_service.entities.ProductEntity;
import br.edu.atitus.product_service.repositories.ProductRepository;
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
@RequestMapping("products")
public class OpenProductController {
	
	private final ProductRepository repository;
	private final CurrencyClient currencyClient;

	public OpenProductController(ProductRepository repository, CurrencyClient currencyClient) {
		super();
		this.repository = repository;
		this.currencyClient = currencyClient;
	}
	
	@Value("${server.port}")
	private int port;
	

	@GetMapping("/{idProduct}/{targetCurrency}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200")})
	public ResponseEntity<ProductEntity> getProduto(
			@PathVariable Long idProduct,
			@PathVariable String targetCurrency) throws Exception{
		
		ProductEntity product = repository.findById(idProduct).orElseThrow(() -> new Exception("Produto não encontrado!"));
		CurrencyResponse currency = currencyClient.getCurrency(product.getPrice(),product.getCurrency(),targetCurrency);
		
		product.setConvertedPrice(currency.getConvertedValue());
		//Setar o ambiente
		product.setEnviroment("Product-Service running on port: " + port + " - " + currency.getEnviroment());// + " - " + cambio.getAmbiente());
		return ResponseEntity.ok(product);
	}
	
	@GetMapping("/{targetCurrency}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200")})
	public ResponseEntity<Page<ProductEntity>> getAllProducts(
			@PathVariable String targetCurrency,
			@ParameterObject
			@PageableDefault(page = 0,size = 5,sort = "description", direction = Direction.ASC) 
				Pageable pageable) throws Exception {
		Page<ProductEntity> products = repository.findAll(pageable);
		for (ProductEntity product : products) {
			CurrencyResponse currency = currencyClient.getCurrency(product.getPrice(),product.getCurrency(),targetCurrency);
			
			product.setConvertedPrice(currency.getConvertedValue());
			//Setar o ambiente
			product.setEnviroment("Product-Service running on port: " + port + " - " + currency.getEnviroment());// + " - " + cambio.getAmbiente());
		}
		return ResponseEntity.ok(products);
	}
	
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception e) {
		String cleanMessage = e.getMessage().replaceAll("[\\r\\n]", " ");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cleanMessage);
	}

}

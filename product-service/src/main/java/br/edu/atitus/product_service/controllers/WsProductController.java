package br.edu.atitus.product_service.controllers;

import javax.security.sasl.AuthenticationException;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.atitus.product_service.dtos.ProductDTO;
import br.edu.atitus.product_service.entities.ProductEntity;
import br.edu.atitus.product_service.repositories.ProductRepository;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@ApiResponses(value = {
		@ApiResponse(responseCode = "400", description = "Erro de validação ou requisição inválida", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class), examples = @ExampleObject(value = "Dados inválidos!!!"))),
		@ApiResponse(responseCode = "403", description = "Acesso não permitido", content = @Content(mediaType = "none")),
		@ApiResponse(responseCode = "401", description = "Acesso não permitido", content = @Content(mediaType = "none"))
})
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("ws/products")
public class WsProductController {
	
	private final ProductRepository repository;

	public WsProductController(ProductRepository repository) {
		super();
		this.repository = repository;
	}
	
	private ProductEntity convertDTO2Entity(ProductDTO dto) {
		var product = new ProductEntity();
		BeanUtils.copyProperties(dto, product);
		return product;
	}
	
	@PostMapping()
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntity.class))), })
	public ResponseEntity<ProductEntity> post(
			@RequestBody ProductDTO dto,
			 @RequestHeader("X-User-Id") @Parameter(hidden = true) Long userId,
			 @RequestHeader("X-User-Email") @Parameter(hidden = true) String userEmail,
			 @RequestHeader("X-User-Type") @Parameter(hidden = true) Integer userType) throws Exception {
		
//		System.out.println("Usuário ID: " + userId);
//	    System.out.println("Usuário Email: " + userEmail);
//	    System.out.println("Usuário Tipo: " + userType);
		
	    if (userType != 0) throw new AuthenticationException("Usuário sem permissão!");
	    
		var product = convertDTO2Entity(dto);
		product.setStock(10);
		repository.save(product);
		return ResponseEntity.status(HttpStatus.CREATED).body(product);
	}
	
	@PutMapping("/{idProduct}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200")})
	public ResponseEntity<ProductEntity> put(
			@PathVariable Long idProduct,
			@RequestBody ProductDTO dto,
			 @RequestHeader("X-User-Id") @Parameter(hidden = true) Long userId,
			 @RequestHeader("X-User-Email") @Parameter(hidden = true) String userEmail,
			 @RequestHeader("X-User-Type") @Parameter(hidden = true) Integer userType) throws Exception {
		
		if (userType != 0) throw new AuthenticationException("Usuário sem permissão!");
		
		var product = convertDTO2Entity(dto);
		product.setId(idProduct);
		repository.save(product);
		return ResponseEntity.status(HttpStatus.CREATED).body(product);
	}
	
	@DeleteMapping("/{idProduct}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200")})
	public ResponseEntity<Object> delete(
			@PathVariable Long idProduct,
			 @RequestHeader("X-User-Id") @Parameter(hidden = true) Long userId,
			 @RequestHeader("X-User-Email") @Parameter(hidden = true) String userEmail,
			 @RequestHeader("X-User-Type") @Parameter(hidden = true) Integer userType) throws Exception {
		
		if (userType != 0) throw new AuthenticationException("Usuário sem permissão!");
		
		
		repository.deleteById(idProduct);
		return ResponseEntity.ok(null);
	}
	
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception e) {
		String cleanMessage = e.getMessage().replaceAll("[\\r\\n]", " ");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cleanMessage);
	}
	
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<String> handleExceptionAuth(AuthenticationException e) {
		String cleanMessage = e.getMessage().replaceAll("[\\r\\n]", " ");
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(cleanMessage);
	}

}

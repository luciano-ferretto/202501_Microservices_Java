package br.edu.atitus.auth_service.controllers;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.atitus.auth_service.components.JwtUtil;
import br.edu.atitus.auth_service.dtos.SigninDTO;
import br.edu.atitus.auth_service.dtos.SignupDTO;
import br.edu.atitus.auth_service.entities.UserEntity;
import br.edu.atitus.auth_service.entities.UserType;
import br.edu.atitus.auth_service.services.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/auth")
@ApiResponses(value = {
		@ApiResponse(responseCode = "400", description = "Erro de validação ou requisição inválida", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class), examples = @ExampleObject(value = "Dados inválidos!!!"))) })
public class AuthController {

	private UserService service;
	private final AuthenticationConfiguration authConfig;

	public AuthController(UserService service, AuthenticationConfiguration authConfig) {
		super();
		this.service = service;
		this.authConfig = authConfig;
	}

	private UserEntity convertDTO2Entity(SignupDTO dto) {
		var user = new UserEntity();
		BeanUtils.copyProperties(dto, user);
		return user;
	}

	@PostMapping("/signup")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserEntity.class))), })
	public ResponseEntity<UserEntity> signup(@RequestBody SignupDTO dto) throws Exception {
		var user = convertDTO2Entity(dto);
		user.setType(UserType.Common);
		service.save(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(user);
	}

	@PostMapping("/signin")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Sucesso", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class), examples = @ExampleObject(value = "json WebToken!"))),
			@ApiResponse(responseCode = "401", description = "Credinciais de autenticação inválidas", content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class), examples = @ExampleObject(value = "Usuário inexistente ou senha inválida"))) })
	public ResponseEntity<String> PostSignin(@RequestBody SigninDTO signin) {
		try {
			authConfig.getAuthenticationManager()
					.authenticate(new UsernamePasswordAuthenticationToken(signin.email(), signin.password()));
		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("error", e.getMessage()).build();
		}
		UserEntity user = (UserEntity) service.loadUserByUsername(signin.email());
		return ResponseEntity.ok(JwtUtil.generateToken(user.getEmail(), user.getId(), user.getType()));

	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception e) {
		String cleanMessage = e.getMessage().replaceAll("[\\r\\n]", " ");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cleanMessage);
	}
}
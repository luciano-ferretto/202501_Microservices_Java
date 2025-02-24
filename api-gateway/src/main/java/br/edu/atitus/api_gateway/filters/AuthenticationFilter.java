package br.edu.atitus.api_gateway.filters;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import br.edu.atitus.api_gateway.components.JwtUtil;
import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

	// Lista de rotas públicas que NÃO precisam de autenticação
	private static final List<String> PROTECTED_ROUTES = List.of("/ws/");

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		String path = request.getURI().getPath();

		// Se a rota não for protegida, pula a verificação de autenticação
		if (!PROTECTED_ROUTES.stream().anyMatch(path::startsWith)) {
			return chain.filter(exchange);
		}
		
		// Verifica se o cabeçalho Authorization existe e contém um token válido
		String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			Claims payload = JwtUtil.validateToken(token);
			if (payload != null) {
				// Adiciona o ID do usuário no cabeçalho antes de repassar a requisição
				ServerHttpRequest modifiedRequest = request.mutate()
						.header("X-User-Id", String.valueOf(payload.get("id", Long.class))) // Convertendo para String
						.header("X-User-Email", payload.get("email", String.class))
						.header("X-User-Type", String.valueOf(payload.get("type", Integer.class))) // Adicionando o tipo do usuário
						.build();

				return chain.filter(exchange.mutate().request(modifiedRequest).build());
			}
		}

		// Retorna erro 401 caso o token seja inválido ou não exista
		exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
		return exchange.getResponse().setComplete();
	}

	@Override
	public int getOrder() {
		return -1; // Prioridade alta para rodar antes de outros filtros
	}

}

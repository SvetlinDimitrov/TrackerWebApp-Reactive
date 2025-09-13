package org.nutriGuideBuddy.config.security.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.nutriGuideBuddy.config.security.service.JwtTokenService;
import org.nutriGuideBuddy.config.security.service.ReactiveUserDetailsServiceImpl;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter implements WebFilter {

  public static final String HEADER_PREFIX = "Bearer ";
  private final JwtTokenService tokenService;
  private final ReactiveUserDetailsServiceImpl userDetailsService;
  private final ObjectMapper objectMapper;

  @NotNull
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, @NotNull WebFilterChain chain) {
    String token = resolveToken(exchange.getRequest());

    if (StringUtils.hasText(token)) {
      return tokenService
          .validateToken(token)
          .subscribeOn(Schedulers.boundedElastic())
          .flatMap(
              email ->
                  userDetailsService
                      .findByUsername(email)
                      .flatMap(
                          userDetails -> {
                            UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            return chain
                                .filter(exchange)
                                .contextWrite(
                                    ReactiveSecurityContextHolder.withAuthentication(
                                        authentication));
                          }))
          .onErrorResume(ex -> writeProblemDetail(exchange, ex));
    }

    return chain.filter(exchange);
  }

  private String resolveToken(ServerHttpRequest request) {
    String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(HEADER_PREFIX)) {
      return bearerToken.substring(HEADER_PREFIX.length());
    }
    return null;
  }

  private Mono<Void> writeProblemDetail(ServerWebExchange exchange, Throwable ex) {
    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
    problemDetail.setTitle("Validation Error");
    problemDetail.setDetail(ex.getMessage());
    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    byte[] bytes;
    try {
      bytes = objectMapper.writeValueAsBytes(problemDetail);
    } catch (JsonProcessingException e) {
      bytes = new byte[0];
    }
    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
    return exchange.getResponse().writeWith(Mono.just(buffer));
  }
}

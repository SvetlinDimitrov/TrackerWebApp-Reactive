package org.nutriGuideBuddy.infrastructure.security.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.nutriGuideBuddy.infrastructure.security.service.JwtTokenService;
import org.nutriGuideBuddy.infrastructure.security.service.ReactiveUserDetailsServiceImpl;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@Slf4j
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
    HttpStatus status = resolveHttpStatus(ex);
    String title = resolveTitle(ex);

    ProblemDetail problemDetail = ProblemDetail.forStatus(status);
    problemDetail.setTitle(title);
    problemDetail.setDetail(ex.getMessage());

    exchange.getResponse().setStatusCode(status);
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

  private HttpStatus resolveHttpStatus(Throwable ex) {
    HttpStatus status;
    if (ex instanceof AuthenticationException) {
      status = HttpStatus.UNAUTHORIZED;
    } else if (ex instanceof AccessDeniedException) {
      status = HttpStatus.FORBIDDEN;
    } else if (ex instanceof io.jsonwebtoken.ExpiredJwtException) {
      status = HttpStatus.UNAUTHORIZED;
    } else if (ex instanceof JwtException) {
      status = HttpStatus.UNAUTHORIZED;
    } else if (ex instanceof IllegalArgumentException) {
      status = HttpStatus.BAD_REQUEST;
    } else {
      status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    log.error(
        "Resolved HttpStatus [{}] for exception [{}]: {}",
        status,
        ex.getClass().getName(),
        ex.getMessage());

    return status;
  }

  private String resolveTitle(Throwable ex) {
    if (ex instanceof org.springframework.security.core.AuthenticationException) {
      return "Authentication Failed";
    }
    if (ex instanceof org.springframework.security.access.AccessDeniedException) {
      return "Access Denied";
    }
    if (ex instanceof io.jsonwebtoken.ExpiredJwtException) {
      return "Token Expired";
    }
    if (ex instanceof io.jsonwebtoken.JwtException) {
      return "Invalid Token";
    }
    if (ex instanceof IllegalArgumentException) {
      return "Bad Request";
    }
    return "Internal Server Error";
  }
}

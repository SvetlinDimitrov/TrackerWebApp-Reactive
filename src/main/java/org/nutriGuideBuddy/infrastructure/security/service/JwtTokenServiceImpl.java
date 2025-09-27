package org.nutriGuideBuddy.infrastructure.security.service;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.INVALID_JWT_TOKEN;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.infrastructure.security.dto.JwtToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtTokenServiceImpl implements JwtTokenService {

  @Value("${jwt.token.secret}")
  private String secretKeyConfig;

  @Value("${jwt.token.expiration}")
  private long tokenExpirationSeconds;

  private SecretKey secretKey;

  @PostConstruct
  public void init() {
    byte[] keyBytes = Base64.getDecoder().decode(secretKeyConfig);
    if (keyBytes.length < 32) {
      log.warn(
          "jwt.token.secret should be >= 32 bytes (256-bit) for HS256. Current: {} bytes",
          keyBytes.length);
    }
    this.secretKey = Keys.hmacShaKeyFor(keyBytes);
  }

  @Override
  public JwtToken generateToken(String email) {
    Date expireAt = new Date(System.currentTimeMillis() + tokenExpirationSeconds * 1000);

    String token =
        Jwts.builder()
            .subject(email)
            .expiration(expireAt)
            .signWith(secretKey)
            .compact();

    return new JwtToken(
        token, expireAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
  }

  @Override
  public Mono<String> validateToken(String token) {
    try {
      Claims claims =
          Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();

      return Mono.just(claims.getSubject());
    } catch (Exception e) {
      log.warn("Invalid JWT token: {}", e.getMessage());
      return Mono.error(new AccessDeniedException(INVALID_JWT_TOKEN));
    }
  }
}

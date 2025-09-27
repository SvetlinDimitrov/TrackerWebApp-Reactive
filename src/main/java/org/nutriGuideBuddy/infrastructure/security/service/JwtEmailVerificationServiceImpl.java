package org.nutriGuideBuddy.infrastructure.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.infrastructure.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class JwtEmailVerificationServiceImpl implements JwtEmailVerificationService {

  @Value("${jwt.email.token.secret}")
  private String secretKeyConfig; // Base64-encoded

  @Value("${jwt.email.token.expiration}")
  private long emailTokenExpirationSeconds;

  private SecretKey secretKey;

  @PostConstruct
  public void init() {
    byte[] keyBytes = Base64.getDecoder().decode(secretKeyConfig);
    if (keyBytes.length < 32) {
      log.warn(
          "jwt.email.token.secret should be >= 32 bytes (256-bit) for HS256. Current: {} bytes",
          keyBytes.length);
    }
    this.secretKey = Keys.hmacShaKeyFor(keyBytes);
  }

  @Override
  public String generateToken(String email) {
    Date exp = new Date(System.currentTimeMillis() + emailTokenExpirationSeconds * 1000);
    return Jwts.builder()
        .subject(email)
        .expiration(exp)
        .signWith(secretKey) // HS256 inferred
        .compact();
  }

  @Override
  public Mono<String> validateToken(String token) {
    try {
      Claims claims =
          Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();

      return Mono.just(claims.getSubject());
    } catch (Exception e) {
      log.warn("Invalid JWT token: {}", e.getMessage());
      return Mono.error(BadRequestException.of("JWT", "invalid token"));
    }
  }
}

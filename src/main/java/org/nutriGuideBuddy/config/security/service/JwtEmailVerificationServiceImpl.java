package org.nutriGuideBuddy.config.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.exceptions.BadRequestException;
import org.nutriGuideBuddy.exceptions.ExceptionMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class JwtEmailVerificationServiceImpl implements JwtEmailVerificationService {

  @Value("${jwt.email.token.secret}")
  private String secretKeyConfig;

  @Value("${jwt.email.token.expiration}")
  private long emailTokenExpirationSeconds;

  private Key secretKey;

  @PostConstruct
  public void init() {
    secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyConfig));
  }

  @Override
  public String generateToken(String email) {
    Date expireAt = new Date(System.currentTimeMillis() + emailTokenExpirationSeconds * 1000);
    return Jwts.builder().setSubject(email).setExpiration(expireAt).signWith(secretKey).compact();
  }

  @Override
  public Mono<String> validateToken(String token) {
    try {
      Claims claims =
          Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();

      return Mono.just(claims.getSubject());
    } catch (Exception e) {
      log.warn("Invalid JWT token: {}", e.getMessage());
      return Mono.error(new BadRequestException(ExceptionMessages.INVALID_JWT_TOKEN));
    }
  }
}

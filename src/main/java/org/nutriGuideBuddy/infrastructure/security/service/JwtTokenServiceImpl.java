package org.nutriGuideBuddy.infrastructure.security.service;

import static org.nutriGuideBuddy.infrastructure.exceptions.ExceptionMessages.INVALID_JWT_TOKEN;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.time.ZoneId;
import java.util.Date;
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

  private Key secretKey;

  @PostConstruct
  public void init() {
    secretKey = Keys.hmacShaKeyFor(secretKeyConfig.getBytes());
  }

  @Override
  public JwtToken generateToken(String email) {
    Date expireAt = new Date(System.currentTimeMillis() + tokenExpirationSeconds * 1000);
    return new JwtToken(
        Jwts.builder()
            .setSubject(email)
            .setExpiration(expireAt)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact(),
        expireAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
  }

  @Override
  public Mono<String> validateToken(String token) {
    try {
      Claims claims =
          Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();

      return Mono.just(claims.getSubject());
    } catch (io.jsonwebtoken.security.SignatureException e) {
      log.warn("Invalid JWT token: {}", e.getMessage());
    } catch (io.jsonwebtoken.MalformedJwtException e) {
      log.warn("Invalid JWT token: It is malformed.");
    } catch (io.jsonwebtoken.ExpiredJwtException e) {
      log.warn("Expired JWT token: {}", e.getMessage());
    } catch (io.jsonwebtoken.UnsupportedJwtException e) {
      log.warn("Unsupported JWT token: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      log.warn("JWT token compact of handler are invalid: {}", e.getMessage());
    }

    return Mono.error(new AccessDeniedException(INVALID_JWT_TOKEN));
  }
}

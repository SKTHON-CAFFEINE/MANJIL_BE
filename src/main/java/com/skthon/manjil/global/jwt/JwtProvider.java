package com.skthon.manjil.global.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.skthon.manjil.domain.auth.exception.AuthErrorCode;
import com.skthon.manjil.global.exception.CustomException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtProvider {

  private Key key;
  private final String secretKey;
  @Getter private final long accessTokenExpireTime;
  @Getter private final long refreshTokenExpireTime;

  public JwtProvider(
      @Value("${spring.jwt.secret}") String secretKey,
      @Value("${spring.jwt.access-token-expire-time}") long accessTokenExpireTime,
      @Value("${spring.jwt.refresh-token-expire-time}") long refreshTokenExpireTime) {
    this.secretKey = secretKey;
    this.accessTokenExpireTime = accessTokenExpireTime;
    this.refreshTokenExpireTime = refreshTokenExpireTime;
  }

  @PostConstruct
  public void init() {
    byte[] keyBytes = Base64.getDecoder().decode(secretKey);
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  public String createAccessToken(Long userId) {
    return createToken(userId, accessTokenExpireTime);
  }

  public String createRefreshToken(Long userId) {
    return createToken(userId, refreshTokenExpireTime);
  }

  private String createToken(Long userId, long expireTimeMillis) {
    Date now = new Date();
    return Jwts.builder()
        .setSubject(String.valueOf(userId))
        .setId(UUID.randomUUID().toString())
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + expireTimeMillis))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (ExpiredJwtException e) {
      throw new CustomException(AuthErrorCode.JWT_TOKEN_EXPIRED);
    } catch (UnsupportedJwtException e) {
      throw new CustomException(AuthErrorCode.UNSUPPORTED_TOKEN);
    } catch (MalformedJwtException e) {
      throw new CustomException(AuthErrorCode.MALFORMED_JWT_TOKEN);
    } catch (io.jsonwebtoken.SignatureException e) {
      throw new CustomException(AuthErrorCode.INVALID_SIGNATURE);
    } catch (IllegalArgumentException e) {
      throw new CustomException(AuthErrorCode.ILLEGAL_ARGUMENT);
    }
  }

  public Long extractUserId(String token) {
    return Long.parseLong(parseClaims(token).getSubject());
  }

  public String extractTokenId(String token) {
    return parseClaims(token).getId();
  }

  public Date extractExpiration(String token) {
    return parseClaims(token).getExpiration();
  }

  private Claims parseClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }
}

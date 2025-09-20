package com.skthon.manjil.domain.auth.service;

import java.time.Duration;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.skthon.manjil.domain.auth.exception.AuthErrorCode;
import com.skthon.manjil.domain.user.dto.request.UserRequest;
import com.skthon.manjil.domain.user.dto.response.UserResponse;
import com.skthon.manjil.domain.user.entity.User;
import com.skthon.manjil.domain.user.exception.UserErrorCode;
import com.skthon.manjil.domain.user.mapper.UserMapper;
import com.skthon.manjil.domain.user.repository.UserRepository;
import com.skthon.manjil.global.exception.CustomException;
import com.skthon.manjil.global.jwt.JwtProvider;
import com.skthon.manjil.infra.redis.RedisUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;
  private final RedisUtil redisUtil;
  private final UserMapper userMapper;

  private static final String REFRESH_TOKEN_PREFIX = "user:refresh:";

  @Value("${cookie.secure}")
  private boolean secure;

  /** 일반 로그인 */
  public UserResponse login(UserRequest.LoginRequest loginRequest, HttpServletResponse response) {
    User user = validateUserCredentials(loginRequest);
    return issueTokensAndSetResponse(user, response);
  }

  /** 테스트 로그인 (ID=1 고정) */
  public UserResponse testLogin(HttpServletResponse response) {
    User user =
        userRepository
            .findById(1L)
            .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
    return issueTokensAndSetResponse(user, response);
  }

  /** 로그아웃: AccessToken 블랙리스트, RefreshToken 삭제, 쿠키 만료 */
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    String accessToken = resolveAccessToken(request);
    if (accessToken == null || !jwtProvider.validateToken(accessToken)) {
      throw new CustomException(AuthErrorCode.INVALID_ACCESS_TOKEN);
    }

    long expiration =
        jwtProvider.extractExpiration(accessToken).getTime() - System.currentTimeMillis();
    redisUtil.setData("blacklist:" + accessToken, "logout", expiration / 1000);

    Long userId = jwtProvider.extractUserId(accessToken);
    redisUtil.deleteData(REFRESH_TOKEN_PREFIX + userId);

    deleteRefreshTokenCookie(response);
  }

  /** 액세스 토큰 재발급 */
  public void reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
    String refreshToken = extractRefreshTokenFromCookie(request);
    if (refreshToken == null || !jwtProvider.validateToken(refreshToken)) {
      throw new CustomException(AuthErrorCode.REFRESH_TOKEN_REQUIRED);
    }

    Long userId = jwtProvider.extractUserId(refreshToken);
    String storedToken = redisUtil.getData(REFRESH_TOKEN_PREFIX + userId);
    if (!refreshToken.equals(storedToken)) {
      throw new CustomException(AuthErrorCode.REFRESH_TOKEN_REQUIRED);
    }

    String newAccessToken = jwtProvider.createAccessToken(userId);
    setAccessTokenHeader(response, newAccessToken);
  }

  // ===== 내부 유틸 =====

  // 자격 증명 검증 (이메일/비밀번호)
  private User validateUserCredentials(UserRequest.LoginRequest loginRequest) {
    User user =
        userRepository
            .findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new CustomException(AuthErrorCode.INVALID_PASSWORD));

    if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
      throw new CustomException(AuthErrorCode.INVALID_PASSWORD);
    }
    return user;
  }

  // 토큰 발급 + 응답 세팅 + 사용자 응답 DTO
  private UserResponse issueTokensAndSetResponse(User user, HttpServletResponse response) {
    String accessToken = jwtProvider.createAccessToken(user.getId());
    String refreshToken = jwtProvider.createRefreshToken(user.getId());

    long refreshTokenExpireSeconds = jwtProvider.getRefreshTokenExpireTime() / 1000;
    redisUtil.setData(REFRESH_TOKEN_PREFIX + user.getId(), refreshToken, refreshTokenExpireSeconds);

    setAccessTokenHeader(response, accessToken);
    setRefreshTokenCookie(response, refreshToken, refreshTokenExpireSeconds);

    return userMapper.toResponse(user);
  }

  private void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
    response.setHeader("Authorization", "Bearer " + accessToken);
  }

  private void setRefreshTokenCookie(
      HttpServletResponse response, String refreshToken, long maxAgeSec) {
    ResponseCookie.ResponseCookieBuilder cookie =
        ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .path("/")
            .maxAge(Duration.ofSeconds(maxAgeSec));

    if (secure) {
      cookie.secure(true).sameSite("None");
    } else {
      cookie.secure(false).sameSite("Lax");
    }
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.build().toString());
  }

  private String extractRefreshTokenFromCookie(HttpServletRequest request) {
    if (request.getCookies() == null) {
      return null;
    }
    for (Cookie cookie : request.getCookies()) {
      if ("refreshToken".equals(cookie.getName())) {
        return cookie.getValue();
      }
    }
    return null;
  }

  private String resolveAccessToken(HttpServletRequest request) {
    String bearer = request.getHeader("Authorization");
    if (bearer != null && bearer.startsWith("Bearer ")) {
      return bearer.substring(7);
    }
    return null;
  }

  private void deleteRefreshTokenCookie(HttpServletResponse response) {
    ResponseCookie.ResponseCookieBuilder cookie =
        ResponseCookie.from("refreshToken", "").httpOnly(true).path("/").maxAge(Duration.ZERO);

    if (secure) {
      cookie.secure(true).sameSite("None");
    } else {
      cookie.secure(false).sameSite("Lax");
    }
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.build().toString());
  }

  /** 현재 세션(토큰)을 무효화 */
  public void invalidateCurrentSessionQuietly(
      HttpServletRequest request, HttpServletResponse response) {
    try {
      logout(request, response);
    } catch (CustomException ignore) {
      deleteRefreshTokenCookie(response);
    }
  }
}

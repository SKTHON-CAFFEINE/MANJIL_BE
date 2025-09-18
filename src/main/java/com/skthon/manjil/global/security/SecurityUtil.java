package com.skthon.manjil.global.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.skthon.manjil.domain.auth.exception.AuthErrorCode;
import com.skthon.manjil.global.exception.CustomException;

@Component
public class SecurityUtil {

  public static Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      throw new CustomException(AuthErrorCode.INVALID_AUTH_CONTEXT);
    }

    Object principal = authentication.getPrincipal();
    if (!(principal instanceof CustomUserDetails userDetails)) {
      throw new CustomException(AuthErrorCode.AUTHENTICATION_NOT_FOUND);
    }

    return userDetails.getUserId();
  }

  public static boolean isAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null && authentication.isAuthenticated();
  }
}

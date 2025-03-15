package com.WearWeather.wear.global.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class JwtCookieManager {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final String DOMAIN = "lookattheweather.store";

    private static final int ACCESS_TOKEN_EXPIRATION = 24 * 60 * 60; // 24시간 (초 단위)
    private static final int REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60; // 7일 (초 단위)

    public void saveAccessTokenToCookie(HttpServletResponse response, String accessToken) {
        ResponseCookie accessTokenCookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, accessToken)
          .path("/")
          .httpOnly(true)
          .secure(false)
          .sameSite("Lax")
//          .domain(DOMAIN)
          .maxAge(ACCESS_TOKEN_EXPIRATION)
          .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
    }

    public void saveRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie refreshTokenCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
          .path("/")
          .httpOnly(true)
          .secure(false)
          .sameSite("Lax")
//          .domain(DOMAIN)
          .maxAge(REFRESH_TOKEN_EXPIRATION)
          .build();

        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }

    public void clearAuthCookies(HttpServletResponse response) {
        ResponseCookie accessTokenCookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, "")
          .path("/")
          .httpOnly(true)
          .secure(false)
          .sameSite("Lax")
//          .domain(DOMAIN)
          .maxAge(0) // 즉시 만료
          .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
          .path("/")
          .httpOnly(true)
          .secure(false)
          .sameSite("Lax")
//          .domain(DOMAIN)
          .maxAge(0) // 즉시 만료
          .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }
}

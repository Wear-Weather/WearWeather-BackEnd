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
    private static final String LOCALHOST = "localhost";

    private static final int ACCESS_TOKEN_EXPIRATION = 24 * 60 * 60; // 24시간
    private static final int REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60; // 7일

    private boolean isLocalRequest(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        return origin != null && (origin.contains(LOCALHOST) || origin.contains("127.0.0.1"));
    }

    private ResponseCookie createCookie(String name, String value, boolean isLocal, int maxAge) {
        return ResponseCookie.from(name, value)
          .path("/")
          .httpOnly(true)
          .secure(!isLocal)  // 로컬이면 false, 운영이면 true
          .sameSite(isLocal ? "Lax" : "Strict")  // 로컬은 Lax, 운영은 Strict
          .domain(isLocal ? LOCALHOST : DOMAIN) // 로컬이면 localhost, 운영이면 도메인 지정
          .maxAge(maxAge)
          .build();
    }

    public void saveAccessTokenToCookie(HttpServletRequest request, HttpServletResponse response, String accessToken) {
        boolean isLocal = isLocalRequest(request);
        ResponseCookie accessTokenCookie = createCookie(ACCESS_TOKEN_COOKIE_NAME, accessToken, isLocal, ACCESS_TOKEN_EXPIRATION);
        response.addHeader("Set-Cookie", accessTokenCookie.toString());
    }

    public void saveRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        boolean isLocal = isLocalRequest(request);
        ResponseCookie refreshTokenCookie = createCookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken, isLocal, REFRESH_TOKEN_EXPIRATION);
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }

    public void clearAuthCookies(HttpServletRequest request, HttpServletResponse response) {
        boolean isLocal = isLocalRequest(request);

        ResponseCookie accessTokenCookie = createCookie(ACCESS_TOKEN_COOKIE_NAME, "", isLocal, 0);
        ResponseCookie refreshTokenCookie = createCookie(REFRESH_TOKEN_COOKIE_NAME, "", isLocal, 0);

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }
}

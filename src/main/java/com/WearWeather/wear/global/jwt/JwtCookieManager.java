package com.WearWeather.wear.global.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class JwtCookieManager {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final String DOMAIN = "lookattheweather.store";

    private static final int ACCESS_TOKEN_EXPIRATION = 60 * 600; // 10시간 (초 단위)
    private static final int REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60; // 7일 (초 단위)

    public void saveAccessTokenToCookie(HttpServletResponse response, String accessToken) {
        Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, accessToken);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setDomain(DOMAIN);
        accessTokenCookie.setMaxAge(ACCESS_TOKEN_EXPIRATION);
        response.addCookie(accessTokenCookie);
    }

    public void saveRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setDomain(DOMAIN);
        refreshTokenCookie.setMaxAge(REFRESH_TOKEN_EXPIRATION);
        response.addCookie(refreshTokenCookie);
    }

    public void clearAuthCookies(HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, "");
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setDomain(DOMAIN);
        accessTokenCookie.setMaxAge(0); // 즉시 만료
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setDomain(DOMAIN);
        refreshTokenCookie.setMaxAge(0); // 즉시 만료
        response.addCookie(refreshTokenCookie);
    }
}

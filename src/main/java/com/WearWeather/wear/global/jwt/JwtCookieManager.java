package com.WearWeather.wear.global.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtCookieManager {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final String DOMAIN = "lookattheweather.store";
    private static final String LOCALHOST = "localhost";

    private static final int ACCESS_TOKEN_EXPIRATION = 24 * 60 * 60; // 24시간
    private static final int REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60; // 7일

    /**
     * 요청을 보낸 클라이언트가 localhost인지 확인 (Origin 기반)
     */
    private boolean isLocalRequest(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        boolean isLocal = origin != null && (origin.contains(LOCALHOST) || origin.contains("127.0.0.1"));

        log.info("요청 Origin: {}", origin);
        log.info("요청이 로컬인가? {}", isLocal ? "YES (Localhost)" : "NO (Production)");

        return isLocal;
    }

    /**
     * 공통적으로 사용되는 쿠키 생성 메서드
     */
    private ResponseCookie createCookie(String name, String value, boolean isLocal, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
          .path("/")
          .httpOnly(true)
          .secure(!isLocal)  // 로컬이면 false, 운영이면 true
          .sameSite(isLocal ? "Lax" : "Strict")  // 로컬은 Lax, 운영은 Strict
          .domain(isLocal ? LOCALHOST : DOMAIN) // 로컬이면 localhost, 운영이면 도메인 지정
          .maxAge(maxAge)
          .build();

        log.info("쿠키 생성: [{}]", name);
        log.info("   - 값: {}", value.isEmpty() ? "삭제됨 (Empty)" : "설정됨");
        log.info("   - Secure: {}", cookie.isSecure() ? "true (운영)" : "false (로컬)");
        log.info("   - SameSite: {}", cookie.getSameSite());
        log.info("   - Domain: {}", cookie.getDomain());
        log.info("   - Max Age: {} 초", maxAge);

        return cookie;
    }

    public void saveAccessTokenToCookie(HttpServletRequest request, HttpServletResponse response, String accessToken) {
        boolean isLocal = isLocalRequest(request);
        ResponseCookie accessTokenCookie = createCookie(ACCESS_TOKEN_COOKIE_NAME, accessToken, isLocal, ACCESS_TOKEN_EXPIRATION);

        log.info("📢 [쿠키 생성] Set-Cookie 헤더 추가: {}", accessTokenCookie.toString());

        response.addHeader("Set-Cookie", accessTokenCookie.toString());

        // 응답 직후에도 헤더 확인 (이 부분은 null일 가능성이 있음)
        log.info("📢 [Set-Cookie 응답 확인] {}", response.getHeader("Set-Cookie"));
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

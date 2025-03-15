package com.WearWeather.wear.domain.oauth.controller;

import com.WearWeather.wear.domain.auth.dto.response.LoginResponse;
import com.WearWeather.wear.domain.auth.dto.response.TokenResponse;
import com.WearWeather.wear.domain.oauth.infrastructure.kakao.KakaoLoginParam;
import com.WearWeather.wear.domain.oauth.service.OAuthLoginService;
import com.WearWeather.wear.global.jwt.JwtCookieManager;
import com.WearWeather.wear.global.jwt.TokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
@Slf4j
public class OAuthController {

    private final OAuthLoginService oAuthLoginService;
    private final TokenProvider tokenProvider;
    private final JwtCookieManager jwtCookieManager;

    @GetMapping("/kakao")
    public ResponseEntity<Void> kakaoLogin(@RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response) {
        KakaoLoginParam param = new KakaoLoginParam(code);
        TokenResponse tokenResponse  = oAuthLoginService.login(param);
        jwtCookieManager.saveAccessTokenToCookie(request,response, tokenResponse.getAccessToken());
        jwtCookieManager.saveRefreshTokenToCookie(request,response, tokenResponse.getRefreshToken());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
package com.WearWeather.wear.domain.oauth.controller;

import com.WearWeather.wear.domain.auth.dto.response.LoginResponse;
import com.WearWeather.wear.domain.oauth.infrastructure.kakao.KakaoLoginParam;
import com.WearWeather.wear.domain.oauth.service.OAuthLoginService;
import com.WearWeather.wear.global.jwt.TokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/kakao")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestParam("code") String code, HttpServletResponse response) {
        KakaoLoginParam param = new KakaoLoginParam(code);
        LoginResponse loginResponse = oAuthLoginService.login(param);

        String refreshToken = tokenProvider.renewRefreshToken(loginResponse.getAccessToken());
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshTokenCookie);

        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }
}

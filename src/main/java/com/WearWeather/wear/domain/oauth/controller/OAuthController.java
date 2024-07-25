package com.WearWeather.wear.domain.oauth.controller;

import com.WearWeather.wear.domain.auth.dto.response.LoginResponse;
import com.WearWeather.wear.domain.oauth.infrastructure.kakao.KakaoLoginParam;
import com.WearWeather.wear.domain.oauth.service.OAuthLoginService;
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

    @GetMapping("/kakao")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestParam("code") String code) {
        KakaoLoginParam param = new KakaoLoginParam(code);
        LoginResponse response = oAuthLoginService.login(param);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

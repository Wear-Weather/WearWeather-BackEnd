package com.WearWeather.wear.domain.oauth.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// 임시 카카오 로그인 페이지 생성 - 프론트 작업 완료 시 삭제 예정
@Controller
@RequestMapping("/login")
public class KakaoLoginPageController {

    @Value("${oauth.kakao.client-id}")
    private String clientId;

    @Value("${oauth.kakao.redirect-uri}")
    private String redirect_uri;

    @GetMapping("/page")
    public String loginPage(Model model) {
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirect_uri;
        model.addAttribute("location", location);

        return "login";
    }
}

package com.WearWeather.wear.domain.auth.controller;

import static com.WearWeather.wear.global.jwt.JwtFilter.AUTHORIZATION_HEADER;

import com.WearWeather.wear.domain.auth.dto.request.LoginRequest;
import com.WearWeather.wear.domain.auth.dto.response.LoginResponse;
import com.WearWeather.wear.domain.auth.entity.TokenInfo;
import com.WearWeather.wear.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return new ResponseEntity<>(authService.checkLogin(request), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(String userEmail, @RequestHeader(AUTHORIZATION_HEADER) String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");
        authService.logout(userEmail, token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenInfo> reissue(@RequestBody String refreshToken) {
        TokenInfo newToken = authService.reissue(refreshToken);
        return new ResponseEntity<>(newToken, HttpStatus.OK);
    }

}

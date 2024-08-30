package com.WearWeather.wear.domain.auth.controller;

import static com.WearWeather.wear.global.jwt.JwtFilter.AUTHORIZATION_HEADER;

import com.WearWeather.wear.domain.auth.dto.request.LoginRequest;
import com.WearWeather.wear.domain.auth.dto.request.RefresehTokenRequest;
import com.WearWeather.wear.domain.auth.dto.response.LoginResponse;
import com.WearWeather.wear.domain.auth.dto.response.TokenResponse;
import com.WearWeather.wear.domain.auth.service.AuthService;
import com.WearWeather.wear.global.common.dto.ResponseCommonDTO;
import com.WearWeather.wear.global.jwt.LoggedInUser;
import jakarta.validation.Valid;
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
        LoginResponse response = authService.checkLogin(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseCommonDTO> logout(@LoggedInUser Long userId, @RequestHeader(AUTHORIZATION_HEADER) String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");
        authService.logout(userId, token);
        return ResponseEntity.ok(new ResponseCommonDTO(true, "success logout"));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(@RequestBody RefresehTokenRequest request) {
        TokenResponse newToken = authService.reissue(request);
        return ResponseEntity.ok(newToken);
    }
}

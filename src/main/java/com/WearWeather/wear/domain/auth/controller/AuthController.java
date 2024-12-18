package com.WearWeather.wear.domain.auth.controller;

import static com.WearWeather.wear.global.jwt.JwtFilter.AUTHORIZATION_HEADER;

import com.WearWeather.wear.domain.auth.dto.request.LoginRequest;
import com.WearWeather.wear.domain.auth.dto.response.LoginResponse;
import com.WearWeather.wear.domain.auth.dto.response.TokenResponse;
import com.WearWeather.wear.domain.auth.facade.LogOutFacade;
import com.WearWeather.wear.domain.auth.facade.LoginFacade;
import com.WearWeather.wear.domain.auth.facade.ReissueFacade;
import com.WearWeather.wear.global.common.dto.ResponseCommonDTO;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import com.WearWeather.wear.global.jwt.LoggedInUser;
import com.WearWeather.wear.global.jwt.TokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Arrays;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginFacade loginFacade;
    private final LogOutFacade logOutFacade;
    private final ReissueFacade reissueFacade;
    private final TokenProvider tokenProvider;

    public AuthController(TokenProvider tokenProvider, LoginFacade loginFacade, LogOutFacade logOutFacade, ReissueFacade reissueFacade) {
        this.tokenProvider = tokenProvider;
        this.loginFacade = loginFacade;
        this.logOutFacade = logOutFacade;
        this.reissueFacade = reissueFacade;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = loginFacade.checkLogin(request);

        String refreshToken = tokenProvider.renewRefreshToken(loginResponse.getAccessToken());
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
          .path("/")
          .sameSite("Strict")
          .httpOnly(true)
          .secure(true)
          .domain("lookattheweather.store")
          .maxAge(7 * 24 * 60 * 60)
          .build();
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseCommonDTO> logout(@LoggedInUser Long userId, @RequestHeader(AUTHORIZATION_HEADER) String tokenHeader, HttpServletResponse response) {
        String token = tokenHeader.replace("Bearer ", "");
        logOutFacade.logout(userId, token);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
          .path("/")
          .sameSite("Strict")
          .httpOnly(true)
          .secure(true)
          .domain("lookattheweather.store")
          .maxAge(0)
          .build();
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        return ResponseEntity.ok(new ResponseCommonDTO(true, "success logout"));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_COOKIE);
        }

        String refreshToken = Arrays.stream(cookies)
          .filter(cookie -> "refreshToken".equals(cookie.getName()))
          .map(Cookie::getValue)
          .findFirst()
          .orElse(null);

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_REFRESH_TOKEN_IN_COOKIE);
        }

        TokenResponse newToken = reissueFacade.reissue(refreshToken);
        return ResponseEntity.ok(newToken);
    }
}

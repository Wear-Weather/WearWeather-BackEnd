package com.WearWeather.wear.domain.auth.facade;


import com.WearWeather.wear.domain.auth.dto.request.LoginRequest;
import com.WearWeather.wear.domain.auth.dto.response.LoginResponse;
import com.WearWeather.wear.domain.auth.dto.response.TokenResponse;
import com.WearWeather.wear.domain.auth.provider.AuthenticationProvider;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.global.jwt.JwtCookieManager;
import com.WearWeather.wear.global.jwt.TokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginFacade {

    private final AuthenticationProvider authenticationProvider;
    private final TokenProvider tokenProvider;
    private final UserService userService;

    public TokenResponse checkLogin(LoginRequest request) {
        User user = userService.getUserByEmail(request.getEmail());
        Authentication authentication = authenticationProvider.authenticateWithCredentials(request.getEmail(), request.getPassword());
        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(user.getUserId());

        return TokenResponse.of(accessToken, refreshToken);
    }
}

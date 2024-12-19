package com.WearWeather.wear.domain.auth.facade;


import com.WearWeather.wear.domain.auth.dto.request.LoginRequest;
import com.WearWeather.wear.domain.auth.dto.response.LoginResponse;
import com.WearWeather.wear.domain.auth.provider.AuthenticationProvider;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.global.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginFacade {

    private final AuthenticationProvider authenticationProvider;
    private final TokenProvider tokenProvider;
    private final UserService userService;

    public LoginResponse checkLogin(LoginRequest request) {
        User user = userService.getUserByEmail(request.getEmail());
        Authentication authentication = authenticationProvider.authenticateWithCredentials(request.getEmail(), request.getPassword());
        String accessToken = tokenProvider.createAccessToken(authentication);
        return LoginResponse.of(user, accessToken);
    }
}

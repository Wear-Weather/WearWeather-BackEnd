package com.WearWeather.wear.domain.auth.service;

import com.WearWeather.wear.domain.auth.dto.request.LoginRequest;
import com.WearWeather.wear.domain.auth.dto.request.RefresehTokenRequest;
import com.WearWeather.wear.domain.auth.dto.response.LoginResponse;
import com.WearWeather.wear.domain.auth.dto.response.TokenResponse;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.repository.UserRepository;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import com.WearWeather.wear.global.jwt.TokenProvider;
import com.WearWeather.wear.global.redis.RedisService;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RedisService redisService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;

    public LoginResponse checkLogin(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_IS_NULL_EXCEPTION));

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        String accessToken = tokenProvider.createAccessToken(user.getUserId(), authentication);
        String refreshToken = tokenProvider.createRefreshToken(user.getUserId());

        return LoginResponse.of(user, accessToken, refreshToken);
    }

    public void logout(Long userId, String accessToken) {
        validatedUserId(userId);

        Long accessTokenExpiration = tokenProvider.getExpiration(accessToken);
        redisService.logoutFromRedis(userId, accessToken, accessTokenExpiration);
    }

    public TokenResponse reissue(RefresehTokenRequest request) {
        Long userId = tokenProvider.getRefreshTokenInfo(request.getRefreshToken());

        User user = userService.getUserById(userId);
        validateRefreshToken(userId, request.getRefreshToken());

        Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

        String newAccessToken = tokenProvider.createAccessToken(user.getUserId(), authentication);
        String newRefreshToken = tokenProvider.createRefreshToken(user.getUserId());

        return new TokenResponse(newAccessToken, newRefreshToken);
    }


    private void validateRefreshToken(Long userId, String token) {
        String storedToken = redisService.getValues(userId);
        if (storedToken == null || !storedToken.equals(token)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    public User validatedUserId(Long userId) {

        return userRepository.findByUserId(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_EMAIL));

    }

}

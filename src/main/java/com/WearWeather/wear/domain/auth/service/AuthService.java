package com.WearWeather.wear.domain.auth.service;

import com.WearWeather.wear.domain.auth.dto.request.LoginRequest;
import com.WearWeather.wear.domain.auth.dto.request.RefresehTokenRequest;
import com.WearWeather.wear.domain.auth.dto.response.LoginResponse;
import com.WearWeather.wear.domain.auth.dto.response.TokenResponse;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.repository.UserRepository;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import com.WearWeather.wear.global.jwt.TokenProvider;
import com.WearWeather.wear.global.redis.RedisService;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RedisService redisService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public LoginResponse checkLogin(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_IS_NULL_EXCEPTION));

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(user.getEmail());

        return LoginResponse.of(user, accessToken, refreshToken);
    }

    public void logout(String email, String accessToken) {
        findByUserEmail(email);

        Long accessTokenExpiration = tokenProvider.getExpiration(accessToken);
        redisService.logoutFromRedis(email, accessToken, accessTokenExpiration);
    }

    public TokenResponse reissue(RefresehTokenRequest request) {
        String userEmail = tokenProvider.getRefreshTokenInfo(request.getRefreshToken());
        validateRefreshToken(userEmail, request.getRefreshToken());

        Authentication authentication = new UsernamePasswordAuthenticationToken(userEmail, null, Collections.emptyList());

        String newAccessToken = tokenProvider.createAccessToken(authentication);
        String newRefreshToken = tokenProvider.createRefreshToken(userEmail);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }


    private void validateRefreshToken(String userEmail, String token) {
        String storedToken = redisService.getValues(userEmail);
        if (storedToken == null || !storedToken.equals(token)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    private User findByUserEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_IS_NULL_EXCEPTION));
    }

}

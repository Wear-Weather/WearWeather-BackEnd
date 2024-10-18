package com.WearWeather.wear.domain.auth.service;

import com.WearWeather.wear.domain.auth.dto.request.LoginRequest;
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
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
        User user = userService.getUserByEmail(request.getEmail());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        String accessToken = tokenProvider.createAccessToken(authentication);
        return LoginResponse.of(user, accessToken);
    }

    public void logout(Long userId, String accessToken) {
        validatedUserId(userId);

        Long accessTokenExpiration = tokenProvider.getExpiration(accessToken);
        redisService.logoutFromRedis(userId, accessToken, accessTokenExpiration);
    }

    public TokenResponse reissue(String refreshToken) {
        Long userId = tokenProvider.getRefreshTokenInfo(refreshToken);

        validateRefreshToken(userId, refreshToken);

        User user = userService.getUser(userId);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userId,
            null,
            user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList())
        );

        String newAccessToken = tokenProvider.createAccessToken(authentication);
        return new TokenResponse(newAccessToken);
    }

    private void validateRefreshToken(Long userId, String token) {
        String storedToken = redisService.getValues(userId);
        if(storedToken == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        if (!storedToken.equals(token)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
    }

    public void validatedUserId(Long userId) {
        userRepository.findByUserIdAndIsDeleteFalse(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_EMAIL));

    }

}

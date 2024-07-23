package com.WearWeather.wear.auth.service;

import com.WearWeather.wear.auth.dto.TokenInfo;
import com.WearWeather.wear.auth.dto.request.LoginRequest;
import com.WearWeather.wear.auth.dto.response.LoginResponse;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import com.WearWeather.wear.global.jwt.TokenProvider;
import com.WearWeather.wear.global.redis.RedisService;
import com.WearWeather.wear.user.entity.Role;
import com.WearWeather.wear.user.entity.User;
import com.WearWeather.wear.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RedisService redisService;

    public LoginResponse checkLogin(LoginRequest request) {
        User user = userRepository.findOneWithAuthoritiesByEmail(request.getEmail())
            .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_IS_NULL_EXCEPTION));

        validatePassword(request.getPassword(), user.getPassword());

        String accessToken = tokenProvider.createAccessToken(user.getEmail(), Role.USER);
        String refreshToken = tokenProvider.createRefreshToken(user.getEmail(), Role.USER);

        return LoginResponse.of(user, accessToken, refreshToken);
    }

    public void logout(String email, String accessToken) {
        findByUserEmail(email);

        Long accessTokenExpiration = tokenProvider.getExpiration(accessToken);
        redisService.logoutFromRedis(email, accessToken, accessTokenExpiration);
    }

    public TokenInfo reissue(String refreshToken) {
        Claims claims = tokenProvider.parseClaims(refreshToken);
        User user = findByUserEmail(claims.getSubject());

        findRefreshTokenInRedis(user.getEmail(), refreshToken);

        String newAccessToken = tokenProvider.createAccessToken(user.getEmail(), Role.USER);
        String newRefreshToken = tokenProvider.createRefreshToken(user.getEmail(), Role.USER);

        return new TokenInfo(newAccessToken, newRefreshToken);
    }

    private void findRefreshTokenInRedis(String userEmail, String RefreshToken) {
        String storedRefreshToken = redisService.getValues(userEmail);
        if (!storedRefreshToken.equals(RefreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    private User findByUserEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_IS_NULL_EXCEPTION));
    }

    private void validatePassword(String password, String encodePassword) {
        if (!passwordEncoder.matches(password, encodePassword)) {
            throw new CustomException(ErrorCode.PASSWORD_INVALID_EXCEPTION);
        }
    }

}

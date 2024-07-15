package com.WearWeather.wear.auth.service;

import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import com.WearWeather.wear.global.jwt.TokenProvider;
import com.WearWeather.wear.global.redis.RedisService;
import com.WearWeather.wear.auth.dto.request.LoginRequest;
import com.WearWeather.wear.auth.dto.response.LoginResponse;
import com.WearWeather.wear.user.entity.User;
import com.WearWeather.wear.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final RedisService  redisService;

    public LoginResponse checkLogin(LoginRequest request) {
        User user = userRepository.findOneWithAuthoritiesByEmail(request.getEmail())
            .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_IS_NULL_EXCEPTION));

        validatePassword(request.getPassword(), user.getPassword());

        Authentication authentication = getAuthentication(request.getEmail(), request.getPassword());
        String accessToken = tokenProvider.createToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(request.getEmail());

        redisService.setValues(request.getEmail(), refreshToken);

        return LoginResponse.of(user, accessToken, refreshToken);
    }

    private void validatePassword(String password, String encodePassword) {
        if (!passwordEncoder.matches(password, encodePassword)) {
            throw new CustomException(ErrorCode.PASSWORD_INVALID_EXCEPTION);
        }
    }

    private Authentication getAuthentication(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }
}

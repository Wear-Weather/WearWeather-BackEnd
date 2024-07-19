package com.WearWeather.wear.auth.service;

import com.WearWeather.wear.auth.dto.TokenDto;
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
import org.springframework.transaction.annotation.Transactional;

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

    public void logout(String email, String accessToken) {
        validateExistedUserEmail(email);

        Long accessTokenExpiration = tokenProvider.getExpiration(accessToken);
        redisService.logoutFromRedis(email,accessToken, accessTokenExpiration);
    }

    public TokenDto reissue(TokenDto tokenDto) {
        String accessToken = tokenDto.getAccessToken();
        String refreshToken = tokenDto.getRefreshToken();

        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        String userEmail = authentication.getName();

        validateRefreshToken(userEmail, refreshToken);

        String newAccessToken = tokenProvider.createToken(authentication);
        String newRefreshToken = tokenProvider.createRefreshToken(userEmail);
        redisService.setValues(userEmail, newRefreshToken);

        return new TokenDto(newAccessToken, newRefreshToken);
    }

    private void validateRefreshToken(String userEmail, String RefreshToken) {
        String storedRefreshToken = redisService.getValues(userEmail);
        if (!storedRefreshToken.equals(RefreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    private void validateExistedUserEmail(String email) {
        userRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_IS_NULL_EXCEPTION));
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

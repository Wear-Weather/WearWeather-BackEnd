package com.WearWeather.wear.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import com.WearWeather.wear.global.jwt.TokenProvider;
import com.WearWeather.wear.auth.dto.request.LoginRequest;
import com.WearWeather.wear.auth.dto.response.LoginResponse;
import com.WearWeather.wear.auth.service.AuthService;
import com.WearWeather.wear.global.redis.RedisService;
import com.WearWeather.wear.user.entity.User;
import com.WearWeather.wear.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("로그인 시도 시 존재하지 않는 이메일이면 예외를 발생시킨다")
    public void testLoginWithNonexistentEmail() {
        // given
        LoginRequest request = new LoginRequest("nonexistent@example.com", "password");
        when(userRepository.findOneWithAuthoritiesByEmail(anyString())).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> authService.checkLogin(request));
        assertEquals(ErrorCode.EMAIL_IS_NULL_EXCEPTION, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그인 시 비밀번호가 올바르지 않아서 예외를 발생시킨다")
    public void testLoginWithIncorrectPassword() {
        // given
        User user = User.builder()
            .email("user@example.com")
            .password("encodedPassword")
            .build();
        LoginRequest request = new LoginRequest("user@example.com", "wrongPassword");

        when(userRepository.findOneWithAuthoritiesByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> authService.checkLogin(request));
        assertEquals(ErrorCode.PASSWORD_INVALID_EXCEPTION, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그인 성공 시 올바른 응답을 반환한다")
    public void testSuccessfulLogin() {
        // given
        User user = User.builder()
            .email("user@example.com")
            .password("encodedPassword")
            .build();
        LoginRequest request = new LoginRequest("user@example.com", "correctPassword");

        when(userRepository.findOneWithAuthoritiesByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);

        when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);

        when(tokenProvider.createToken(any(Authentication.class))).thenReturn("accessToken");
        when(tokenProvider.createRefreshToken(anyString())).thenReturn("refreshToken");

        // when
        LoginResponse response = authService.checkLogin(request);

        // then
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
    }

    @Test
    @DisplayName("로그인 성공 시 AccessToken, RefreshToken이 생성된다")
    public void testTokenCreation() {
        // given
        User user = User.builder()
            .email("user@example.com")
            .password("encodedPassword")
            .build();
        LoginRequest request = new LoginRequest("user@example.com", "correctPassword");

        when(userRepository.findOneWithAuthoritiesByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);

        when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);

        when(tokenProvider.createToken(any(Authentication.class))).thenReturn("accessToken");
        when(tokenProvider.createRefreshToken(anyString())).thenReturn("refreshToken");

        // when
        authService.checkLogin(request);

        // then
        verify(tokenProvider).createToken(authentication);
        verify(tokenProvider).createRefreshToken("user@example.com");
    }

    @Test
    @DisplayName("로그인 성공 시 refresh token이 Redis에 저장된다")
    public void testRedisRefreshTokenStorage() {
        // given
        User user = User.builder()
            .email("user@example.com")
            .password("encodedPassword")
            .build();
        LoginRequest request = new LoginRequest("user@example.com", "correctPassword");

        when(userRepository.findOneWithAuthoritiesByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);

        when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);

        when(tokenProvider.createToken(any(Authentication.class))).thenReturn("accessToken");
        when(tokenProvider.createRefreshToken(anyString())).thenReturn("refreshToken");

        // when
        authService.checkLogin(request);

        // then
        verify(redisService).setValues("user@example.com", "refreshToken");
    }
}

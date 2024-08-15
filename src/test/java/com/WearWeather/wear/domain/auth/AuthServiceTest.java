package com.WearWeather.wear.domain.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.WearWeather.wear.domain.auth.dto.request.LoginRequest;
import com.WearWeather.wear.domain.auth.dto.request.RefresehTokenRequest;
import com.WearWeather.wear.domain.auth.dto.response.LoginResponse;
import com.WearWeather.wear.domain.auth.dto.response.TokenResponse;
import com.WearWeather.wear.domain.auth.service.AuthService;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.repository.UserRepository;
import com.WearWeather.wear.fixture.UserFixture;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import com.WearWeather.wear.global.jwt.TokenProvider;
import com.WearWeather.wear.global.redis.RedisService;
import java.util.Collections;
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

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Test
    @DisplayName("예외 테스트 : 로그인 시 존재하지 않는 이메일")
    public void loginWithNonexistentEmail() {
        // given
        LoginRequest request = new LoginRequest("nonexistent@example.com", "password");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> authService.checkLogin(request));
        assertEquals(ErrorCode.EMAIL_IS_NULL_EXCEPTION, exception.getErrorCode());
    }

    @Test
    @DisplayName("정상 테스트 : 스프링 시큐리티 내부에서 진행된 인증을 통해 로그인에 성공한다.")
    public void successfulLogin() {
        // given
        LoginRequest request = new LoginRequest("user@example.com", "correctPassword");
        User userFixture = UserFixture.createUser(request.getEmail(), "encodedPassword");

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userFixture, null, Collections.emptyList());

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(userFixture));
        when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
        when(authenticationManager.authenticate(authenticationToken)).thenReturn(authentication);
        when(tokenProvider.createAccessToken(authentication)).thenReturn("accessToken");
        when(tokenProvider.createRefreshToken(anyString())).thenReturn("refreshToken");

        // when
        LoginResponse result = authService.checkLogin(request);

        // then
        assertNotNull(result);
        assertEquals("accessToken", result.getAccessToken());
        assertEquals("refreshToken", result.getRefreshToken());
    }


    @Test
    @DisplayName("정상 테스트 : 로그인 성공 시 AccessToken, RefreshToken이 생성된다.")
    public void tokenCreationOnLogin() {
        // given
        User user = UserFixture.createUser("user@example.com", "encodedPassword");
        LoginRequest request = new LoginRequest("user@example.com", "correctPassword");

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
        when(authenticationManager.authenticate(authenticationToken)).thenReturn(authentication);
        when(tokenProvider.createAccessToken(authentication)).thenReturn("accessToken");
        when(tokenProvider.createRefreshToken(anyString())).thenReturn("refreshToken");

        // when
        LoginResponse response = authService.checkLogin(request);

        // then
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        verify(tokenProvider).createAccessToken(authentication);
        verify(tokenProvider).createRefreshToken(user.getEmail());
    }


    @Test
    @DisplayName("예외 테스트 : 로그아웃 시 존재하지 않는 이메일")
    public void logoutWithNonexistentEmail() {
        // given
        String email = "nonexistent@example.com";
        String accessToken = "someAccessToken";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> authService.logout(email, accessToken));
        assertEquals(ErrorCode.EMAIL_IS_NULL_EXCEPTION, exception.getErrorCode());
    }

    @Test
    @DisplayName("정상 테스트 : 로그아웃 시 Redis에서 로그아웃 처리하는지 검증한다.")
    public void successfulLogout() {
        // given
        String accessToken = "AccessToken";
        Long expiration = 3600L;
        User user = UserFixture.createUser("user@example.com", "encodedPassword");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(tokenProvider.getExpiration(anyString())).thenReturn(expiration);

        // when
        authService.logout(user.getEmail(), accessToken);

        // then
        verify(redisService).logoutFromRedis(user.getEmail(), accessToken, expiration);
    }

    @Test
    @DisplayName("예외 테스트 : 유효하지 않은 refresh token으로 재발급 시도하여 예외가 발생한다.")
    public void reissueWithInvalidRefreshToken() {
        // given
        String userEmail = "test@example.com";
        String refreshToken = "invalid_refresh_token";
        RefresehTokenRequest request = new RefresehTokenRequest(refreshToken);

        when(tokenProvider.getRefreshTokenInfo(refreshToken)).thenReturn(userEmail);
        when(redisService.getValues(userEmail)).thenReturn("different_refresh_token");

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            authService.reissue(request);
        });

        assertEquals(ErrorCode.INVALID_REFRESH_TOKEN, exception.getErrorCode());

    }


    @Test
    @DisplayName("정상 테스트 : 정상적인 토큰 재발급 시 새로운 access token과 refresh token이 생성된다.")
    public void successfulReissueTokens() {
        // given
        String userEmail = "test@example.com";
        String refreshToken = "valid_refresh_token";
        String newAccessToken = "new_access_token";
        String newRefreshToken = "new_refresh_token";
        RefresehTokenRequest request = new RefresehTokenRequest(refreshToken);

        when(tokenProvider.getRefreshTokenInfo(refreshToken)).thenReturn(userEmail);
        when(redisService.getValues(userEmail)).thenReturn(refreshToken);
        when(tokenProvider.createAccessToken(any(Authentication.class))).thenReturn(newAccessToken);
        when(tokenProvider.createRefreshToken(userEmail)).thenReturn(newRefreshToken);

        // when
        TokenResponse response = authService.reissue(request);

        // then
        assertNotNull(response);
        assertEquals(newAccessToken, response.getAccessToken());
        assertEquals(newRefreshToken, response.getRefreshToken());
    }
}

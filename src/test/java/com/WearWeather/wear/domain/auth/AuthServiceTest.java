package com.WearWeather.wear.domain.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.WearWeather.wear.domain.auth.dto.request.LoginRequest;
import com.WearWeather.wear.domain.auth.dto.response.LoginResponse;
import com.WearWeather.wear.domain.auth.dto.response.TokenResponse;
import com.WearWeather.wear.domain.auth.service.AuthService;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.repository.UserRepository;
import com.WearWeather.wear.domain.user.service.UserService;
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

    @Mock
    private UserService userService;

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
        when(userService.getUserByEmail(request.getEmail())).thenThrow(new CustomException(ErrorCode.NOT_EXIST_EMAIL));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> authService.checkLogin(request));
        assertEquals(ErrorCode.NOT_EXIST_EMAIL, exception.getErrorCode());
    }

    @Test
    @DisplayName("정상 테스트 : 스프링 시큐리티 내부에서 진행된 인증을 통해 로그인에 성공한다.")
    public void successfulLogin() {
        // given
        User user = UserFixture.createUser();
        LoginRequest request = new LoginRequest("abcd@gmail.com", "abcd12!@");

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUserId(), null, Collections.emptyList());

        when(userService.getUserByEmail(request.getEmail())).thenReturn(user);
        when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
        when(authenticationManager.authenticate(authenticationToken)).thenReturn(authentication);
        when(tokenProvider.createAccessToken(authentication)).thenReturn("accessToken");

        // when
        LoginResponse result = authService.checkLogin(request);

        // then
        assertNotNull(result);
    }


    @Test
    @DisplayName("정상 테스트 : 로그인 성공 시 AccessToken이 생성된다.")
    public void tokenCreationOnLogin() {
        // given
        User user = UserFixture.createUser();
        LoginRequest request = new LoginRequest("abcd@gmail.com", "abcd12!@");

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUserId(), null, Collections.emptyList());

        when(userService.getUserByEmail(request.getEmail())).thenReturn(user);
        when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
        when(authenticationManager.authenticate(authenticationToken)).thenReturn(authentication);
        when(tokenProvider.createAccessToken(authentication)).thenReturn("accessToken");

        // when
        LoginResponse response = authService.checkLogin(request);

        // then
        assertEquals("accessToken", response.getAccessToken());
        verify(tokenProvider).createAccessToken(authentication);
    }

    @Test
    @DisplayName("예외 테스트 : 로그아웃 시 존재하지 않는 이메일")
    public void logoutWithNonexistentEmail() {
        // given
        Long userId = 1L;
        String accessToken = "someAccessToken";
        when(userRepository.findByUserIdAndIsDeleteFalse(userId)).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> authService.logout(userId, accessToken));
        assertEquals(ErrorCode.NOT_EXIST_EMAIL, exception.getErrorCode());
    }

    @Test
    @DisplayName("정상 테스트 : 로그아웃 시 Redis에서 로그아웃 처리하는지 검증한다.")
    public void successfulLogout() {
        // given
        String accessToken = "AccessToken";
        Long expiration = 3600L;
        User user = UserFixture.createUser("user@example.com", "encodedPassword");

        when(userRepository.findByUserIdAndIsDeleteFalse(user.getUserId())).thenReturn(Optional.of(user));
        when(tokenProvider.getExpiration(anyString())).thenReturn(expiration);

        // when
        authService.logout(user.getUserId(), accessToken);

        // then
        verify(redisService).logoutFromRedis(user.getUserId(), accessToken, expiration);
    }

    @Test
    @DisplayName("예외 테스트 : 유효하지 않은 refresh token으로 재발급 시도하여 예외가 발생한다.")
    public void reissueWithInvalidRefreshToken() {
        // given
        Long userId = 1L;
        String refreshToken = "invalid_refresh_token";

        when(tokenProvider.getTokenInfo(refreshToken)).thenReturn(userId);
        when(redisService.getValues(userId)).thenReturn(null);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> authService.reissue(refreshToken));
        assertEquals(ErrorCode.INVALID_REFRESH_TOKEN, exception.getErrorCode());
    }


    @Test
    @DisplayName("정상 테스트 : 정상적인 토큰 재발급 시 새로운 accessToken이 생성된다.")
    public void successfulReissueTokens() {
        // given
        Long userId = 1L;
        String refreshToken = "valid_refresh_token";
        String newAccessToken = "new_access_token";

        Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

        when(tokenProvider.getTokenInfo(refreshToken)).thenReturn(userId);
        when(redisService.getValues(userId)).thenReturn(refreshToken);
        when(tokenProvider.createAccessToken(authentication)).thenReturn(newAccessToken);

        // when
        TokenResponse response = authService.reissue(refreshToken);

        // then
        assertNotNull(response);
        assertEquals(newAccessToken, response.getAccessToken());
    }
}

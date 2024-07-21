package com.WearWeather.wear.auth;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.WearWeather.wear.auth.dto.TokenInfo;
import com.WearWeather.wear.fixture.UserFixture;
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
    @DisplayName("예외 테스트 : 로그인 시 존재하지 않는 이메일")
    public void loginWithNonexistentEmail() {
        // given
        LoginRequest request = new LoginRequest("nonexistent@example.com", "password");
        when(userRepository.findOneWithAuthoritiesByEmail(anyString())).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> authService.checkLogin(request));
        assertEquals(ErrorCode.EMAIL_IS_NULL_EXCEPTION, exception.getErrorCode());
    }

    @Test
    @DisplayName("예외 테스트 : 로그인 시 이메일에 맞는 올바르지 않은 비밀번호")
    public void loginWithIncorrectPassword() {
        // given
        User user = UserFixture.createUser("user@example.com", "encodedPassword");
        LoginRequest request = new LoginRequest("user@example.com", "wrongPassword");

        when(userRepository.findOneWithAuthoritiesByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> authService.checkLogin(request));
        assertEquals(ErrorCode.PASSWORD_INVALID_EXCEPTION, exception.getErrorCode());
    }

    @Test
    @DisplayName("정상 테스트 : 이메일과 패스워드가 일치하여 로그인에 성공한다. ")
    public void successfulLogin() {
        // given
        LoginRequest request = new LoginRequest("user@example.com", "correctPassword");
        User userFixture = UserFixture.createUser(request.getEmail(), request.getPassword());

        when(userRepository.findOneWithAuthoritiesByEmail(request.getEmail())).thenReturn(Optional.of(userFixture));
        when(passwordEncoder.matches(request.getPassword(), userFixture.getPassword())).thenReturn(true);

        when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);

        when(tokenProvider.createToken(any(Authentication.class))).thenReturn("accessToken");
        when(tokenProvider.createRefreshToken(anyString())).thenReturn("refreshToken");

        // when
        LoginResponse result = authService.checkLogin(request);

        // then
        assertNotNull(result);
    }

    @Test
    @DisplayName("정상 테스트 : 로그인 성공 시 AccessToken, RefreshToken이 생성된다.")
    public void tokenCreationOnLogin() {
        // given
        User user = UserFixture.createUser("user@example.com", "encodedPassword");
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
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        verify(tokenProvider).createToken(authentication);
        verify(tokenProvider).createRefreshToken("user@example.com");
    }

    @Test
    @DisplayName("정상 테스트 : 로그인 성공 시 refresh token이 Redis에 저장되는지 검증한다.")
    public void redisRefreshTokenStorageOnLogin() {
        // given
        User user = UserFixture.createUser("user@example.com", "encodedPassword");
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
    @DisplayName("정상 테스트 :  로그아웃 시 Redis에서 로그아웃 처리하는지 검증한다.")
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
        String email = "user@example.com";
        TokenInfo tokenDto = new TokenInfo("AccessToken", "invalidRefreshToken");

        Authentication authentication = mock(Authentication.class);
        when(tokenProvider.getAuthentication(anyString())).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        when(redisService.getValues(anyString())).thenReturn("validRefreshToken");

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> authService.reissue(tokenDto));
        assertEquals(ErrorCode.INVALID_REFRESH_TOKEN, exception.getErrorCode());
    }

    @Test
    @DisplayName("정상 테스트 : 정상적인 토큰 재발급 시 새로운 access token과 refresh token이 생성된다.")
    public void successfulReissueTokens() {
        // given
        String email = "user@example.com";
        TokenInfo tokenDto = new TokenInfo("AccessToken", "validRefreshToken");

        Authentication authentication = mock(Authentication.class);
        when(tokenProvider.getAuthentication(anyString())).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        when(redisService.getValues(anyString())).thenReturn(tokenDto.getRefreshToken());
        when(tokenProvider.createToken(any(Authentication.class))).thenReturn("newAccessToken");
        when(tokenProvider.createRefreshToken(anyString())).thenReturn("newRefreshToken");

        // when
        TokenInfo newTokenInfo = authService.reissue(tokenDto);

        // then
        assertNotNull(newTokenInfo);
        assertEquals("newAccessToken", newTokenInfo.getAccessToken());
        assertEquals("newRefreshToken", newTokenInfo.getRefreshToken());
        verify(redisService).setValues(email, "newRefreshToken");
    }

}

package com.WearWeather.wear.domain.auth.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.WearWeather.wear.domain.auth.dto.request.LoginRequest;
import com.WearWeather.wear.domain.auth.dto.response.LoginResponse;
import com.WearWeather.wear.domain.auth.dto.response.TokenResponse;
import com.WearWeather.wear.domain.auth.provider.AuthenticationProvider;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.fixture.UserFixture;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import com.WearWeather.wear.global.jwt.TokenProvider;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class LoginFacadeTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationProvider authenticationProvider;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private LoginFacade loginFacade;

    @Test
    @DisplayName("예외 테스트: 로그인 시 존재하지 않는 이메일")
    public void loginWithNonexistentEmail() {
        // given
        LoginRequest request = new LoginRequest("nonexistent@example.com", "password");
        when(userService.getUserByEmail(request.getEmail())).thenThrow(new CustomException(ErrorCode.NOT_EXIST_EMAIL));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> loginFacade.checkLogin(request));
        assertEquals(ErrorCode.NOT_EXIST_EMAIL, exception.getErrorCode());
    }

    @Test
    @DisplayName("정상 테스트: 스프링 시큐리티 인증 성공을 통해 로그인에 성공한다.")
    public void successfulLogin() {
        // given
        User user = UserFixture.createUser();
        LoginRequest request = new LoginRequest("abcd@gmail.com", "abcd12!@");

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUserId(), null, Collections.emptyList());

        when(userService.getUserByEmail(request.getEmail())).thenReturn(user);
        when(authenticationProvider.authenticateWithCredentials(request.getEmail(), request.getPassword()))
          .thenReturn(authentication);
        when(tokenProvider.createAccessToken(authentication)).thenReturn("accessToken");

        // when
        TokenResponse result = loginFacade.checkLogin(request);

        // then
        assertNotNull(result);
        assertEquals("accessToken", result.getAccessToken());
        verify(tokenProvider).createAccessToken(authentication);
    }

    @Test
    @DisplayName("정상 테스트: 로그인 성공 시 AccessToken이 생성된다.")
    public void tokenCreationOnLogin() {
        // given
        User user = UserFixture.createUser();
        LoginRequest request = new LoginRequest("abcd@gmail.com", "abcd12!@");

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUserId(), null, Collections.emptyList());

        when(userService.getUserByEmail(request.getEmail())).thenReturn(user);
        when(authenticationProvider.authenticateWithCredentials(request.getEmail(), request.getPassword()))
          .thenReturn(authentication);
        when(tokenProvider.createAccessToken(authentication)).thenReturn("accessToken");

        // when
        TokenResponse response = loginFacade.checkLogin(request);

        // then
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        verify(tokenProvider).createAccessToken(authentication);
    }
}

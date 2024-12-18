package com.WearWeather.wear.domain.auth.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.WearWeather.wear.domain.auth.dto.response.TokenResponse;
import com.WearWeather.wear.domain.auth.provider.AuthenticationProvider;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.fixture.UserFixture;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import com.WearWeather.wear.global.jwt.TokenProvider;
import com.WearWeather.wear.global.redis.RedisService;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
public class ReissueFacadeTest {

    @Mock
    private UserService userService;

    @Mock
    private RedisService redisService;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private AuthenticationProvider authenticationProvider;

    @InjectMocks
    private ReissueFacade reissueFacade;

    @Test
    @DisplayName("예외 테스트: 유효하지 않은 refreshToken 으로 재발급 시도 시 예외가 발생한다.")
    public void reissueWithInvalidRefreshToken() {
        // given
        Long userId = 1L;
        String refreshToken = "invalid_refresh_token";

        when(tokenProvider.getRefreshTokenInfo(refreshToken)).thenReturn(userId);
        when(redisService.getValues(userId)).thenThrow(new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> reissueFacade.reissue(refreshToken));

        assertEquals(ErrorCode.REFRESH_TOKEN_EXPIRED, exception.getErrorCode());
    }

    @Test
    @DisplayName("예외 테스트: 저장된 RefreshToken과 요청된 RefreshToken이 다르면 예외가 발생한다.")
    public void reissueWithMismatchedRefreshToken() {
        // given
        Long userId = 1L;
        String savedRefreshToken = "saved_refresh_token";
        String requestRefreshToken = "request_refresh_token";

        when(tokenProvider.getRefreshTokenInfo(requestRefreshToken)).thenReturn(userId);
        when(redisService.getValues(userId)).thenReturn(savedRefreshToken);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> reissueFacade.reissue(requestRefreshToken));

        assertEquals(ErrorCode.REFRESH_TOKEN_INVALID, exception.getErrorCode());
    }

    @Test
    @DisplayName("정상 테스트: 정상적인 토큰 재발급 시 새로운 accessToken이 생성된다.")
    public void successfulReissueTokens() {
        // given
        String refreshToken = "valid_refresh_token";
        String newAccessToken = "new_access_token";
        Long userId = 1L;

        User user = UserFixture.createUserWithAuthority("ROLE_USER");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
          user.getUserId(), null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(tokenProvider.getRefreshTokenInfo(refreshToken)).thenReturn(userId);
        when(redisService.getValues(userId)).thenReturn(refreshToken);
        when(userService.getUser(userId)).thenReturn(user);
        when(authenticationProvider.createAuthenticatedToken(user)).thenReturn(authentication);
        when(tokenProvider.createAccessToken(authentication)).thenReturn(newAccessToken);

        // when
        TokenResponse response = reissueFacade.reissue(refreshToken);

        // then
        assertNotNull(response);
        assertEquals(newAccessToken, response.getAccessToken());

    }
}

package com.WearWeather.wear.domain.auth.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import com.WearWeather.wear.global.jwt.TokenProvider;
import com.WearWeather.wear.global.redis.RedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LogOutFacadeTest {

    @Mock
    private RedisService redisService;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private LogOutFacade logOutFacade;

    @Test
    @DisplayName("예외 테스트: 유효하지 않은 access token으로 로그아웃 시도 시 예외가 발생한다.")
    public void logoutWithInvalidAccessToken() {
        // given
        Long userId = 1L;
        String invalidAccessToken = "invalid_access_token";

        when(tokenProvider.getExpiration(invalidAccessToken))
          .thenThrow(new CustomException(ErrorCode.INVALID_ACCESS_TOKEN));

        // when & then
        CustomException exception = assertThrows(CustomException.class,
          () -> logOutFacade.logout(userId, invalidAccessToken));

        assertEquals(ErrorCode.INVALID_ACCESS_TOKEN, exception.getErrorCode());
    }

}

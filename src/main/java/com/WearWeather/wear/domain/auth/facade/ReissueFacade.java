package com.WearWeather.wear.domain.auth.facade;

import com.WearWeather.wear.domain.auth.dto.response.TokenResponse;
import com.WearWeather.wear.domain.auth.provider.AuthenticationProvider;
import com.WearWeather.wear.domain.user.entity.User;
import com.WearWeather.wear.domain.user.service.UserService;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import com.WearWeather.wear.global.jwt.TokenProvider;
import com.WearWeather.wear.global.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReissueFacade {

    private final UserService userService;
    private final RedisService redisService;
    private final AuthenticationProvider authenticationProvider;
    private final TokenProvider tokenProvider;

    public TokenResponse reissue(String refreshToken) {
        Long userId = tokenProvider.getRefreshTokenInfo(refreshToken);
        String savedRefreshToken = redisService.getValues(userId);
        validateRefreshToken(savedRefreshToken, refreshToken);

        User user = userService.getUser(userId);
        Authentication authentication = authenticationProvider.createAuthenticatedToken(user);
        String newAccessToken = tokenProvider.createAccessToken(authentication);

        return new TokenResponse(newAccessToken);
    }

    private void validateRefreshToken(String savedRefreshToken, String refreshToken) {
        if(savedRefreshToken == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        if (!savedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
    }


}

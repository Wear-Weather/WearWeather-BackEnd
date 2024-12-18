package com.WearWeather.wear.domain.auth.facade;

import com.WearWeather.wear.global.jwt.TokenProvider;
import com.WearWeather.wear.global.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogOutFacade {

    private final RedisService redisService;
    private final TokenProvider tokenProvider;

    public void logout(Long userId, String accessToken) {
        Long accessTokenExpiration = tokenProvider.getExpiration(accessToken);
        redisService.logoutFromRedis(userId, accessToken, accessTokenExpiration);
    }

}

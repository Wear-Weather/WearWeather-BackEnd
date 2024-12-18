package com.WearWeather.wear.global.redis;


import static com.WearWeather.wear.global.exception.ErrorCode.REDIS_VALUE_INVALID;
import static com.WearWeather.wear.global.exception.ErrorCode.REDIS_VALUE_NOT_FOUND;

import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisDao redisDao;

    public Boolean setValues(String key, String value, Long expirationInSeconds) {
        redisDao.setValues(key, value,Duration.ofSeconds(expirationInSeconds));
        return true;
    }

    public String getValues(Long key) {
        return redisDao.getValues(String.valueOf(key));
    }

    public Integer getValuesInteger(Long key) {
        String values = redisDao.getValues(key.toString());

        if (values.isBlank()) {
            throw new CustomException(REDIS_VALUE_NOT_FOUND);
        }

        return Integer.valueOf(values);
    }

    public Boolean logoutFromRedis(Long userId, String accessToken, Long accessTokenExpiration) {
        String userKey = "user:" + userId;
        redisDao.deleteValues(userKey);
        redisDao.setValues(accessToken, "BlackList", Duration.ofMillis(accessTokenExpiration));
        return true;
    }


}

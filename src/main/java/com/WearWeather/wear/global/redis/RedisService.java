package com.WearWeather.wear.global.redis;


import static com.WearWeather.wear.global.exception.ErrorCode.REDIS_VALUE_NOT_FOUND;
import static com.WearWeather.wear.global.exception.ErrorCode.REFRESH_TOKEN_INVALID;

import com.WearWeather.wear.global.exception.CustomException;
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
        String values = redisDao.getValues(String.valueOf(key));

        if(values == null) {
            throw new CustomException(REDIS_VALUE_NOT_FOUND);
        }

        if(values.isBlank()){
            throw new CustomException(REFRESH_TOKEN_INVALID);
        }

        return values;
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

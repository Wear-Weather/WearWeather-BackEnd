package com.WearWeather.wear.global.redis;


import static com.WearWeather.wear.global.exception.ErrorCode.REDIS_VALUE_NOT_FOUND;

import com.WearWeather.wear.global.exception.CustomException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisDao redisDao;

    public Boolean setValues(String key, String value) {
        redisDao.setValues(key, value);
        return true;
    }

    public String getValues(String key) {
        String values = redisDao.getValues(key);

        if (values != null && values.isBlank()) {
            throw new CustomException(REDIS_VALUE_NOT_FOUND);
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

    public Boolean logoutFromRedis(String email, String accessToken, Long accessTokenExpiration) {
        redisDao.deleteValues(email);
        redisDao.setValues(accessToken, "BlackList", Duration.ofMillis(accessTokenExpiration));
        return true;
    }


}

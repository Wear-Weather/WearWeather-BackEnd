package com.WearWeather.wear.domain.weather.domain;

import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum OutfitGuideByTemperature {

    WINTER_CLOTHES(1L, -50, 5, "겨울 옷(야상, 패딩, 장갑)"),
    SPRING_COAT(2L, 6, 9, "트렌치코트, 간절기 야상 "),
    MID_SPRING(3L,10, 11, "트렌치코트, 간절기 야상 "),
    JACKET_CLOTHES(4L, 12, 16, "자켓, 셔츠, 가디건, 간절기 야상 "),
    KNIT_CLOTHES(5L, 17, 19, "니트, 가디건, 후드티 등"),
    LONG_SLEEVE_TEE(6L, 20, 22, "긴팔티, 슬랙스, 가디건 "),
    SHORT_SLEEVE_CLOTHES(7L, 23, 26, "반팔, 얇은 셔츠, 얇은 긴팔 등"),
    SUMMER_CLOTHES(8L, 27, 50, "나시티, 면바지, 민소매 등");

    private final Long guideId;
    private final int rangeStart;
    private final int rangeEnd;
    private final String recommendLook;

    OutfitGuideByTemperature(Long guideId, int rangeStart, int rangeEnd, String recommendLook) {
        this.guideId = guideId;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.recommendLook = recommendLook;
    }

    public boolean isInRange(int tmp) {
        return tmp >= rangeStart && tmp <= rangeEnd;
    }

    public static OutfitGuideByTemperature fromTemperature(int tmp) {
        for (OutfitGuideByTemperature range : OutfitGuideByTemperature.values()) {
            if (range.isInRange(tmp)) {
                return range;
            }
        }
        throw new CustomException(ErrorCode.NOT_MATCH_TEMPERATURE_RANGE);
    }

}

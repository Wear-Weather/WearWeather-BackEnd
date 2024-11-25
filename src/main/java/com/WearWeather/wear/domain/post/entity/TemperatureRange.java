package com.WearWeather.wear.domain.post.entity;

import com.WearWeather.wear.global.exception.CustomErrorResponse;
import com.WearWeather.wear.global.exception.CustomException;
import com.WearWeather.wear.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum TemperatureRange {

    WINTER_CLOTHES(-50, 5, "겨울 옷(야상, 패딩, 장갑)"),
    SPRING_COAT(6, 9, "트렌치코트, 간절기 야상 "),
    MID_SPRING(10, 11, "트렌치코트, 간절기 야상 "),
    JACKET_CLOTHES(12, 16, "자켓, 셔츠, 가디건, 간절기 야상 "),
    KNIT_CLOTHES(17, 19, "니트, 가디건, 후드티 등"),
    LONG_SLEEVE_TEE(20, 22, "긴팔티, 슬랙스, 가디건 "),
    SHORT_SLEEVE_CLOTHES(23, 26, "반팔, 얇은 셔츠, 얇은 긴팔 등"),
    SUMMER_CLOTHES(27, 50, "나시티, 면바지, 민소매 등");

    private final int rangeStart;
    private final int rangeEnd;
    @Getter
    private final String label;

    TemperatureRange(int rangeStart, int rangeEnd, String label) {
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.label = label;
    }

    public boolean isInRange(int tmp) {
        return tmp >= rangeStart && tmp <= rangeEnd;
    }

    public static TemperatureRange fromTemperature(int tmp) {
        for (TemperatureRange range : TemperatureRange.values()) {
            if (range.isInRange(tmp)) {
                return range;
            }
        }
        throw new CustomException(ErrorCode.NOT_MATCH_TEMPERATURE_RANGE);
    }

}

package com.WearWeather.wear.domain.user.enums;

import lombok.Getter;

@Getter
public enum DeleteReason {
    NOT_USING("사용을 잘 안하게 돼요"),
    LOW_ACTIVITY("서비스 활성화가 잘 안되어 있어요"),
    PRIVACY_CONCERNS("개인정보 보호를 위해 삭제할 필요가 있어요"),
    POOR_FUNCTIONALITY("서비스 기능이 미흡해요"),
    ERROR_FREQUENT("오류가 잦아요");

    private final String description;

    DeleteReason(String description) {
        this.description = description;
    }

}

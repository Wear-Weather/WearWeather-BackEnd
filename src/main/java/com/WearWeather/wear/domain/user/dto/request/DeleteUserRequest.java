package com.WearWeather.wear.domain.user.dto.request;

import com.WearWeather.wear.domain.user.entity.UserDelete;
import jakarta.validation.constraints.NotNull;

public record DeleteUserRequest(
        @NotNull(message = "탈퇴 이유는 필수입니다.")
        Long deleteReasonId
){

        public UserDelete toEntity(Long userId, Long deleteReasonId){
                return UserDelete.builder()
                        .userId(userId)
                        .deleteReasonId(deleteReasonId)
                        .build();
        }
}

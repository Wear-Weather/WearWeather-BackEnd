package com.WearWeather.wear.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DeleteReasonRequest(
        @NotBlank(message = "탈퇴 이유는 필수입니다.")
        String deleteReason
){

}

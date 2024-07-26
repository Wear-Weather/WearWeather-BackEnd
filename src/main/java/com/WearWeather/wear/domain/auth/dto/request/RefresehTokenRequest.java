package com.WearWeather.wear.domain.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Data
public class RefresehTokenRequest {

    private final String refreshToken;
}

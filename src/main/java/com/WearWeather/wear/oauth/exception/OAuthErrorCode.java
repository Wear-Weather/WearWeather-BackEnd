package com.WearWeather.wear.oauth.exception;

import com.WearWeather.wear.global.NewException.NewErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OAuthErrorCode implements NewErrorCode {
    KAKAO_TOKEN_EMPTY(HttpStatus.BAD_REQUEST, "카카오 토큰이 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getErrorDetail() {
        return this.message;
    }
}
